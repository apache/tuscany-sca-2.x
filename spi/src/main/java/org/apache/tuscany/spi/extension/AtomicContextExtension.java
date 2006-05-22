package org.apache.tuscany.spi.extension;

import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.context.CompositeContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public abstract class AtomicContextExtension<T> extends ComponentContextExtension<T> implements AtomicContext<T> {

    protected ScopeContext scopeContext;
    protected Scope scope;

    protected AtomicContextExtension(String name, CompositeContext<?> parent) {
        super(name, parent);
    }

    public Scope getScope() {
        return scope;
    }

    public void setScopeContext(ScopeContext context) {
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
