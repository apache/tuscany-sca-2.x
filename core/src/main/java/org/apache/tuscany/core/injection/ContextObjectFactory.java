package org.apache.tuscany.core.injection;

import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.context.AggregateContext;

/**
 * Implementation of ObjectFactory that resolves the current context.
 * 
 * @version $Rev: 380903 $ $Date: 2006-02-25 00:53:26 -0800 (Sat, 25 Feb 2006) $
 */
public class ContextObjectFactory<T> implements ObjectFactory<AggregateContext> {
    
    private final ContextResolver resolver;

    public ContextObjectFactory(ContextResolver resolver) {
        assert (resolver != null) : "Resolver cannot be null";
        this.resolver = resolver;
    }

    public AggregateContext getInstance() {
        return resolver.getCurrentContext();
    }

}
