package org.apache.tuscany.spi.extension;

import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.TargetException;

/**
 * @version $$Rev$$ $$Date$$
 */
public abstract class AtomicContextExtension<T> extends ComponentContextExtension<T> implements AtomicContext<T> {

    protected ScopeContext<AtomicContext> scopeContext;
    protected Scope scope;

    public Scope getScope() {
        return scope;
    }

    public void setScopeContext(ScopeContext<AtomicContext> context) {
        scopeContext = context;
        context.register(this);
    }

    public boolean isEagerInit() {
        return false;
    }

    public void init(Object instance) throws TargetException {

    }

    public void destroy(Object instance) throws TargetException {

    }
}
