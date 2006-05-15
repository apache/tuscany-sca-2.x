/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.container.java.context;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.common.ObjectCreationException;
import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.container.java.invocation.ScopedJavaComponentInvoker;
import org.apache.tuscany.core.context.PojoInstanceWrapper;
import org.apache.tuscany.core.injection.ArrayMultiplicityObjectFactory;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.InvalidAccessorException;
import org.apache.tuscany.core.injection.ListMultiplicityObjectFactory;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.NoAccessorException;
import org.apache.tuscany.core.injection.ProxyObjectFactory;
import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.context.InstanceWrapper;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.extension.AtomicContextExtension;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * Provides a runtime context for Java component implementations
 *
 * @version $Rev$ $Date$
 */
public class JavaAtomicContext<T> extends AtomicContextExtension<T> {

    private List<Injector> injectors;
    private Map<String, Member> members;

    protected boolean eagerInit;
    protected EventInvoker<Object> initInvoker;
    protected EventInvoker<Object> destroyInvoker;
    protected ObjectFactory<?> objectFactory;
    protected List<Class<?>> serviceInterfaces;
    protected Scope scope;

    public JavaAtomicContext(String name, List<Class<?>> serviceInterfaces, ObjectFactory<?> objectFactory, Scope scope, boolean eagerInit, EventInvoker<Object> initInvoker,
                             EventInvoker<Object> destroyInvoker, List<Injector> injectors, Map<String, Member> members) {
        this.name = name;
        this.injectors = injectors != null ? injectors : new ArrayList<Injector>();
        this.members = members != null ? members : new HashMap<String, Member>();
        this.objectFactory = objectFactory;
        this.eagerInit = eagerInit;
        this.initInvoker = initInvoker;
        this.destroyInvoker = destroyInvoker;
        this.serviceInterfaces = serviceInterfaces;
        this.scope = scope;
    }

    public List<Class<?>> getServiceInterfaces() {
        return serviceInterfaces;
    }

    public Scope getScope() {
        return scope;
    }

    public boolean isEagerInit() {
        return eagerInit;
    }

    public void init(Object instance) throws TargetException {
        if (initInvoker != null) {
            initInvoker.invokeEvent(instance);
        }
    }

    public void destroy(Object instance) throws TargetException {
        if (destroyInvoker != null) {
            destroyInvoker.invokeEvent(instance);
        }
    }

    @SuppressWarnings("unchecked")
    public T getTargetInstance() throws TargetException {
        return (T) scopeContext.getInstance(this);
    }


    public Object getService(String name) throws TargetException {
        // TODO implement proxying
        return getTargetInstance();
    }

    public T getService() throws TargetException {
        if (serviceInterfaces.size() == 1) {
            return getTargetInstance();
        } else {
            throw new TargetException("Context must contain exactly one service");
        }
    }

    public InstanceWrapper createInstance() throws ObjectCreationException {
        Object instance = objectFactory.getInstance();
        // inject the instance with properties and references
        for (Injector<Object> injector : injectors) {
            injector.inject(instance);
        }
        InstanceWrapper ctx = new PojoInstanceWrapper(this, instance);
        ctx.start();
        return ctx;
    }

    public void onSourceWire(SourceWire wire) {
        String referenceName = wire.getReferenceName();
        Member member = members.get(referenceName);
        if (member == null) {
            throw new NoAccessorException(referenceName);
        }
        injectors.add(createInjector(member, wire));
    }

    public void onSourceWires(Class<?> multiplicityClass, List<SourceWire> wires) {
        assert(wires.size() > 0): "Wire wires was empty";
        String referenceName = wires.get(0).getReferenceName();
        Member member = members.get(referenceName);
        if (member == null) {
            throw new NoAccessorException(referenceName);
        }
        injectors.add(createMultiplicityInjector(member, multiplicityClass, wires));
    }


    public void onTargetWire(TargetWire wire) {

    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        return new ScopedJavaComponentInvoker(operation, this);
    }

    public void prepare() {
    }

    private Injector createInjector(Member member, SourceWire wire) {
        ObjectFactory<?> factory = new ProxyObjectFactory(wire);
        if (member instanceof Field) {
            return new FieldInjector(((Field) member), factory);
        } else if (member instanceof Method) {
            return new MethodInjector(((Method) member), factory);
        } else {
            InvalidAccessorException e = new InvalidAccessorException("Member must be a field or method");
            e.setIdentifier(member.getName());
            throw e;
        }
    }

    private Injector createMultiplicityInjector(Member member, Class<?> interfaceType, List<SourceWire> wireFactories) {
        List<ObjectFactory<?>> factories = new ArrayList<ObjectFactory<?>>();
        for (SourceWire wire : wireFactories) {
            factories.add(new ProxyObjectFactory(wire));
        }
        if (member instanceof Field) {
            Field field = (Field) member;
            if (field.getType().isArray()) {
                return new FieldInjector(field, new ArrayMultiplicityObjectFactory(interfaceType, factories));
            } else {
                return new FieldInjector(field, new ListMultiplicityObjectFactory(factories));
            }
        } else if (member instanceof Method) {
            Method method = (Method) member;
            if (method.getParameterTypes()[0].isArray()) {
                return new MethodInjector(method, new ArrayMultiplicityObjectFactory(interfaceType, factories));
            } else {
                return new MethodInjector(method, new ListMultiplicityObjectFactory(factories));
            }
        } else {
            InvalidAccessorException e = new InvalidAccessorException("Member must be a field or method");
            e.setIdentifier(member.getName());
            throw e;
        }
    }

}
