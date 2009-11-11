/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tuscany.sca.implementation.java.invocation;

import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.context.ComponentContextFactory;
import org.apache.tuscany.sca.context.PropertyValueFactory;
import org.apache.tuscany.sca.context.RequestContextFactory;
import org.apache.tuscany.sca.core.factory.InstanceWrapper;
import org.apache.tuscany.sca.core.factory.ObjectCreationException;
import org.apache.tuscany.sca.core.factory.ObjectFactory;
import org.apache.tuscany.sca.core.invocation.CallableReferenceObjectFactory;
import org.apache.tuscany.sca.core.invocation.CallbackReferenceObjectFactory;
import org.apache.tuscany.sca.core.invocation.CallbackWireObjectFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.invocation.WireObjectFactory;
import org.apache.tuscany.sca.core.scope.ScopeContainer;
import org.apache.tuscany.sca.core.scope.TargetResolutionException;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.implementation.java.JavaConstructorImpl;
import org.apache.tuscany.sca.implementation.java.JavaElementImpl;
import org.apache.tuscany.sca.implementation.java.JavaResourceImpl;
import org.apache.tuscany.sca.implementation.java.context.InstanceFactory;
import org.apache.tuscany.sca.implementation.java.injection.JavaPropertyValueObjectFactory;
import org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.oasisopen.sca.ServiceReference;

/**
 * The runtime instantiation of Java component implementations
 *
 * @version $Rev$ $Date$
 */
public class JavaComponentContextProvider {
    private JavaPropertyValueObjectFactory propertyValueFactory;
    private RuntimeComponent component;
    private JavaInstanceFactoryProvider<?> instanceFactoryProvider;
    private ProxyFactory proxyFactory;
    private InstanceFactory instanceFactory;

    public JavaComponentContextProvider(RuntimeComponent component,
                                        JavaInstanceFactoryProvider configuration,
                                        DataBindingExtensionPoint dataBindingExtensionPoint,
                                        PropertyValueFactory propertyValueObjectFactory,
                                        ComponentContextFactory componentContextFactory,
                                        RequestContextFactory requestContextFactory) {
        super();
        this.instanceFactoryProvider = configuration;
        this.proxyFactory = configuration.getProxyFactory();
        //        if (componentContextFactory != null) {
        //            this.componentContext = componentContextFactory.createComponentContext(component, requestContextFactory);
        //        } else {
        //            this.componentContext = new ComponentContextImpl(this, requestContextFactory, this.proxyService);
        //        }
        this.component = component;
        this.propertyValueFactory = (JavaPropertyValueObjectFactory) propertyValueObjectFactory;
    }

    InstanceWrapper<?> createInstanceWrapper() throws ObjectCreationException {
        return instanceFactory.newInstance();
    }

    void configureProperties(List<ComponentProperty> definedProperties) {
        for (ComponentProperty p : definedProperties) {
            configureProperty(p);
        }
    }

    private void configureProperty(ComponentProperty configuredProperty) {
        JavaElementImpl element =
            instanceFactoryProvider.getImplementation().getPropertyMembers().get(configuredProperty.getName());

        if (element != null && configuredProperty.getValue() != null) {
            if (!(element.getAnchor() instanceof Constructor)) {
                if(element.getElementType() == ElementType.FIELD) {
                    // Field field = (Field)element.getAnchor();
                    instanceFactoryProvider.getInjectionSites().add(element);
                    /*
                    if(Modifier.isPublic(field.getModifiers())) {
                        instanceFactoryProvider.getInjectionSites().add(element);
                    } else if(field.getAnnotation(org.oasisopen.sca.annotation.Property.class) != null) {
                        instanceFactoryProvider.getInjectionSites().add(element);
                    }
                    */
                } else {
                    instanceFactoryProvider.getInjectionSites().add(element);
                }
            }

            //Class propertyJavaType = JavaIntrospectionHelper.getBaseType(element.getType(), element.getGenericType());
            ObjectFactory<?> propertyObjectFactory =
                createPropertyValueFactory(configuredProperty, configuredProperty.getValue(), element);
            instanceFactoryProvider.setObjectFactory(element, propertyObjectFactory);

            JavaConstructorImpl<?> constructor = instanceFactoryProvider.getImplementation().getConstructor();
            for(JavaElementImpl p: constructor.getParameters()){
                if(element.getName().equals(p.getName())) {
                    instanceFactoryProvider.setObjectFactory(p, propertyObjectFactory);
                }
            }
        }
    }

    void start() {
        if (!instanceFactoryProvider.getImplementation().getCallbackMembers().isEmpty()) {
            Map<String, List<RuntimeWire>> callbackWires = new HashMap<String, List<RuntimeWire>>();
            for (ComponentService service : component.getServices()) {

                RuntimeComponentReference callbackReference = (RuntimeComponentReference)service.getCallbackReference();
                if (callbackReference != null) {
                    List<RuntimeWire> wires = callbackReference.getRuntimeWires();
                    if (!wires.isEmpty()) {
                        callbackWires.put(wires.get(0).getEndpointReference().getInterfaceContract().getInterface().toString(),
                                          wires);
                    }
                }
            }

            for (Map.Entry<String, Collection<JavaElementImpl>> entry : instanceFactoryProvider.getImplementation()
                .getCallbackMembers().entrySet()) {
                List<RuntimeWire> wires = callbackWires.get(entry.getKey());
                if (wires == null) {
                    // this can happen when there are no client wires to a
                    // component that has a callback
                    continue;
                }
                for(JavaElementImpl element : entry.getValue()) {
                    Class<?> businessInterface = element.getType();
                    ObjectFactory<?> factory = null;
                    if (ServiceReference.class.isAssignableFrom(element.getType())) {
                        businessInterface =
                            JavaIntrospectionHelper.getBusinessInterface(element.getType(), element.getGenericType());
                        factory =
                            new CallbackReferenceObjectFactory(businessInterface, proxyFactory, wires);
                    } else {
                        factory = new CallbackWireObjectFactory(businessInterface, proxyFactory, wires);
                    }
                    if (!(element.getAnchor() instanceof Constructor)) {
                        instanceFactoryProvider.getInjectionSites().add(element);
                    }
                    instanceFactoryProvider.setObjectFactory(element, factory);
                }
            }
        }
        for (Reference ref : instanceFactoryProvider.getImplementation().getReferences()) {
            JavaElementImpl element =
                instanceFactoryProvider.getImplementation().getReferenceMembers().get(ref.getName());
            if (element != null) {
                if (!(element.getAnchor() instanceof Constructor)) {
                    if(element.getElementType() == ElementType.FIELD) {
                        Field field = (Field)element.getAnchor();
                        if(Modifier.isPublic(field.getModifiers())) {
                            instanceFactoryProvider.getInjectionSites().add(element);
                        } else if(field.getAnnotation(org.oasisopen.sca.annotation.Reference.class) != null) {
                            instanceFactoryProvider.getInjectionSites().add(element);
                        }
                    } else {
                        instanceFactoryProvider.getInjectionSites().add(element);
                    }
                }
                ComponentReference componentReference = null;
                List<RuntimeWire> wireList = null;
                for (ComponentReference reference : component.getReferences()) {
                    if (reference.getName().equals(ref.getName())) {
                        wireList = ((RuntimeComponentReference)reference).getRuntimeWires();
                        componentReference = reference;
                        break;
                    }
                }
                if (ref.getMultiplicity() == Multiplicity.ONE_N || ref.getMultiplicity() == Multiplicity.ZERO_N) {
                    List<ObjectFactory<?>> factories = new ArrayList<ObjectFactory<?>>();
                    Class<?> baseType =
                        JavaIntrospectionHelper.getBaseType(element.getType(), element.getGenericType());
                    for (int i = 0; i < wireList.size(); i++) {
                        ObjectFactory<?> factory = null;
                        if (ServiceReference.class.isAssignableFrom(baseType)) {
                            Type callableRefType = JavaIntrospectionHelper.getParameterType(element.getGenericType());
                            // Type businessType = JavaIntrospectionHelper.getParameterType(callableRefType);
                            Class<?> businessInterface =
                                JavaIntrospectionHelper.getBusinessInterface(baseType, callableRefType);
                            factory =
                                new CallableReferenceObjectFactory(businessInterface, component,
                                                                   (RuntimeComponentReference)wireList.get(i)
                                                                       .getEndpointReference().getReference(), wireList.get(i)
                                                                       .getEndpointReference());
                        } else {
                            factory = createObjectFactory(baseType, wireList.get(i));
                        }
                        factories.add(factory);
                    }
                    instanceFactoryProvider.setObjectFactories(element, factories);
                    JavaConstructorImpl<?> constructor = instanceFactoryProvider.getImplementation().getConstructor();
                    for(JavaElementImpl p: constructor.getParameters()){
                        if(element.getName().equals(p.getName())) {
                            instanceFactoryProvider.setObjectFactories(p, factories);
                        }
                    }
                } else {
                    if (wireList == null && ref.getMultiplicity() == Multiplicity.ONE_ONE) {
                        throw new IllegalStateException("Required reference is missing: " + ref.getName());
                    }
                    if (wireList != null && !wireList.isEmpty()) {
                        ObjectFactory<?> factory = null;
                        if (ServiceReference.class.isAssignableFrom(element.getType())) {
                            Class<?> businessInterface =
                                JavaIntrospectionHelper.getBusinessInterface(element.getType(), element
                                    .getGenericType());
                            factory =
                                new CallableReferenceObjectFactory(businessInterface, component,
                                                                   (RuntimeComponentReference)componentReference, wireList.get(0).getEndpointReference());
                        } else {
                            factory = createObjectFactory(element.getType(), wireList.get(0));
                        }
                        instanceFactoryProvider.setObjectFactory(element, factory);
                        JavaConstructorImpl<?> constructor = instanceFactoryProvider.getImplementation().getConstructor();
                        for(JavaElementImpl p: constructor.getParameters()){
                            if(element.getName().equals(p.getName())) {
                                instanceFactoryProvider.setObjectFactory(p, factory);
                            }
                        }
                    }
                }
            }
        }

        //setUpPolicyHandlers();
        this.instanceFactory = instanceFactoryProvider.createFactory();

    }

    void addResourceFactory(String name, ObjectFactory<?> factory) {
        JavaResourceImpl resource = instanceFactoryProvider.getImplementation().getResources().get(name);

        if (resource != null && !(resource.getElement().getAnchor() instanceof Constructor)) {
            instanceFactoryProvider.getInjectionSites().add(resource.getElement());
        }

        instanceFactoryProvider.setObjectFactory(resource.getElement(), factory);
    }

    Object createInstance() throws ObjectCreationException {
        return createInstanceWrapper().getInstance();
    }

    JavaInstanceFactoryProvider<?> getInstanceFactoryProvider() {
        return instanceFactoryProvider;
    }

    void stop() {
        //cleanUpPolicyHandlers();
    }

    Invoker createInvoker(Operation operation) throws NoSuchMethodException {
        Class<?> implClass = instanceFactoryProvider.getImplementationClass();

        Method method = JavaInterfaceUtil.findMethod(implClass, operation);
        return new JavaImplementationInvoker(operation, method, component);
    }

    private static class OptimizedObjectFactory<T> implements ObjectFactory<T> {
        private ScopeContainer scopeContainer;

        public OptimizedObjectFactory(ScopeContainer scopeContainer) {
            super();
            this.scopeContainer = scopeContainer;
        }

        public T getInstance() throws ObjectCreationException {
            try {
                return (T)scopeContainer.getWrapper(null).getInstance();
            } catch (TargetResolutionException e) {
                throw new ObjectCreationException(e);
            }
        }

    }

    private <B> ObjectFactory<B> createObjectFactory(Class<B> interfaze, RuntimeWire wire) {
        // FIXME: [rfeng] Disable the optimization for new as it needs more discussions
        /*
        boolean conversational = wire.getSource().getInterfaceContract().getInterface().isConversational();
        Binding binding = wire.getSource().getBinding();
        // Check if it's wireable binding for optimization
        if (!conversational && binding instanceof OptimizableBinding) {
            OptimizableBinding optimizableBinding = (OptimizableBinding)binding;
            Component component = optimizableBinding.getTargetComponent();
            if (component != null) {
                Implementation implementation = component.getImplementation();
                // Check if the target component is java component
                if (implementation instanceof JavaImplementation) {
                    JavaImplementation javaImplementation = (JavaImplementation)implementation;
                    if (interfaze.isAssignableFrom(javaImplementation.getJavaClass())) {
                        ScopedRuntimeComponent scopedComponent = (ScopedRuntimeComponent)component;
                        ScopeContainer scopeContainer = scopedComponent.getScopeContainer();
                        Scope scope = scopeContainer.getScope();
                        if (scope == Scope.COMPOSITE || scope == Scope.STATELESS || scope == Scope.SYSTEM) {
                            boolean optimizable = true;
                            for (InvocationChain chain : wire.getInvocationChains()) {
                                if (chain.getHeadInvoker() != chain.getTailInvoker()) {
                                    optimizable = false;
                                    break;
                                }
                            }
                            if (optimizable) {
                                return new OptimizedObjectFactory<B>(scopeContainer);
                            }
                        }
                    }
                }
            }
        }
        */
        return new WireObjectFactory<B>(interfaze, wire, proxyFactory);
    }

    private ObjectFactory<?> createPropertyValueFactory(ComponentProperty property,
                                                        Object propertyValue,
                                                        JavaElementImpl javaElement) {
        return propertyValueFactory.createValueFactory(property, propertyValue, javaElement);
    }

    /**
     * @return the component
     */
    RuntimeComponent getComponent() {
        return component;
    }

    /*private void setUpPolicyHandlers() {
        for (PolicyHandler policyHandler : policyHandlers.values()) {
            policyHandler.setUp(component.getImplementation());
        }
    }

    private void cleanUpPolicyHandlers() {
        for (PolicyHandler policyHandler : policyHandlers.values() ) {
            policyHandler.cleanUp(this);
        }
    }*/

}
