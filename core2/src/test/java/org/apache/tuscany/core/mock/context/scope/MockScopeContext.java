package org.apache.tuscany.core.mock.context.scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.core.AbstractLifecycle;
import org.apache.tuscany.core.context.scope.AbstractScopeContext;
import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.InstanceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.event.Event;

/**
 * @version $$Rev$$ $$Date$$
 */
public class MockScopeContext extends AbstractScopeContext<AtomicContext> {

    private Map<Context, InstanceContext> instanceContexts;
    private Scope scope;
    private static final InstanceContext EMPTY = new EmptyContext();

    public MockScopeContext() {
        this(null);
    }

    public MockScopeContext(Scope scope) {
        super("Module Scope", null);
        instanceContexts = new ConcurrentHashMap<Context, InstanceContext>();
        this.scope = scope;
    }

    public Scope getScope() {
        return scope;
    }

    public InstanceContext getInstanceContext(AtomicContext context) throws TargetException {
        InstanceContext ctx = instanceContexts.get(context);
        if(ctx == EMPTY){
            ctx = context.createInstance();
            instanceContexts.put(context,ctx);
        }
        return ctx;
    }

    public void onEvent(Event event) {

    }

    public void register(AtomicContext context) {
        instanceContexts.put(context, EMPTY);
    }


    private static class EmptyContext extends AbstractLifecycle implements InstanceContext {
        public Object getInstance() {
            return null;
        }
    }
}
