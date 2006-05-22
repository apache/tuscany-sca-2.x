package org.apache.tuscany.core.mock.context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.tuscany.core.system.context.SystemReferenceContext;
import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.context.AbstractContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * A mock reference context which returns a singleton
 *
 * @version $Rev: 399991 $ $Date: 2006-05-04 23:44:07 -0700 (Thu, 04 May 2006) $
 */
public class MockReferenceContext<T> extends AbstractContext<T> implements SystemReferenceContext<T> {

    private Class<T> referenceInterface;
    private TargetWire<T> wire;

    public MockReferenceContext(String name, CompositeContext<?> parent, Class<T> referenceInterface) {
        super(name, parent);
        assert (referenceInterface != null) : "Reference interface was null";
        this.referenceInterface = referenceInterface;
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        throw new UnsupportedOperationException();
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    public T getService() throws TargetException {
        return wire.getTargetService();
    }

    public InvocationHandler getHandler() throws TargetException {
        throw new UnsupportedOperationException();
    }

    public TargetWire<T> getTargetWire() {
        return wire;
    }

    public Class<T> getInterface() {
        return referenceInterface;
    }

    public void setTargetWire(TargetWire<T> wire) {
        this.wire = wire;
    }


}
