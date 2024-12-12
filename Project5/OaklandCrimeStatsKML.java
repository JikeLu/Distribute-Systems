package org.myorg;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class OaklandCrimeStatsKML {

    public static class CrimeMapper extends Mapper<Object, Text, Text, Text> {
        // Forbes Avenue coordinates in feet
        private static final double forbesX = 1354326.897;
        private static final double forbesY = 411447.7828;
        // Convert meters to feet (1 meter = 3.28084 feet)
        private static final double radius = 350 * 3.28084;

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] parts = value.toString().split("\t");
            if (parts.length > 8) {
                try {
                    double x = Double.parseDouble(parts[0]);
                    double y = Double.parseDouble(parts[1]);
                    double lat = Double.parseDouble(parts[7]);
                    double lon = Double.parseDouble(parts[8]);
                    String typeOfOffense = parts[4];

                    if (typeOfOffense.equalsIgnoreCase("Aggravated Assault")) {
                        double distance = Math.sqrt(Math.pow((x - forbesX), 2) + Math.pow((y - forbesY), 2));
                        if (distance <= radius) {
                            context.write(new Text(""), new Text(String.format("<Placemark><name>%s</name><description>%s, %s</description><Point><coordinates>%f,%f,0</coordinates></Point></Placemark>", typeOfOffense, parts[3], parts[5], lon, lat)));
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignore parsing errors
                }
            }
        }
    }

    public static class CrimeReducer extends Reducer<Text, Text, Text, NullWritable> {
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            StringBuilder kml = new StringBuilder();
            kml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            kml.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");
            kml.append("<Document>\n");

            for (Text value : values) {
                kml.append(value.toString()).append("\n");
            }

            kml.append("</Document>\n");
            kml.append("</kml>");

            context.write(new Text(kml.toString()), NullWritable.get());
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Oakland Crime Stats KML");
        job.setJarByClass(OaklandCrimeStatsKML.class);
        job.setMapperClass(CrimeMapper.class);
        job.setReducerClass(CrimeReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
