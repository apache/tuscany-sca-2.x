package org.apache.tuscany.container.groovy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.context.Component;
import org.apache.tuscany.spi.context.CompositeComponent;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.deployer.DeploymentContext;

/**
 * Extension point for creating {@link GroovyAtomicComponent}s from an assembly configuration
 *
 * @version $$Rev$$ $$Date$$
 */
public class GroovyComponentBuilder extends ComponentBuilderExtension<GroovyImplementation> {

    protected Class<GroovyImplementation> getImplementationType() {
        return GroovyImplementation.class;
    }

    public Component<?> build(CompositeComponent<?> parent, ComponentDefinition<GroovyImplementation> componentDefinition, DeploymentContext deploymentContext) throws BuilderConfigException {
        List<Class<?>> services = new ArrayList<Class<?>>();
        Collection<ServiceDefinition> collection = componentDefinition.getImplementation().getComponentType().getServices().values();
        for (ServiceDefinition serviceDefinition : collection) {
            services.add(serviceDefinition.getServiceContract().getInterfaceClass());
        }
        String script = componentDefinition.getImplementation().getScript();
        String name = componentDefinition.getName();
        Scope scope = componentDefinition.getImplementation().getComponentType().getLifecycleScope();
        return new GroovyAtomicComponent(name, script, services, scope, null,parent, deploymentContext.getModuleScope(),wireService);
    }

}
