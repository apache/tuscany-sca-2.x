package org.apache.tuscany.core.system.context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.tuscany.spi.context.AbstractContext;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.ReferenceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * The default implementation of an external service context
 *
 * @version $Rev: 399991 $ $Date: 2006-05-04 23:44:07 -0700 (Thu, 04 May 2006) $
 */
public class AutowireSystemReferenceContext<T> extends AbstractContext<T> implements ReferenceContext<T> {

    private Class<T> referenceInterface;
    private AutowireContext<?> autowireContext;

    public AutowireSystemReferenceContext(String name, Class<T> referenceInterface, CompositeContext parent) {
        super(name);
        assert (referenceInterface != null) : "Reference interface was null";
        assert (parent instanceof AutowireContext) : "Parent must implement " + AutowireContext.class.getName();
        this.referenceInterface = referenceInterface;
        autowireContext = (AutowireContext) parent;
        setParent(parent);
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        return null;
    }

    public T getService() throws TargetException {
        return autowireContext.resolveInstance(referenceInterface);
    }

    public InvocationHandler getHandler() throws TargetException {
        throw new UnsupportedOperationException();
    }

    public Class<T> getInterface() {
        return referenceInterface;
    }

    public TargetWire getTargetWire() {
        throw new UnsupportedOperationException();
    }


}
