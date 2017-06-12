package com.hzgc.ftpserver.kafka.consumer.picture2;

import com.hzgc.ftpserver.kafka.consumer.WorkerThread;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class PicWorkerThread extends WorkerThread {
    private final Logger LOG = Logger.getLogger(PicWorkerThread.class);

    Table picTable = null;
    public PicWorkerThread(ConsumerRecord<String, byte[]> record, Connection conn, String tableName,
                           String columnFamily, String column, ConcurrentHashMap<String, Boolean> commit) {
        super(record, conn, tableName, columnFamily, column, commit);
        LOG.info("Create [" + Thread.currentThread().getName() + "] of PicWorkerThreads success");
    }

    @Override
    public void run() {
        this.send();
//        FileOutputStream fis = null;
//        System.out.printf(Thread.currentThread().getName() + "offset = %d, key = %s, value = %s\n, patition = %s\n",
//                consumerRecord.offset(), consumerRecord.key(), consumerRecord.value(), consumerRecord.partition());
//        try {
//            fis = new FileOutputStream(new File("E:\\run.jpg"));
//            fis.write(consumerRecord.value());
//        } catch (Exception e) {
//            isCommit.replace("isCommit", false);
//            e.printStackTrace();
//        } finally {
//            IoUtils.close(fis);
//        }
    }

    @Override
    public void send() {
        try {
            picTable = hbaseConn.getTable(TableName.valueOf(tableName));
            Put put = new Put(Bytes.toBytes(rowKey));
            put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column),consumerRecord.value());
            picTable.put(put);
            System.out.printf(Thread.currentThread().getName() + "offset = %d, key = %s, value = %s\n, patition = %s\n",
                consumerRecord.offset(), consumerRecord.key(), consumerRecord.value(), consumerRecord.partition());
        } catch (Exception e) {
            isCommit.replace("isCommit", false);
            e.printStackTrace();
        } finally {
            try {
                picTable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
