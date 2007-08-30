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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.context.ComponentContextFactory;
import org.apache.tuscany.sca.context.RequestContextFactory;
import org.apache.tuscany.sca.core.context.InstanceWrapper;
import org.apache.tuscany.sca.core.factory.ObjectCreationException;
import org.apache.tuscany.sca.core.factory.ObjectFactory;
import org.apache.tuscany.sca.core.invocation.CallableReferenceObjectFactory;
import org.apache.tuscany.sca.core.invocation.CallbackWireObjectFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.invocation.TargetInvokerCreationException;
import org.apache.tuscany.sca.core.invocation.WireObjectFactory;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.implementation.java.impl.JavaElementImpl;
import org.apache.tuscany.sca.implementation.java.impl.JavaResourceImpl;
import org.apache.tuscany.sca.implementation.java.injection.ConversationIDObjectFactory;
import org.apache.tuscany.sca.implementation.java.injection.InvalidAccessorException;
import org.apache.tuscany.sca.implementation.java.injection.JavaPropertyValueObjectFactory;
import org.apache.tuscany.sca.implementation.java.introspect.impl.JavaIntrospectionHelper;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.CallableReference;
import org.osoa.sca.annotations.ConversationID;

/**
 * The runtime instantiation of Java component implementations
 * 
 * @version $Rev$ $Date$
 */
public class JavaComponentContextProvider {
    private JavaPropertyValueObjectFactory propertyValueFactory;
    private DataBindingExtensionPoint dataBindingRegistry;
    private RuntimeComponent component;
    private JavaInstanceFactoryProvider<?> instanceFactoryProvider;
    private ProxyFactory proxyFactory;

    public JavaComponentContextProvider(RuntimeComponent component,
                                        JavaInstanceFactoryProvider configuration,
                                        DataBindingExtensionPoint dataBindingExtensionPoint,
                                        JavaPropertyValueObjectFactory propertyValueObjectFactory,
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
        this.dataBindingRegistry = dataBindingExtensionPoint;
        this.propertyValueFactory = propertyValueObjectFactory;
    }

    InstanceWrapper<?> createInstanceWrapper() throws ObjectCreationException {
        return instanceFactoryProvider.createFactory().newInstance();
    }

    void configureProperties(List<ComponentProperty> definedProperties) {
        for (ComponentProperty p : definedProperties) {
            configureProperty(p);
        }
    }

    private void configureProperty(ComponentProperty configuredProperty) {
        JavaElementImpl element =
            instanceFactoryProvider.getImplementation().getPropertyMembers().get(configuredProperty.getName());

        if (element != null && !(element.getAnchor() instanceof Constructor) && configuredProperty.getValue() != null) {
            instanceFactoryProvider.getInjectionSites().add(element);

            Class propertyJavaType = JavaIntrospectionHelper.getBaseType(element.getType(), element.getGenericType());
            ObjectFactory<?> propertyObjectFactory =
                createPropertyValueFactory(configuredProperty, configuredProperty.getValue(), propertyJavaType);
            instanceFactoryProvider.setObjectFactory(element, propertyObjectFactory);
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
                        callbackWires.put(wires.get(0).getSource().getInterfaceContract().getInterface().toString(),
                                          wires);
                    }
                }
            }

            for (Map.Entry<String, JavaElementImpl> entry : instanceFactoryProvider.getImplementation()
                .getCallbackMembers().entrySet()) {
                List<RuntimeWire> wires = callbackWires.get(entry.getKey());
                if (wires == null) {
                    // this can happen when there are no client wires to a
                    // component that has a callback
                    continue;
                }
                JavaElementImpl element = entry.getValue();
                Class<?> businessInterface = element.getType();
                ObjectFactory<?> factory = null;
                if (CallableReference.class.isAssignableFrom(element.getType())) {
                    businessInterface = JavaIntrospectionHelper.getBusinessInterface(element.getType(), element.getGenericType());
                    factory =
                        new CallableReferenceObjectFactory(new CallbackWireObjectFactory(businessInterface,
                                                                                         proxyFactory, wires));
                } else {
                    factory = new CallbackWireObjectFactory(businessInterface, proxyFactory, wires);
                }
                if (!(element.getAnchor() instanceof Constructor)) {
                    instanceFactoryProvider.getInjectionSites().add(element);
                }
                instanceFactoryProvider.setObjectFactory(element, factory);
            }
        }
        for (Reference ref : instanceFactoryProvider.getImplementation().getReferences()) {
            JavaElementImpl element =
                instanceFactoryProvider.getImplementation().getReferenceMembers().get(ref.getName());
            if (element != null) {
                if (!(element.getAnchor() instanceof Constructor)) {
                    instanceFactoryProvider.getInjectionSites().add(element);
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
                        if (CallableReference.class.isAssignableFrom(baseType)) {
                            Type callableRefType = JavaIntrospectionHelper.getParameterType(element.getGenericType());
                            Type businessType = JavaIntrospectionHelper.getParameterType(callableRefType);
                            Class<?> businessInterface =
                                JavaIntrospectionHelper.getBusinessInterface(baseType, businessType);
                            factory =
                                new CallableReferenceObjectFactory(businessInterface, component,
                                                                   (RuntimeComponentReference)componentReference);
                        } else {
                            factory = createWireFactory(baseType, wireList.get(i));
                        }
                        factories.add(factory);
                    }
                    instanceFactoryProvider.setObjectFactories(element, factories);
                } else {
                    if (wireList == null && ref.getMultiplicity() == Multiplicity.ONE_ONE) {
                        throw new IllegalStateException("Required reference is missing: " + ref.getName());
                    }
                    if (wireList != null && !wireList.isEmpty()) {
                        ObjectFactory<?> factory = null;
                        if (CallableReference.class.isAssignableFrom(element.getType())) {
                            Class<?> businessInterface =
                                JavaIntrospectionHelper.getBusinessInterface(element.getType(), element
                                    .getGenericType());
                            factory =
                                new CallableReferenceObjectFactory(businessInterface, component,
                                                                   (RuntimeComponentReference)componentReference);
                        } else {
                            factory = createWireFactory(element.getType(), wireList.get(0));
                        }
                        instanceFactoryProvider.setObjectFactory(element, factory);
                    }
                }
            }
        }
    }

    void addResourceFactory(String name, ObjectFactory<?> factory) {
        JavaResourceImpl resource = instanceFactoryProvider.getImplementation().getResources().get(name);

        if (resource != null && !(resource.getElement().getAnchor() instanceof Constructor)) {
            instanceFactoryProvider.getInjectionSites().add(resource.getElement());
        }

        instanceFactoryProvider.setObjectFactory(resource.getElement(), factory);
    }

    void addConversationIDFactories(List<Member> names) {
        ObjectFactory<String> factory = new ConversationIDObjectFactory();
        for (Member name : names) {
            if (name instanceof Field) {
                JavaElementImpl element = new JavaElementImpl((Field)name);
                element.setClassifer(ConversationID.class);
                instanceFactoryProvider.setObjectFactory(element, factory);
            } else if (name instanceof Method) {
                JavaElementImpl element = new JavaElementImpl((Method)name, 0);
                element.setName(JavaIntrospectionHelper.toPropertyName(name.getName()));
                element.setClassifer(ConversationID.class);
                instanceFactoryProvider.setObjectFactory(element, factory);
            } else {
                throw new InvalidAccessorException("Member must be a field or method: " + name.getName());
            }
        }
    }

    Object createInstance() throws ObjectCreationException {
        return createInstanceWrapper().getInstance();
    }

    JavaInstanceFactoryProvider<?> getInstanceFactoryProvider() {
        return instanceFactoryProvider;
    }

    void stop() {
    }

    Invoker createInvoker(Operation operation) throws TargetInvokerCreationException {
        Class<?> implClass = instanceFactoryProvider.getImplementationClass();

        try {
            Method method = JavaInterfaceUtil.findMethod(implClass, operation);
            boolean passByValue =
                operation.getInterface().isRemotable() && (!instanceFactoryProvider.getImplementation()
                    .isAllowsPassByReference(method));

            Invoker invoker = new JavaImplementationInvoker(method, component);
            if (passByValue) {
                return new PassByValueInvoker(dataBindingRegistry, operation, method, component);
            } else {
                return invoker;
            }
        } catch (NoSuchMethodException e) {
            throw new TargetMethodNotFoundException(operation);
        }

    }

    private <B> WireObjectFactory<B> createWireFactory(Class<B> interfaze, RuntimeWire wire) {
        return new WireObjectFactory<B>(interfaze, wire, proxyFactory);
    }

    private ObjectFactory<?> createPropertyValueFactory(ComponentProperty property, Object propertyValue, Class javaType) {
        return propertyValueFactory.createValueFactory(property, propertyValue, javaType);
    }

    /**
     * @return the component
     */
    RuntimeComponent getComponent() {
        return component;
    }

}
