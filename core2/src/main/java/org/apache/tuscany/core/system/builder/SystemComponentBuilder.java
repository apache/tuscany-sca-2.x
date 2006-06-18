package org.apache.tuscany.core.system.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.util.Map;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.PojoConfiguration;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.injection.ContextInjector;
import org.apache.tuscany.spi.injection.Injector;
import org.apache.tuscany.spi.injection.PojoObjectFactory;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.PojoComponentType;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.wire.OutboundWire;

import org.apache.tuscany.core.component.AutowireComponent;
import org.apache.tuscany.core.system.component.SystemAtomicComponent;
import org.apache.tuscany.core.system.component.SystemAtomicComponentImpl;
import org.apache.tuscany.core.system.model.SystemImplementation;
import org.apache.tuscany.core.system.wire.SystemInboundWire;
import org.apache.tuscany.core.system.wire.SystemInboundWireImpl;
import org.apache.tuscany.core.system.wire.SystemOutboundAutowire;
import org.apache.tuscany.core.system.wire.SystemOutboundWireImpl;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;

/**
 * Produces system atomic components by evaluating an assembly
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemComponentBuilder implements ComponentBuilder<SystemImplementation> {

    public AtomicComponent<?> build(CompositeComponent<?> parent,
                                    ComponentDefinition<SystemImplementation> definition,
                                    DeploymentContext deploymentContext) throws BuilderConfigException {
        assert parent instanceof AutowireComponent : "Parent must implement " + AutowireComponent.class.getName();
        AutowireComponent autowireContext = (AutowireComponent) parent;

        PojoComponentType<?, ?, ?> componentType = definition.getImplementation().getComponentType();
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setParent(parent);
        configuration.setEagerInit(componentType.isEagerInit());
        configuration.setInitInvoker(componentType.getInitInvoker());
        configuration.setDestroyInvoker(componentType.getDestroyInvoker());
        configuration.setScopeContainer(deploymentContext.getModuleScope());
        for (ServiceDefinition serviceDefinition : componentType.getServices().values()) {
            configuration.addServiceInterface(serviceDefinition.getServiceContract().getInterfaceClass());
        }
        configuration.getInjectors().addAll(componentType.getInjectors());
        for (Map.Entry<String, Member> entry : componentType.getReferenceMembers().entrySet()) {
            configuration.addMember(entry.getKey(), entry.getValue());
        }
        for (Injector injector : configuration.getInjectors()) {
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

        Constructor<?> constr;
        try {
            constr =
                JavaIntrospectionHelper.getDefaultConstructor(definition.getImplementation().getImplementationClass());
        } catch (NoSuchMethodException e) {
            BuilderConfigException bce = new BuilderConfigException("Error building component", e);
            bce.setIdentifier(definition.getName());
            bce.addContextName(parent.getName());
            throw bce;
        }
        configuration.setObjectFactory(new PojoObjectFactory(constr));
        SystemAtomicComponent systemContext = new SystemAtomicComponentImpl(definition.getName(), configuration);

        for (ServiceDefinition serviceDefinition : componentType.getServices().values()) {
            Class interfaze = serviceDefinition.getServiceContract().getInterfaceClass();
            SystemInboundWire<?> wire =
                new SystemInboundWireImpl(serviceDefinition.getName(), interfaze, systemContext);
            systemContext.addInboundWire(wire);
        }
        for (ReferenceTarget target : definition.getReferenceTargets().values()) {
            String referenceName = target.getReferenceName();
            Class interfaze = target.getReference().getServiceContract().getInterfaceClass();
            Member member = componentType.getReferenceMember(referenceName);
            if (member == null) {
                BuilderConfigException e = new BuilderConfigException("ReferenceDefinition not found");
                e.setIdentifier(target.getReferenceName());
                e.addContextName(definition.getName());
                e.addContextName(parent.getName());
                throw e;
            }
            OutboundWire<?> wire;
            if (target.getReference().isAutowire()) {
                wire = new SystemOutboundAutowire(referenceName, interfaze, autowireContext);
            } else {
                //FIXME support multiplicity!
                assert target.getTargets().size() == 1 : "Multiplicity not yet implemented";
                QualifiedName targetName = new QualifiedName(target.getTargets().get(0).getPath());
                wire = new SystemOutboundWireImpl(referenceName, targetName, interfaze);
            }
            systemContext.addOutboundWire(wire);
        }
        return systemContext;
    }
}
