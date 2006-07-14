package org.apache.tuscany.core.implementation.system.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.PropertyValue;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.wire.OutboundWire;

import org.apache.tuscany.core.component.AutowireComponent;
import org.apache.tuscany.core.implementation.ConstructorDefinition;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.system.component.SystemAtomicComponentImpl;
import org.apache.tuscany.core.implementation.system.model.SystemImplementation;
import org.apache.tuscany.core.implementation.system.wire.SystemInboundWire;
import org.apache.tuscany.core.implementation.system.wire.SystemInboundWireImpl;
import org.apache.tuscany.core.implementation.system.wire.SystemOutboundAutowire;
import org.apache.tuscany.core.implementation.system.wire.SystemOutboundWireImpl;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;

/**
 * Produces system atomic components from a component definition
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemComponentBuilder extends ComponentBuilderExtension<SystemImplementation> {

    protected Class<SystemImplementation> getImplementationType() {
        return SystemImplementation.class;
    }

    public AtomicComponent<?> build(CompositeComponent<?> parent,
                                    ComponentDefinition<SystemImplementation> definition,
                                    DeploymentContext deploymentContext) throws BuilderConfigException {
        assert parent instanceof AutowireComponent : "Parent must implement " + AutowireComponent.class.getName();
        AutowireComponent autowireContext = (AutowireComponent) parent;
        PojoComponentType<ServiceDefinition, JavaMappedReference, JavaMappedProperty<?>> componentType =
            definition.getImplementation().getComponentType();

        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setParent(parent);
        configuration.setScopeContainer(deploymentContext.getModuleScope());
        configuration.setEagerInit(componentType.isEagerInit());
        Method initMethod = componentType.getInitMethod();
        if (initMethod != null) {
            configuration.setInitInvoker(new MethodEventInvoker<Object>(initMethod));
        }
        Method destroyMethod = componentType.getDestroyMethod();
        if (destroyMethod != null) {
            configuration.setDestroyInvoker(new MethodEventInvoker<Object>(destroyMethod));
        }
        for (ServiceDefinition serviceDefinition : componentType.getServices().values()) {
            configuration.addServiceInterface(serviceDefinition.getServiceContract().getInterfaceClass());
        }
        // setup property injection sites
        for (JavaMappedProperty<?> property : componentType.getProperties().values()) {
            configuration.addPropertySite(property.getName(), property.getMember());
        }
        // setup reference injection sites
        for (JavaMappedReference reference : componentType.getReferences().values()) {
            Member member = reference.getMember();
            if (member != null) {
                // could be null if the reference is mapped to a constructor
                configuration.addReferenceSite(reference.getName(), member);
            }
        }
        // setup constructor injection
        ConstructorDefinition<?> ctorDef = componentType.getConstructorDefinition();
        Constructor<?> constr = ctorDef.getConstructor();
        PojoObjectFactory<?> instanceFactory = new PojoObjectFactory(constr);
        configuration.setInstanceFactory(instanceFactory);
        configuration.getConstructorParamNames().addAll(ctorDef.getInjectionNames());
        SystemAtomicComponentImpl component = new SystemAtomicComponentImpl(definition.getName(), configuration);
        // handle properties
        Map<String, PropertyValue<?>> propertyValues = definition.getPropertyValues();
        for (JavaMappedProperty<?> property : componentType.getProperties().values()) {
            PropertyValue value = propertyValues.get(property.getName());
            ObjectFactory<?> factory;
            if (value != null) {
                factory = value.getValueFactory();
            } else {
                factory = property.getDefaultValueFactory();
            }
            if (factory != null) {
                component.addPropertyFactory(property.getName(), factory);
            }
        }
        // handle inbound wires
        for (ServiceDefinition serviceDefinition : componentType.getServices().values()) {
            Class interfaze = serviceDefinition.getServiceContract().getInterfaceClass();
            SystemInboundWire<?> wire =
                new SystemInboundWireImpl(serviceDefinition.getName(), interfaze, component);
            component.addInboundWire(wire);
        }
        // handle references directly with no proxies
        for (ReferenceTarget target : definition.getReferenceTargets().values()) {
            String referenceName = target.getReferenceName();
            JavaMappedReference referenceDefiniton = componentType.getReferences().get(referenceName);
            Class interfaze = referenceDefiniton.getServiceContract().getInterfaceClass();
            OutboundWire<?> wire;
            if (referenceDefiniton.isAutowire()) {
                wire = new SystemOutboundAutowire(referenceName, interfaze, autowireContext);
            } else {
                //FIXME support multiplicity!
                assert target.getTargets().size() == 1 : "Multiplicity not yet implemented";
                QualifiedName targetName = new QualifiedName(target.getTargets().get(0).getPath());
                wire = new SystemOutboundWireImpl(referenceName, targetName, interfaze);
            }
            component.addOutboundWire(wire);
        }
        // FIXME we need a way to build configuration references from autowires in the loader to eliminate this eval
        for (ReferenceDefinition reference : componentType.getReferences().values()) {
            if (reference.isAutowire()) {
                Class interfaze = reference.getServiceContract().getInterfaceClass();
                OutboundWire<?> wire = new SystemOutboundAutowire(reference.getName(), interfaze, autowireContext);
                component.addOutboundWire(wire);
            }
        }
        return component;
    }
}