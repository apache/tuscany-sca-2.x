package org.apache.tuscany.container.groovy;

import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * Model object representing a Groovy component type
 *
 * @version $$Rev$$ $$Date$$
 */
public class GroovyComponentType extends ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> {
    private Scope lifecycleScope;

    public Scope getLifecycleScope() {
        return lifecycleScope;
    }

    public void setLifecycleScope(Scope lifecycleScope) {
        this.lifecycleScope = lifecycleScope;
    }

}
