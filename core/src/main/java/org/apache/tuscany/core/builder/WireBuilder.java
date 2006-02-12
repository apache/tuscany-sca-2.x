package org.apache.tuscany.core.builder;

import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;

public interface WireBuilder {

    public void wire(ProxyFactory sourceFactory, ProxyFactory targetFactory, Class targetType, boolean downScope,
            ScopeContext targetScopeContext) throws BuilderConfigException;

}
