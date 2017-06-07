package com.hzgc.ftpserver.util;

import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;


public class Test {
    private static String str = "2017_04_24_06_02_00_28184_0.jpg";
    public static void main(String ars[]) {
        System.out.println(Utils.pickPicture("2017_04_24_06_02_00_28184_0.jpg"));
    }
}
