package com.hzgc.ftpserver.common;

import org.apache.ftpserver.DataConnectionConfiguration;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
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

    private static DataConnectionConfigurationFactory dataConnConf;

    private static File loadResource(String resourceName) {
        final URL resource = ClusterOverFtpServer.class.getResource(resourceName);
        if (resource == null) {
            log.error("Please check to see if there are any \"./conf/cluster-over-ftp.properties\"");
            System.exit(1);
        }
        return new File(resource.getFile());
    }

    private static void loadConfig() throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(loadResource("/cluster-over-ftp.properties")));

        try {
            port = Integer.parseInt(props.getProperty("port"));
            checkPort(port, "ftpserver port");
            log.info("The port:" + port + " for ftpserver is already set, FtpServer will be started");
        } catch (Exception e) {
            log.info("The port for ftpserver is not set, Check that the FTP port is set");
            System.exit(1);
        }

        try {
            sslPort = Integer.parseInt(props.getProperty("ssl-port"));
            log.warn("The port:" + sslPort +" for SSL is already set. SSL server will be started");
        } catch (Exception e) {
            log.warn("The port for SSL is not set, so SSL server is not applicable");
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

    public static void startFtpServer() throws Exception {
        dataConnConf = new DataConnectionConfigurationFactory();

    }

    public static void checkPort(int checkPort, String server) throws Exception {
        if (checkPort != 0 && checkPort > 0) {
            startFtpServer();
        } else {
            log.error(" The port settings for " + server +" are illegal and must be greater than 1024 ");
        }
    }

    public static void main(String args[]) throws Exception {
        loadConfig();
    }
}
