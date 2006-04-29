package org.apache.tuscany.core.config.impl;

import java.util.List;

import org.apache.tuscany.core.config.ComponentTypeIntrospector;
import org.apache.tuscany.core.extension.config.ImplementationProcessor;
import org.apache.tuscany.core.config.processor.ProcessorUtils;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.AssemblyFactory;

/**
 * @version $$Rev$$ $$Date$$
 */
public class IntrospectorFactory {

    private static final AssemblyFactory factory = new SystemAssemblyFactoryImpl();

    public static ComponentTypeIntrospector createIntrospector() {
        ComponentTypeIntrospector introspector = new Java5ComponentTypeIntrospector(factory);
        List<ImplementationProcessor> processors = ProcessorUtils.createCoreProcessors(factory);
        for (ImplementationProcessor processor : processors) {
            introspector.registerProcessor(processor);
        }
        return introspector;
    }
}
