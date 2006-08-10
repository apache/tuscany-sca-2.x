package org.apache.tuscany.container.spring;

import org.springframework.context.ConfigurableApplicationContext;

import org.apache.tuscany.spi.model.Binding;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.Property;

import java.net.URL;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SpringImplementation extends Implementation<
        CompositeComponentType<
                BoundServiceDefinition<? extends Binding>,
                BoundReferenceDefinition<? extends Binding>,
                ? extends Property>> {

    private String location;
    private URL applicationXml;

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
    public String getLocation() {
        return location;
    }

    /**
     * Sets the path of the Spring application context configuration
     */
    public void setLocation(String location) {
        this.location = location;
    }

    public URL getApplicationXml() {
        return applicationXml;
    }

    public void setApplicationXml(URL applicationXml) {
        this.applicationXml = applicationXml;
    }
}
