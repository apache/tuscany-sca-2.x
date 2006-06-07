package org.apache.tuscany.core.component.scope;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.core.component.event.CompositeStart;
import org.apache.tuscany.core.component.event.CompositeStop;
import org.apache.tuscany.spi.AbstractLifecycle;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.model.Scope;

/**
 * A scope context which manages atomic component instances keyed by module
 *
 * @version $Rev$ $Date$
 */
public class ModuleScopeContainer extends AbstractScopeContainer {

    private final Map<AtomicComponent, InstanceWrapper> instanceContexts;
    // the queue of instanceContexts to destroy, in the order that their instances were created
    private final List<InstanceWrapper> destroyQueue;
    private static final InstanceWrapper EMPTY = new EmptyWrapper();

    public ModuleScopeContainer() {
        this(null);
    }

    public ModuleScopeContainer(WorkContext workContext) {
        super("Module Scope", workContext);
        instanceContexts = new ConcurrentHashMap<AtomicComponent, InstanceWrapper>();
        destroyQueue = new ArrayList<InstanceWrapper>();
    }

    public Scope getScope() {
        return Scope.MODULE;
    }


    public void onEvent(Event event) {
        checkInit();
        if (event instanceof CompositeStart) {
            eagerInitContexts();
            lifecycleState = RUNNING;
        } else if (event instanceof CompositeStop) {
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
        synchronized (destroyQueue) {
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

    public void register(AtomicComponent component) {
        checkInit();
        instanceContexts.put(component, EMPTY);
    }


    protected InstanceWrapper getInstanceWrapper(AtomicComponent component) throws TargetException {
        checkInit();
        InstanceWrapper ctx = instanceContexts.get(component);
        assert ctx != null : "Component not registered with scope: " + component;
        if (ctx == EMPTY) {
            ctx = new InstanceWrapperImpl(component, component.createInstance());
            ctx.start();
            instanceContexts.put(component, ctx);
            synchronized (destroyQueue) {
                destroyQueue.add(ctx);
            }
        }
        return ctx;
    }

    private void eagerInitContexts() throws CoreRuntimeException {
        for (Map.Entry<AtomicComponent, InstanceWrapper> entry : instanceContexts.entrySet()) {
            AtomicComponent component = entry.getKey();
            if (component.isEagerInit()) {
                InstanceWrapper ctx = new InstanceWrapperImpl(component, component.createInstance());
                ctx.start();
                instanceContexts.put(component, ctx);
                destroyQueue.add(ctx);
            }
        }

    }

    private static class EmptyWrapper extends AbstractLifecycle implements InstanceWrapper {
        public Object getInstance() {
            return null;
        }
    }

}
