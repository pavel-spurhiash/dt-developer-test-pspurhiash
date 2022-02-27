package com.example.dtdevelopertestpspurhiash;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {

    private static final String CONFIG_PROPERTY_FILE_LOCATION = "configuration.properties";

    private Properties properties;

    public PropertyUtil() {
        this.properties = new Properties();
        try {
            InputStream propertiesStream = getClass().getResourceAsStream(CONFIG_PROPERTY_FILE_LOCATION);
            if (propertiesStream != null) {
                properties.load(propertiesStream);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Config property file is not found.");
        }
    }

    public String getProperty(String name) {
        return properties.getProperty(name);
    }

}