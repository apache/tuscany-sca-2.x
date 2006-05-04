package org.apache.tuscany.core.context.scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.core.context.event.InstanceCreated;
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

    private Map<AtomicContext, Map<Thread, InstanceContext>> contexts;
    private Map<Thread, List<InstanceContext>> destroyQueues;

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
            shutdownInstances();
        } else if (event instanceof InstanceCreated) {
            checkInit();
            InstanceContext context = ((InstanceCreated) event).getContext();
            List<InstanceContext> destroyQueue = destroyQueues.get(Thread.currentThread());
            destroyQueue.add(context);
        }
    }

    public synchronized void start() {
        if (lifecycleState != UNINITIALIZED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED state [" + lifecycleState + "]");
        }
    }

    public synchronized void stop() {
        //TODO stop semantics
        lifecycleState = STOPPED;
    }

    public void register(AtomicContext context) {
        contexts.put(context, new ConcurrentHashMap<Thread, InstanceContext>());
        List<InstanceContext> destroyQueue = destroyQueues.get(Thread.currentThread());

        if (destroyQueue == null) {
            destroyQueues.put(Thread.currentThread(), new ArrayList<InstanceContext>());
        }
        context.addListener(this);

    }

    public InstanceContext getInstanceContext(AtomicContext context) throws TargetException {
       Map<Thread,InstanceContext> instanceContextMap = contexts.get(context);
       InstanceContext ctx = instanceContextMap.get(Thread.currentThread());
        if(ctx == null){
            ctx = context.createInstance();
            instanceContextMap.put(Thread.currentThread(),ctx);
        }
        return ctx;
    }

    private void shutdownInstances() {
        List<InstanceContext> destroyQueue = destroyQueues.get(Thread.currentThread());
        if (destroyQueue != null) {
            for (InstanceContext ctx : destroyQueue) {
                ctx.stop();
            }
        }
    }

}
