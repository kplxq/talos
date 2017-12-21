package com.kxd.framework.hbase;

import java.util.Properties;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.FactoryBean;

/**
 * Creates HBase Configuration for the given HBase master.
 * @author Vaibhav Puranik
 * @version $Id: HBaseConfigurationFactoryBean.java 9392 2011-07-19 23:19:39Z
 *          ken $
 */
public class HBaseConfigurationFactoryBean implements FactoryBean<Configuration> {

    /**
     * You can set various hbase client properties. hbase.zookeeper.quorum must
     * be set.
     */
    private Properties hbaseProperties;

    @Override
    public Configuration getObject() throws Exception {
        if (hbaseProperties != null) {
            Configuration config = HBaseConfiguration.create();
            Set<String> propertyNames = hbaseProperties.stringPropertyNames();
            for (String propertyName : propertyNames) {
                config.set(propertyName, hbaseProperties.getProperty(propertyName));
            }
            return config;
        } else {
            throw new RuntimeException("hbase properties cannot be null");
        }
    }

    @Override
    public Class getObjectType() {
        return Configuration.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public Properties getHbaseProperties() {
        return hbaseProperties;
    }

    public void setHbaseProperties(Properties hbaseProperties) {
        this.hbaseProperties = hbaseProperties;
    }
}

