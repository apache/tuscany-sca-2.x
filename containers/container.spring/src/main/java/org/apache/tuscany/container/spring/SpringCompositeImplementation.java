package org.apache.tuscany.container.spring;

import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Implementation;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SpringCompositeImplementation extends Implementation<CompositeComponentType> {
    private String contextPath;

    public SpringCompositeImplementation() {
    }

    public SpringCompositeImplementation(CompositeComponentType componentType) {
        super(componentType);
    }

    /**
     * Returns the path of the Spring application context configuration
     */
    public String getContextPath() {
        return contextPath;
    }

    /**
     * Sets the path of the Spring application context configuration
     */
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
}
