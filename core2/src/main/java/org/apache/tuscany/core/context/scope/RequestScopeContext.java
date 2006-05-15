package org.apache.tuscany.core.context.scope;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.core.context.event.RequestEnd;
import org.apache.tuscany.core.context.event.RequestStart;
import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.InstanceWrapper;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.event.Event;

/**
 * An implementation of a request-scoped component container.
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public class RequestScopeContext extends AbstractScopeContext<AtomicContext> {

    private final Map<AtomicContext, Map<Thread, InstanceWrapper>> contexts;
    private final Map<Thread, List<InstanceWrapper>> destroyQueues;

    public RequestScopeContext(){
        this(null);
    }

    public RequestScopeContext(WorkContext workContext) {
        super("Request Scope", workContext);
        contexts = new ConcurrentHashMap<AtomicContext, Map<Thread, InstanceWrapper>>();
        destroyQueues = new ConcurrentHashMap<Thread, List<InstanceWrapper>>();
    }

    public Scope getScope() {
        return Scope.REQUEST;
    }

    public void onEvent(Event event) {
        checkInit();
        if (event instanceof RequestStart) {
            for (Map.Entry<AtomicContext, Map<Thread, InstanceWrapper>> entry : contexts.entrySet()) {
                if (entry.getKey().isEagerInit()) {
                    getInstance(entry.getKey());
                }
            }
        } else if (event instanceof RequestEnd) {
            shutdownInstances(Thread.currentThread());
        }
    }

    public synchronized void start() {
        if (lifecycleState != UNINITIALIZED && lifecycleState != STOPPED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED or STOPPED state [" + lifecycleState + "]");
        }
        lifecycleState = RUNNING;
    }

    public synchronized void stop() {
        contexts.clear();
        synchronized(destroyQueues){
            destroyQueues.clear();
        }
        lifecycleState = STOPPED;
    }

    public void register(AtomicContext context) {
        contexts.put(context, new ConcurrentHashMap<Thread, InstanceWrapper>());
    }

    public InstanceWrapper getInstanceContext(AtomicContext context) throws TargetException {
        Map<Thread, InstanceWrapper> instanceContextMap = contexts.get(context);
        assert(instanceContextMap != null):"Atomic context not registered";
        InstanceWrapper ctx = instanceContextMap.get(Thread.currentThread());
        if (ctx == null) {
            ctx = context.createInstance();
            instanceContextMap.put(Thread.currentThread(), ctx);
            List<InstanceWrapper> destroyQueue = destroyQueues.get(Thread.currentThread());
            if (destroyQueue == null) {
                destroyQueue = new ArrayList<InstanceWrapper>();
                destroyQueues.put(Thread.currentThread(), destroyQueue);
            }
            synchronized (destroyQueue) {
                destroyQueue.add(ctx);
            }
        }
        return ctx;
    }

    private void shutdownInstances(Thread key) {
        List<InstanceWrapper> destroyQueue = destroyQueues.remove(key);
        if (destroyQueue != null && destroyQueue.size() > 0) {
            if (destroyQueue != null) {
                Thread thread = Thread.currentThread();
                for (Map<Thread, InstanceWrapper> map : contexts.values()) {
                    map.remove(thread);
                }
                ListIterator<InstanceWrapper> iter = destroyQueue.listIterator(destroyQueue.size());
                synchronized (destroyQueue) {
                    while (iter.hasPrevious()) {
                        try {
                            iter.previous().stop();
                        } catch (TargetException e) {
                            // TODO send a monitoring event
                        }
                    }
                }
            }

        }
    }

}
