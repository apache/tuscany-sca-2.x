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
package org.apache.tuscany.sca.implementation.osgi.context;


import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.context.RequestContextFactory;
import org.apache.tuscany.sca.core.context.RequestContextImpl;
import org.apache.tuscany.sca.core.factory.ObjectCreationException;
import org.apache.tuscany.sca.core.factory.ObjectFactory;
import org.apache.tuscany.sca.core.invocation.CallbackWireObjectFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.impl.JavaElementImpl;
import org.apache.tuscany.sca.implementation.java.impl.JavaResourceImpl;
import org.apache.tuscany.sca.implementation.java.injection.ArrayMultiplicityObjectFactory;
import org.apache.tuscany.sca.implementation.java.injection.ConversationIDObjectFactory;
import org.apache.tuscany.sca.implementation.java.injection.FieldInjector;
import org.apache.tuscany.sca.implementation.java.injection.Injector;
import org.apache.tuscany.sca.implementation.java.injection.InvalidAccessorException;
import org.apache.tuscany.sca.implementation.java.injection.JavaPropertyValueObjectFactory;
import org.apache.tuscany.sca.implementation.java.injection.ListMultiplicityObjectFactory;
import org.apache.tuscany.sca.implementation.java.injection.MethodInjector;
import org.apache.tuscany.sca.implementation.java.injection.ResourceObjectFactory;
import org.apache.tuscany.sca.implementation.java.introspect.impl.JavaIntrospectionHelper;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.annotations.ConversationID;


/**
 * OSGi property injection support
 *
 * @version $Rev$ $Date$
 */
public class OSGiPropertyInjector {
    
    
    private ArrayList<JavaElementImpl> injectionSites = new ArrayList<JavaElementImpl>();
    private Hashtable<JavaElementImpl, ObjectFactory> factories = 
        new Hashtable<JavaElementImpl, ObjectFactory>();
    
    private Injector[] injectors;
    
    public OSGiPropertyInjector(
            JavaImplementation javaImpl,
            RuntimeComponent component,
            JavaPropertyValueObjectFactory propertyValueFactory,
            ProxyFactory proxyFactory,
            RequestContextFactory requestContextFactory) throws IntrospectionException {
          
        createInjectionSites(javaImpl, component, propertyValueFactory, proxyFactory, requestContextFactory);
           
        injectors = createInjectors();
      
    }

    
    @SuppressWarnings("unchecked")
    public void injectProperties(Object instance) {
        
        for (Injector injector : injectors) {
            injector.inject(instance);
        }            

    }
    
  
    @SuppressWarnings("unchecked")
    private void createInjectionSites(
            JavaImplementation javaImpl,
            RuntimeComponent component,
            JavaPropertyValueObjectFactory propertyValueFactory,
            ProxyFactory proxyFactory,
            RequestContextFactory requestContextFactory) 
    {

        List<ComponentProperty> componentProperties = component.getProperties();
        Map<String, JavaElementImpl> propertyMembers = javaImpl.getPropertyMembers();
        
        for (ComponentProperty prop : componentProperties) {
            JavaElementImpl element = propertyMembers.get(prop.getName());
            
            if (element != null && !(element.getAnchor() instanceof Constructor) && prop.getValue() != null) {
                Class propertyJavaType = JavaIntrospectionHelper.getBaseType(element.getType(), element.getGenericType());
                ObjectFactory objFactory = propertyValueFactory.createValueFactory(prop, prop.getValue(), propertyJavaType);
                    
                factories.put(element, objFactory);
                injectionSites.add(element);
            }  
        }
        
        for (Member member : javaImpl.getConversationIDMembers()) {
            ObjectFactory<String> factory = new ConversationIDObjectFactory();
            if (member instanceof Field) {
                JavaElementImpl element = new JavaElementImpl((Field) member);
                element.setClassifer(ConversationID.class);
                injectionSites.add(element);
                factories.put(element, factory);
            } else if (member instanceof Method) {
                JavaElementImpl element = new JavaElementImpl((Method) member, 0);
                element.setName(JavaIntrospectionHelper.toPropertyName(member.getName()));
                element.setClassifer(ConversationID.class);
                injectionSites.add(element);
                factories.put(element, factory);
            } else {
                throw new InvalidAccessorException(
                        "Member must be a field or method: " + member.getName());
            }

        }
        
        if (!javaImpl.getCallbackMembers().isEmpty()) {
            Map<String, List<RuntimeWire>> callbackWires = new HashMap<String, List<RuntimeWire>>();
            for (ComponentService service : component.getServices()) {

                RuntimeComponentReference callbackReference = (RuntimeComponentReference)service.getCallbackReference();
                if (callbackReference != null) {
                    List<RuntimeWire> wires = callbackReference.getRuntimeWires();
                    if (!wires.isEmpty()) {
                        callbackWires.put(wires.get(0).getSource().getInterfaceContract().getInterface().toString(), wires);
                    }
                }
            }

            for (Map.Entry<String, Collection<JavaElementImpl>> entry : javaImpl.getCallbackMembers()
                .entrySet()) {
                List<RuntimeWire> wires = callbackWires.get(entry.getKey());
                if (wires == null) {
                    // this can happen when there are no client wires to a
                    // component that has a callback
                    continue;
                }
                for(JavaElementImpl element : entry.getValue()) {
                    ObjectFactory<?> factory = new CallbackWireObjectFactory(element.getType(), proxyFactory, wires);
                    if (!(element.getAnchor() instanceof Constructor)) {
                        injectionSites.add(element);
                    }
                    factories.put(element, factory);
                }
            }
        }
        
        for (JavaResourceImpl resource : javaImpl.getResources().values()) {
            
            ObjectFactory<?> objectFactory;
            Class<?> type = resource.getElement().getType();
            if (ComponentContext.class.equals(type)) {
                objectFactory = new ComponentContextFactory(component);
                
            } else if (RequestContext.class.equals(type)) {
                objectFactory = new RequestContextObjectFactory(requestContextFactory, proxyFactory);
               
            } else {
                boolean optional = resource.isOptional();
                String mappedName = resource.getMappedName();
                objectFactory = new ResourceObjectFactory(type, mappedName, optional, null);
            }
            factories.put(resource.getElement(), objectFactory);
            if (!(resource.getElement().getAnchor() instanceof Constructor)) {
                injectionSites.add(resource.getElement());
            }
        }
        
        
    }
    

    @SuppressWarnings("unchecked")
    private Injector[] createInjectors() {
        
        Injector[] injectors = (Injector[])new Injector[injectionSites.size()];

        int i = 0;
        for (JavaElementImpl element : injectionSites) {
            Object obj = factories.get(element);
            if (obj != null) {
                if (obj instanceof ObjectFactory) {
                    ObjectFactory<?> factory = (ObjectFactory<?>)obj;
                    Member member = (Member)element.getAnchor();
                    if (element.getElementType() == ElementType.FIELD) {
                        injectors[i++] = new FieldInjector((Field)member, factory);
                    } else if (element.getElementType() == ElementType.PARAMETER && member instanceof Method) {
                        injectors[i++] = new MethodInjector((Method)member, factory);
                    } else if (member instanceof Constructor) {
                        // Ignore
                    } else {
                        throw new AssertionError(String.valueOf(element));
                    }
                } else {
                    injectors[i++] = createMultiplicityInjector(element, (List<ObjectFactory<?>>)obj);
                }
            }
        }
        return injectors;
    }
    
    @SuppressWarnings("unchecked")
    protected Injector createMultiplicityInjector(JavaElementImpl element, List<ObjectFactory<?>> factories) {
        Class<?> interfaceType = JavaIntrospectionHelper.getBaseType(element.getType(), element.getGenericType());

        if (element.getAnchor() instanceof Field) {
            Field field = (Field)element.getAnchor();
            if (field.getType().isArray()) {
                return new FieldInjector(field, new ArrayMultiplicityObjectFactory(interfaceType, factories));
            } else {
                return new FieldInjector(field, new ListMultiplicityObjectFactory(factories));
            }
        } else if (element.getAnchor() instanceof Method) {
            Method method = (Method)element.getAnchor();
            if (method.getParameterTypes()[0].isArray()) {
                return new MethodInjector(method, new ArrayMultiplicityObjectFactory(interfaceType, factories));
            } else {
                return new MethodInjector(method, new ListMultiplicityObjectFactory(factories));
            }
        } else {
            throw new InvalidAccessorException("Member must be a field or method: " + element.getName());
        }
    }

    private static class ComponentContextFactory implements ObjectFactory {
        
        RuntimeComponent component;
        
        private ComponentContextFactory(RuntimeComponent component) {
            this.component = component;
        }

        public Object getInstance() throws ObjectCreationException {
            return component.getComponentContext();
        }
        
    }
    

    private static class RequestContextObjectFactory implements ObjectFactory {
        
        private RequestContextFactory factory;
        private ProxyFactory proxyFactory;

        public RequestContextObjectFactory(RequestContextFactory factory) {
            this(factory, null);
        }

        public RequestContextObjectFactory(RequestContextFactory factory, ProxyFactory proxyFactory) {
            this.factory = factory;
            this.proxyFactory = proxyFactory;
        }

        public RequestContext getInstance() throws ObjectCreationException {
            if (factory != null) {
                return factory.createRequestContext();
            } else {
                return new RequestContextImpl(proxyFactory);
            }
        }
        
    }
    
}
