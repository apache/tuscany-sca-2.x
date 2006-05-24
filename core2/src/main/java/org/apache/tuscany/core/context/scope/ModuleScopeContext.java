package org.apache.tuscany.core.context.scope;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.spi.AbstractLifecycle;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.InstanceWrapper;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.event.Event;

/**
 * Manages instanceContexts whose implementations are module scoped
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public class ModuleScopeContext extends AbstractScopeContext {

    private final Map<AtomicContext, InstanceWrapper> instanceContexts;
    // the queue of instanceContexts to destroy, in the order that their instances were created
    private final List<InstanceWrapper> destroyQueue;
    private static final InstanceWrapper EMPTY = new EmptyWrapper();

    public ModuleScopeContext(){
        this(null);
    }

    public ModuleScopeContext(WorkContext workContext) {
        super("Module Scope", workContext);
        instanceContexts = new ConcurrentHashMap<AtomicContext, InstanceWrapper>();
        destroyQueue = new ArrayList<InstanceWrapper>();
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
        if (lifecycleState != UNINITIALIZED && lifecycleState != STOPPED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED or STOPPED state [" + lifecycleState + "]");
        }
        lifecycleState = RUNNING;
    }

    public synchronized void stop() {
        checkInit();
        instanceContexts.clear();
        synchronized (destroyQueue){
            destroyQueue.clear();
        }
        lifecycleState = STOPPED;
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
            ListIterator<InstanceWrapper> iter = destroyQueue.listIterator(destroyQueue.size());
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


    public InstanceWrapper getInstanceContext(AtomicContext context) throws TargetException {
        checkInit();
        InstanceWrapper ctx = instanceContexts.get(context);
        assert ctx != null : "Context not registered with scope: " + context;
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
        for (Map.Entry<AtomicContext, InstanceWrapper> entry : instanceContexts.entrySet()) {
            AtomicContext context = entry.getKey();
            if (context.isEagerInit()) {
                InstanceWrapper instanceCtx = context.createInstance();
                instanceContexts.put(context, instanceCtx);
                destroyQueue.add(instanceCtx);
            }
        }

    }

    private static class EmptyWrapper extends AbstractLifecycle implements InstanceWrapper {
        public Object getInstance() {
            return null;
        }
    }

}
