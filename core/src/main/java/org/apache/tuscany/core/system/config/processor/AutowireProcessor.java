package org.apache.tuscany.core.system.config.processor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.InvalidSetterException;
import org.apache.tuscany.core.config.processor.ImplementationProcessorSupport;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.system.config.extensibility.AutowireExtensibilityElement;
import org.apache.tuscany.core.context.SystemCompositeContext;
import org.apache.tuscany.core.context.ConfigurationContext;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.model.assembly.ComponentInfo;
import org.apache.tuscany.common.monitor.MonitorFactory;

/**
 * Processes {@link Autowire} annotations
 *
 * @version $$Rev$$ $$Date$$
 */
public class AutowireProcessor extends ImplementationProcessorSupport {

    @Override
    public void visitMethod(Method method, ComponentInfo type) throws ConfigurationLoadException {
        Autowire annotation = method.getAnnotation(Autowire.class);
        if (annotation != null) {
            if (!Modifier.isPublic(method.getModifiers())) {
                InvalidSetterException e = new InvalidSetterException("Autowire setter method is not public");
                e.setIdentifier(method.toString());
                throw e;
            }
            if (method.getParameterTypes().length != 1){
                InvalidSetterException e = new InvalidSetterException("Autowire setter method must have one parameter");
                e.setIdentifier(method.toString());
                throw e;
            }
            checkAutowireType(method.getParameterTypes()[0],method.getDeclaringClass());
            type.getExtensibilityElements().add(new AutowireExtensibilityElement(method));
        }
    }

    @Override
    public void visitField(Field field, ComponentInfo type) throws ConfigurationLoadException {
        checkAutowireType(field.getType(),field.getDeclaringClass());
        int modifiers = field.getModifiers();
        Autowire annotation = field.getAnnotation(Autowire.class);
        if (annotation != null) {
            if (!Modifier.isPublic(modifiers) && !Modifier.isProtected(modifiers)) {
                InvalidSetterException e = new InvalidSetterException("Autowire field is not public or protected");
                e.setIdentifier(field.getName());
                throw e;
            }
            type.getExtensibilityElements().add(new AutowireExtensibilityElement(field));
        }
    }



    private void checkAutowireType(Class paramClass, Class declaringClass) throws BuilderConfigException{
        if (SystemCompositeContext.class.isAssignableFrom(declaringClass)
                && !(paramClass.equals(ConfigurationContext.class)
                        || paramClass.equals(MonitorFactory.class)
                        || paramClass.equals(RuntimeContext.class) || paramClass.equals(
                        AutowireContext.class))) {
            BuilderConfigException e = new BuilderConfigException("Illegal autowire type for system context");
            e.setIdentifier(paramClass.getName());
            throw e;
        }
    }
}
