package com.hzgc.ftpserver.kafka.consumer.picture2;

import com.hzgc.ftpserver.kafka.consumer.WorkerThread;
import org.apache.ftpserver.util.IoUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ConcurrentHashMap;

public class PicWorkerThread extends WorkerThread {
    private final Logger LOG = Logger.getLogger(PicWorkerThread.class);
    public PicWorkerThread(ConsumerRecord<String, byte[]> record, ConcurrentHashMap<String, Boolean> commit) {
        super(record, commit);
        LOG.info("Create [" + Thread.currentThread().getName() + "] of PicWorkerThreads success");
    }

    @Override
    public void run() {
            FileOutputStream fis = null;
            System.out.printf(Thread.currentThread().getName() + "offset = %d, key = %s, value = %s\n, patition = %s\n",
                    consumerRecord.offset(), consumerRecord.key(), consumerRecord.value(), consumerRecord.partition());
            try {
                fis = new FileOutputStream(new File("E:\\run.jpg"));
                fis.write(consumerRecord.value());
                isCommit.replace("isCommit", true);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IoUtils.close(fis);
            }
    }
}
