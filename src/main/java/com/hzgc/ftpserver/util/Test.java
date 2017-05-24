package com.hzgc.ftpserver.util;

import java.io.*;
import java.net.URL;


public class Test {
    static URL url = Utils.class.getResource("/");
    public static void main(String args[]) throws Exception {
        File file = new File("/Users/zhaozhe/Desktop/2017/05/23/17130NCY0HZ0004-0/15/58/2017_05_23_15_58_37_5696_0.json");
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while((len = fis.read(buffer)) > -1) {
            System.out.println("====" + len + "====");
            baos.write(buffer, 0, len);
        }
        baos.flush();
        baos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

        Utils.analysisJsonFile("[" + Utils.loadJsonFile(bais) + "]");
    }
}
