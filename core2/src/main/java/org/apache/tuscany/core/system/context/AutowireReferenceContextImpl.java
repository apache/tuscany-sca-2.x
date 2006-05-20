package org.apache.tuscany.core.system.context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.extension.ReferenceContextExtension;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.model.Scope;

/**
 * @version $Rev: 399991 $ $Date: 2006-05-04 23:44:07 -0700 (Thu, 04 May 2006) $
 */
public class AutowireReferenceContextImpl<T> extends ReferenceContextExtension<T> implements SystemReferenceContext<T> {

    private AutowireContext<?> autowireContext;

    public AutowireReferenceContextImpl(String name, Class<T> referenceInterface, CompositeContext parent) {
        assert (referenceInterface != null) : "Reference interface was null";
        assert (parent instanceof AutowireContext) : "Parent must implement " + AutowireContext.class.getName();
        this.name = name;
        this.referenceInterface = referenceInterface;
        autowireContext = (AutowireContext) parent;
        setParent(parent);
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        throw new UnsupportedOperationException();
    }

    public T getService() throws TargetException {
        return autowireContext.resolveInstance(referenceInterface);
    }

    public InvocationHandler getHandler() throws TargetException {
        throw new UnsupportedOperationException();
    }


}
