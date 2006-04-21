package org.apache.tuscany.binding.jsonrpc.mocks.tuscany;

import java.util.Map;

import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.impl.CompositeContextImpl;

public class MockCompositeContextImpl extends CompositeContextImpl {
    public Map<String, ScopeContext> getScopeIndex() {
        return scopeIndex;
    }
}
