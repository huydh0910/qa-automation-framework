package com.automation.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    private static final Logger log = LogManager.getLogger(ConfigManager.class);
    private static ConfigManager instance;
    private final Properties properties = new Properties();

    private ConfigManager() {
        String env = System.getProperty("env", "qa");
        loadFile("config/common.properties");
        loadFile("config/" + env + ".properties");
        log.info("ConfigManager initialised for environment: {}", env);
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void loadFile(String fileName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (is != null) {
                properties.load(is);
            } else {
                log.warn("Config file not found on classpath: {}", fileName);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config: " + fileName, e);
        }
    }

    public String get(String key) {
        String value = System.getProperty(key, properties.getProperty(key));
        if (value == null) {
            throw new IllegalArgumentException("Missing config key: " + key);
        }
        return value.trim();
    }

    public String get(String key, String defaultValue) {
        return System.getProperty(key, properties.getProperty(key, defaultValue)).trim();
    }

    public int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key, "false"));
    }
}
