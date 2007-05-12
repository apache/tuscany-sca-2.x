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
package org.apache.tuscany.implementation.java.invocation;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.context.JavaPropertyValueObjectFactory;
import org.apache.tuscany.implementation.java.context.TargetMethodNotFoundException;
import org.apache.tuscany.implementation.java.impl.JavaElementImpl;
import org.apache.tuscany.implementation.java.injection.ArrayMultiplicityObjectFactory;
import org.apache.tuscany.implementation.java.injection.ConversationIDObjectFactory;
import org.apache.tuscany.implementation.java.injection.FieldInjector;
import org.apache.tuscany.implementation.java.injection.Injector;
import org.apache.tuscany.implementation.java.injection.InvalidAccessorException;
import org.apache.tuscany.implementation.java.injection.ListMultiplicityObjectFactory;
import org.apache.tuscany.implementation.java.injection.MethodInjector;
import org.apache.tuscany.implementation.java.injection.ObjectCallbackException;
import org.apache.tuscany.implementation.java.introspect.impl.JavaIntrospectionHelper;
import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.core.RuntimeComponent;
import org.apache.tuscany.sca.core.RuntimeComponentReference;
import org.apache.tuscany.sca.core.RuntimeComponentService;
import org.apache.tuscany.sca.core.RuntimeWire;
import org.apache.tuscany.sca.core.component.ComponentContextImpl;
import org.apache.tuscany.sca.core.component.ComponentContextProvider;
import org.apache.tuscany.sca.core.component.ServiceReferenceImpl;
import org.apache.tuscany.sca.core.invocation.CallbackWireObjectFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.invocation.WireObjectFactory;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.sca.provider.ScopedImplementationProvider;
import org.apache.tuscany.sca.scope.InstanceWrapper;
import org.apache.tuscany.sca.scope.Scope;
import org.apache.tuscany.sca.scope.ScopeContainer;
import org.apache.tuscany.sca.spi.CoreRuntimeException;
import org.apache.tuscany.sca.spi.ObjectCreationException;
import org.apache.tuscany.sca.spi.ObjectFactory;
import org.apache.tuscany.sca.spi.component.ComponentException;
import org.apache.tuscany.sca.spi.component.TargetDestructionException;
import org.apache.tuscany.sca.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.sca.spi.component.TargetResolutionException;
import org.apache.tuscany.sca.spi.component.WorkContext;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.ConversationID;

/**
 * The runtime instantiation of Java component implementations
 * 
 * @version $Rev$ $Date$
 */
public class JavaComponentInfo implements ComponentContextProvider {
    private JavaPropertyValueObjectFactory propertyValueFactory;
    private DataBindingExtensionPoint dataBindingRegistry;

    protected RuntimeComponent component;
    protected PojoConfiguration<?> configuration;
    protected Scope scope;
    protected ProxyFactory proxyService;
    protected WorkContext workContext;
    protected URI groupId;

    private final ComponentContext componentContext;

    public JavaComponentInfo(RuntimeComponent component, PojoConfiguration configuration, 
                             DataBindingExtensionPoint dataBindingExtensionPoint,
                             JavaPropertyValueObjectFactory propertyValueObjectFactory) {
        super();
        this.configuration = configuration;
        componentContext = new ComponentContextImpl(this);
        this.groupId = configuration.getGroupId();
        this.component = component;
        this.proxyService = configuration.getProxyFactory();
        this.dataBindingRegistry = dataBindingExtensionPoint;
        this.propertyValueFactory = propertyValueObjectFactory;
    }
  
    public void destroy(Object instance) throws TargetDestructionException {
        if (configuration.getDestroyInvoker() != null) {
            try {
                configuration.getDestroyInvoker().invokeEvent(instance);
            } catch (ObjectCallbackException e) {
                throw new TargetDestructionException("Error destroying component instance : " + getUri().toString(), e);
            }
        }
    }

    public Object getTargetInstance() throws TargetResolutionException {
        InstanceWrapper wrapper = component.getScopeContainer().getWrapper(groupId);
        if (!wrapper.isStarted()) {
            wrapper.start();
        }
        return wrapper.getInstance();
    }

    public InstanceWrapper<?> createInstanceWrapper() throws ObjectCreationException {
        return configuration.createFactory().newInstance();
    }

    public void configureProperties(List<ComponentProperty> definedProperties) {
        for (ComponentProperty p : definedProperties) {
            configureProperty(p);
        }
    }

    public void configureProperty(ComponentProperty configuredProperty) {
        JavaElementImpl element = configuration.getDefinition().getPropertyMembers().get(configuredProperty.getName());

        if (element != null && !(element.getAnchor() instanceof Constructor) && configuredProperty.getValue() != null) {
            configuration.getInjectionSites().add(element);

            Class propertyJavaType = JavaIntrospectionHelper.getBaseType(element.getType(), element.getGenericType());
            ObjectFactory<?> propertyObjectFactory = createPropertyValueFactory(configuredProperty, configuredProperty
                .getValue(), propertyJavaType);
            configuration.setObjectFactory(element, propertyObjectFactory);
        }
    }

    public void start() throws CoreRuntimeException {
        if (!configuration.getDefinition().getCallbackMembers().isEmpty()) {
            Map<String, List<RuntimeWire>> callbackWires = new HashMap<String, List<RuntimeWire>>();
            for (ComponentService service : component.getServices()) {

                RuntimeComponentService componentService = (RuntimeComponentService)service;
                if (!componentService.getCallbackWires().isEmpty()) {
                    callbackWires.put(componentService.getCallbackWires().get(0).getTarget().getInterfaceContract()
                        .getCallbackInterface().toString(), componentService.getCallbackWires());
                }
            }

            for (Map.Entry<String, JavaElementImpl> entry : configuration.getDefinition().getCallbackMembers()
                .entrySet()) {
                List<RuntimeWire> wires = callbackWires.get(entry.getKey());
                if (wires == null) {
                    // this can happen when there are no client wires to a
                    // component that has a callback
                    continue;
                }
                JavaElementImpl element = entry.getValue();
                ObjectFactory<?> factory = new CallbackWireObjectFactory(element.getType(), proxyService, wires);
                if (!(element.getAnchor() instanceof Constructor)) {
                    configuration.getInjectionSites().add(element);
                }
                configuration.setObjectFactory(element, factory);
            }
        }
        for (Reference ref : configuration.getDefinition().getReferences()) {
            JavaElementImpl element = configuration.getDefinition().getReferenceMembers().get(ref.getName());
            if (element != null) {
                if (!(element.getAnchor() instanceof Constructor)) {
                    configuration.getInjectionSites().add(element);
                }
                List<RuntimeWire> wireList = null;
                for (ComponentReference reference : component.getReferences()) {
                    if (reference.getName().equals(ref.getName())) {
                        wireList = ((RuntimeComponentReference)reference).getRuntimeWires();
                        break;
                    }
                }
                if (ref.getMultiplicity() == Multiplicity.ONE_N || ref.getMultiplicity() == Multiplicity.ZERO_N) {
                    List<ObjectFactory<?>> factories = new ArrayList<ObjectFactory<?>>();
                    for (int i = 0; i < wireList.size(); i++) {
                        ObjectFactory<?> factory = createWireFactory(element.getType(), wireList.get(i));
                        factories.add(factory);
                    }
                    configuration.setObjectFactories(element, factories);
                } else {
                    if (wireList == null && ref.getMultiplicity() == Multiplicity.ONE_ONE) {
                        throw new IllegalStateException("Required reference is missing: " + ref.getName());
                    }
                    if (wireList != null && !wireList.isEmpty()) {
                        ObjectFactory<?> factory = createWireFactory(element.getType(), wireList.get(0));
                        configuration.setObjectFactory(element, factory);
                    }
                }
            }
        }
    }

    public void addPropertyFactory(String name, ObjectFactory<?> factory) {
        JavaElementImpl element = configuration.getDefinition().getPropertyMembers().get(name);

        if (element != null && !(element.getAnchor() instanceof Constructor)) {
            configuration.getInjectionSites().add(element);
        }

        configuration.setObjectFactory(element, factory);
    }

    public void addResourceFactory(String name, ObjectFactory<?> factory) {
        org.apache.tuscany.implementation.java.impl.JavaResourceImpl resource = configuration.getDefinition()
            .getResources().get(name);

        if (resource != null && !(resource.getElement().getAnchor() instanceof Constructor)) {
            configuration.getInjectionSites().add(resource.getElement());
        }

        configuration.setObjectFactory(resource.getElement(), factory);
    }

    public void addConversationIDFactory(Member member) {
        ObjectFactory<String> factory = new ConversationIDObjectFactory(workContext);

        if (member instanceof Field) {
            JavaElementImpl element = new JavaElementImpl((Field)member);
            element.setClassifer(ConversationID.class);
            configuration.setObjectFactory(element, factory);
        } else if (member instanceof Method) {
            JavaElementImpl element = new JavaElementImpl((Method)member, 0);
            element.setName(JavaIntrospectionHelper.toPropertyName(member.getName()));
            element.setClassifer(ConversationID.class);
            configuration.setObjectFactory(element, factory);
        } else {
            throw new InvalidAccessorException("Member must be a field or method: " + member.getName());
        }
    }

    public boolean implementsCallback(Class callbackClass) {
        Class<?>[] implementedInterfaces = configuration.getDefinition().getJavaClass().getInterfaces();
        for (Class<?> implementedInterface : implementedInterfaces) {
            if (implementedInterface.isAssignableFrom(callbackClass)) {
                return true;
            }
        }

        return false;
    }

    protected Injector<Object> createInjector(Member member, RuntimeWire wire) {
        if (member instanceof Field) {
            Class<?> type = ((Field)member).getType();
            ObjectFactory<?> factory = createWireFactory(type, wire);
            return new FieldInjector<Object>((Field)member, factory);
        } else if (member instanceof Method) {
            Class<?> type = ((Method)member).getParameterTypes()[0];
            ObjectFactory<?> factory = createWireFactory(type, wire);
            return new MethodInjector<Object>((Method)member, factory);
        } else if (member instanceof Constructor) {
            return null;
        } else {
            throw new InvalidAccessorException("Member must be a field or method: " + member.getName());
        }
    }

    protected Injector<Object> createMultiplicityInjector(Member member,
                                                          Class<?> interfaceType,
                                                          List<RuntimeWire> wireFactories) {
        List<ObjectFactory<?>> factories = new ArrayList<ObjectFactory<?>>();
        for (RuntimeWire wire : wireFactories) {
            factories.add(createWireFactory(interfaceType, wire));
        }
        if (member instanceof Field) {
            Field field = (Field)member;
            if (field.getType().isArray()) {
                return new FieldInjector<Object>(field, new ArrayMultiplicityObjectFactory(interfaceType, factories));
            } else {
                return new FieldInjector<Object>(field, new ListMultiplicityObjectFactory(factories));
            }
        } else if (member instanceof Method) {
            Method method = (Method)member;
            if (method.getParameterTypes()[0].isArray()) {
                return new MethodInjector<Object>(method, new ArrayMultiplicityObjectFactory(interfaceType, factories));
            } else {
                return new MethodInjector<Object>(method, new ListMultiplicityObjectFactory(factories));
            }
        } else {
            throw new InvalidAccessorException("Member must be a field or method: " + member.getName());
        }
    }

    public ComponentContext getComponentContext() {
        return componentContext;
    }

    public <B> B getProperty(Class<B> type, String propertyName) {
        JavaElementImpl element = configuration.getDefinition().getPropertyMembers().get(propertyName);
        Object obj = configuration.getFactories().get(element);
        if (obj instanceof ObjectFactory) {
            return type.cast(((ObjectFactory<?>)obj).getInstance());
        } else if (obj instanceof List) {
            List<ObjectFactory<?>> factories = (List<ObjectFactory<?>>)obj;
            if (type.isArray()) {
                Object array = Array.newInstance(type, factories.size());
                for (int i = 0; i < factories.size(); i++) {
                    Array.set(array, i, factories.get(i).getInstance());
                }
                return type.cast(array);
            } else {
                List<Object> list = new ArrayList<Object>();
                for (ObjectFactory factory : factories) {
                    list.add(factory.getInstance());
                }
                return type.cast(list);
            }
        }
        return null;

    }

    public <B> B getService(Class<B> type, String name) {
        List<RuntimeWire> referenceWires = getWiresForReference(name);
        if (referenceWires == null || referenceWires.size() < 1) {
            return null;
        } else {
            // TODO support multiplicity
            RuntimeWire wire = referenceWires.get(0);
            ObjectFactory<B> factory = createWireFactory(type, wire);
            return factory.getInstance();
        }
    }

    public <B> ServiceReference<B> getServiceReference(Class<B> type, String name) {
        List<RuntimeWire> referenceWires = getWiresForReference(name);
        if (referenceWires == null || referenceWires.size() < 1) {
            return null;
        } else {
            // TODO support multiplicity
            RuntimeWire wire = referenceWires.get(0);
            ObjectFactory<B> factory = createWireFactory(type, wire);
            return new ServiceReferenceImpl<B>(type, factory);
        }
    }

    private List<RuntimeWire> getWiresForReference(String name) {
        for (ComponentReference ref : component.getReferences()) {
            if (ref.getName().equals(name) || (name.equals("$self$.") && ref.getName().startsWith(name))) {
                return ((RuntimeComponentReference)ref).getRuntimeWires();
            }
        }
        return null;
    }

    public <B, R extends CallableReference<B>> R cast(B target) {
        return (R)proxyService.cast(target);
    }

    public <B> ServiceReference<B> createSelfReference(Class<B> businessInterface, String serviceName) {
        return null;
    }

    public <B> ServiceReference<B> createSelfReference(Class<B> businessInterface) {
        return null;
    }

    public Object createInstance() throws ObjectCreationException {
        return createInstanceWrapper().getInstance();
    }

    public PojoConfiguration<?> getConfiguration() {
        return configuration;
    }

    public void stop() {
    }

    public void removeInstance() throws ComponentException {
        component.getScopeContainer().remove();
    }

    public URI getUri() {
        return URI.create(component.getURI());
    }

    public TargetInvoker createTargetInvoker(Operation operation) throws TargetInvokerCreationException {
        Class<?> implClass = configuration.getImplementationClass();

        try {
            Method method = JavaInterfaceUtil.findMethod(implClass, operation);
            boolean passByValue = operation.getInterface().isRemotable() && (!configuration.getDefinition()
                                      .isAllowsPassByReference(method));

            TargetInvoker invoker = new JavaTargetInvoker(method, component);
            if (passByValue) {
                return new PassByValueInvoker(dataBindingRegistry, operation, method, component);
            } else {
                return invoker;
            }
        } catch (NoSuchMethodException e) {
            throw new TargetMethodNotFoundException(operation);
        }

    }

    protected <B> ObjectFactory<B> createWireFactory(Class<B> interfaze, RuntimeWire wire) {
        return new WireObjectFactory<B>(interfaze, wire, proxyService);
    }

    protected ObjectFactory<?> createPropertyValueFactory(ComponentProperty property,
                                                          Object propertyValue,
                                                          Class javaType) {
        return propertyValueFactory.createValueFactory(property, propertyValue, javaType);
    }

}
