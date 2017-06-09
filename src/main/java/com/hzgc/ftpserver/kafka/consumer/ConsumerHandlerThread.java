package com.hzgc.ftpserver.kafka.consumer;

import com.hzgc.ftpserver.kafka.consumer.picture2.PicWorkerThread;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.*;

public class ConsumerHandlerThread implements Runnable{
    protected final Logger LOG;
    private final KafkaConsumer<String, byte[]> consumer;
    private ExecutorService executors;
    private Properties propers;
    private final ConcurrentHashMap<String, Boolean> isCommit;

    public ConsumerHandlerThread(Properties propers, Class logClass) {
        this.propers = propers;
        this.consumer = new KafkaConsumer<>(propers);
        this.LOG = Logger.getLogger(logClass);
        String topic = propers.getProperty("topic");
        consumer.subscribe(Arrays.asList(StringUtils.split(topic, ",")));
        isCommit = new ConcurrentHashMap<>();
        isCommit.put("isCommit", false);
    }

    public void run() {
        int workerNum = Integer.parseInt(propers.getProperty("workerNum"));
        long getTimeOut = Long.parseLong(propers.getProperty("getTimeOut"));
        executors = new ThreadPoolExecutor(workerNum, workerNum, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.CallerRunsPolicy());
        final int  minBatchSize = Integer.parseInt(propers.getProperty("minBatchSize"));
        final int commitFailure = Integer.parseInt(propers.getProperty("commitFailure"));
        int consumerTimes = 1;
        int failWorker = 0;
        try {
            while (true) {
                ConsumerRecords<String, byte[]> records = consumer.poll(getTimeOut);
                for (final ConsumerRecord<String, byte[]> record : records) {
                    executors.submit(new PicWorkerThread(record, isCommit));
                    consumerTimes ++;
                    if (!isCommit.get("isCommit")) {
                        failWorker ++;
                    }
                }
                if (consumerTimes >= minBatchSize && failWorker <= commitFailure) {
                    consumer.commitSync();
                    LOG.info(Thread.currentThread().getName() + "Commit offset success");
                    consumerTimes = 0;
                    failWorker = 0;
                }
            }
        } finally {
            consumer.commitSync();
        }
    }
//    public void shutDown() {
//        if (consumer != null) {
//            consumer.close();
//        }
//        if (executors != null) {
//            executors.shutdown();
//        }
//        try {
//            if (!executors.awaitTermination(10, TimeUnit.MILLISECONDS)) {
//                System.out.println("TimeOut..... Ignore for this case");
//            }
//        } catch (InterruptedException ignored) {
//            System.out.println("Other thread interrupted this shutdown, ignore for this case.");
//            Thread.currentThread().interrupt();
//        }
//    }
}
