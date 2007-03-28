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
package org.apache.tuscany.core.implementation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.TargetDestructionException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.extension.AtomicComponentExtension;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.Wire;

import org.apache.tuscany.core.component.ComponentContextImpl;
import org.apache.tuscany.core.component.ComponentContextProvider;
import org.apache.tuscany.core.component.InstanceFactory;
import org.apache.tuscany.core.component.ServiceReferenceImpl;
import org.apache.tuscany.core.component.scope.ReflectiveInstanceWrapper;
import org.apache.tuscany.core.injection.ArrayMultiplicityObjectFactory;
import org.apache.tuscany.core.injection.CallbackWireObjectFactory;
import org.apache.tuscany.core.injection.ConversationIDObjectFactory;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.InvalidAccessorException;
import org.apache.tuscany.core.injection.ListMultiplicityObjectFactory;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.NoAccessorException;
import org.apache.tuscany.core.injection.NoMultiplicityTypeException;
import org.apache.tuscany.core.injection.ObjectCallbackException;
import org.apache.tuscany.core.injection.PojoObjectFactory;

/**
 * Base implementation of an
 * {@link org.apache.tuscany.spi.component.AtomicComponent} whose type is a Java
 * class
 * 
 * @version $$Rev$$ $$Date: 2007-03-19 22:08:36 -0700 (Mon, 19 Mar
 *          2007) $$
 */
public abstract class PojoAtomicComponent extends AtomicComponentExtension implements ComponentContextProvider {
    protected EventInvoker<Object> initInvoker;
    protected EventInvoker<Object> destroyInvoker;
    protected PojoObjectFactory<?> instanceFactory;
    protected InstanceFactory<?> instanceFactory2;
    protected List<String> constructorParamNames;
    protected Map<String, Member> referenceSites;
    protected Map<String, Member> resourceSites;
    protected Map<String, Member> propertySites;
    protected Map<String, Member> callbackSites;
    protected List<Injector<Object>> injectors;
    protected Class implementationClass;
    protected Map<String, List<Wire>> wires = new HashMap<String, List<Wire>>();
    protected Map<String, List<Wire>> callBackwires = new HashMap<String, List<Wire>>();

    private final ComponentContext componentContext;
    private final Map<String, ObjectFactory<?>> propertyFactories = new ConcurrentHashMap<String, ObjectFactory<?>>();
    private List<Class<?>> constructorParamTypes = new ArrayList<Class<?>>();

    public PojoAtomicComponent(PojoConfiguration configuration) {
        super(configuration.getName(), configuration.getProxyService(), configuration.getWorkContext(), configuration
            .getGroupId(), configuration.getInitLevel(), configuration.getMaxIdleTime(), configuration.getMaxAge());
        assert configuration.getInstanceFactory() != null : "Object factory was null";
        initInvoker = configuration.getInitInvoker();
        destroyInvoker = configuration.getDestroyInvoker();
        instanceFactory = configuration.getInstanceFactory();
        instanceFactory2 = configuration.getInstanceFactory2();
        constructorParamNames = configuration.getConstructorParamNames();
        constructorParamTypes = configuration.getConstructorParamTypes();
        injectors = new ArrayList<Injector<Object>>();
        referenceSites = configuration.getReferenceSite() != null ? configuration.getReferenceSite()
                                                                 : new HashMap<String, Member>();
        propertySites = configuration.getPropertySites() != null ? configuration.getPropertySites()
                                                                : new HashMap<String, Member>();
        resourceSites = configuration.getResourceSites() != null ? configuration.getResourceSites()
                                                                : new HashMap<String, Member>();
        callbackSites = configuration.getCallbackSites() != null ? configuration.getCallbackSites()
                                                                : new HashMap<String, Member>();
        implementationClass = configuration.getImplementationClass();

        componentContext = new ComponentContextImpl(this);
    }

    public void destroy(Object instance) throws TargetDestructionException {
        if (destroyInvoker != null) {
            try {
                destroyInvoker.invokeEvent(instance);
            } catch (ObjectCallbackException e) {
                throw new TargetDestructionException("Error destroying component instance", getUri().toString(), e);
            }
        }
    }

    public boolean isOptimizable() {
        // stateless implementations that require a destroy callback cannot be
        // optimized since the callback is
        // performed by the JavaTargetInvoker
        return !(getScope() == Scope.STATELESS && destroyInvoker != null);
    }

    public Object getTargetInstance() throws TargetResolutionException {
        InstanceWrapper wrapper = scopeContainer.getWrapper(this, groupId);
        if (!wrapper.isStarted()) {
            wrapper.start();
        }
        return wrapper.getInstance();
    }

    public Object createInstance() throws ObjectCreationException {
        Object instance = instanceFactory.getInstance();
        // inject the instance with properties and references
        for (Injector<Object> injector : injectors) {
            injector.inject(instance);
        }
        return instance;
    }

    public InstanceWrapper<?> createInstanceWrapper() throws ObjectCreationException {
        /*
         * FIXME make this work return instanceFactory2.newInstance();
         */
        Object instance = createInstance();
        return new ReflectiveInstanceWrapper<Object>(instance, initInvoker, destroyInvoker);
    }

    public List<Wire> getWires(String name) {
        return wires.get(name);
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
        Member member = referenceSites.get(referenceName);
        if (member != null && !(member instanceof Constructor)) {
            injectors.add(createInjector(member, wire));
        }
        // cycle through constructor param names as well
        for (int i = 0; i < constructorParamNames.size(); i++) {
            if (referenceName.equals(constructorParamNames.get(i))) {
                ObjectFactory[] initializerFactories = instanceFactory.getInitializerFactories();
                initializerFactories[i] = createWireFactory(constructorParamTypes.get(i), wire);
                break;
            }
        }
        // TODO error if ref not set on constructor or ref site

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
        Member member = referenceSites.get(referenceName);
        if (member == null) {
            if (constructorParamNames.contains(referenceName)) {
                // injected on the constructor
                throw new UnsupportedOperationException();
            } else {
                throw new NoAccessorException(referenceName);
            }
        }

        Class<?> type = attachWires.get(0).getSourceContract().getInterfaceClass();
        if (type == null) {
            throw new NoMultiplicityTypeException("Java interface must be specified for multiplicity", referenceName);
        }
        injectors.add(createMultiplicityInjector(member, type, wireList));

    }

    public void attachCallbackWire(Wire wire) {
        assert wire.getSourceUri().getFragment() != null;
        String callbackName = wire.getSourceContract().getCallbackName();
        assert callbackSites.get(callbackName) != null;
        List<Wire> wireList = callBackwires.get(callbackName);
        if (wireList == null) {
            wireList = new ArrayList<Wire>();
            callBackwires.put(callbackName, wireList);
        }
        wireList.add(wire);
    }

    public void start() throws CoreRuntimeException {
        if (!callbackSites.isEmpty()) {
            for (Map.Entry<String, Member> entry : callbackSites.entrySet()) {
                List<Wire> wires = callBackwires.get(entry.getKey());
                if (wires == null) {
                    // this can happen when there are no client wires to a
                    // component that has a callback
                    continue;
                }
                Member member = entry.getValue();
                if (member instanceof Field) {
                    Field field = (Field)member;
                    ObjectFactory<?> factory = new CallbackWireObjectFactory(field.getType(), proxyService, wires);
                    injectors.add(new FieldInjector<Object>(field, factory));
                } else if (member instanceof Method) {
                    Method method = (Method)member;
                    Class<?> type = method.getParameterTypes()[0];
                    ObjectFactory<?> factory = new CallbackWireObjectFactory(type, proxyService, wires);
                    injectors.add(new MethodInjector<Object>(method, factory));
                } else {
                    throw new InvalidAccessorException("Member must be a field or method", member.getName());
                }
            }
        }
        super.start();

    }

    public void addPropertyFactory(String name, ObjectFactory<?> factory) {
        Member member = propertySites.get(name);
        if (member instanceof Field) {
            injectors.add(new FieldInjector<Object>((Field)member, factory));
        } else if (member instanceof Method) {
            injectors.add(new MethodInjector<Object>((Method)member, factory));
        }
        // cycle through constructor param names as well
        for (int i = 0; i < constructorParamNames.size(); i++) {
            if (name.equals(constructorParamNames.get(i))) {
                ObjectFactory[] initializerFactories = instanceFactory.getInitializerFactories();
                initializerFactories[i] = factory;
                break;
            }
        }
        // FIXME throw an error if no injection site found

        propertyFactories.put(name, factory);
    }

    public void addResourceFactory(String name, ObjectFactory<?> factory) {
        Member member = resourceSites.get(name);
        if (member instanceof Field) {
            injectors.add(new FieldInjector<Object>((Field)member, factory));
        } else if (member instanceof Method) {
            injectors.add(new MethodInjector<Object>((Method)member, factory));
        }
        // cycle through constructor param names as well
        for (int i = 0; i < constructorParamNames.size(); i++) {
            if (name.equals(constructorParamNames.get(i))) {
                ObjectFactory[] initializerFactories = instanceFactory.getInitializerFactories();
                initializerFactories[i] = factory;
                break;
            }
        }
        // FIXME throw an error if no injection site found
    }

    public void addConversationIDFactory(Member member) {
        ObjectFactory<String> convIDObjectFactory = new ConversationIDObjectFactory(workContext);
        if (member instanceof Field) {
            injectors.add(new FieldInjector<Object>((Field)member, convIDObjectFactory));
        } else if (member instanceof Method) {
            injectors.add(new MethodInjector<Object>((Method)member, convIDObjectFactory));
        } else {
            throw new InvalidAccessorException("Member must be a field or method", member.getName());
        }
    }

    public boolean implementsCallback(Class callbackClass) {
        Class<?>[] implementedInterfaces = implementationClass.getInterfaces();
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
        ObjectFactory<?> factory = propertyFactories.get(propertyName);
        if (factory != null) {
            return type.cast(factory.getInstance());
        } else {
            return null;
        }

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

    protected abstract <B> ObjectFactory<B> createWireFactory(Class<B> interfaze, Wire wire);

}
