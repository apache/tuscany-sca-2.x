package org.apache.tuscany.core.implementation.processor;

import java.lang.reflect.Constructor;

import org.osoa.sca.annotations.Property;

import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ProcessingException;

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

    public void visitConstructor(CompositeComponent<?> parent, Constructor<?> constructor,
                                 PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                                 DeploymentContext context) throws ProcessingException {
        // override since heuristic pojo processor evalautes properties
    }
}
