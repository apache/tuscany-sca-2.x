package org.apache.tuscany.core.injection;

import org.apache.tuscany.common.ObjectCreationException;
import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.spi.context.AtomicContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class TargetServiceResolver<T> implements ObjectFactory<T> {

    private AtomicContext<T> context;
    private String targetServiceName;

    public TargetServiceResolver(AtomicContext<T> context, String targetServiceName) {
        assert(context != null): "Context was null";
        assert(targetServiceName != null): "Target service name was null";
        this.context = context;
        this.targetServiceName = targetServiceName;
    }

    @SuppressWarnings("unchecked")
    public T getInstance() throws ObjectCreationException {
        return (T) context.getService(targetServiceName);
    }
}
