package org.apache.tuscany.core.system.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.component.AutowireComponent;
import org.apache.tuscany.core.injection.ContextInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.model.PojoComponentType;
import org.apache.tuscany.core.system.context.SystemAtomicComponent;
import org.apache.tuscany.core.system.context.SystemAtomicComponentImpl;
import org.apache.tuscany.core.system.model.SystemImplementation;
import org.apache.tuscany.core.system.wire.SystemInboundWireImpl;
import org.apache.tuscany.core.system.wire.SystemOutboundAutowire;
import org.apache.tuscany.core.system.wire.SystemOutboundWireImpl;
import org.apache.tuscany.core.system.wire.SystemInboundWire;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.context.Component;
import org.apache.tuscany.spi.context.CompositeComponent;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SystemComponentBuilder implements ComponentBuilder<SystemImplementation> {

    public Component<?> build(CompositeComponent<?> parent, ComponentDefinition<SystemImplementation> componentDefinition, DeploymentContext deploymentContext) throws BuilderConfigException {
        assert(parent instanceof AutowireComponent): "Parent must implement " + AutowireComponent.class.getName();
        AutowireComponent autowireContext = (AutowireComponent) parent;
        PojoComponentType<?,?,?> componentType = componentDefinition.getImplementation().getComponentType();
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
        Map<String, Member> members = componentType.getReferenceMembers();
        for (Injector injector : injectors) {
            if (injector instanceof ContextInjector) {
                // a context injector is found; determine if the parent context implements the interface
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
        ScopeContext scopeContext = deploymentContext.getModuleScope();
        SystemAtomicComponent systemContext =
                new SystemAtomicComponentImpl(componentDefinition.getName(),
                        parent,
                        scopeContext,
                        serviceInterfaces,
                        factory,
                        componentType.isEagerInit(),
                        componentType.getInitInvoker(),
                        componentType.getDestroyInvoker(),
                        injectors,
                        members);

        for (ServiceDefinition serviceDefinition : componentType.getServices().values()) {
            Class interfaze = serviceDefinition.getServiceContract().getInterfaceClass();
            SystemInboundWire<?> wire = new SystemInboundWireImpl(serviceDefinition.getName(), interfaze, systemContext);
            systemContext.addInboundWire(wire);
        }
        for (ReferenceTarget target : componentDefinition.getReferenceTargets().values()) {
            String referenceName = target.getReferenceName();
            Class interfaze = target.getReference().getServiceContract().getInterfaceClass();
            Member member = componentType.getReferenceMember(referenceName);
            if (member == null) {
                BuilderConfigException e = new BuilderConfigException("ReferenceDefinition not found");
                e.setIdentifier(target.getReferenceName());
                e.addContextName(componentDefinition.getName());
                e.addContextName(parent.getName());
                throw e;
            }
            OutboundWire<?> wire;
            if (target.getReference().isAutowire()) {
                wire = new SystemOutboundAutowire(referenceName, interfaze, autowireContext);
            } else {
                //FIXME support multiplicity!
                assert(target.getTargets().size() == 1): "Multiplicity not yet implemented";
                QualifiedName targetName = new QualifiedName(target.getTargets().get(0).getPath());
                wire = new SystemOutboundWireImpl(referenceName, targetName, interfaze);
            }
            systemContext.addOutboundWire(wire);
        }
        return systemContext;
    }
}
