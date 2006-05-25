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
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.Component;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.Service;

/**
 * @version $$Rev$$ $$Date$$
 */
public class JavaComponentBuilder extends ComponentBuilderExtension<JavaImplementation> {

    public JavaAtomicContext build(CompositeContext parent, Component<JavaImplementation> component, DeploymentContext deploymentContext)
            throws BuilderConfigException {
        PojoComponentType componentType = component.getImplementation().getComponentType();

        List<Class<?>> serviceInterfaces = new ArrayList<Class<?>>();
        for (Service service : componentType.getServices().values()) {
            serviceInterfaces.add(service.getServiceContract().getInterfaceClass());
        }
        Constructor<?> constr;
        try {
            constr = JavaIntrospectionHelper.getDefaultConstructor(component.getImplementation().getImplementationClass());
        } catch (NoSuchMethodException e) {
            BuilderConfigException bce = new BuilderConfigException("Error building component", e);
            bce.setIdentifier(component.getName());
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
                    BuilderConfigException e = new BuilderConfigException("Context not found for type");
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
        return new JavaAtomicContext(component.getName(),
                parent,
                scopeContext,
                serviceInterfaces,
                factory,
                scope,
                componentType.isEagerInit(),
                componentType.getInitInvoker(),
                componentType.getDestroyInvoker(),
                injectors,
                componentType.getReferenceMembers());
    }


    protected Class<JavaImplementation> getImplementationType() {
        return JavaImplementation.class;
    }
}
