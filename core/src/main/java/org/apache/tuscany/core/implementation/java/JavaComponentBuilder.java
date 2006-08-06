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
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

import org.apache.tuscany.core.implementation.ConstructorDefinition;
import org.apache.tuscany.core.implementation.JavaMappedCallback;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.JavaServiceContract;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.policy.async.AsyncMonitor;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;

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
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> componentType =
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
        configuration.setWorkContext(workContext);
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

        for (JavaMappedService service : componentType.getServices().values()) {
            // setup callback injection sites
            JavaMappedCallback callback = service.getCallbackReference();
            if (callback != null) {
                // Only if there is a callback reference in the service
                configuration.addCallbackSite(callback.getName(), callback.getMember());
            }
            component.addInboundWire(createWire(service));
        }

        for (ReferenceTarget reference : definition.getReferenceTargets().values()) {
            Map<String, JavaMappedReference> references = componentType.getReferences();
            JavaMappedReference mappedReference = references.get(reference.getReferenceName());
            OutboundWire wire = createWire(reference, mappedReference);
            component.addOutboundWire(wire);
        }
        return component;
    }


    @SuppressWarnings("unchecked")
    private OutboundWire createWire(ReferenceTarget reference, JavaMappedReference def) {
        //TODO multiplicity
        if (reference.getTargets().size() != 1) {
            throw new UnsupportedOperationException();
        }
        Class<?> interfaze = def.getServiceContract().getInterfaceClass();
        OutboundWire wire = wireService.createOutboundWire();
        wire.setTargetName(new QualifiedName(reference.getTargets().get(0).toString()));
        wire.setBusinessInterface(interfaze);
        wire.setReferenceName(reference.getReferenceName());
        for (Method method : interfaze.getMethods()) {
            //TODO handle policy
            OutboundInvocationChain chain = wireService.createOutboundChain(method);
            wire.addInvocationChain(method, chain);
        }
        // FIXME Using JavaServiceContract for now; this may be ok, but if it's not, then getCallbackClass
        //       will need to be promoted to ServiceContract
        JavaServiceContract jsc = (JavaServiceContract) def.getServiceContract();
        Class<?> callbackInterface = jsc.getCallbackClass();
        if (callbackInterface != null) {
            wire.setCallbackInterface(callbackInterface);
            for (Method callbackMethod : callbackInterface.getMethods()) {
                InboundInvocationChain callbackTargetChain = new InboundInvocationChainImpl(callbackMethod);
                OutboundInvocationChain callbackSourceChain = new OutboundInvocationChainImpl(callbackMethod);
                // TODO handle policy
                //TODO statement below could be cleaner
                callbackTargetChain.addInterceptor(new InvokerInterceptor());
                wire.addTargetCallbackInvocationChain(callbackMethod, callbackTargetChain);
                wire.addSourceCallbackInvocationChain(callbackMethod, callbackSourceChain);
            }
        }
        return wire;
    }

    @SuppressWarnings("unchecked")
    private InboundWire createWire(JavaMappedService service) {
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
        // FIXME Using JavaServiceContract for now; this may be ok, but if it's not, then getCallbackClass
        //       will need to be promoted to ServiceContract
        JavaServiceContract jsc = (JavaServiceContract) service.getServiceContract();
        Class<?> callbackInterface = jsc.getCallbackClass();
        if (callbackInterface != null) {
            wire.setCallbackReferenceName(service.getCallbackReference().getName());
        }
        return wire;
    }

    protected Class<JavaImplementation> getImplementationType() {
        return JavaImplementation.class;
    }

}
