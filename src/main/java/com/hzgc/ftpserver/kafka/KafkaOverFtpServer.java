package com.hzgc.ftpserver.kafka;

import com.hzgc.ftpserver.ClusterOverFtp;
import com.hzgc.ftpserver.local.LocalCmdFactoryFactory;
import com.hzgc.ftpserver.local.LocalFileSystemFactory;
import com.hzgc.ftpserver.local.LocalPropertiesUserManagerFactory;
import com.hzgc.ftpserver.util.Utils;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class KafkaOverFtpServer implements ClusterOverFtp{
    private static Logger log = Logger.getLogger(KafkaOverFtpServer.class);
    private static int listenerPort = 0;
    private static String passivePorts = null;
    private static String jsonLogPath;
    private static DataConnectionConfigurationFactory dataConnConf;

    public void loadConfig() throws Exception {
        Properties props = new Properties();
        dataConnConf = new DataConnectionConfigurationFactory();
        props.load(new FileInputStream(Utils.loadResourceFile("local-over-ftp.properties")));
        log.info("Load configuration for ftp server from ./conf/local-over-ftp.properties");

        try {
            listenerPort = Integer.parseInt(props.getProperty("listener-port"));
            boolean checkPort = Utils.checkPort(listenerPort);
            if (!checkPort) {
                log.error("The port settings for listener port is illegal and must be greater than 1024");
                System.exit(1);
            }
            log.info("The listener port:" + listenerPort + " for ftpserver is already set");
        } catch (Exception e) {
            log.error("The port for listener is not set, Check that the \"listener-port\" is set", e);
            System.exit(1);
        }

        try {
            jsonLogPath = props.getProperty("json-log");
            File jsonLogFile;
            if (null != jsonLogPath) {
                jsonLogFile = new File(jsonLogPath);
                if (jsonLogFile.exists()) {
                    Utils.jsonLogPath = jsonLogFile;
                    log.info(jsonLogFile.getPath() + "is exist, append to it");
                } else {
//                    jsonLogFile.createNewFile();
                    Utils.jsonLogPath = jsonLogFile;
                    log.info(jsonLogFile.getPath() + "is not exist, create it");
                }
            }
        } catch (Exception e) {
            log.error("Get the path for local json path failure", e);
        }

        if (listenerPort != 0) {
            passivePorts = props.getProperty("data-ports");
            if (passivePorts == null) {
                log.info("The data ports is not set, use any available port");
            } else {
                dataConnConf.setPassivePorts(passivePorts);
                log.warn("The data ports is set:" + passivePorts);
            }
        }
    }

    public void startFtpServer() throws Exception {
        FtpServerFactory serverFactory = new FtpServerFactory();
        log.info("Create " + FtpServerFactory.class + " successful");
        ListenerFactory listenerFactory = new ListenerFactory();
        log.info("Create " + ListenerFactory.class + " successful");
        //set the port of the listener
        listenerFactory.setPort(listenerPort);
        log.info("The port for listener is " + listenerPort);
        // replace the default listener
        serverFactory.addListener("default", listenerFactory.createListener());
        log.info("Add listner, name:default, class:" + serverFactory.getListener("default").getClass());
        // set customer user manager
        LocalPropertiesUserManagerFactory userManagerFactory = new LocalPropertiesUserManagerFactory();
        userManagerFactory.setFile(Utils.loadResourceFile("users.properties"));
        serverFactory.setUserManager(userManagerFactory.createUserManager());
        log.info("Set customer user manager factory is successful, " + userManagerFactory.getClass());
        //set customer cmd factory
        LocalCmdFactoryFactory cmdFactoryFactory = new LocalCmdFactoryFactory();
        serverFactory.setCommandFactory(cmdFactoryFactory.createCommandFactory());
        log.info("Set customer command factory is successful, " + cmdFactoryFactory.getClass());
        //set local file system
        LocalFileSystemFactory localFileSystemFactory = new LocalFileSystemFactory();
        serverFactory.setFileSystem(localFileSystemFactory);
        log.info("Set customer file system factory is successful, " + localFileSystemFactory.getClass());
        FtpServer server = serverFactory.createServer();
        server.start();

    }

    public static void main(String args[]) throws Exception {
        KafkaOverFtpServer kafkaOverFtpServer = new KafkaOverFtpServer();
        kafkaOverFtpServer.loadConfig();
        kafkaOverFtpServer.startFtpServer();
    }
}
