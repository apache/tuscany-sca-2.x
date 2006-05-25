package org.apache.tuscany.core.system.context;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.context.PojoAtomicContext;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Default implementation of a system atomic context
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemAtomicContextImpl<T> extends PojoAtomicContext<T> implements SystemAtomicContext<T> {


    public SystemAtomicContextImpl(String name,
                                   CompositeContext<?> parent,
                                   ScopeContext scopeContext,
                                   Class<?> serviceInterface,
                                   ObjectFactory<?> objectFactory,
                                   boolean eagerInit,
                                   EventInvoker<Object> initInvoker,
                                   EventInvoker<Object> destroyInvoker,
                                   List<Injector> injectors,
                                   Map<String, Member> members) {
        super(name, parent, scopeContext, serviceInterface, objectFactory, eagerInit, initInvoker, destroyInvoker, injectors, members);
        scope = Scope.MODULE;
    }

    public SystemAtomicContextImpl(String name,
                                   CompositeContext<?> parent,
                                   ScopeContext scopeContext,
                                   List<Class<?>> serviceInterfaces,
                                   ObjectFactory<?> objectFactory,
                                   boolean eagerInit,
                                   EventInvoker<Object> initInvoker,
                                   EventInvoker<Object> destroyInvoker,
                                   List<Injector> injectors,
                                   Map<String, Member> members) {
        super(name, parent, scopeContext, serviceInterfaces, objectFactory, eagerInit, initInvoker, destroyInvoker, injectors, members);
        scope = Scope.MODULE;
    }

    @SuppressWarnings("unchecked")
    public T getTargetInstance() throws TargetException {
        return (T) scopeContext.getInstance(this);
    }

    public Object getService(String name) throws TargetException {
        return getTargetInstance();
    }

    public T getService() throws TargetException {
        return getTargetInstance();
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        throw new UnsupportedOperationException();
    }

    public void prepare() {
        // override and do nothing since system services do not proxy
    }


}
