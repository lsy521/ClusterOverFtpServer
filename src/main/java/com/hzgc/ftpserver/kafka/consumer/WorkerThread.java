package com.hzgc.ftpserver.kafka.consumer;

import org.apache.hadoop.hbase.client.Connection;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class WorkerThread implements Runnable {
    protected ConsumerRecord<String, byte[]> consumerRecord;
    protected ConcurrentHashMap<String, Boolean> isCommit;
    protected Connection hbaseConn;
    protected String tableName;
    protected String columnFamily;
    protected String column;
    protected String rowKey;

    public WorkerThread(ConsumerRecord<String, byte[]> record, Connection conn, String tableName,
                        String columnFamily, String column, ConcurrentHashMap<String, Boolean> commit) {
        this.consumerRecord = record;
        this.hbaseConn = conn;
        this.isCommit = commit;
        this.tableName = tableName;
        this.columnFamily = columnFamily;
        this.column = column;
    }

    public void run() {

    }

    public void send() {

    }
}
