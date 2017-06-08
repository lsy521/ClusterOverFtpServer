package com.hzgc.ftpserver.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;

public class Worker implements Runnable {
    private static Logger log = Logger.getLogger(Worker.class);
    protected ConsumerRecord<String, byte[]> consumerRecord;

    public Worker(ConsumerRecord<String, byte[]> record) {
        this.consumerRecord = record;
    }

    public void run() {

    }
}
