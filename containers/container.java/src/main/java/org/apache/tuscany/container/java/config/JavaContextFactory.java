/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.container.java.config;

import commonj.sdo.DataObject;
import org.apache.tuscany.container.java.context.JavaAtomicContext;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.ContextCreationException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.core.builder.NoAccessorException;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.builder.impl.ArrayMultiplicityObjectFactory;
import org.apache.tuscany.core.builder.impl.ListMultiplicityObjectFactory;
import org.apache.tuscany.core.builder.impl.ProxyObjectFactory;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.AtomicContext;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.databinding.sdo.SDOObjectFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A ContextFactory that handles POJO component implementation types
 *
 * @version $Rev$ $Date$
 */
public class JavaContextFactory{ //implements ContextFactory<AtomicContext>, ContextResolver {

    // the component name as configured in the hosting module
    private String name;

    // the parent context of the component
    private CompositeContext parentContext;

    private Map<String, TargetWireFactory> targetProxyFactories = new HashMap<String, TargetWireFactory>();

    private List<SourceWireFactory> sourceProxyFactories = new ArrayList<SourceWireFactory>();

    // the implementation type constructor
    private Constructor<Object> ctr;

    private Set<Field> fields;

    private Set<Method> methods;

    // injectors for properties, references and other metadata values such as
    private List<Injector> setters;

    // an invoker for a method decorated with @Init
    private EventInvoker<Object> init;

    // whether the component should be eagerly initialized when its scope starts
    private boolean eagerInit;

    // an invoker for a method decorated with @Destroy
    private EventInvoker<Object> destroy;

    // the scope of the implementation instance
    private Scope scope;

    // whether the component is stateless
    private boolean stateless;

    /**
     * Creates a new context factory
     *
     * @param name  the SCDL name of the component the context refers to
     * @param ctr   the implementation type constructor
     * @param scope the scope of the component implementation type
     */
    public JavaContextFactory(String name, Constructor<Object> ctr, Scope scope) {
        assert (name != null) : "Name was null";
        assert (ctr != null) : "Constructor was null";
        this.name = name;
        this.ctr = ctr;
        this.scope = scope;
        stateless = (scope == Scope.INSTANCE);
        fields = JavaIntrospectionHelper.getAllFields(ctr.getDeclaringClass());
        methods = JavaIntrospectionHelper.getAllUniqueMethods(ctr.getDeclaringClass());
    }

    public String getName() {
        return name;
    }

    public Scope getScope() {
        return scope;
    }

    public AtomicContext createContext() throws ContextCreationException {
        PojoObjectFactory<?> objectFactory = new PojoObjectFactory<Object>(ctr, null, setters);
        return new JavaAtomicContext(name, objectFactory, eagerInit, init, destroy, stateless);
    }

    public void addTargetWireFactory(String serviceName, TargetWireFactory factory) {
        targetProxyFactories.put(serviceName, factory);
    }

    public TargetWireFactory getTargetWireFactory(String serviceName) {
        return targetProxyFactories.get(serviceName);
    }

    public Map<String, TargetWireFactory> getTargetWireFactories() {
        return targetProxyFactories;
    }

    public void addProperty(String propertyName, Object value) {
        setters.add(createPropertyInjector(propertyName, value));
    }

    public void addSourceWireFactory(String referenceName, SourceWireFactory factory) {
        sourceProxyFactories.add(factory);
        setters.add(createReferenceInjector(referenceName, factory, false));
    }

    public void addSourceWireFactories(String referenceName, Class referenceInterface, List<SourceWireFactory> factories, boolean multiplicity) {
        sourceProxyFactories.addAll(factories);
        setters.add(createReferenceInjector(referenceName, factories, multiplicity));
    }

    public List<SourceWireFactory> getSourceWireFactories() {
        return sourceProxyFactories;
    }

    public void setSetters(List<Injector> setters) {
        this.setters = setters;
    }

    public void setEagerInit(boolean val) {
        eagerInit = val;
    }

    public void setInitInvoker(EventInvoker<Object> invoker) {
        init = invoker;
    }

    public void setDestroyInvoker(EventInvoker<Object> invoker) {
        destroy = invoker;
    }

    public void prepare(CompositeContext parent) {
        parentContext = parent;
    }

    public CompositeContext getCurrentContext() {
        return parentContext;
    }

    /**
     * Creates an <code>Injector</code> for component properties
     */
    private Injector createPropertyInjector(String propertyName, Object value)
            throws NoAccessorException {
        Class type = value.getClass();

        // There is no efficient way to do this
        Method method = null;
        Field field = JavaIntrospectionHelper.findClosestMatchingField(propertyName, type, fields);
        if (field == null) {
            method = JavaIntrospectionHelper.findClosestMatchingMethod(propertyName, new Class[]{type}, methods);
            if (method == null) {
                throw new NoAccessorException(propertyName);
            }
        }
        Injector injector = null;
        if (value instanceof DataObject) {
            if (field != null) {
                injector = new FieldInjector(field, new SDOObjectFactory((DataObject) value));
            } else {
                injector = new MethodInjector(method, new SDOObjectFactory((DataObject) value));
            }
        } else if (JavaIntrospectionHelper.isImmutable(type)) {
            if (field != null) {
                injector = new FieldInjector(field, new SingletonObjectFactory<Object>(value));
            } else {
                injector = new MethodInjector(method, new SingletonObjectFactory<Object>(value));
            }
        }
        return injector;

    }

    /**
     * Creates proxy factories that represent target(s) of a reference and an <code>Injector</code> responsible for injecting them
     * into the reference
     */
    private Injector createReferenceInjector(String refName, List<SourceWireFactory> wireFactories, boolean multiplicity) {
        assert wireFactories.size() > 0;
        Class refClass = wireFactories.get(0).getBusinessInterface(); //reference.getPort().getServiceContract().getInterface();
        // iterate through the targets
        List<ObjectFactory> objectFactories = new ArrayList<ObjectFactory>();
        for (SourceWireFactory wireFactory : wireFactories) {
            objectFactories.add(new ProxyObjectFactory(wireFactory));
        }
        return createInjector(refName, refClass, multiplicity, objectFactories, fields, methods);

    }

    private Injector createReferenceInjector(String refName, SourceWireFactory wireFactory, boolean multiplicity) {
        Class refClass = wireFactory.getBusinessInterface();//reference.getPort().getServiceContract().getInterface();
        List<ObjectFactory> objectFactories = new ArrayList<ObjectFactory>();
        objectFactories.add(new ProxyObjectFactory(wireFactory));
        return createInjector(refName, refClass, multiplicity, objectFactories, fields, methods);

    }

    /**
     * Creates an <code>Injector</code> for a set of object factories associated with a reference.
     */
    private Injector createInjector(String refName, Class refClass, boolean multiplicity, List<ObjectFactory> objectFactories,
                                    Set<Field> fields, Set<Method> methods) throws NoAccessorException, BuilderConfigException {
        Field field;
        Method method = null;
        if (multiplicity) {
            // since this is a multiplicity, we cannot match on business interface type, so scan through the fields,
            // matching on name and List or Array
            field = JavaIntrospectionHelper.findMultiplicityFieldByName(refName, fields);
            if (field == null) {
                // No fields found. Again, since this is a multiplicity, we cannot match on business interface type, so
                // scan through the fields, matching on name and List or Array
                method = JavaIntrospectionHelper.findMultiplicityMethodByName(refName, methods);
                if (method == null) {
                    throw new NoAccessorException(refName);
                }
            }
            Injector injector;
            // for multiplicities, we need to inject the reference proxy or proxies using an object factory
            // which first delegates to create the proxies and then returns them in the appropriate List or array type
            if (field != null) {
                if (field.getType().isArray()) {
                    injector = new FieldInjector(field, new ArrayMultiplicityObjectFactory(refClass, objectFactories));
                } else {
                    injector = new FieldInjector(field, new ListMultiplicityObjectFactory(objectFactories));
                }
            } else {
                if (method.getParameterTypes()[0].isArray()) {
                    injector = new MethodInjector(method, new ArrayMultiplicityObjectFactory(refClass, objectFactories));
                } else {
                    injector = new MethodInjector(method, new ListMultiplicityObjectFactory(objectFactories));
                }
            }
            return injector;
        } else {
            field = JavaIntrospectionHelper.findClosestMatchingField(refName, refClass, fields);
            if (field == null) {
                method = JavaIntrospectionHelper.findClosestMatchingMethod(refName, new Class[]{refClass}, methods);
                if (method == null) {
                    throw new NoAccessorException(refName);
                }
            }
            Injector injector;
            if (field != null) {
                injector = new FieldInjector(field, objectFactories.get(0));
            } else {
                injector = new MethodInjector(method, objectFactories.get(0));
            }
            return injector;
        }
    }


}
