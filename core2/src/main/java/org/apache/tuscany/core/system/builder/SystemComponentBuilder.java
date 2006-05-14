package org.apache.tuscany.core.system.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.core.injection.ContextInjector;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.LazyIntraCompositeResolver;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.model.PojoComponentType;
import org.apache.tuscany.core.system.context.SystemAtomicContextImpl;
import org.apache.tuscany.core.system.model.SystemImplementation;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;
import org.apache.tuscany.model.Component;
import org.apache.tuscany.model.ReferenceTarget;
import org.apache.tuscany.model.Service;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;

/**
 * @version $$Rev$$ $$Date$$
 */
//@SuppressWarnings("unchecked")
public class SystemComponentBuilder implements ComponentBuilder<SystemImplementation> {

    public Context build(CompositeContext parent, Component<SystemImplementation> component) throws BuilderConfigException {
        PojoComponentType componentType = component.getImplementation().getComponentType();
        List<Class<?>> serviceInterfaces = new ArrayList<Class<?>>();
        for (Service service : componentType.getServices().values()) {
            serviceInterfaces.add(service.getServiceContract().getInteface());
        }
        Constructor<?> constr;
        try {
            constr = JavaIntrospectionHelper.getDefaultConstructor(
                    component.getImplementation().getImplementationClass());
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
        for (ReferenceTarget target : component.getReferenceTargets().values()) {
            LazyIntraCompositeResolver resolver = new LazyIntraCompositeResolver(parent, new QualifiedName(target.getTarget().getPath()));
            Member member = componentType.getReferenceMember(target.getReferenceName());
            if (member == null) {
                BuilderConfigException e = new BuilderConfigException("Reference not found");
                e.setIdentifier(target.getReferenceName());
                e.addContextName(component.getName());
                e.addContextName(parent.getName());
                throw e;
            } else if (member instanceof Field) {
                injectors.add(new FieldInjector((Field) member, resolver));
            } else if (member instanceof Method) {
                injectors.add(new MethodInjector((Method) member, resolver));
            }
        }
        return new SystemAtomicContextImpl(component.getName(), serviceInterfaces, factory,
                componentType.isEagerInit(), componentType.getInitInvoker(), componentType.getDestroyInvoker(), injectors);
    }
}
