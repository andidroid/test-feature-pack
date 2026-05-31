package config;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.modcluster.container.Engine;
import org.jboss.modcluster.load.metric.impl.AbstractLoadMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigValueLoadMetric extends AbstractLoadMetric {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigValueLoadMetric.class);

    @ConfigProperty(name = "config.load", defaultValue = "0d")
    private double load = 0d;

    public ConfigValueLoadMetric() {
        LOGGER.info("create ConfigValueLoadMetric: {}", load);
    }
    
    @Override
    public double getLoad(Engine engine) {
        LOGGER.info("ConfigValueLoadMetric.getLoad: {}", load);
        return load;
    }

}
