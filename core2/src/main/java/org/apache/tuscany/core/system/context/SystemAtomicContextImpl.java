package org.apache.tuscany.core.system.context;

import java.lang.reflect.Method;
import java.lang.reflect.Member;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.core.context.PojoAtomicContext;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.model.Scope;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SystemAtomicContextImpl<T> extends PojoAtomicContext<T> implements SystemAtomicContext<T> {


    public SystemAtomicContextImpl(String name, Class<?> serviceInterface, ObjectFactory<?> objectFactory, boolean eagerInit, EventInvoker<Object> initInvoker,
                                   EventInvoker<Object> destroyInvoker, List<Injector> injectors, Map<String, Member> members) {
        super(name, serviceInterface, objectFactory, eagerInit, initInvoker, destroyInvoker, injectors, members);
        scope = Scope.MODULE;
    }

    public SystemAtomicContextImpl(String name, List<Class<?>> serviceInterfaces, ObjectFactory<?> objectFactory, boolean eagerInit, EventInvoker<Object> initInvoker,
                                   EventInvoker<Object> destroyInvoker, List<Injector> injectors,Map<String, Member> members) {
        super(name, serviceInterfaces, objectFactory, eagerInit, initInvoker, destroyInvoker, injectors, members);
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
        // overrid and do nothing since system services do not proxy
    }


}
