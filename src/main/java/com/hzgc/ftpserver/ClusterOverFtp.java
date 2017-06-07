package com.hzgc.ftpserver;

import com.hzgc.ftpserver.util.Utils;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public abstract class ClusterOverFtp {
    protected static Logger log = Logger.getLogger(ClusterOverFtp.class);
    protected static int listenerPort = 0;
    protected static String passivePorts = null;
    protected static String jsonLogPath;
    protected static DataConnectionConfigurationFactory dataConnConf;

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

//        try {
//            jsonLogPath = props.getProperty("json-log");
//            File jsonLogFile;
//            if (null != jsonLogPath) {
//                jsonLogFile = new File(jsonLogPath);
//                if (jsonLogFile.exists()) {
//                    Utils.jsonLogPath = jsonLogFile;
//                    log.info(jsonLogFile.getPath() + "is exist, append to it");
//                } else {
////                    jsonLogFile.createNewFile();
//                    Utils.jsonLogPath = jsonLogFile;
//                    log.info(jsonLogFile.getPath() + "is not exist, create it");
//                }
//            }
//        } catch (Exception e) {
//            log.error("Get the path for local json path failure", e);
//        }

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

    public abstract void startFtpServer();
}
