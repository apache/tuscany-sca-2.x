package org.apache.tuscany.core.system.context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.context.AbstractContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SystemServiceContextImpl<T> extends AbstractContext<T> implements SystemServiceContext<T> {

    private Class<T> interfaze;
    private SourceWire<T> wire;

    public SystemServiceContextImpl(String name, Class<T> interfaze, SourceWire<T> wire, CompositeContext parent) throws CoreRuntimeException {
        super(name);
        this.interfaze = interfaze;
        this.parentContext = parent;
        this.wire = wire;
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    @SuppressWarnings("unchecked")
    public T getService() {
        return wire.getTargetService();
    }


    public InvocationHandler getHandler() {
        return null;
    }

    public Class<T> getInterface() {
        return interfaze;
    }

    public SourceWire<T> getSourceWire() {
        return wire;
    }

    public void setSourceWire(SourceWire<T> wire) {
        this.wire = wire;
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        throw new UnsupportedOperationException();
    }


}
