package org.apache.tuscany.core.context.scope;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.core.AbstractLifecycle;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.InstanceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.event.Event;

/**
 * Manages instanceContexts whose implementations are module scoped
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public class ModuleScopeContext extends AbstractScopeContext<AtomicContext> {

    private final Map<AtomicContext, InstanceContext> instanceContexts;
    // the queue of instanceContexts to destroy, in the order that their instances were created
    private final List<InstanceContext> destroyQueue;
    private static final InstanceContext EMPTY = new EmptyContext();

    public ModuleScopeContext(WorkContext workContext) {
        super("Module Scope", workContext);
        instanceContexts = new ConcurrentHashMap<AtomicContext, InstanceContext>();
        destroyQueue = new ArrayList<InstanceContext>();
    }

    public Scope getScope() {
        return Scope.MODULE;
    }


    public void onEvent(Event event) {
        checkInit();
        if (event instanceof ModuleStart) {
            eagerInitContexts();
            lifecycleState = RUNNING;
        } else if (event instanceof ModuleStop) {
            shutdownContexts();
        }
    }

    public synchronized void start() {
        if (lifecycleState != UNINITIALIZED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED state [" + lifecycleState + "]");
        }
        lifecycleState = RUNNING;
    }

    public synchronized void stop() {
        checkInit();
        lifecycleState = STOPPED;
        //TODO implement stop semantics
    }


    /**
     * Notifies instanceContexts of a shutdown in reverse order to which they were started
     */
    private void shutdownContexts() {
        if (destroyQueue.size() == 0) {
            return;
        }
        synchronized (destroyQueue) {
            // shutdown destroyable instances in reverse instantiation order
            ListIterator<InstanceContext> iter = destroyQueue.listIterator(destroyQueue.size());
            while (iter.hasPrevious()) {
                iter.previous().stop();
            }
            destroyQueue.clear();
        }
    }

    public void register(AtomicContext context) {
        checkInit();
        instanceContexts.put(context, EMPTY);
    }


    public InstanceContext getInstanceContext(AtomicContext context) throws TargetException {
        checkInit();
        InstanceContext ctx = instanceContexts.get(context);
        if (ctx == EMPTY) {
            ctx = context.createInstance();
            instanceContexts.put(context, ctx);
            synchronized (destroyQueue) {
                destroyQueue.add(ctx);
            }
        }
        return ctx;
    }

    private void eagerInitContexts() throws CoreRuntimeException {
        for (Map.Entry<AtomicContext, InstanceContext> entry : instanceContexts.entrySet()) {
            AtomicContext context = entry.getKey();
            if (context.isEagerInit()) {
                InstanceContext instanceCtx = context.createInstance();
                instanceContexts.put(context, instanceCtx);
                destroyQueue.add(instanceCtx);
            }
        }

    }

    private static class EmptyContext extends AbstractLifecycle implements InstanceContext {
        public Object getInstance() {
            return null;
        }
    }

}
