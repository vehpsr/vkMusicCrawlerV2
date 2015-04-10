package com.gans.vk.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.TreeSet;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.ServletContextAware;

public class CrawlerConfiguration implements ServletContextAware {

    private static final Log LOG = LogFactory.getLog(CrawlerConfiguration.class);
    private static final String CONFIG_HOME_DIR_PROPERTY = "com.gans.vk.config.home";
    private static final String CONFIG_HOME_DIR_DEFAULT = "C://local/config/vkCrawler";

    private Properties _properties;
    private ServletContext _servletContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
        _servletContext = servletContext;
    }

    public synchronized Properties getProperties() {
        if (_properties == null) {
            _properties = initProperties();
        }
        return _properties;
    }

    private Properties initProperties() {
        Properties properties = new Properties();

        properties.putAll(readProperties(getHome()));
        properties.putAll(readProperties(CONFIG_HOME_DIR_DEFAULT)); // override

        // trace
        for (String key : new TreeSet<String>(properties.stringPropertyNames())) {
            LOG.info(key + "=" + properties.getProperty(key));
        }

        // system properties has higher priority
        properties.putAll(System.getProperties());

        return properties;
    }

    private String getHome() {
        String home = System.getProperty(CONFIG_HOME_DIR_PROPERTY);
        if (home == null) {
            if (_servletContext != null) {
                home = _servletContext.getInitParameter(CONFIG_HOME_DIR_PROPERTY);
            }
        }
        if (home == null) {
            LOG.warn(MessageFormat.format("Fail to find configuration home directory. To set config directory use {0} property. Using instead default {1}", CONFIG_HOME_DIR_PROPERTY, CONFIG_HOME_DIR_DEFAULT));
            return CONFIG_HOME_DIR_DEFAULT;
        }
        return home;
    }

    private Properties readProperties(String dirPath) {
        Properties properties = new Properties();
        File homeDir = new File(dirPath);
        if (!homeDir.canRead()) {
            LOG.warn(MessageFormat.format("Configuration directory {0} is not accessible", homeDir));
            return properties;
        }
        File[] configFiles = homeDir.listFiles(CONFIG_FILE_FILTER);
        if (configFiles.length == 0) {
            LOG.warn(MessageFormat.format("Fail to find any configuration files in {0}", homeDir));
        }
        for (File configFile : configFiles) {
            try (FileInputStream stream = new FileInputStream(configFile)) {
                properties.load(stream);
            } catch (IOException e) {
                LOG.error("Fail to read config file", e);
            }
        }
        return properties;
    }

    private static final FilenameFilter CONFIG_FILE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".properties");
        }

    };

}
