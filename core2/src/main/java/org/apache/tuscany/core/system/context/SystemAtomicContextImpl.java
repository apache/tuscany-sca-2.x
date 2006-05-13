package org.apache.tuscany.core.system.context;

import java.util.List;
import java.util.Map;
import java.lang.reflect.Method;

import org.apache.tuscany.common.ObjectCreationException;
import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.core.context.PojoAtomicContext;
import org.apache.tuscany.core.context.PojoInstanceWrapper;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.spi.context.InstanceWrapper;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SystemAtomicContextImpl<T> extends PojoAtomicContext<T> implements SystemAtomicContext<T> {

    public SystemAtomicContextImpl(String name, Class<?> serviceInterface, ObjectFactory<?> objectFactory) {
        super(name, serviceInterface, objectFactory, false, null, null);
    }

    public SystemAtomicContextImpl(String name, Class<?> serviceInterface, ObjectFactory<?> objectFactory, boolean eagerInit, EventInvoker<Object> initInvoker,
                                   EventInvoker<Object> destroyInvoker) {
        super(name, serviceInterface, objectFactory, eagerInit, initInvoker, destroyInvoker);
    }

    public SystemAtomicContextImpl(String name, List<Class<?>> serviceInterfaces, ObjectFactory<?> objectFactory, boolean eagerInit, EventInvoker<Object> initInvoker,
                                   EventInvoker<Object> destroyInvoker) {
        super(name, serviceInterfaces, objectFactory, eagerInit, initInvoker, destroyInvoker);
    }

    @SuppressWarnings("unchecked")
    public T getTargetInstance() throws TargetException {
        return (T)scopeContext.getInstance(this);
    }

    public InstanceWrapper createInstance() throws ObjectCreationException {
        InstanceWrapper ctx = new PojoInstanceWrapper(this, objectFactory.getInstance());
        ctx.start();
        return ctx;
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

    public TargetInvoker createTargetInvoker(String serviceName, Method operation){
        throw new UnsupportedOperationException();
    }

}
