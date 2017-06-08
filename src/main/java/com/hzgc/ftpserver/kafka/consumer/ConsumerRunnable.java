package com.hzgc.ftpserver.kafka.consumer;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Properties;

public class ConsumerRunnable implements Runnable{
    private static Logger log = Logger.getLogger(ConsumerRunnable.class);
    protected final KafkaConsumer<String, byte[]> consumer;

    public ConsumerRunnable(Properties propers) {
        this.consumer = new KafkaConsumer<String, byte[]>(propers);
        String topic = propers.getProperty("topic");
        consumer.subscribe(Arrays.asList(StringUtils.split(topic, ",")));
    }

    public void run() {

    }
}
