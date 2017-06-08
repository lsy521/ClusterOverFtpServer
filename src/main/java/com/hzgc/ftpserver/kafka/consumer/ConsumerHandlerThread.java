package com.hzgc.ftpserver.kafka.consumer;

import com.google.gson.annotations.Since;
import com.hzgc.ftpserver.kafka.consumer.picture2.PicWorker;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.*;

public class ConsumerHandlerThread implements Runnable{
    private final KafkaConsumer<String, byte[]> consumer;
    private ExecutorService executors;
    private Properties propers;

    public ConsumerHandlerThread(Properties propers) {
        this.propers = propers;
        this.consumer = new KafkaConsumer(propers);
        String topic = propers.getProperty("topic");
        consumer.subscribe(Arrays.asList(StringUtils.split(topic, ",")));
    }

    public void execute(int workerNum) {

    }

    public void run() {
        int workerNum = Integer.parseInt(propers.getProperty("workerNum"));
        long getTimeOut = Long.parseLong(propers.getProperty("getTimeOut"));
        executors = new ThreadPoolExecutor(workerNum, workerNum, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.CallerRunsPolicy());
        final int  minBatchSize = Integer.parseInt(propers.getProperty("minBatchSize"));
        int count = 0;
        LinkedBlockingDeque<ConsumerRecord<String, byte[]>> buffer = new LinkedBlockingDeque<>();
        while (true) {
            ConsumerRecords<String, byte[]> records = consumer.poll(getTimeOut);
            for (final ConsumerRecord<String, byte[]> record : records) {
                executors.submit(new PicWorker(record));
                buffer.add(record);
            }
//            count ++;
//            if (buffer.size() >= minBatchSize) {
//                try {
//                    executors.submit(new PicWorker(buffer.take()));
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                consumer.commitAsync();
//                count = 0;
//            }
        }
    }
    public void shutDown() {
        if (consumer != null) {
            consumer.close();
        }
        if (executors != null) {
            executors.shutdown();
        }
        try {
            if (!executors.awaitTermination(10, TimeUnit.MILLISECONDS)) {
                System.out.println("TimeOut..... Ignore for this case");
            }
        } catch (InterruptedException ignored) {
            System.out.println("Other thread interrupted this shutdown, ignore for this case.");
            Thread.currentThread().interrupt();
        }
    }
}
