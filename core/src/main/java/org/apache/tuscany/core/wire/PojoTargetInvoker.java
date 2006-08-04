package org.apache.tuscany.core.wire;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;

import static org.apache.tuscany.core.util.JavaIntrospectionHelper.findClosestMatchingMethod;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.getAllUniquePublicProtectedMethods;

/**
 * Base class for dispatching to a Java based component implementation. Subclasses implement a strategy for resolving
 * implementation instances.
 *
 * @version $Rev$ $Date$
 */
public abstract class PojoTargetInvoker implements TargetInvoker {

    protected Method operation;
    protected boolean cacheable;

    public PojoTargetInvoker(Method operation) {
        assert operation != null : "Operation method cannot be null";
        this.operation = operation;
    }

    public Object invokeTarget(final Object payload) throws InvocationTargetException {
        try {
            Object instance = getInstance();
            if (!operation.getDeclaringClass().isInstance(instance)) {
                Set<Method> methods = getAllUniquePublicProtectedMethods(instance.getClass());
                Method newOperation = findClosestMatchingMethod(operation.getName(),
                    operation.getParameterTypes(), methods);
                if (newOperation != null) {
                    operation = newOperation;
                }
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

    protected abstract Object getInstance() throws TargetException;

}
