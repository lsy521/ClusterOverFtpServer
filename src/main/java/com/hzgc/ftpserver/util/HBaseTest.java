package com.hzgc.ftpserver.util;

import com.hzgc.ftpserver.kafka.hbase.HBaseConnectionFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

import java.io.IOException;
import java.util.Iterator;

public class HBaseTest {
    public static void main(String args[]) throws Exception{
        Connection conn = HBaseConnectionFactory.createConnection();
        try {
            Table table = conn.getTable(TableName.valueOf("t_hzgc".getBytes()));
            Put put = new Put("row1".getBytes());
            Scan scan = new Scan();
            scan.setStopRow("2".getBytes());
//            put.addColumn("BigImage".getBytes(), "0".getBytes(), "test".getBytes());
//            table.put(put);
            ResultScanner pp = table.getScanner(scan);
            int i = 0;
            for (Result result : pp) {
                i++;
            }
            System.out.println(i);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
