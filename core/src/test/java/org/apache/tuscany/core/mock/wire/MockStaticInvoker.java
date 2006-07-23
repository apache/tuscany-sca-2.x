package org.apache.tuscany.core.mock.wire;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Caches component instances that do not need to be resolved for every wire, e.g. an wire originating from a lesser
 * scope intended for a target with a wider scope
 *
 * @version $Rev$ $Date$
 */
public class MockStaticInvoker implements TargetInvoker {

    private Object instance;
    private Method operation;
    private boolean cacheable;


    public MockStaticInvoker(Method operation, Object instance) {
        this.operation = operation;
        this.instance = instance;
    }

    public boolean isCacheable() {
        return cacheable;
    }

    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    public boolean isOptimizable() {
        return isCacheable();
    }

    public Object invokeTarget(final Object payload) throws InvocationTargetException {
        try {
            if (payload != null && !payload.getClass().isArray()) {
                return operation.invoke(instance, payload);
            } else {
                return operation.invoke(instance, (Object[]) payload);
            }
        } catch (IllegalAccessException e) {
            throw new InvocationRuntimeException(e);
        }
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            Object resp = invokeTarget(msg.getBody());
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setBody(e.getCause());
        } catch (Throwable e) {
            msg.setBody(e);
        }
        return msg;
    }

    public void setNext(Interceptor next) {
        throw new IllegalStateException("This interceptor must be the last interceptor in an interceptor chain");
    }

    public Object clone() throws CloneNotSupportedException {
        try {
            MockStaticInvoker invoker = (MockStaticInvoker) super.clone();
            invoker.instance = this.instance;
            invoker.operation = this.operation;
            return invoker;
        } catch (CloneNotSupportedException e) {
            return null; // will not happen
        }
    }
}
