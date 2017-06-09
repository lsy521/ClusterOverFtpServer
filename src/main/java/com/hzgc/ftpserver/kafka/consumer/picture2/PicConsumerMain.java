package com.hzgc.ftpserver.kafka.consumer.picture2;

import com.hzgc.ftpserver.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class PicConsumerMain {
    private static File resourceFile;
    private static Properties propers = new Properties();

    public static void main(String args[]) {
        try {
            resourceFile = Utils.loadResourceFile("consumer-picture.properties");
            System.out.println("****************************************************************************");
            propers.list(System.out);
            System.out.println("****************************************************************************");
            if (resourceFile != null) {
                propers.load(new FileInputStream(resourceFile));
            }
            PicConsumerHandlerGroup consumerGroup = new PicConsumerHandlerGroup(propers);
            consumerGroup.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
