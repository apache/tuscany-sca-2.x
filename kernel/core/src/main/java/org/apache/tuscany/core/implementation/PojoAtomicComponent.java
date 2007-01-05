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

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.TargetDestructionException;
import org.apache.tuscany.spi.component.TargetInitializationException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.extension.AtomicComponentExtension;
import org.apache.tuscany.spi.wire.OutboundWire;

import org.apache.tuscany.core.injection.ArrayMultiplicityObjectFactory;
import org.apache.tuscany.core.injection.ConversationIDObjectFactory;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.InvalidAccessorException;
import org.apache.tuscany.core.injection.ListMultiplicityObjectFactory;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.NoAccessorException;
import org.apache.tuscany.core.injection.ObjectCallbackException;
import org.apache.tuscany.core.injection.PojoObjectFactory;

/**
 * Base implementation of an {@link org.apache.tuscany.spi.component.AtomicComponent} whose type is a Java class
 *
 * @version $$Rev$$ $$Date$$
 */
public abstract class PojoAtomicComponent extends AtomicComponentExtension {
    protected EventInvoker<Object> initInvoker;
    protected EventInvoker<Object> destroyInvoker;
    protected PojoObjectFactory<?> instanceFactory;
    protected List<String> constructorParamNames;
    protected Map<String, Member> referenceSites;
    protected Map<String, Member> resourceSites;
    protected Map<String, Member> propertySites;
    protected Map<String, Member> callbackSites;
    protected List<Injector<Object>> injectors;
    protected Class implementationClass;
    private List<Class<?>> constructorParamTypes = new ArrayList<Class<?>>();

    public PojoAtomicComponent(PojoConfiguration configuration) {
        super(configuration.getName(),
            configuration.getParent(),
            configuration.getScopeContainer(),
            configuration.getWireService(),
            configuration.getWorkContext(),
            configuration.getScheduler(),
            configuration.getMonitor(),
            configuration.getInitLevel(),
            configuration.getMaxIdleTime(),
            configuration.getMaxAge());
        assert configuration.getInstanceFactory() != null : "Object factory was null";
        initInvoker = configuration.getInitInvoker();
        destroyInvoker = configuration.getDestroyInvoker();
        instanceFactory = configuration.getInstanceFactory();
        constructorParamNames = configuration.getConstructorParamNames();
        constructorParamTypes = configuration.getConstructorParamTypes();
        injectors = new ArrayList<Injector<Object>>();
        referenceSites = configuration.getReferenceSite() != null ? configuration.getReferenceSite()
            : new HashMap<String, Member>();
        propertySites = configuration.getPropertySites() != null ? configuration.getPropertySites()
            : new HashMap<String, Member>();
        resourceSites = configuration.getResourceSites() != null ? configuration.getResourceSites()
            : new HashMap<String, Member>();
        callbackSites = configuration.getCallbackSite() != null ? configuration.getCallbackSite()
            : new HashMap<String, Member>();
        implementationClass = configuration.getImplementationClass();
    }


    public boolean isDestroyable() {
        return destroyInvoker != null;
    }

    public void init(Object instance) throws TargetInitializationException {
        if (initInvoker != null) {
            try {
                initInvoker.invokeEvent(instance);
            } catch (ObjectCallbackException e) {
                throw new TargetInitializationException("Error initializing component instance", getName(), e);
            }
        }
    }

    public void destroy(Object instance) throws TargetDestructionException {
        if (destroyInvoker != null) {
            try {
                destroyInvoker.invokeEvent(instance);
            } catch (ObjectCallbackException e) {
                throw new TargetDestructionException("Error destroying component instance", getName(), e);
            }
        }
    }

    public Object getTargetInstance() throws TargetResolutionException {
        return scopeContainer.getInstance(this);
    }

    public Object getAssociatedTargetInstance() throws TargetResolutionException {
        return scopeContainer.getAssociatedInstance(this);
    }

    public Object createInstance() throws ObjectCreationException {
        Object instance = instanceFactory.getInstance();
        // inject the instance with properties and references
        for (Injector<Object> injector : injectors) {
            injector.inject(instance);
        }
        return instance;
    }

    public void addPropertyFactory(String name, ObjectFactory<?> factory) {
        Member member = propertySites.get(name);
        if (member instanceof Field) {
            injectors.add(new FieldInjector<Object>((Field) member, factory));
        } else if (member instanceof Method) {
            injectors.add(new MethodInjector<Object>((Method) member, factory));
        }
        // cycle through constructor param names as well
        for (int i = 0; i < constructorParamNames.size(); i++) {
            if (name.equals(constructorParamNames.get(i))) {
                ObjectFactory[] initializerFactories = instanceFactory.getInitializerFactories();
                initializerFactories[i] = factory;
                break;
            }
        }
        //FIXME throw an error if no injection site found
    }

    public void addResourceFactory(String name, ObjectFactory<?> factory) {
        Member member = resourceSites.get(name);
        if (member instanceof Field) {
            injectors.add(new FieldInjector<Object>((Field) member, factory));
        } else if (member instanceof Method) {
            injectors.add(new MethodInjector<Object>((Method) member, factory));
        }
        // cycle through constructor param names as well
        for (int i = 0; i < constructorParamNames.size(); i++) {
            if (name.equals(constructorParamNames.get(i))) {
                ObjectFactory[] initializerFactories = instanceFactory.getInitializerFactories();
                initializerFactories[i] = factory;
                break;
            }
        }
        //FIXME throw an error if no injection site found
    }

    public void addConversationIDFactory(Member member) {
        ObjectFactory<String> convIDObjectFactory = new ConversationIDObjectFactory(workContext);
        if (member instanceof Field) {
            injectors.add(new FieldInjector<Object>((Field) member, convIDObjectFactory));
        } else if (member instanceof Method) {
            injectors.add(new MethodInjector<Object>((Method) member, convIDObjectFactory));
        } else {
            throw new InvalidAccessorException("Member must be a field or method", member.getName());
        }
    }

    protected void onReferenceWire(OutboundWire wire) {
        String name = wire.getReferenceName();
        Member member = referenceSites.get(name);
        if (member != null) {
            injectors.add(createInjector(member, wire));
        }
        // cycle through constructor param names as well
        for (int i = 0; i < constructorParamNames.size(); i++) {
            if (name.equals(constructorParamNames.get(i))) {
                ObjectFactory[] initializerFactories = instanceFactory.getInitializerFactories();
                initializerFactories[i] = createWireFactory(constructorParamTypes.get(i), wire);
                break;
            }
        }
        //TODO error if ref not set on constructor or ref site
    }

    public void onReferenceWires(Class<?> multiplicityClass, List<OutboundWire> wires) {
        assert wires.size() > 0 : "Wires were empty";
        String referenceName = wires.get(0).getReferenceName();
        Member member = referenceSites.get(referenceName);
        if (member == null) {
            if (constructorParamNames.contains(referenceName)) {
                // injected on the constructor

            } else {
                throw new NoAccessorException(referenceName);
            }
        }
        injectors.add(createMultiplicityInjector(member, multiplicityClass, wires));
        //TODO multiplicity for constructor injection
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

    protected Injector<Object> createInjector(Member member, OutboundWire wire) {
        if (member instanceof Field) {
            Class<?> type = ((Field) member).getType();
            ObjectFactory<?> factory = createWireFactory(type, wire);
            return new FieldInjector<Object>((Field) member, factory);
        } else if (member instanceof Method) {
            Class<?> type = ((Method) member).getParameterTypes()[0];
            ObjectFactory<?> factory = createWireFactory(type, wire);
            return new MethodInjector<Object>((Method) member, factory);
        } else {
            throw new InvalidAccessorException("Member must be a field or method", member.getName());
        }
    }

    protected Injector<Object> createMultiplicityInjector(Member member,
                                                          Class<?> interfaceType,
                                                          List<OutboundWire> wireFactories) {
        List<ObjectFactory<?>> factories = new ArrayList<ObjectFactory<?>>();
        for (OutboundWire wire : wireFactories) {
            factories.add(createWireFactory(interfaceType, wire));
        }
        if (member instanceof Field) {
            Field field = (Field) member;
            if (field.getType().isArray()) {
                return new FieldInjector<Object>(field, new ArrayMultiplicityObjectFactory(interfaceType, factories));
            } else {
                return new FieldInjector<Object>(field, new ListMultiplicityObjectFactory(factories));
            }
        } else if (member instanceof Method) {
            Method method = (Method) member;
            if (method.getParameterTypes()[0].isArray()) {
                return new MethodInjector<Object>(method, new ArrayMultiplicityObjectFactory(interfaceType, factories));
            } else {
                return new MethodInjector<Object>(method, new ListMultiplicityObjectFactory(factories));
            }
        } else {
            throw new InvalidAccessorException("Member must be a field or method", member.getName());
        }
    }

    protected abstract ObjectFactory<?> createWireFactory(Class<?> interfaze, OutboundWire wire);

}
