package org.apache.tuscany.core.context.scope;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.core.context.event.HttpSessionEnd;
import org.apache.tuscany.core.context.event.HttpSessionStart;
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
public class HttpSessionScopeContext extends AbstractScopeContext<AtomicContext> {

    public static final Object HTTP_IDENTIFIER = new Object();

    private final Map<AtomicContext, Map<Object, InstanceWrapper>> contexts;
    private final Map<Object, List<InstanceWrapper>> destroyQueues;

    public HttpSessionScopeContext(){
        this(null);
    }

    public HttpSessionScopeContext(WorkContext workContext) {
        super("Session Scope", workContext);
        contexts = new ConcurrentHashMap<AtomicContext, Map<Object, InstanceWrapper>>();
        destroyQueues = new ConcurrentHashMap<Object, List<InstanceWrapper>>();
    }

    public Scope getScope() {
        return Scope.SESSION;
    }

    public void onEvent(Event event) {
        checkInit();
        if (event instanceof HttpSessionStart) {
            Object key = ((HttpSessionStart) event).getId();
            for (Map.Entry<AtomicContext, Map<Object, InstanceWrapper>> entry : contexts.entrySet()) {
                if(entry.getKey().isEagerInit()){
                    getInstance(entry.getKey(),key);
                }
            }
        } else if (event instanceof HttpSessionEnd) {
            shutdownInstances(((HttpSessionEnd) event).getId());
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
        contexts.put(context, new ConcurrentHashMap<Object, InstanceWrapper>());
        context.addListener(this);

    }

    public InstanceWrapper getInstanceContext(AtomicContext context) throws TargetException {
        Object key = workContext.getIdentifier(HTTP_IDENTIFIER);
        assert(key != null):"HTTP session key not bound in work context";
        return getInstance(context, key);
    }

    private InstanceWrapper getInstance(AtomicContext context, Object key) {
        Map<Object, InstanceWrapper> wrappers = contexts.get(context);
        InstanceWrapper ctx = wrappers.get(key);
        if (ctx == null) {
            ctx = context.createInstance();
            wrappers.put(key, ctx);
            List<InstanceWrapper> destroyQueue = destroyQueues.get(key);
            if (destroyQueue == null) {
                destroyQueue = new ArrayList<InstanceWrapper>();
                destroyQueues.put(key, destroyQueue);
            }
            synchronized (destroyQueue) {
                destroyQueue.add(ctx);
            }
        }
        return ctx;

    }

    private void shutdownInstances(Object key) {
        List<InstanceWrapper> destroyQueue = destroyQueues.remove(key);
        if (destroyQueue != null) {
            for (Map<Object, InstanceWrapper> map : contexts.values()) {
                map.remove(key);
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
