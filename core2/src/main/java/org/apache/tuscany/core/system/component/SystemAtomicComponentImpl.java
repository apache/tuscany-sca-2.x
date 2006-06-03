package org.apache.tuscany.core.system.component;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.component.PojoAtomicComponent;
import org.apache.tuscany.core.injection.ArrayMultiplicityObjectFactory;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.InvalidAccessorException;
import org.apache.tuscany.core.injection.ListMultiplicityObjectFactory;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.system.wire.SystemOutboundWire;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContext;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Default implementation of a system atomic context
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemAtomicComponentImpl<T> extends PojoAtomicComponent<T> implements SystemAtomicComponent<T> {


    public SystemAtomicComponentImpl(String name,
                                     CompositeComponent<?> parent,
                                     ScopeContext scopeContext,
                                     Class<?> serviceInterface,
                                     ObjectFactory<?> objectFactory,
                                     boolean eagerInit,
                                     EventInvoker<Object> initInvoker,
                                     EventInvoker<Object> destroyInvoker,
                                     List<Injector> injectors,
                                     Map<String, Member> members) {
        super(name, parent, scopeContext, serviceInterface, objectFactory, eagerInit, initInvoker, destroyInvoker, injectors, members, null);
        scope = Scope.MODULE;
    }

    public SystemAtomicComponentImpl(String name,
                                     CompositeComponent<?> parent,
                                     ScopeContext scopeContext,
                                     List<Class<?>> serviceInterfaces,
                                     ObjectFactory<?> objectFactory,
                                     boolean eagerInit,
                                     EventInvoker<Object> initInvoker,
                                     EventInvoker<Object> destroyInvoker,
                                     List<Injector> injectors,
                                     Map<String, Member> members) {
        super(name, parent, scopeContext, serviceInterfaces, objectFactory, eagerInit, initInvoker, destroyInvoker, injectors, members, null);
        scope = Scope.MODULE;
    }

    @SuppressWarnings("unchecked")
    public T getTargetInstance() throws TargetException {
        return (T) scopeContext.getInstance(this);
    }

    public Object getServiceInstance(String name) throws TargetException {
        return getTargetInstance();
    }

    public T getServiceInstance() throws TargetException {
        return getTargetInstance();
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        throw new UnsupportedOperationException();
    }

    protected Injector createInjector(Member member, OutboundWire wire) {
        assert(wire instanceof SystemOutboundWire): "wire must be an instance of " + SystemOutboundWire.class.getName();
        SystemOutboundWire systemWire = (SystemOutboundWire) wire;
        ObjectFactory<?> factory = new SystemWireObjectFactory(systemWire);
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

    protected Injector createMultiplicityInjector(Member member, Class<?> interfaceType, List<OutboundWire> wireFactories) {
        List<ObjectFactory<?>> factories = new ArrayList<ObjectFactory<?>>();
        for (OutboundWire wire : wireFactories) {
            assert(wire instanceof SystemOutboundWire): "wire must be an instance of " + SystemOutboundWire.class.getName();
            SystemOutboundWire systemWire = (SystemOutboundWire) wire;
            factories.add(new SystemWireObjectFactory(systemWire));
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


    public void prepare() {
        // override and do nothing since system services do not proxy
    }


}
