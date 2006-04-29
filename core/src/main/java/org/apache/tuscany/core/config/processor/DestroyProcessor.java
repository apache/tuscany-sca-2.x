package org.apache.tuscany.core.config.processor;

import java.lang.reflect.Method;

import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.ComponentInfo;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.JavaExtensibilityHelper;
import org.apache.tuscany.core.assembly.JavaExtensibilityElement;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.osoa.sca.annotations.Destroy;

/**
 * Processes a {@link Destroy}
 * 
 * @version $$Rev$$ $$Date$$
 */
public class DestroyProcessor extends ImplementationProcessorSupport {

    public DestroyProcessor(AssemblyFactory factory) {
        super(factory);
    }

    public void visitMethod(Method method, ComponentInfo type) throws ConfigurationLoadException {
        Destroy destroy = method.getAnnotation(Destroy.class);
        if (destroy == null) {
            return;
        }
        if (method.getParameterTypes().length != 0){
            throw new ConfigurationLoadException("Destroy methods cannot take parameters");
        }
        JavaExtensibilityElement element = JavaExtensibilityHelper.getExtensibilityElement(type);
        element.setDestroy(new MethodEventInvoker(method));
    }
}
