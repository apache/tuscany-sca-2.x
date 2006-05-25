package org.apache.tuscany.spi.extension;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.model.Scope;

/**
 * An extension point for atomic contexts. When adding support for new component types, implementations may
 * extend this class as a convenience.
 *
 * @version $$Rev$$ $$Date$$
 */
public abstract class AtomicContextExtension<T> extends ComponentContextExtension<T> implements AtomicContext<T> {

    protected ScopeContext scopeContext;
    protected Scope scope;

    protected AtomicContextExtension(String name, CompositeContext<?> parent, ScopeContext scopeContext) {
        super(name, parent);
        this.scopeContext = scopeContext;
    }

    public Scope getScope() {
        return scope;
    }

    public boolean isEagerInit() {
        return false;
    }

    public void start() throws CoreRuntimeException {
        super.start();
        scopeContext.register(this);
    }

    public void init(Object instance) throws TargetException {

    }

    public void destroy(Object instance) throws TargetException {

    }
}
