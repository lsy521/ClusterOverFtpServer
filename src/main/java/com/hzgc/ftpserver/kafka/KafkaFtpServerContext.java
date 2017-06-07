package com.hzgc.ftpserver.kafka;

import org.apache.ftpserver.impl.DefaultFtpServerContext;
import org.apache.log4j.Logger;

public class KafkaFtpServerContext extends DefaultFtpServerContext{
    private static Logger log = Logger.getLogger(KafkaFtpServerContext.class);
    private ProducerOverFtpSingle producerOverFtp = ProducerOverFtpSingle.getInstance();

    public ProducerOverFtpSingle getProducerOverFtp() {
        return producerOverFtp;
    }
    public void setProducerOverFtp(ProducerOverFtpSingle producerOverFtp) {
        this.producerOverFtp = producerOverFtp;
    }

}
