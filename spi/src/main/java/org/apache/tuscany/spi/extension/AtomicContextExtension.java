package org.apache.tuscany.spi.extension;

import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.ScopeContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public abstract class AtomicContextExtension<T> extends ComponentContextExtension<T> implements AtomicContext<T> {

    protected ScopeContext scopeContext;

    public Scope getScope() {
        if (scopeContext != null) {
            return scopeContext.getScope();
        } else {
            return null;
        }
    }

    public void setScopeContext(ScopeContext context) {
        scopeContext = context;
    }

}
