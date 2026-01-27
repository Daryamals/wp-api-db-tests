package com.simbirsoft.wordpress.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesHelper {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream inputStream = PropertiesHelper.class.getResourceAsStream("/config.properties")) {
            if (inputStream == null) {
                System.err.println("Sorry, unable to find config.properties");
            } else {
                PROPERTIES.load(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not read properties file", e);
        }
    }

    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }
}