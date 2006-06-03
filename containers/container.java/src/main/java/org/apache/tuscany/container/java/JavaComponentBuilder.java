package org.apache.tuscany.container.java;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.core.injection.ContextInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.model.PojoComponentType;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContext;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * @version $$Rev$$ $$Date$$
 */
public class JavaComponentBuilder extends ComponentBuilderExtension<JavaImplementation> {

    public Component<?> build(CompositeComponent<?> parent, ComponentDefinition<JavaImplementation> componentDefinition, DeploymentContext deploymentContext)
            throws BuilderConfigException {
        PojoComponentType<?, ?, ?> componentType = componentDefinition.getImplementation().getComponentType();

        List<Class<?>> serviceInterfaces = new ArrayList<Class<?>>();
        for (ServiceDefinition serviceDefinition : componentType.getServices().values()) {
            serviceInterfaces.add(serviceDefinition.getServiceContract().getInterfaceClass());
        }
        Constructor<?> constr;
        try {
            constr = JavaIntrospectionHelper.getDefaultConstructor(componentDefinition.getImplementation().getImplementationClass());
        } catch (NoSuchMethodException e) {
            BuilderConfigException bce = new BuilderConfigException("Error building componentDefinition", e);
            bce.setIdentifier(componentDefinition.getName());
            bce.addContextName(parent.getName());
            throw bce;
        }
        ObjectFactory<?> factory = new PojoObjectFactory(constr);
        List<Injector> injectors = new ArrayList<Injector>();
        injectors.addAll(componentType.getInjectors());
        for (Injector injector : injectors) {
            if (injector instanceof ContextInjector) {
                //iterate and determine if the parent context implements the interface
                Class contextType = JavaIntrospectionHelper.introspectGeneric(injector.getClass(), 0);
                if (contextType.isAssignableFrom(parent.getClass())) {
                    ((ContextInjector) injector).setContext(parent);
                } else {
                    BuilderConfigException e = new BuilderConfigException("SCAObject not found for type");
                    e.setIdentifier(contextType.getName());
                    throw e;
                }
            }
        }

        ScopeContext scopeContext;
        Scope scope = componentType.getLifecycleScope();
        if (Scope.MODULE == scope) {
            scopeContext = deploymentContext.getModuleScope();
        } else {
            scopeContext = scopeRegistry.getScopeContext(scope);
        }
        return new JavaAtomicComponent(componentDefinition.getName(),
                parent,
                scopeContext,
                serviceInterfaces,
                factory,
                scope,
                componentType.isEagerInit(),
                componentType.getInitInvoker(),
                componentType.getDestroyInvoker(),
                injectors,
                componentType.getReferenceMembers(),
                wireService);
    }


    protected Class<JavaImplementation> getImplementationType() {
        return JavaImplementation.class;
    }
}
