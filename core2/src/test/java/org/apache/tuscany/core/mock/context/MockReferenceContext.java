package org.apache.tuscany.core.mock.context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.spi.context.AbstractContext;
import org.apache.tuscany.spi.context.ReferenceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.model.Scope;

/**
 * A mock reference context which returns a singleton
 *
 * @version $Rev: 399991 $ $Date: 2006-05-04 23:44:07 -0700 (Thu, 04 May 2006) $
 */
public class MockReferenceContext<T> extends AbstractContext<T> implements ReferenceContext<T> {

    private Class<T> referenceInterface;
    private T instance;

    public MockReferenceContext(String name, Class<T> referenceInterface, T instance) {
        super(name);
        assert (referenceInterface != null) : "Reference interface was null";
        this.referenceInterface = referenceInterface;
        this.instance = instance;
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        throw new UnsupportedOperationException();
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    public T getService() throws TargetException {
        return instance;
    }

    public InvocationHandler getHandler() throws TargetException {
        throw new UnsupportedOperationException();
    }

    public TargetWire<T> getTargetWire() {
        throw new UnsupportedOperationException();
    }

    public Class<T> getInterface() {
        return referenceInterface;
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
}
