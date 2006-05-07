package org.apache.tuscany.core.system.config.processor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.InvalidSetterException;
import org.apache.tuscany.core.config.processor.ImplementationProcessorSupport;
import org.apache.tuscany.core.system.annotation.Monitor;
import org.apache.tuscany.core.system.config.extensibility.MonitorExtensibilityElement;
import org.apache.tuscany.model.assembly.ComponentType;

/**
 * Processes {@link org.apache.tuscany.core.system.annotation.Autowire} annotations
 *
 * @version $$Rev$$ $$Date$$
 */
public class MonitorProcessor extends ImplementationProcessorSupport {

    @Override
    public void visitMethod(Method method, ComponentType type) throws ConfigurationLoadException {
        Monitor annotation = method.getAnnotation(Monitor.class);
        if (annotation != null) {
            if (!Modifier.isPublic(method.getModifiers())) {
                InvalidSetterException e = new InvalidSetterException("Monitor setter method is not public");
                e.setIdentifier(method.toString());
                throw e;
            }
            if (method.getParameterTypes().length != 1) {
                InvalidSetterException e = new InvalidSetterException("Monitor setter method must have one parameter");
                e.setIdentifier(method.toString());
                throw e;
            }
            type.getExtensibilityElements().add(new MonitorExtensibilityElement(method));
        }
    }

    @Override
    public void visitField(Field field, ComponentType type) throws ConfigurationLoadException {
        int modifiers = field.getModifiers();
        Monitor annotation = field.getAnnotation(Monitor.class);
        if (annotation != null) {
            if (!Modifier.isPublic(modifiers) && !Modifier.isProtected(modifiers)) {
                InvalidSetterException e = new InvalidSetterException("Monitor field is not public or protected");
                e.setIdentifier(field.getName());
                throw e;
            }
            type.getExtensibilityElements().add(new MonitorExtensibilityElement(field));
        }
    }

}
