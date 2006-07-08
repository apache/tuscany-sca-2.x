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
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.ReferenceDefinition;

/**
 * Builds a Java-based atomic context from a component definition
 *
 * @version $$Rev$$ $$Date$$
 */
public class JavaComponentBuilder extends ComponentBuilderExtension<JavaImplementation> {

    @SuppressWarnings("unchecked")
    public AtomicComponent<?> build(CompositeComponent<?> parent,
                                    ComponentDefinition<JavaImplementation> definition,
                                    DeploymentContext deployment)
        throws BuilderConfigException {
        PojoComponentType<ServiceDefinition, JavaMappedReference, JavaMappedProperty<?>> componentType =
            definition.getImplementation().getComponentType();

        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setParent(parent);
        Scope scope = componentType.getLifecycleScope();
        if (Scope.MODULE == scope) {
            configuration.setScopeContainer(deployment.getModuleScope());
        } else {
            configuration.setScopeContainer(scopeRegistry.getScopeContainer(scope));
        }
        configuration.setEagerInit(componentType.isEagerInit());
        Method initMethod = componentType.getInitMethod();
        if (initMethod != null) {
            configuration.setInitInvoker(new MethodEventInvoker(initMethod));
        }
        Method destroyMethod = componentType.getDestroyMethod();
        if (destroyMethod != null) {
            configuration.setDestroyInvoker(new MethodEventInvoker(destroyMethod));
        }
        configuration.setWireService(wireService);
        try {
            Constructor<?> constr = JavaIntrospectionHelper
                .getDefaultConstructor(definition.getImplementation().getImplementationClass());
            configuration.setInstanceFactory(new PojoObjectFactory(constr));
        } catch (NoSuchMethodException e) {
            BuilderConfigException bce = new BuilderConfigException("Error building definition", e);
            bce.setIdentifier(definition.getName());
            bce.addContextName(parent.getName());
            throw bce;
        }
        for (ServiceDefinition serviceDefinition : componentType.getServices().values()) {
            configuration.addServiceInterface(serviceDefinition.getServiceContract().getInterfaceClass());
        }

        // handle properties
        for (JavaMappedProperty<?> property : componentType.getProperties().values()) {
            ObjectFactory<?> factory = property.getDefaultValueFactory();
            if (property.getMember() instanceof Field) {
                configuration.addPropertyInjector(new FieldInjector((Field) property.getMember(), factory));
            } else if (property.getMember() instanceof Method) {
                configuration.addPropertyInjector(new MethodInjector((Method) property.getMember(), factory));
            } else {
                BuilderConfigException e = new BuilderConfigException("Invalid property injection site");
                e.setIdentifier(property.getName());
                throw e;
            }
        }
        for (JavaMappedReference reference : componentType.getReferences().values()) {
            configuration.addReferenceSite(reference.getName(), reference.getMember());
        }

        JavaAtomicComponent component = new JavaAtomicComponent(definition.getName(), configuration);
        for (ServiceDefinition service : componentType.getServices().values()) {
            component.addInboundWire(createWire(service));
        }
        for (ReferenceTarget reference : definition.getReferenceTargets().values()) {
            component.addOutboundWire(
                createWire(reference, componentType.getReferences().get(reference.getReferenceName())));
        }
        return component;
    }


    //FIXME attach referenceDefinition to ref in loader
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
