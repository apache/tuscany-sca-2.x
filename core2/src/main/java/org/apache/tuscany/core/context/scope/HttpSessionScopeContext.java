package org.apache.tuscany.core.context.scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.core.context.event.HttpSessionEnd;
import org.apache.tuscany.core.context.event.InstanceCreated;
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

    private Map<AtomicContext, Map<Object, InstanceContext>> contexts;
    private Map<Object, List<InstanceContext>> destroyQueues;

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
        if (event instanceof HttpSessionEnd) {
            checkInit();
            shutdownInstances(((HttpSessionEnd)event).getId());
        } else if (event instanceof InstanceCreated) {
            checkInit();
            InstanceContext context = ((InstanceCreated) event).getContext();
            Object key = workContext.getIdentifier(HTTP_IDENTIFIER);
            List<InstanceContext> destroyQueue = destroyQueues.get(key);
            if (destroyQueue == null) {
                destroyQueue = new ArrayList<InstanceContext>();
                destroyQueues.put(key, destroyQueue);
            }
            synchronized (destroyQueue) {
                destroyQueue.add(context);
            }
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
        Map<Object, InstanceContext> contextMap = contexts.get(context);
        Object key = workContext.getIdentifier(HTTP_IDENTIFIER);
        InstanceContext ctx = contextMap.get(key);
        if (ctx == null) {
            ctx = context.createInstance();
            contextMap.put(key, ctx);
        }
        return ctx;
    }

    private void shutdownInstances(Object key) {
        List<InstanceContext> destroyQueue = destroyQueues.get(key);
        if (destroyQueue != null) {
            for (Map<Object, InstanceContext> map : contexts.values()) {
                map.remove(key);
            }
            for (InstanceContext ctx : destroyQueue) {
                ctx.stop();
            }
        }
    }

}
