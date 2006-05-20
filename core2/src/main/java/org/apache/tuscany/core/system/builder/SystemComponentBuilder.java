package org.apache.tuscany.core.system.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.core.injection.ContextInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.model.PojoComponentType;
import org.apache.tuscany.core.system.context.SystemAtomicContext;
import org.apache.tuscany.core.system.context.SystemAtomicContextImpl;
import org.apache.tuscany.core.system.model.SystemImplementation;
import org.apache.tuscany.core.system.wire.SystemSourceWire;
import org.apache.tuscany.core.system.wire.SystemTargetWire;
import org.apache.tuscany.core.system.wire.SystemTargetAutowire;
import org.apache.tuscany.core.system.wire.SystemSourceAutowire;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.model.Component;
import org.apache.tuscany.model.ReferenceTarget;
import org.apache.tuscany.model.Service;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.wire.SourceWire;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SystemComponentBuilder implements ComponentBuilder<SystemImplementation> {

    public ComponentContext build(CompositeContext parent, Component<SystemImplementation> component) throws BuilderConfigException {
        assert(parent instanceof AutowireContext): "Parent must implement "+ AutowireContext.class.getName();
        AutowireContext autowireContext = (AutowireContext)parent;
        PojoComponentType componentType = component.getImplementation().getComponentType();
        List<Class<?>> serviceInterfaces = new ArrayList<Class<?>>();
        for (Service service : componentType.getServices().values()) {
            serviceInterfaces.add(service.getServiceContract().getInterface());
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
        Map<String, Member> members = componentType.getReferenceMembers();
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
        SystemAtomicContext systemContext = new SystemAtomicContextImpl(component.getName(), serviceInterfaces, factory,
                componentType.isEagerInit(), componentType.getInitInvoker(), componentType.getDestroyInvoker(), injectors, members);

        for (Service service : component.getImplementation().getComponentType().getServices().values()) {
            Class interfaze = service.getServiceContract().getInterface();
            SystemTargetWire wire = new SystemTargetWire(service.getName(), interfaze, systemContext);
            systemContext.addTargetWire(wire);
        }
        for (ReferenceTarget target : component.getReferenceTargets().values()) {
            String referenceName = target.getReferenceName();
            Class interfaze = target.getReference().getServiceContract().getInterface();
            Member member = componentType.getReferenceMember(referenceName);
            if (member == null) {
                BuilderConfigException e = new BuilderConfigException("Reference not found");
                e.setIdentifier(target.getReferenceName());
                e.addContextName(component.getName());
                e.addContextName(parent.getName());
                throw e;
            }
            SourceWire<?> wire;
            if (target.getReference().isAutowire()) {
                wire = new SystemSourceAutowire(referenceName,interfaze,autowireContext);
            } else {
                //FIXME support multiplicity!
                assert(target.getTargets().size() == 1): "Multiplicity not yet implemented";
                QualifiedName targetName = new QualifiedName(target.getTargets().get(0).getPath());
                wire = new SystemSourceWire(referenceName, targetName, interfaze);
            }
            systemContext.addSourceWire(wire);
        }
        return systemContext;
    }
}
