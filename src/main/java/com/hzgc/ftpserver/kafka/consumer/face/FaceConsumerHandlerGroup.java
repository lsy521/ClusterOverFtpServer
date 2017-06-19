package com.hzgc.ftpserver.kafka.consumer.face;

import com.hzgc.ftpserver.kafka.consumer.ConsumerGroup;
import com.hzgc.ftpserver.kafka.consumer.ConsumerHandlerThread;
import com.hzgc.ftpserver.kafka.consumer.picture2.PicConsumerHandlerThread;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FaceConsumerHandlerGroup implements ConsumerGroup {
    private final Logger LOG = Logger.getLogger(FaceConsumerHandlerGroup.class);
    private List<ConsumerHandlerThread> consumerHandler;
    private Connection hbaseConn;

    public FaceConsumerHandlerGroup(Properties propers, Connection conn) {
        this.hbaseConn = conn;
        consumerHandler = new ArrayList<>();
        int consumerNum = Integer.parseInt(propers.getProperty("consumerNum"));
        LOG.info("The number of consumer thread is " + consumerNum);
        for (int i = 0; i < consumerNum; i++ ) {
            LOG.info("Start create the thread FaceConsumerHandlerThread");
            ConsumerHandlerThread consumerThread = new FaceConsumerHandlerThread(propers, hbaseConn, PicConsumerHandlerThread.class);
            consumerHandler.add(consumerThread);
        }
    }

    @Override
    public void execute() {
        for (ConsumerHandlerThread thread : consumerHandler) {
            LOG.info("Start-up the thread is FaceConsumerHandlerThread");
            new Thread(thread).start();
        }
    }
}
