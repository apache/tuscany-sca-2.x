package org.apache.tuscany.container.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.util.Map;

import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.PojoConfiguration;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.injection.ContextInjector;
import org.apache.tuscany.spi.injection.Injector;
import org.apache.tuscany.spi.injection.PojoObjectFactory;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.PojoComponentType;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceDefinition;

import org.apache.tuscany.core.util.JavaIntrospectionHelper;

/**
 * @version $$Rev$$ $$Date$$
 */
public class JavaComponentBuilder extends ComponentBuilderExtension<JavaImplementation> {

    @SuppressWarnings("unchecked")
    public Component<?> build(CompositeComponent<?> parent,
                              ComponentDefinition<JavaImplementation> definition,
                              DeploymentContext deployment)
        throws BuilderConfigException {
        PojoComponentType<?, ?, ?> componentType = definition.getImplementation().getComponentType();

        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setParent(parent);
        configuration.setEagerInit(componentType.isEagerInit());
        configuration.setInitInvoker(componentType.getInitInvoker());
        configuration.setDestroyInvoker(componentType.getDestroyInvoker());
        configuration.setWireService(wireService);
        for (Map.Entry<String, Member> entry : componentType.getReferenceMembers().entrySet()) {
            configuration.addMember(entry.getKey(), entry.getValue());
        }

        for (ServiceDefinition serviceDefinition : componentType.getServices().values()) {
            configuration.addServiceInterface(serviceDefinition.getServiceContract().getInterfaceClass());
        }
        Constructor<?> constr;
        try {
            constr = JavaIntrospectionHelper
                .getDefaultConstructor(definition.getImplementation().getImplementationClass());
        } catch (NoSuchMethodException e) {
            BuilderConfigException bce = new BuilderConfigException("Error building definition", e);
            bce.setIdentifier(definition.getName());
            bce.addContextName(parent.getName());
            throw bce;
        }
        configuration.setObjectFactory(new PojoObjectFactory(constr));
        configuration.getInjectors().addAll(componentType.getInjectors());
        for (Injector injector : configuration.getInjectors()) {
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

        Scope scope = componentType.getLifecycleScope();
        if (Scope.MODULE == scope) {
            configuration.setScopeContainer(deployment.getModuleScope());
        } else {
            configuration.setScopeContainer(scopeRegistry.getScopeContainer(scope));
        }
        return new JavaAtomicComponent(definition.getName(), configuration);
    }


    protected Class<JavaImplementation> getImplementationType() {
        return JavaImplementation.class;
    }
}
