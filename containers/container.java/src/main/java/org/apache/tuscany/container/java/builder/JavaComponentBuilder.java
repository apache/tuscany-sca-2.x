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
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.osoa.sca.annotations.Scope;

/**
 * @version $$Rev$$ $$Date$$
 */
@Scope("MODULE")
public class JavaComponentBuilder extends ComponentBuilderExtension<JavaImplementation> {

    public JavaAtomicContext build(CompositeContext parent, Component<JavaImplementation> component)
            throws BuilderConfigException {
        PojoComponentType componentType = component.getImplementation().getComponentType();

        List<Class<?>> serviceInterfaces = new ArrayList<Class<?>>();
        for (Service service : componentType.getServices().values()) {
            serviceInterfaces.add(((JavaServiceContract) service.getServiceContract()).getInterfaceClass());
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
        return new JavaAtomicContext(component.getName(), serviceInterfaces, factory, componentType.getLifecycleScope(),
                componentType.isEagerInit(), componentType.getInitInvoker(), componentType.getDestroyInvoker(),
                injectors, componentType.getReferenceMembers());
    }


    protected Class<JavaImplementation> getImplementationType() {
        return JavaImplementation.class;
    }
}
