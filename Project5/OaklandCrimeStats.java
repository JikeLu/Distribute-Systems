package org.myorg;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class OaklandCrimeStats {

    public static class CrimeMapper extends Mapper<Object, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text("AggravatedAssaultsNearForbes");

        // Forbes Avenue coordinates in feet
        private static final double forbesX = 1354326.897;
        private static final double forbesY = 411447.7828;
        // Convert meters to feet (1 meter = 3.28084 feet)
        private static final double radius = 350 * 3.28084;

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] parts = value.toString().split("\t");
            if (parts.length > 6) {
                try {
                    double x = Double.parseDouble(parts[0]);
                    double y = Double.parseDouble(parts[1]);
                    String typeOfOffense = parts[4];

                    if (typeOfOffense.equalsIgnoreCase("Aggravated Assault")) {
                        double distance = Math.sqrt(Math.pow((x - forbesX), 2) + Math.pow((y - forbesY), 2));
                        if (distance <= radius) {
                            context.write(word, one);
                        }
                    }
                } catch (NumberFormatException e) {
                    // Skip lines that do not contain valid double values in coordinate positions
                    return;
                }
            }
        }
    }

    public static class CrimeReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            context.write(key, new IntWritable(sum));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Oakland Crime Stats");
        job.setJarByClass(OaklandCrimeStats.class);
        job.setMapperClass(CrimeMapper.class);
        job.setCombinerClass(CrimeReducer.class);
        job.setReducerClass(CrimeReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
