package com.hzgc.ftpserver.kafka.consumer.picture2;

import com.hzgc.ftpserver.kafka.consumer.ConsumerHandlerThread;
import org.apache.hadoop.hbase.client.Connection;

import java.util.Properties;

public class PicConsumerHandlerThread extends ConsumerHandlerThread{
    public PicConsumerHandlerThread(Properties propers, Connection conn, Class logClass) {
        super(propers, conn, logClass);
        LOG.info("Create [" + Thread.currentThread().getName() + "] of PicConsumerHandlerThreads success");
    }
}
