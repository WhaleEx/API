package com.whaleex.api.client.util;

import java.io.*;
import java.util.Properties;

/**
 * 本地储存的相关utils
 */
public class StoreUtils {
    static Properties  properties = new Properties();
    static {
        try {
            InputStream fis = StoreUtils.class.getClassLoader().getResourceAsStream("keys.properties");
            properties.load(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void storeProperties(String key,String value){
        File file = new File(StoreUtils.class.getClassLoader().getResource("keys.properties").getFile());
        try {
            FileOutputStream outStream = new FileOutputStream(file);
            properties.setProperty(key, value);
            properties.store(outStream,null);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static String getStorePublicKey(){
        return properties.getProperty("publicKey");
    }

    public static void storePublicKye(String publicKey){
        storeProperties("publicKey",publicKey);
    }
    public static String getStorePrivateKey(){
        return properties.getProperty("privateKey");
    }

    public static void storePrivateKye(String privateKey){
        storeProperties("privateKey",privateKey);
    }

    public static String getStoreAPIkey(){
        return properties.getProperty("apiKey");
    }
    public static void storeAPIKey(String apiKey){
        storeProperties("apiKey",apiKey);
    }

}
