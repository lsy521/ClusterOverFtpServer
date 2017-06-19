package com.hzgc.ftpserver.util;

import com.hzgc.ftpserver.kafka.hbase.HBaseConnectionFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;

import java.io.IOException;
import java.util.Iterator;

public class HBaseTest {
    public static void main(String args[]) throws Exception{
        Connection conn = HBaseConnectionFactory.createConnection();
        try {
            Table table = conn.getTable(TableName.valueOf("t_hzgc".getBytes()));
            Put put = new Put("row1".getBytes());
            Scan scan = new Scan();
            scan.setStartRow("000004018220170424002125".getBytes());
            scan.setStopRow("999999999999999999999999".getBytes());
//            Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL,new SubstringComparator("0000040182201704240021"));
//            scan.setFilter(filter);
//            put.addColumn("BigImage".getBytes(), "0".getBytes(), "test".getBytes());
//            table.put(put);
            ResultScanner pp = table.getScanner(scan);
            int i = 0;
            for (Result result : pp) {
                i++;
            }
            System.out.println(i);
//            System.out.println("cc".hashCode());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
