package com.hzgc.ftpserver.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by zhaozhe on 2017/5/22.
 */
public class Test {
    static URL url = Utiles.class.getResource("/");
    public static void main(String args[]) throws Exception {
        File file = new File("/Users/zhaozhe/test.json");
        InputStream fis = new FileInputStream(file);
        Utiles.analysisJsonFile("[" + Utiles.loadJsonFile(fis) + "]");
        fis.close();
    }
}
