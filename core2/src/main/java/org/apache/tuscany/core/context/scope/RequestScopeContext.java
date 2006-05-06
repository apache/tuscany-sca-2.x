package org.apache.tuscany.core.context.scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.core.context.event.RequestEnd;
import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.InstanceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.event.Event;

/**
 * An implementation of a request-scoped component container.
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public class RequestScopeContext extends AbstractScopeContext<AtomicContext> {

    private final Map<AtomicContext, Map<Thread, InstanceContext>> contexts;
    private final Map<Thread, List<InstanceContext>> destroyQueues;
    //flip Thread, Map<AtomicContext,InstanceContext>

    public RequestScopeContext(WorkContext workContext) {
        super("Request Scope", workContext);
        contexts = new ConcurrentHashMap<AtomicContext, Map<Thread, InstanceContext>>();
        destroyQueues = new ConcurrentHashMap<Thread, List<InstanceContext>>();
    }

    public Scope getScope() {
        return Scope.REQUEST;
    }

    public void onEvent(Event event) {
        checkInit();
        if (event instanceof RequestEnd) {
            checkInit();
            shutdownInstances(Thread.currentThread());
        }
    }

    public synchronized void start() {
        if (lifecycleState != UNINITIALIZED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED state [" + lifecycleState + "]");
        }
        lifecycleState = RUNNING;
    }

    public synchronized void stop() {
        //TODO stop semantics
        lifecycleState = STOPPED;
    }

    public void register(AtomicContext context) {
        contexts.put(context, new ConcurrentHashMap<Thread, InstanceContext>());
    }

    public InstanceContext getInstanceContext(AtomicContext context) throws TargetException {
        Map<Thread, InstanceContext> instanceContextMap = contexts.get(context);
        InstanceContext ctx = instanceContextMap.get(Thread.currentThread());
        if (ctx == null) {
            ctx = context.createInstance();
            instanceContextMap.put(Thread.currentThread(), ctx);
            List<InstanceContext> destroyQueue = destroyQueues.get(Thread.currentThread());
            if (destroyQueue == null) {
                destroyQueue = new ArrayList<InstanceContext>();
                destroyQueues.put(Thread.currentThread(), destroyQueue);
            }
            synchronized (destroyQueue) {
                destroyQueue.add(ctx);
            }
        }
        return ctx;
    }

    private void shutdownInstances(Thread key) {
        List<InstanceContext> destroyQueue = destroyQueues.remove(key);
        if (destroyQueue != null && destroyQueue.size() > 0) {
            synchronized (destroyQueue) {
                Thread thread = Thread.currentThread();
                for (Map<Thread, InstanceContext> map : contexts.values()) {
                    map.remove(thread);
                }
                for (InstanceContext ctx : destroyQueue) {
                    ctx.stop();
                }
            }
        }
    }

}
