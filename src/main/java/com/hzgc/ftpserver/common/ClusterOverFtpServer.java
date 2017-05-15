package com.hzgc.ftpserver.common;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class ClusterOverFtpServer {
    private static Logger log = Logger.getLogger(ClusterOverFtpServer.class);

    private static int port = 0;
    private static int sslPort = 0;
    private static String passivePorts = null;
    private static String sslPassivePorts = null;
    private static String hdfsUri = null;

    private static File loadResource(String resourceName) {
        final URL resource = ClusterOverFtpServer.class.getResource(resourceName);
        if (resource == null) {
            log.error("Please check to see if there are any \"./conf/hdfs-over-ftp.properties\"");
            System.exit(1);
        }
        return new File(resource.getFile());
    }

    private static void loadConfig() throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(loadResource("/conf/hdfs-over-ftp.properties")));

        try {
            port = Integer.parseInt(props.getProperty("port"));
            log.info("port is set. ftp server will be started");
        } catch (Exception e) {
            log.info("port is not set. so ftp server will not be started");
        }

        try {
            sslPort = Integer.parseInt(props.getProperty("ssl-port"));
            log.info("ssl-port is set. ssl server will be started");
        } catch (Exception e) {
            log.info("ssl-port is not set. so ssl server will not be started");
        }

        if (port != 0) {
            passivePorts = props.getProperty("data-ports");
            if (passivePorts == null) {
                log.fatal("data-ports is not set");
                System.exit(1);
            }
        }

        if (sslPort != 0) {
            sslPassivePorts = props.getProperty("ssl-data-ports");
            if (sslPassivePorts == null) {
                log.fatal("ssl-data-ports is not set");
                System.exit(1);
            }
        }

        hdfsUri = props.getProperty("hdfs-uri");
        if (hdfsUri == null) {
            log.fatal("hdfs-uri is not set");
            System.exit(1);
        }

        String superuser = props.getProperty("superuser");
        if (superuser == null) {
            log.fatal("superuser is not set");
            System.exit(1);
        }
    }
    public static void main(String args[]) throws Exception {
        loadConfig();
    }
}
