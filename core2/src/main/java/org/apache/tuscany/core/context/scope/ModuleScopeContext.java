package org.apache.tuscany.core.context.scope;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.core.context.event.InstanceCreated;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.InstanceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.event.Event;

/**
 * Manages instanceContexts whose implementations are module scoped. This scope instanceContexts eagerly
 * starts instanceContexts when a {@link org.apache.tuscany.core.context.event.ModuleStart} event is received.
 * If a contained context has an implementation marked to eagerly initialized, the an instance will be created
 * at that time as well. Contained instanceContexts are shutdown when a {@link
 * org.apache.tuscany.core.context.event.ModuleStop} event is received in reverse order to which their
 * implementation instances were created.
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public class ModuleScopeContext extends AbstractScopeContext<AtomicContext> {

    private Map<Context, InstanceContext> instanceContexts;

    // the queue of instanceContexts to destroy, in the order that their instances were created
    private Map<CompositeContext, List<AtomicContext>> registeredContexts;

    // the queue of instanceContexts to destroy, in the order that their instances were created
    private Map<CompositeContext, List<InstanceContext>> destroyQueues;

    public ModuleScopeContext(WorkContext workContext) {
        super("Module Scope", workContext);
        instanceContexts = new ConcurrentHashMap<Context, InstanceContext>();
        registeredContexts = new ConcurrentHashMap<CompositeContext, List<AtomicContext>>();
        destroyQueues = new ConcurrentHashMap<CompositeContext, List<InstanceContext>>();
    }

    public Scope getScope() {
        return Scope.MODULE;
    }


    public void onEvent(Event event) {
        checkInit();
        if (event instanceof ModuleStart) {
            lifecycleState = RUNNING;
            eagerInitContexts(((ModuleStart) event).getContext());
        } else if (event instanceof ModuleStop) {
            shutdownContexts(((ModuleStop) event).getContext());
        } else if (event instanceof InstanceCreated) {
            checkInit();
            // Queue the context to have its implementation instance released if destroyable
            List<InstanceContext> destroyQueue = destroyQueues.get(workContext.getCurrentModule());
            destroyQueue.add(((InstanceCreated) event).getContext());
        }
    }

    public synchronized void start() {
        if (lifecycleState != UNINITIALIZED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED state [" + lifecycleState + "]");
        }
    }

    public synchronized void stop() {
        checkInit();
        lifecycleState = STOPPED;
        //TODO implement stop semantics
    }


    /**
     * Notifies instanceContexts of a shutdown in reverse order to which they were started
     */
    private void shutdownContexts(CompositeContext ctx) {
        checkInit();
        List<InstanceContext> destroyQueue = destroyQueues.remove(ctx);
        if (destroyQueue == null || destroyQueue.size() == 0) {
            return;
        }
        synchronized (destroyQueue) {
            // shutdown destroyable instances in reverse instantiation order
            ListIterator<InstanceContext> iter = destroyQueue.listIterator(destroyQueue.size());
            while (iter.hasPrevious()) {
                InstanceContext context = iter.previous();
                if (context.getLifecycleState() == RUNNING) {
                    context.stop();
                }
            }
        }
    }

    private void eagerInitContexts(CompositeContext module) throws CoreRuntimeException {
        checkInit();
        assert(module != null): "Current module not set in work context";
        List<AtomicContext> contexts = registeredContexts.get(module);
        synchronized (contexts) {
            for (AtomicContext context : contexts) {
                if (context.isEagerInit()) {
                    InstanceContext instanceCtx = context.createInstance();
                    instanceContexts.put(context, instanceCtx);
                    instanceCtx.start();
                }
            }
        }
    }

    public void register(AtomicContext context) {
        checkInit();
        CompositeContext module = workContext.getCurrentModule();
        List<AtomicContext> atomicContexts = registeredContexts.get(module);
        List<InstanceContext> destroyQueue = destroyQueues.get(module);
        if (atomicContexts == null) {
            atomicContexts = registeredContexts.put(module, new ArrayList<AtomicContext>());
        }
        if (destroyQueue == null) {
            destroyQueues.put(module, new ArrayList<InstanceContext>());
        }
        synchronized (atomicContexts) {
            atomicContexts.add(context);
        }
        context.addListener(this);
    }


    public InstanceContext getInstanceContext(AtomicContext context) throws TargetException {
        checkInit();
        InstanceContext ctx = instanceContexts.get(context);
        if (ctx == null) {
            ctx = context.createInstance();
            instanceContexts.put(context, ctx);
        }
        return ctx;
    }


}
