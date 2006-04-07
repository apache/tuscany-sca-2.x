package org.apache.tuscany.core.injection;

import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.context.CompositeContext;

/**
 * An implementation of <code>ObjectFactory</code> that resolves the current context.
 * 
 * @version $Rev: 380903 $ $Date: 2006-02-25 00:53:26 -0800 (Sat, 25 Feb 2006) $
 */
public class ContextObjectFactory implements ObjectFactory<CompositeContext> {
    
    private final ContextResolver resolver;

    public ContextObjectFactory(ContextResolver resolver) {
        assert (resolver != null) : "Resolver cannot be null";
        this.resolver = resolver;
    }

    public CompositeContext getInstance() {
        return resolver.getCurrentContext();
    }

}
