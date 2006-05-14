package org.apache.tuscany.container.java.builder;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.container.java.context.JavaAtomicContext;
import org.apache.tuscany.container.java.model.JavaImplementation;
import org.apache.tuscany.core.injection.ContextInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.model.PojoComponentType;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;
import org.apache.tuscany.model.Component;
import org.apache.tuscany.model.JavaServiceContract;
import org.apache.tuscany.model.Service;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.osoa.sca.annotations.Scope;

/**
 * @version $$Rev$$ $$Date$$
 */
@Scope("MODULE")
public class JavaComponentBuilder implements ComponentBuilder<JavaImplementation> {

    public Context build(CompositeContext parent, Component<JavaImplementation> component) throws BuilderConfigException {
        String name = component.getName();
        PojoComponentType componentType = component.getImplementation().getComponentType();
        Class<?> clazz = component.getImplementation().getImplementationClass();
        Constructor<?> ctr;
        try {
            ctr = clazz.getConstructor((Class[]) null);
        } catch (NoSuchMethodException e) {
            throw new BuilderConfigException(e);
        }
        List<Injector> injectors = componentType.getInjectors();
        for (Injector injector : injectors) {
            if (injector instanceof ContextInjector) {
                // a context injector is found; iterate and determine if the parent context
                // implements the interface
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
        List<Class<?>> serviceInterfaces = new ArrayList<Class<?>>();
        for (Service service : componentType.getServices().values()) {
            serviceInterfaces.add(((JavaServiceContract) service.getServiceContract()).getInterfaceClass());
        }
        PojoObjectFactory<?> factory = new PojoObjectFactory(ctr, null, componentType.getInjectors());
        return new JavaAtomicContext(name, serviceInterfaces, factory, componentType.isEagerInit(), componentType.getInitInvoker(),
                componentType.getDestroyInvoker(), injectors, componentType.getMembers());
    }

}
