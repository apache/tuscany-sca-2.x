/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.implementation.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.annotation.Monitor;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

import org.apache.tuscany.core.implementation.ConstructorDefinition;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.policy.async.AsyncMonitor;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;

/**
 * Builds a Java-based atomic context from a component definition
 *
 * @version $$Rev$$ $$Date$$
 */
public class JavaComponentBuilder extends ComponentBuilderExtension<JavaImplementation> {

    private AsyncMonitor monitor;

    @Monitor
    public void setMonitor(AsyncMonitor monitor) {
        this.monitor = monitor;
    }

    @SuppressWarnings("unchecked")
    public AtomicComponent<?> build(CompositeComponent<?> parent,
                                    ComponentDefinition<JavaImplementation> definition,
                                    DeploymentContext deployment)
        throws BuilderConfigException {
        PojoComponentType<ServiceDefinition, JavaMappedReference, JavaMappedProperty<?>> componentType =
            definition.getImplementation().getComponentType();

        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setParent(parent);
        Scope scope = componentType.getImplementationScope();
        if (Scope.MODULE == scope) {
            configuration.setScopeContainer(deployment.getModuleScope());
        } else {
            configuration.setScopeContainer(scopeRegistry.getScopeContainer(scope));
        }
        if (definition.getInitLevel() != null) {
            configuration.setInitLevel(definition.getInitLevel());
        } else {
            configuration.setInitLevel(componentType.getInitLevel());
        }
        Method initMethod = componentType.getInitMethod();
        if (initMethod != null) {
            configuration.setInitInvoker(new MethodEventInvoker(initMethod));
        }
        Method destroyMethod = componentType.getDestroyMethod();
        if (destroyMethod != null) {
            configuration.setDestroyInvoker(new MethodEventInvoker(destroyMethod));
        }

        configuration.setWireService(wireService);

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

        JavaAtomicComponent component =
            new JavaAtomicComponent(definition.getName(), configuration, workScheduler, monitor);

        // handle properties
        for (JavaMappedProperty<?> property : componentType.getProperties().values()) {
            ObjectFactory<?> factory = property.getDefaultValueFactory();
            if (factory != null) {
                component.addPropertyFactory(property.getName(), factory);
            }
        }

        for (ServiceDefinition service : componentType.getServices().values()) {
            component.addInboundWire(createWire(service));
        }
        for (ReferenceTarget reference : definition.getReferenceTargets().values()) {
            Map<String, JavaMappedReference> references = componentType.getReferences();
            OutboundWire wire = createWire(reference, references.get(reference.getReferenceName()));
            component.addOutboundWire(wire);
        }
        return component;
    }


    @SuppressWarnings("unchecked")
    private OutboundWire createWire(ReferenceTarget reference, ReferenceDefinition def) {
        //TODO multiplicity
        if (reference.getTargets().size() != 1) {
            throw new UnsupportedOperationException();
        }
        Class<?> interfaze = def.getServiceContract().getInterfaceClass();
        OutboundWire wire = new OutboundWireImpl();
        wire.setTargetName(new QualifiedName(reference.getTargets().get(0).toString()));
        wire.setBusinessInterface(interfaze);
        wire.setReferenceName(reference.getReferenceName());
        for (Method method : interfaze.getMethods()) {
            //TODO handle policy
            OutboundInvocationChain chain = new OutboundInvocationChainImpl(method);
            wire.addInvocationChain(method, chain);
        }
        return wire;
    }

    @SuppressWarnings("unchecked")
    private InboundWire createWire(ServiceDefinition service) {
        Class<?> interfaze = service.getServiceContract().getInterfaceClass();
        InboundWire wire = new InboundWireImpl();
        wire.setBusinessInterface(interfaze);
        wire.setServiceName(service.getName());
        for (Method method : interfaze.getMethods()) {
            InboundInvocationChain chain = new InboundInvocationChainImpl(method);
            // TODO handle policy
            //TODO statement below could be cleaner
            chain.addInterceptor(new InvokerInterceptor());
            wire.addInvocationChain(method, chain);
        }
        return wire;
    }

    protected Class<JavaImplementation> getImplementationType() {
        return JavaImplementation.class;
    }

}
