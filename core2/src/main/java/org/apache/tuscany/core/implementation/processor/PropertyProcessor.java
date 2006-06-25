package org.apache.tuscany.core.implementation.processor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.osoa.sca.annotations.Property;

import org.apache.tuscany.spi.deployer.DeploymentContext;

import org.apache.tuscany.core.implementation.ImplementationProcessorSupport;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ProcessingException;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.toPropertyName;

/**
 * Processes an {@link @Property} annotation, updating the component type with corresponding {@link JavaMappedProperty}
 *
 * @version $Rev$ $Date$
 */
public class PropertyProcessor extends ImplementationProcessorSupport {

    public void visitMethod(Method method,
                            PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                            DeploymentContext context)
        throws ProcessingException {
        Property annotation = method.getAnnotation(Property.class);
        if (annotation == null) {
            return;
        }
        if (method.getParameterTypes().length != 1) {
            IllegalPropertyException e = new IllegalPropertyException("Setter must have one parameter");
            e.setIdentifier(method.getName());
            throw e;
        }
        String name = annotation.name();
        if (name.length() == 0) {
            if (method.getName().startsWith("set")) {
                name = toPropertyName(method.getName());
            } else {
                name = method.getName();
            }
        }
        if (type.getProperties().get(name) != null) {
            throw new DuplicatePropertyException(name);
        }
        JavaMappedProperty property = new JavaMappedProperty();
        property.setMember(method);
        property.setRequired(annotation.required());
        property.setJavaType(method.getParameterTypes()[0]);
        type.getProperties().put(name, property);
    }

    public void visitField(Field field,
                           PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                           DeploymentContext context) throws ProcessingException {
        Property annotation = field.getAnnotation(Property.class);
        if (annotation == null) {
            return;
        }
        String name = annotation.name();
        if (name.length() == 0) {
            name = field.getName();
        }
        if (type.getProperties().get(name) != null) {
            throw new DuplicatePropertyException(name);
        }
        JavaMappedProperty property = new JavaMappedProperty();
        property.setMember(field);
        property.setRequired(annotation.required());
        property.setJavaType(field.getType());
        type.getProperties().put(name, property);
    }

}
