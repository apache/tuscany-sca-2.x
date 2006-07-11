package org.apache.tuscany.core.implementation.processor;

import org.osoa.sca.annotations.Property;

import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;

/**
 * Processes an {@link @Property} annotation, updating the component type with corresponding {@link JavaMappedProperty}
 *
 * @version $Rev$ $Date$
 */
public class PropertyProcessor extends AbstractPropertyProcessor<Property> {

    public PropertyProcessor() {
        super(Property.class);
    }

    protected String getName(Property annotation) {
        return annotation.name();
    }

    protected <T> void initProperty(JavaMappedProperty<T> property,
                                    Property annotation,
                                    CompositeComponent<?> parent,
                                    DeploymentContext context) {
        property.setRequired(annotation.required());
    }
}
