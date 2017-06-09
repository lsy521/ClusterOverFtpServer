package com.hzgc.ftpserver.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.ConcurrentHashMap;

public class WorkerThread implements Runnable {
    protected ConsumerRecord<String, byte[]> consumerRecord;
    protected ConcurrentHashMap<String, Boolean> isCommit;

    public WorkerThread(ConsumerRecord<String, byte[]> record, ConcurrentHashMap<String, Boolean> commit) {
        this.consumerRecord = record;
        this.isCommit = commit;
    }

    public void run() {

    }
}
