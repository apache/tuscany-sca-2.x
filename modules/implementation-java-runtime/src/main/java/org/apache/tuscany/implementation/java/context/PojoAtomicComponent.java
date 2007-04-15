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
package org.apache.tuscany.implementation.java.context;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.assembly.ComponentProperty;
import org.apache.tuscany.assembly.Multiplicity;
import org.apache.tuscany.assembly.Reference;
import org.apache.tuscany.core.component.ComponentContextImpl;
import org.apache.tuscany.core.component.ComponentContextProvider;
import org.apache.tuscany.core.component.ServiceReferenceImpl;
import org.apache.tuscany.implementation.java.impl.JavaElement;
import org.apache.tuscany.implementation.java.impl.Parameter;
import org.apache.tuscany.implementation.java.injection.ArrayMultiplicityObjectFactory;
import org.apache.tuscany.implementation.java.injection.CallbackWireObjectFactory;
import org.apache.tuscany.implementation.java.injection.ConversationIDObjectFactory;
import org.apache.tuscany.implementation.java.injection.FieldInjector;
import org.apache.tuscany.implementation.java.injection.Injector;
import org.apache.tuscany.implementation.java.injection.InvalidAccessorException;
import org.apache.tuscany.implementation.java.injection.ListMultiplicityObjectFactory;
import org.apache.tuscany.implementation.java.injection.MethodInjector;
import org.apache.tuscany.implementation.java.injection.NoMultiplicityTypeException;
import org.apache.tuscany.implementation.java.injection.ObjectCallbackException;
import org.apache.tuscany.implementation.java.introspect.impl.JavaIntrospectionHelper;
import org.apache.tuscany.interfacedef.java.JavaInterface;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.TargetDestructionException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.extension.AtomicComponentExtension;
import org.apache.tuscany.spi.wire.Wire;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.ConversationID;

/**
 * Base implementation of an
 * {@link org.apache.tuscany.spi.component.AtomicComponent} whose type is a Java
 * class
 * 
 * @version $$Rev$$ $$Date: 2007-03-19 22:08:36 -0700 (Mon, 19 Mar
 *          2007) $$
 */
public abstract class PojoAtomicComponent extends AtomicComponentExtension implements ComponentContextProvider {

    protected Map<String, List<Wire>> wires = new HashMap<String, List<Wire>>();
    protected Map<String, List<Wire>> callBackwires = new HashMap<String, List<Wire>>();

    protected PojoConfiguration<?> configuration;

    private final ComponentContext componentContext;

    public PojoAtomicComponent(PojoConfiguration configuration) {
        super(configuration.getName(), configuration.getProxyService(), configuration.getWorkContext(), configuration
            .getGroupId(), 50, configuration.getDefinition().getMaxIdleTime(), configuration.getDefinition()
            .getMaxAge());
        this.configuration = configuration;
        componentContext = new ComponentContextImpl(this);
    }

    public void destroy(Object instance) throws TargetDestructionException {
        if (configuration.getDestroyInvoker() != null) {
            try {
                configuration.getDestroyInvoker().invokeEvent(instance);
            } catch (ObjectCallbackException e) {
                throw new TargetDestructionException("Error destroying component instance", getUri().toString(), e);
            }
        }
    }

    public boolean isOptimizable() {
        // stateless implementations that require a destroy callback cannot be
        // optimized since the callback is
        // performed by the JavaTargetInvoker
        return !(getScope() == Scope.STATELESS && configuration.getDestroyInvoker() != null);
    }

    public Object getTargetInstance() throws TargetResolutionException {
        InstanceWrapper wrapper = scopeContainer.getWrapper(this, groupId);
        if (!wrapper.isStarted()) {
            wrapper.start();
        }
        return wrapper.getInstance();
    }

    public InstanceWrapper<?> createInstanceWrapper() throws ObjectCreationException {
        return configuration.createFactory().newInstance();
    }

    public List<Wire> getWires(String name) {
        return wires.get(name);
    }
    
    public void configureProperty(String propertyName) {
        JavaElement element = configuration.getDefinition().getPropertyMembers().get(propertyName);

        if (element != null && !(element.getAnchor() instanceof Constructor)) {
            configuration.getInjectionSites().add(element);
        }
        
        ComponentProperty configuredProperty = (ComponentProperty)getDefaultPropertyValues().get(propertyName);
        Class propertyJavaType = JavaIntrospectionHelper.getBaseType(element.getType(), element.getGenericType());
        ObjectFactory<?> propertyObjectFactory =
            createPropertyValueFactory(configuredProperty, configuredProperty.getValue(), propertyJavaType);
        configuration.setObjectFactory(element, propertyObjectFactory);
    }


    public void attachWire(Wire wire) {
        assert wire.getSourceUri().getFragment() != null;
        String referenceName = wire.getSourceUri().getFragment();
        List<Wire> wireList = wires.get(referenceName);
        if (wireList == null) {
            wireList = new ArrayList<Wire>();
            wires.put(referenceName, wireList);
        }
        wireList.add(wire);

        JavaElement element = configuration.getDefinition().getReferenceMembers().get(referenceName);
        if (element != null && !(element.getAnchor() instanceof Constructor)) {
            configuration.getInjectionSites().add(element);
        }

        configuration.setObjectFactory(element, createWireFactory(element.getType(), wire));

    }

    private Parameter getParameter(String name, Class<? extends Annotation> classifer) {
        for (Parameter param : configuration.getDefinition().getConstructorDefinition().getParameters()) {
            if (param.getClassifer() == classifer && param.getName().equals(name)) {
                return param;
            }
        }
        return null;
    }

    public void attachWires(List<Wire> attachWires) {
        assert attachWires.size() > 0;
        assert attachWires.get(0).getSourceUri().getFragment() != null;
        String referenceName = attachWires.get(0).getSourceUri().getFragment();
        List<Wire> wireList = wires.get(referenceName);
        if (wireList == null) {
            wireList = new ArrayList<Wire>();
            wires.put(referenceName, wireList);
        }
        wireList.addAll(attachWires);
        JavaElement element = configuration.getDefinition().getReferenceMembers().get(referenceName);

        Class<?> type = ((JavaInterface)attachWires.get(0).getSourceContract().getInterface()).getJavaClass();
        if (type == null) {
            throw new NoMultiplicityTypeException("Java interface must be specified for multiplicity", referenceName);
        }

        List<ObjectFactory<?>> factories = new ArrayList<ObjectFactory<?>>();
        for (Wire wire : wireList) {
            factories.add(createWireFactory(element.getType(), wire));
        }
        configuration.getInjectionSites().add(element);
        configuration.setObjectFactories(element, factories);

    }

    public void attachCallbackWire(Wire wire) {
        assert wire.getSourceUri().getFragment() != null;
        // FIXME: [rfeng] This is a hack to get it compiled
        String callbackName = wire.getSourceContract().getCallbackInterface().toString();
        assert configuration.getDefinition().getCallbackMembers().get(callbackName) != null;
        List<Wire> wireList = callBackwires.get(callbackName);
        if (wireList == null) {
            wireList = new ArrayList<Wire>();
            callBackwires.put(callbackName, wireList);
        }
        wireList.add(wire);
    }

    public void start() throws CoreRuntimeException {
        if (!configuration.getDefinition().getCallbackMembers().isEmpty()) {
            for (Map.Entry<String, JavaElement> entry : configuration.getDefinition().getCallbackMembers().entrySet()) {
                List<Wire> wires = callBackwires.get(entry.getKey());
                if (wires == null) {
                    // this can happen when there are no client wires to a
                    // component that has a callback
                    continue;
                }
                JavaElement element = entry.getValue();
                ObjectFactory<?> factory = new CallbackWireObjectFactory(element.getType(), proxyService, wires);
                if (!(element.getAnchor() instanceof Constructor)) {
                    configuration.getInjectionSites().add(element);
                }
                configuration.setObjectFactory(element, factory);
            }
        }
        for (Reference ref : configuration.getDefinition().getReferences()) {
            JavaElement element = configuration.getDefinition().getReferenceMembers().get(ref.getName());
            if (element != null) {
                if (!(element.getAnchor() instanceof Constructor)) {
                    configuration.getInjectionSites().add(element);
                }
                List<Wire> wireList = wires.get(ref.getName());
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
        super.start();

    }

    public void addPropertyFactory(String name, ObjectFactory<?> factory) {
        JavaElement element = configuration.getDefinition().getPropertyMembers().get(name);

        if (element != null && !(element.getAnchor() instanceof Constructor)) {
            configuration.getInjectionSites().add(element);
        }

        configuration.setObjectFactory(element, factory);
    }

    public void addResourceFactory(String name, ObjectFactory<?> factory) {
        org.apache.tuscany.implementation.java.impl.Resource resource = configuration.getDefinition().getResources()
            .get(name);

        if (resource != null && !(resource.getElement().getAnchor() instanceof Constructor)) {
            configuration.getInjectionSites().add(resource.getElement());
        }

        configuration.setObjectFactory(resource.getElement(), factory);
    }

    public void addConversationIDFactory(Member member) {
        ObjectFactory<String> factory = new ConversationIDObjectFactory(workContext);

        if (member instanceof Field) {
            JavaElement element = new JavaElement((Field)member);
            element.setClassifer(ConversationID.class);
            configuration.setObjectFactory(element, factory);
        } else if (member instanceof Method) {
            JavaElement element = new JavaElement((Method)member, 0);
            element.setName(JavaIntrospectionHelper.toPropertyName(member.getName()));
            element.setClassifer(ConversationID.class);
            configuration.setObjectFactory(element, factory);
        } else {
            throw new InvalidAccessorException("Member must be a field or method", member.getName());
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

    protected Injector<Object> createInjector(Member member, Wire wire) {
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
            throw new InvalidAccessorException("Member must be a field or method", member.getName());
        }
    }

    protected Injector<Object> createMultiplicityInjector(Member member,
                                                          Class<?> interfaceType,
                                                          List<Wire> wireFactories) {
        List<ObjectFactory<?>> factories = new ArrayList<ObjectFactory<?>>();
        for (Wire wire : wireFactories) {
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
            throw new InvalidAccessorException("Member must be a field or method", member.getName());
        }
    }

    public ComponentContext getComponentContext() {
        return componentContext;
    }

    public <B> B getProperty(Class<B> type, String propertyName) {
        JavaElement element = configuration.getDefinition().getPropertyMembers().get(propertyName);
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
        List<Wire> referenceWires = wires.get(name);
        if (referenceWires == null || referenceWires.size() < 1) {
            return null;
        } else {
            // TODO support multiplicity
            Wire wire = referenceWires.get(0);
            ObjectFactory<B> factory = createWireFactory(type, wire);
            return factory.getInstance();
        }
    }

    public <B> ServiceReference<B> getServiceReference(Class<B> type, String name) {
        List<Wire> referenceWires = wires.get(name);
        if (referenceWires == null || referenceWires.size() < 1) {
            return null;
        } else {
            // TODO support multiplicity
            Wire wire = referenceWires.get(0);
            ObjectFactory<B> factory = createWireFactory(type, wire);
            return new ServiceReferenceImpl<B>(type, factory);
        }
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

    protected abstract <B> ObjectFactory<B> createWireFactory(Class<B> interfaze, Wire wire);
    protected abstract ObjectFactory<?> createPropertyValueFactory(ComponentProperty property, Object propertyValue, Class javaType);

    /**
     * @see org.apache.tuscany.spi.component.AtomicComponent#createInstance()
     */
    public Object createInstance() throws ObjectCreationException {
        return createInstanceWrapper().getInstance();
    }

}
