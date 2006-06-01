package org.apache.tuscany.container.groovy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.tuscany.spi.model.Component;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.Service;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.deployer.DeploymentContext;

/**
 * Extension point for creating {@link GroovyAtomicContext}s from an assembly configuration
 *
 * @version $$Rev$$ $$Date$$
 */
public class GroovyComponentBuilder extends ComponentBuilderExtension<GroovyImplementation> {

    protected Class<GroovyImplementation> getImplementationType() {
        return GroovyImplementation.class;
    }

    public ComponentContext build(CompositeContext parent, Component<GroovyImplementation> component, DeploymentContext deploymentContext) throws BuilderConfigException {
        List<Class<?>> services = new ArrayList<Class<?>>();
        Collection<Service> collection = component.getImplementation().getComponentType().getServices().values();
        for (Service service : collection) {
            services.add(service.getServiceContract().getInterfaceClass());
        }
        String script = component.getImplementation().getScript();
        String name = component.getName();
        Scope scope = component.getImplementation().getComponentType().getLifecycleScope();
        return new GroovyAtomicContext(name, script, services, scope, null,parent, deploymentContext.getModuleScope(),wireService);
    }

}
