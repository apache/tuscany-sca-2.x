package org.apache.tuscany.core.system.context;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.core.context.PojoAtomicContext;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SystemAtomicContextImpl<T> extends PojoAtomicContext<T> implements SystemAtomicContext<T> {

    public SystemAtomicContextImpl(String name, Class<?> serviceInterface, ObjectFactory<?> objectFactory, List<Injector> injectors) {
        super(name, serviceInterface, objectFactory, false, null, null, injectors);
    }

    public SystemAtomicContextImpl(String name, Class<?> serviceInterface, ObjectFactory<?> objectFactory, boolean eagerInit, EventInvoker<Object> initInvoker,
                                   EventInvoker<Object> destroyInvoker, List<Injector> injectors) {
        super(name, serviceInterface, objectFactory, eagerInit, initInvoker, destroyInvoker, injectors);
    }

    public SystemAtomicContextImpl(String name, List<Class<?>> serviceInterfaces, ObjectFactory<?> objectFactory, boolean eagerInit, EventInvoker<Object> initInvoker,
                                   EventInvoker<Object> destroyInvoker, List<Injector> injectors) {
        super(name, serviceInterfaces, objectFactory, eagerInit, initInvoker, destroyInvoker, injectors);
    }

    @SuppressWarnings("unchecked")
    public T getTargetInstance() throws TargetException {
        return (T) scopeContext.getInstance(this);
    }

    public void prepare() {
    }

    public Object getService(String name) throws TargetException {
        return getTargetInstance();
    }

    public T getService() throws TargetException {
        return getTargetInstance();
    }

    public void addTargetWire(TargetWire wire) {
        throw new UnsupportedOperationException();
    }

    public TargetWire getTargetWire(String serviceName) {
        throw new UnsupportedOperationException();
    }

    public Map<String, TargetWire> getTargetWires() {
        throw new UnsupportedOperationException();
    }

    public void addSourceWire(SourceWire wire) {
        throw new UnsupportedOperationException();
    }

    public void addSourceWires(Class<?> multiplicityClass, List<SourceWire> wires) {
        throw new UnsupportedOperationException();
    }

    public List<SourceWire> getSourceWires() {
        throw new UnsupportedOperationException();
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        throw new UnsupportedOperationException();
    }

}
