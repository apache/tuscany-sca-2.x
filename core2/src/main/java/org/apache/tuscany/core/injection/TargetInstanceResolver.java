package org.apache.tuscany.core.injection;

import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.common.ObjectCreationException;
import org.apache.tuscany.spi.context.Context;

/**
 * @version $$Rev$$ $$Date$$
 */
public class TargetInstanceResolver<T> implements ObjectFactory<T> {

    private Context<T> context;

    public TargetInstanceResolver(Context<T> context) {
        assert(context != null): "Context was null";
        this.context = context;
    }

    public T getInstance() throws ObjectCreationException {
        return context.getService();
    }
}
