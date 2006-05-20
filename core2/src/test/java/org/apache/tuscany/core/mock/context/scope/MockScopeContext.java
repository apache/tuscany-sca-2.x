package org.apache.tuscany.core.mock.context.scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.spi.AbstractLifecycle;
import org.apache.tuscany.core.context.scope.AbstractScopeContext;
import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.InstanceWrapper;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.event.Event;

/**
 * @version $$Rev$$ $$Date$$
 */
public class MockScopeContext extends AbstractScopeContext {

    private Map<Context, InstanceWrapper> instanceContexts;
    private Scope scope;
    private static final InstanceWrapper EMPTY = new EmptyWrapper();

    public MockScopeContext() {
        this(null);
    }

    public MockScopeContext(Scope scope) {
        super("Module Scope", null);
        instanceContexts = new ConcurrentHashMap<Context, InstanceWrapper>();
        this.scope = scope;
    }

    public Scope getScope() {
        return scope;
    }

    public InstanceWrapper getInstanceContext(AtomicContext context) throws TargetException {
        InstanceWrapper ctx = instanceContexts.get(context);
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


    private static class EmptyWrapper extends AbstractLifecycle implements InstanceWrapper {
        public Object getInstance() {
            return null;
        }
    }
}
