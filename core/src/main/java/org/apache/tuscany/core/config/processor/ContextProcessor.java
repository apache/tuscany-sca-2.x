package org.apache.tuscany.core.config.processor;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.ComponentInfo;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.JavaExtensibilityHelper;
import org.apache.tuscany.core.assembly.JavaExtensibilityElement;
import org.osoa.sca.annotations.Context;

/**
 * Processes the {@link org.osoa.sca.annotations.Context} annotation
 *
 *  @version $$Rev$$ $$Date$$
 */
public class ContextProcessor extends ImplementationProcessorSupport {

    public ContextProcessor(AssemblyFactory factory) {
        super(factory);
    }

    public void visitMethod(Method method, ComponentInfo type) throws ConfigurationLoadException {
        Context context = method.getAnnotation(Context.class);
        if (context == null) {
            return;
        }
        JavaExtensibilityElement element = JavaExtensibilityHelper.getExtensibilityElement(type);
        element.setContext(method);
    }

    public void visitField(Field field, ComponentInfo type) throws ConfigurationLoadException {
        Context context = field.getAnnotation(Context.class);
        if (context == null) {
            return;
        }
        JavaExtensibilityElement element = JavaExtensibilityHelper.getExtensibilityElement(type);
        element.setContext(field);
    }

}
