package org.apache.tuscany.core.system.context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.context.AbstractContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.ProxyCreationException;
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
        try {
            return wire.createProxy();
        } catch (ProxyCreationException e) {
            throw new TargetException(e);
        }
//        if (cachedInstance == null) {
//            Context ctx = getParent().getContext(target.getPartName());
//            if ((ctx instanceof AtomicContext)) {
//                cachedInstance = (T) ((AtomicContext) ctx).getService(target.getPortName());
//            } else if ((ctx instanceof ReferenceContext)) {
//                cachedInstance = (T) ctx.getService();
//            } else if (ctx == null) {
//                TargetNotFoundException e = new TargetNotFoundException(name);
//                e.addContextName(getName());
//                throw e;
//            } else {
//                IllegalTargetException e = new IllegalTargetException("Reference target must be a component or reference context");
//                e.setIdentifier(name);
//                e.addContextName(getName());
//                throw e;
//            }
//        }
//        return cachedInstance;
    }


    public InvocationHandler getHandler() {
        return null;
    }

    public Class<T> getInterface() {
        return interfaze;
    }

    public SourceWire<T> getSourceWire() {
        throw new UnsupportedOperationException();
    }

    public void setSourceWire(SourceWire<T> wire) {
        throw new UnsupportedOperationException();
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        throw new UnsupportedOperationException();
    }


}
