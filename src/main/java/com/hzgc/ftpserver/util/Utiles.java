package com.hzgc.ftpserver.util;

import com.hzgc.ftpserver.local.LocalOverFtpServer;
import org.apache.ftpserver.util.IoUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.Iterator;

public class Utiles {
    private static Logger log = Logger.getLogger(Utiles.class);

    public static boolean checkPort(int checkPort) throws Exception {
        if (checkPort < 1024) {
            return false;
        }
        return true;
    }


    public static File loadResourceFile(String resourceName) {
        if (false) {
            URL resource = LocalOverFtpServer.class.getResource("/conf");
            String confPath = resource.getPath();
            confPath = confPath.substring(5, confPath.lastIndexOf("/lib"));
            confPath = confPath + "/conf/";
            System.out.println(confPath);
            File sourceFile = new File(confPath + resourceName);
            PropertyConfigurator.configure(confPath + "/log4j.properties");
            if (!sourceFile.exists()) {
                log.warn("The local resource file:" + new File(confPath).getAbsolutePath()
                        + "/" + resourceName + " is not found, " +
                        "please check it, System exit.");
                System.exit(1);
            }
            return sourceFile;
        } else {
            URL resource = LocalOverFtpServer.class.
                    getResource("/conf/" + resourceName);
            return new File(resource.getFile());
        }
    }

    public static String loadJsonFile(InputStream fis) {
        BufferedInputStream bis;
        BufferedReader reader = null;
        StringBuffer strBuff = new StringBuffer();
        try {
//            bis = (BufferedInputStream) fis;
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            reader = new BufferedReader(isr);
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                strBuff.append(tempStr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                IoUtils.close(reader);
            }
        }
        return strBuff.toString();
    }

    public static void analysisJsonFile(String jsonContext) {
        JSONArray jsonArray = new JSONArray(jsonContext);
        int jsonSize = jsonArray.length();
        System.out.println("Size:" + jsonSize);
        for (int i = 0; i < jsonSize; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Iterator iterable = jsonObject.keys();
            while (iterable.hasNext()) {
                String key = (String)iterable.next();
                String value = jsonObject.get(key).toString();
                if (value.startsWith("[") && value.endsWith("]")) {
                    analysisJsonFile(value);
                }
                System.out.println(key + "=" +value);
            }
        }
    }
}
