package com.hzgc.ftpserver.kafka.consumer.picture2;

import com.hzgc.ftpserver.kafka.consumer.Worker;
import org.apache.ftpserver.util.IoUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.io.FileOutputStream;

public class PicWorker extends Worker {
    public PicWorker(ConsumerRecord<String, byte[]> record) {
        super(record);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            FileOutputStream fis = null;
            System.out.printf(Thread.currentThread().getName() + "offset = %d, key = %s, value = %s\n", consumerRecord.offset(), consumerRecord.key(), consumerRecord.value());
            try {
                fis = new FileOutputStream(new File("E:\\run.jpg"));
                fis.write(consumerRecord.value());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IoUtils.close(fis);
            }
        }
    }
}
