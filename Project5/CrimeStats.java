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

public class CrimeStats {

    public static class CrimeMapper extends Mapper<Object, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text("totalCrimes");

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            // Splitting the line on tab delimiter
            String[] parts = value.toString().split("\t");
            if (parts.length > 4) {
                String typeOfOffense = parts[4];
                // Checking if the type of offense is either Aggravated Assault or Robbery
                if (typeOfOffense.equalsIgnoreCase("Aggravated Assault") || typeOfOffense.equalsIgnoreCase("Robbery")) {
                    context.write(word, one);
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
        Job job = Job.getInstance(conf, "crime count");
        job.setJarByClass(CrimeStats.class);
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