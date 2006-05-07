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
import org.apache.tuscany.spi.context.InstanceContext;
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

    private final Map<AtomicContext, Map<Object, InstanceContext>> contexts;
    private final Map<Object, List<InstanceContext>> destroyQueues;

    public HttpSessionScopeContext(WorkContext workContext) {
        super("Session Scope", workContext);
        contexts = new ConcurrentHashMap<AtomicContext, Map<Object, InstanceContext>>();
        destroyQueues = new ConcurrentHashMap<Object, List<InstanceContext>>();
    }

    public Scope getScope() {
        return Scope.SESSION;
    }

    public void onEvent(Event event) {
        checkInit();
        if (event instanceof HttpSessionStart) {
            Object key = ((HttpSessionStart) event).getId();
            for (Map.Entry<AtomicContext, Map<Object, InstanceContext>> entry : contexts.entrySet()) {
                if(entry.getKey().isEagerInit()){
                    getInstance(entry.getKey(),key);
                }
            }
        } else if (event instanceof HttpSessionEnd) {
            shutdownInstances(((HttpSessionEnd) event).getId());
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
        contexts.put(context, new ConcurrentHashMap<Object, InstanceContext>());
        context.addListener(this);

    }

    public InstanceContext getInstanceContext(AtomicContext context) throws TargetException {
        Object key = workContext.getIdentifier(HTTP_IDENTIFIER);
        assert(key != null):"HTTP session key not bound in work context";
        return getInstance(context, key);
    }

    private InstanceContext getInstance(AtomicContext context, Object key) {
        Map<Object, InstanceContext> contextMap = contexts.get(context);
        InstanceContext ctx = contextMap.get(key);
        if (ctx == null) {
            ctx = context.createInstance();
            contextMap.put(key, ctx);
            List<InstanceContext> destroyQueue = destroyQueues.get(key);
            if (destroyQueue == null) {
                destroyQueue = new ArrayList<InstanceContext>();
                destroyQueues.put(key, destroyQueue);
            }
            synchronized (destroyQueue) {
                destroyQueue.add(ctx);
            }
        }
        return ctx;

    }

    private void shutdownInstances(Object key) {
        List<InstanceContext> destroyQueue = destroyQueues.remove(key);
        if (destroyQueue != null) {
            for (Map<Object, InstanceContext> map : contexts.values()) {
                map.remove(key);
            }
            ListIterator<InstanceContext> iter = destroyQueue.listIterator(destroyQueue.size());
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
