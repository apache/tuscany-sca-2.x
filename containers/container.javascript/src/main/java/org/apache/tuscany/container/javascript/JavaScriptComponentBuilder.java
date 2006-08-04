package org.apache.tuscany.container.javascript;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.container.javascript.rhino.RhinoScript;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * Extension point for creating {@link JavaScriptComponent}s from an assembly configuration
 */
public class JavaScriptComponentBuilder extends ComponentBuilderExtension<JavaScriptImplementation> {

    protected Class<JavaScriptImplementation> getImplementationType() {
        return JavaScriptImplementation.class;
    }

    @SuppressWarnings("unchecked")
    public Component<?> build(CompositeComponent<?> parent, ComponentDefinition<JavaScriptImplementation> componentDefinition,
            DeploymentContext deploymentContext) throws BuilderConfigException {

        String name = componentDefinition.getName();
        JavaScriptImplementation implementation = componentDefinition.getImplementation();
        ComponentType componentType = implementation.getComponentType();

        // get list of services provided by this component
        Collection<ServiceDefinition> collection = componentType.getServices().values();
        List<Class<?>> services = new ArrayList<Class<?>>(collection.size());
        for (ServiceDefinition serviceDefinition : collection) {
            services.add(serviceDefinition.getServiceContract().getInterfaceClass());
        }

        RhinoScript rhinoScript = implementation.getRhinoScript();

        // TODO properties
        Map<String, Object> properties = new HashMap<String, Object>();

        // TODO scopes
        // ScopeContainer scopeContainer = scopeRegistry.getScopeContainer(componentType.getLifecycleScope());
        ScopeContainer scopeContainer = deploymentContext.getModuleScope();

        return new JavaScriptComponent(name, rhinoScript, services, properties, parent, scopeContainer, wireService, workContext);
    }

}
