package org.apache.tuscany.container.groovy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Collections;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceDefinition;

import org.codehaus.groovy.control.CompilationFailedException;

/**
 * Extension point for creating {@link GroovyAtomicComponent}s from an assembly configuration
 *
 * @version $$Rev$$ $$Date$$
 */
public class GroovyComponentBuilder extends ComponentBuilderExtension<GroovyImplementation> {

    protected Class<GroovyImplementation> getImplementationType() {
        return GroovyImplementation.class;
    }

    public Component<?> build(CompositeComponent<?> parent,
                              ComponentDefinition<GroovyImplementation> componentDefinition,
                              DeploymentContext deploymentContext)
        throws BuilderConfigException {
        List<Class<?>> services = new ArrayList<Class<?>>();
        GroovyImplementation implementation = componentDefinition.getImplementation();
        Collection<ServiceDefinition> collection = implementation.getComponentType().getServices().values();
        for (ServiceDefinition serviceDefinition : collection) {
            services.add(serviceDefinition.getServiceContract().getInterfaceClass());
        }
        String script = implementation.getScript();
        String name = componentDefinition.getName();
        Scope scope = implementation.getComponentType().getLifecycleScope();

        // get the Groovy classloader for this deployment context
        GroovyClassLoader groovyClassLoader = (GroovyClassLoader) deploymentContext.getExtension("groovy.classloader");
        if (groovyClassLoader == null) {
            groovyClassLoader = new GroovyClassLoader(deploymentContext.getClassLoader());
            deploymentContext.putExtension("groovy.classloader", groovyClassLoader);
        }

        Class<? extends GroovyObject> groovyClass;
        try {
            groovyClass = groovyClassLoader.parseClass(script);
        } catch (CompilationFailedException e) {
            BuilderConfigException bce = new BuilderConfigException(e);
            bce.setIdentifier(name);
            throw bce;
        }

        List<PropertyInjector> injectors = Collections.emptyList();
        // todo set up injectors

        return new GroovyAtomicComponent(name,
                                         groovyClass,
                                         services,
                                         scope,
                                         injectors,
                                         parent,
                                         deploymentContext.getModuleScope(),
                                         wireService);
    }

}
