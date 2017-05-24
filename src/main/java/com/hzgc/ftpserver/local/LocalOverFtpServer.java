package com.hzgc.ftpserver.local;

import com.hzgc.ftpserver.util.Utiles;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LocalOverFtpServer {
    private static Logger log = Logger.getLogger(LocalOverFtpServer.class);
    private static int listenerPort = 0;
    private static String passivePorts = null;
    private static DataConnectionConfigurationFactory dataConnConf;

    private static void loadConfig() throws IOException {
        Properties props = new Properties();
        dataConnConf = new DataConnectionConfigurationFactory();
        props.load(new FileInputStream(Utiles.loadResourceFile("local-over-ftp.properties")));
        log.info("Load configuration for ftp server from ./conf/local-over-ftp.properties");

        try {
            listenerPort = Integer.parseInt(props.getProperty("listener-port"));
            boolean checkPort = Utiles.checkPort(listenerPort);
            if (!checkPort) {
                log.error("The port settings for listener port is illegal and must be greater than 1024");
                System.exit(1);
            }
            log.info("The listener port:" + listenerPort + " for ftpserver is already set");
        } catch (Exception e) {
            log.error("The port for listener is not set, Check that the \"listener-port\" is set");
            System.exit(1);
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

    private static void startFtpServer() throws Exception {
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
        LocalPropertiesUserManagerFactory userManagerFactory = new LocalPropertiesUserManagerFactory();
        userManagerFactory.setFile(Utiles.loadResourceFile("users.properties"));
        serverFactory.setUserManager(userManagerFactory.createUserManager());

        LocalCmdFactoryFactory cmdFactoryFactory = new LocalCmdFactoryFactory();
        serverFactory.setCommandFactory(cmdFactoryFactory.createCommandFactory());
        FtpServer server = serverFactory.createServer();
        server.start();

    }

    public static void main(String args[]) throws Exception {
        loadConfig();
        startFtpServer();
    }
}
