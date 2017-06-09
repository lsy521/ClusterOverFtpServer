package com.hzgc.ftpserver.kafka.consumer.picture2;

import com.hzgc.ftpserver.kafka.consumer.ConsumerHandlerThread;

import java.util.Properties;

public class PicConsumerHandlerThread extends ConsumerHandlerThread{
    public PicConsumerHandlerThread(Properties propers, Class logClass) {
        super(propers, logClass);
        LOG.info("Create [" + Thread.currentThread().getName() + "] of PicConsumerHandlerThreads success");
    }
}
