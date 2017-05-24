package com.hzgc.ftpserver.util;

import java.io.*;
import java.net.URL;

/**
 * Created by zhaozhe on 2017/5/22.
 */
public class Test {
    static URL url = Utils.class.getResource("/");
    public static void main(String args[]) throws Exception {
        File file = new File("/Users/zhaozhe/pp");
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader bis = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(bis);
        BufferedReader br2 = br;
        String str;
        String str2;
        while((str = br.readLine()) != null) {
            System.out.println("1");
            System.out.println(str);
        }

        while((str2 = br2.readLine()) != null) {
            System.out.println("2");
            System.out.println(str2);
        }
    }
}
