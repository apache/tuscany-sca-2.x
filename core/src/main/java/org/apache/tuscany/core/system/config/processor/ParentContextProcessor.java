package org.apache.tuscany.core.system.config.processor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.InvalidSetterException;
import org.apache.tuscany.core.config.processor.ImplementationProcessorSupport;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.system.annotation.ParentContext;
import org.apache.tuscany.core.system.config.extensibility.ParentContextExtensibilityElement;
import org.apache.tuscany.model.assembly.ComponentType;

/**
 * Processes {@link org.apache.tuscany.core.system.annotation.Autowire} annotations
 *
 * @version $$Rev$$ $$Date$$
 */
public class ParentContextProcessor extends ImplementationProcessorSupport {

    @Override
    public void visitMethod(Method method, ComponentType type) throws ConfigurationLoadException {
        ParentContext annotation = method.getAnnotation(ParentContext.class);
        if (annotation != null) {
            if (!Modifier.isPublic(method.getModifiers())) {
                InvalidSetterException e = new InvalidSetterException("ParentContext setter method is not public");
                e.setIdentifier(method.toString());
                throw e;
            }
            if (method.getParameterTypes().length != 1
                    && !CompositeContext.class.isAssignableFrom(method.getParameterTypes()[0])) {
                InvalidSetterException e = new InvalidSetterException("ParentContext setter method must have one parameter of type " + CompositeContext.class.getName());
                e.setIdentifier(method.toString());
                throw e;
            }
            type.getExtensibilityElements().add(new ParentContextExtensibilityElement(method));
        }
    }

    @Override
    public void visitField(Field field, ComponentType type) throws ConfigurationLoadException {
        int modifiers = field.getModifiers();
        ParentContext annotation = field.getAnnotation(ParentContext.class);
        if (annotation != null) {
            if (!Modifier.isPublic(modifiers) && !Modifier.isProtected(modifiers)) {
                InvalidSetterException e = new InvalidSetterException("ParentContext field is not public or protected");
                e.setIdentifier(field.getName());
                throw e;
            }
            type.getExtensibilityElements().add(new ParentContextExtensibilityElement(field));
        }
    }


}
