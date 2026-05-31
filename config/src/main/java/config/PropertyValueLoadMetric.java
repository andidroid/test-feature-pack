package config;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.modcluster.container.Engine;
import org.jboss.modcluster.load.metric.impl.AbstractLoadMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyValueLoadMetric extends AbstractLoadMetric {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyValueLoadMetric.class);

    private double load = 0d;

    public PropertyValueLoadMetric() {
        LOGGER.info("create PropertyValueLoadMetric: {}", load);
    }
    
    @Override
    public double getLoad(Engine engine) {
        LOGGER.info("PropertyValueLoadMetric.getLoad: {}", load);
        return load;
    }

    public void setLoad(String load) {
        LOGGER.info("PropertyValueLoadMetric.setLoad: {}", load);
        this.load = Double.parseDouble(load);
    }
}
