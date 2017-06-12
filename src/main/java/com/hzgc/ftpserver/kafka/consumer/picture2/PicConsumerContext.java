package com.hzgc.ftpserver.kafka.consumer.picture2;

import com.hzgc.ftpserver.util.Utils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;

import java.util.Properties;

public class PicConsumerContext {
    private static Logger LOG = Logger.getLogger(PicConsumerContext.class);
    private static  File resourceFile;
    private static Properties propers = new Properties();

    public static void main(String args[]) {
        try {
            resourceFile = Utils.loadResourceFile("consumer-picture.properties");
            System.out.println("****************************************************************************");
            propers.list(System.out);
            System.out.println("****************************************************************************");

            LOG.info("Start create the hbase connection ");
            Configuration hbaseConf = HBaseConfiguration.create();
            Connection hbaseConn = ConnectionFactory.createConnection(hbaseConf);
            if (resourceFile != null) {
                propers.load(new FileInputStream(resourceFile));
            }
            PicConsumerHandlerGroup consumerGroup = new PicConsumerHandlerGroup(propers, hbaseConn);
            consumerGroup.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
