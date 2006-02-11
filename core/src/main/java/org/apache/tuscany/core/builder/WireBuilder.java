package org.apache.tuscany.core.builder;

import org.apache.tuscany.core.context.ScopeContext;

public interface WireBuilder {

    public void wire(RuntimeConfiguration source, RuntimeConfiguration target, ScopeContext targetScopeContext)
            throws BuilderConfigException;

}
