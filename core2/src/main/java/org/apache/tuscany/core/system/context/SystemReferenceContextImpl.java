package org.apache.tuscany.core.system.context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.extension.ReferenceContextExtension;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * @version $Rev: 399991 $ $Date: 2006-05-04 23:44:07 -0700 (Thu, 04 May 2006) $
 */
public class SystemReferenceContextImpl<T> extends ReferenceContextExtension<T> implements SystemReferenceContext<T> {

    public SystemReferenceContextImpl(String name, Class<T> referenceInterface, CompositeContext parent) {
        super(name, parent);
        assert (referenceInterface != null) : "Reference interface was null";
        this.referenceInterface = referenceInterface;
    }

    public T getService() throws TargetException {
        return referenceInterface.cast(targetWire.getTargetService());
    }

    public InvocationHandler getHandler() throws TargetException {
        throw new UnsupportedOperationException();
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        throw new UnsupportedOperationException();
    }

}
