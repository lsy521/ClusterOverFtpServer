package com.hzgc.ftpserver.common;

import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class ClusterOverFtpServer {
    private static Logger log = Logger.getLogger(ClusterOverFtpServer.class);

    private static int listenerPort = 0;
//    private static int sslPort = 0;
    private static String passivePorts = null;
    private static String implicitSsl;
    private static String hdfsUri = null;
    private static String sslPassword = null;

    private static DataConnectionConfigurationFactory dataConnConf;

    private static File loadResource(String resourceName) {
        URL resource = ClusterOverFtpServer.class.getResource(resourceName);
        if (resource == null) {
            log.error(" Please check to see if there are any " + resourceName);
            System.exit(1);
        }
        return new File(resource.getFile());
    }

    private static void loadConfig() throws IOException {
        Properties props = new Properties();
        dataConnConf = new DataConnectionConfigurationFactory();
        props.load(new FileInputStream(loadResource("/conf/cluster-over-ftp.properties")));

        try {
            listenerPort = Integer.parseInt(props.getProperty("listener-port"));
            checkPort(listenerPort, "listener port");
            log.info(" The listener port:" + listenerPort + " for ftpserver is already set ");
        } catch (Exception e) {
            log.error(" The port for listener is not set, Check that the \"listener-port\" is set ");
            System.exit(1);
        }

        try {
            implicitSsl = props.getProperty("implicitSsl");
            if ("true".equals(implicitSsl)) {
                dataConnConf.setImplicitSsl(true);
                log.info(" The implicitSsl = true is set, SSL server will be started ");
            } else if ("false".equals(implicitSsl)){
                log.info(" The implicitSsl use default config, the Ssl Server is closed ");
            } else {
                throw new Exception("The value settings for \"implicitSsl\" is illegal, check it");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (listenerPort != 0) {
            passivePorts = props.getProperty("data-ports");
            if (passivePorts == null) {
                log.info(" The data ports is not set, use any available port ");
            } else {
                dataConnConf.setPassivePorts(passivePorts);
                log.info(" The data ports is set:" + passivePorts);
            }
        }

//        if (sslPort != 0) {
//            sslPassivePorts = props.getProperty("ssl-data-ports");
//            if (sslPassivePorts == null) {
//                log.fatal("ssl-data-ports is not set");
//                System.exit(1);
//            }
//        }

        if (implicitSsl != null && "true".equals(implicitSsl)) {
            sslPassword = props.getProperty("password");
            if (sslPassword == null) {
                log.error(" Please set the ssl password, this is important ");
                System.exit(1);
            }
        }

        hdfsUri = props.getProperty("hdfs-url");
        if (hdfsUri == null) {
            log.error(" The hdfs url is not set, this is important ");
            System.exit(1);
        }

        String superuser = props.getProperty("superuser");
        if (superuser == null) {
            log.error(" The superuser is not set, please check it ");
            System.exit(1);
        }
    }

    private static void startFtpServer() throws Exception {
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory listenerFactory = new ListenerFactory();
        //set the port of the listener
        listenerFactory.setPort(listenerPort);
//        listenerFactory.setServerAddress("192.168.2.130");

        if ("true".equals(implicitSsl)) {
            //URL jksResource = ClusterOverFtpServer.class.getResource("/conf/ftpserver.jks");
            //URL userProperResource = ClusterOverFtpServer.class.getResource("/conf/users.properties");

            SslConfigurationFactory sslFactory = new SslConfigurationFactory();
            sslFactory.setKeystoreFile(loadResource("/conf/ftpserver.jks"));
            sslFactory.setKeystorePassword(sslPassword);
            // set the SSL configuration for the listener
            listenerFactory.setSslConfiguration(sslFactory.createSslConfiguration());
            listenerFactory.setImplicitSsl(true);
        }
        // replace the default listener
        serverFactory.addListener("default", listenerFactory.createListener());
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        userManagerFactory.setFile(loadResource("/conf/users.properties"));
        serverFactory.setUserManager(userManagerFactory.createUserManager());
//        serverFactory.setFileSystem();
        //start the server
        FtpServer server = serverFactory.createServer();
        server.start();

    }

    public static void checkPort(int checkPort, String server) throws Exception {
        if (checkPort < 1024) {
            log.error(" The port settings for " + server +" is illegal and must be greater than 1024 ");
            System.exit(1);
        }
    }

    public static void main(String args[]) throws Exception {
        loadConfig();
        startFtpServer();
    }
}
