package com.hzgc.ftpserver.kafka.consumer.picture2;

import com.hzgc.ftpserver.kafka.consumer.ConsumerHandlerThread;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PicConsumerHandlerGroup {
    private List<ConsumerHandlerThread> consumerHandler;

    public PicConsumerHandlerGroup(Properties propers) {
        consumerHandler = new ArrayList<ConsumerHandlerThread>();
        int consumerNum = Integer.parseInt(propers.getProperty("consumerNum"));
        for (int i = 0; i < consumerNum; i++ ) {
            ConsumerHandlerThread consumerThread = new ConsumerHandlerThread(propers);
            consumerHandler.add(consumerThread);
        }
    }

    public void execute() {
        for (ConsumerHandlerThread thread : consumerHandler) {
            new Thread(thread).start();
        }
    }
}
