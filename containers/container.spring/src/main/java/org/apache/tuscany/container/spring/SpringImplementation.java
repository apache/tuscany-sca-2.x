package org.apache.tuscany.container.spring;

import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SpringImplementation extends Implementation<CompositeComponentType> {

    private String location;
    private ConfigurableApplicationContext applicationContext;

    public SpringImplementation() {
    }

    public SpringImplementation(CompositeComponentType componentType) {
        super(componentType);
    }

    /**
     * Returns the path of the Spring application context configuration
     */
    public String getContextLocation() {
        return location;
    }

    /**
     * Sets the path of the Spring application context configuration
     */
    public void setContextLocation(String location) {
        this.location = location;
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
