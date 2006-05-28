package org.apache.tuscany.core.wire.jdk;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;

/**
 * Base class for dispatching to a Java based component implementation. Subclasses implement a strategy for
 * resolving implementation instances.
 *
 * @version $Rev: 408473 $ $Date: 2006-05-21 12:46:01 -0700 (Sun, 21 May 2006) $
 */
public abstract class PojoTargetInvoker implements TargetInvoker {

    protected Method operation;
    protected boolean cacheable;

    public PojoTargetInvoker(Method operation) {
        assert (operation != null) : "Operation method cannot be null";
        this.operation = operation;
    }

    public Object invokeTarget(Object payload) throws InvocationTargetException {
        try {
            Object instance = getInstance();
            if (!operation.getDeclaringClass().isInstance(instance)) {
                Set<Method> methods = JavaIntrospectionHelper.getAllUniqueMethods(instance.getClass());
                Method newOperation = JavaIntrospectionHelper.findClosestMatchingMethod(operation.getName(), operation
                        .getParameterTypes(), methods);
                if (newOperation != null)
                    operation = newOperation;
            }
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

    protected abstract Object getInstance() throws TargetException;

    public void setNext(Interceptor next) {
        throw new IllegalStateException("This interceptor must be the last interceptor in an interceptor chain");
    }

    public boolean isCacheable() {
        return cacheable;
    }

    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    public boolean isOptimizable() {
        return isCacheable(); // we only need to check if the scopes are correct
    }

    @Override
    public PojoTargetInvoker clone() throws CloneNotSupportedException {
        try {
            PojoTargetInvoker clone = (PojoTargetInvoker) super.clone();
            clone.operation = this.operation;
            return clone;
        } catch (CloneNotSupportedException e) {
            return null; // will not happen
        }
    }
}
