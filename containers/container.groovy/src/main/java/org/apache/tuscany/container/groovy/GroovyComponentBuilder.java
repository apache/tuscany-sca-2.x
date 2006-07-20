package org.apache.tuscany.container.groovy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.codehaus.groovy.control.CompilationFailedException;

import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

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

        String name = componentDefinition.getName();
        GroovyImplementation implementation = componentDefinition.getImplementation();
        GroovyComponentType componentType = implementation.getComponentType();

        // get list of services provided by this component
        Collection<ServiceDefinition> collection = componentType.getServices().values();
        List<Class<?>> services = new ArrayList<Class<?>>(collection.size());
        for (ServiceDefinition serviceDefinition : collection) {
            services.add(serviceDefinition.getServiceContract().getInterfaceClass());
        }

        // get the scope container for this component's scope
        ScopeContainer scopeContainer = scopeRegistry.getScopeContainer(componentType.getLifecycleScope());

        // get the Groovy classloader for this deployment context
        GroovyClassLoader groovyClassLoader = (GroovyClassLoader) deploymentContext.getExtension("groovy.classloader");
        if (groovyClassLoader == null) {
            groovyClassLoader = new GroovyClassLoader(deploymentContext.getClassLoader());
            deploymentContext.putExtension("groovy.classloader", groovyClassLoader);
        }

        // create the implementation class for the script
        Class<? extends GroovyObject> groovyClass;
        try {
            String script = implementation.getScript();
            groovyClass = groovyClassLoader.parseClass(script);
        } catch (CompilationFailedException e) {
            BuilderConfigException bce = new BuilderConfigException(e);
            bce.setIdentifier(name);
            throw bce;
        }

        // todo set up injectors
        List<PropertyInjector> injectors = Collections.emptyList();

        // create the actual component
        return new GroovyAtomicComponent(name, groovyClass, services, injectors, parent, scopeContainer, wireService);
    }

}
