package org.apache.tuscany.container.spring;

import org.springframework.context.ConfigurableApplicationContext;

import org.apache.tuscany.spi.model.Binding;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.Property;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SpringImplementation extends Implementation<
        CompositeComponentType<
                BoundServiceDefinition<? extends Binding>,
                BoundReferenceDefinition<? extends Binding>,
                ? extends Property>> {

    private String location;
    private ConfigurableApplicationContext applicationContext;

    public SpringImplementation() {
    }

    public SpringImplementation(CompositeComponentType<
            BoundServiceDefinition<? extends Binding>,
            BoundReferenceDefinition<? extends Binding>,
            ? extends Property> componentType) {
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
