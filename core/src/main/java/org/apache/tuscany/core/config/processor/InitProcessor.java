package org.apache.tuscany.core.config.processor;

import java.lang.reflect.Method;

import org.apache.tuscany.core.assembly.JavaExtensibilityElement;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.JavaExtensibilityHelper;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.ComponentInfo;
import org.osoa.sca.annotations.Init;

/**
 * Processes the {@link org.osoa.sca.annotations.Init} annotation
 *
 * @version $$Rev$$ $$Date$$
 */
public class InitProcessor extends ImplementationProcessorSupport {

    public InitProcessor(AssemblyFactory factory) {
        super(factory);
    }

    public InitProcessor() {
    }

    public void visitMethod(Method method, ComponentInfo type) throws ConfigurationLoadException {
        Init init = method.getAnnotation(Init.class);
        if (init == null) {
            return;
        }
        if (method.getParameterTypes().length != 0){
            throw new ConfigurationLoadException("Initialize methods cannot take parameters");
        }
        JavaExtensibilityElement element = JavaExtensibilityHelper.getExtensibilityElement(type);
        element.setEagerInit(init.eager());
        element.setInit(new MethodEventInvoker(method));
    }
}
