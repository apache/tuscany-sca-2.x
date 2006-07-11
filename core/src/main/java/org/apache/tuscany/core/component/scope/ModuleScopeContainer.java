package org.apache.tuscany.core.component.scope;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.spi.AbstractLifecycle;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.model.Scope;

import org.apache.tuscany.core.component.event.CompositeStart;
import org.apache.tuscany.core.component.event.CompositeStop;

/**
 * A scope context which manages atomic component instances keyed by module
 *
 * @version $Rev: 415162 $ $Date: 2006-06-18 11:19:43 -0700 (Sun, 18 Jun 2006) $
 */
public class ModuleScopeContainer extends AbstractScopeContainer {

    private static final InstanceWrapper EMPTY = new EmptyWrapper();
    private final Map<AtomicComponent, InstanceWrapper> instanceWrappers;
    // the queue of instanceWrappers to destroy, in the order that their instances were created
    private final List<InstanceWrapper> destroyQueue;

    public ModuleScopeContainer() {
        this(null);
    }

    public ModuleScopeContainer(WorkContext workContext) {
        super("Module Scope", workContext);
        instanceWrappers = new ConcurrentHashMap<AtomicComponent, InstanceWrapper>();
        destroyQueue = new ArrayList<InstanceWrapper>();
    }

    public Scope getScope() {
        return Scope.MODULE;
    }


    public void onEvent(Event event) {
        checkInit();
        if (event instanceof CompositeStart) {
            eagerInitComponents();
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
        instanceWrappers.clear();
        synchronized (destroyQueue) {
            destroyQueue.clear();
        }
        lifecycleState = STOPPED;
    }


    /**
     * Notifies instanceWrappers of a shutdown in reverse order to which they were started
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
        instanceWrappers.put(component, EMPTY);
    }


    protected InstanceWrapper getInstanceWrapper(AtomicComponent component) throws TargetException {
        checkInit();
        InstanceWrapper ctx = instanceWrappers.get(component);
        assert ctx != null : "Component not registered with scope: " + component;
        if (ctx == EMPTY) {
            ctx = new InstanceWrapperImpl(component, component.createInstance());
            ctx.start();
            instanceWrappers.put(component, ctx);
            synchronized (destroyQueue) {
                destroyQueue.add(ctx);
            }
        }
        return ctx;
    }

    private void eagerInitComponents() throws CoreRuntimeException {
        for (Map.Entry<AtomicComponent, InstanceWrapper> entry : instanceWrappers.entrySet()) {
            AtomicComponent component = entry.getKey();
            if (component.isEagerInit()) {
                // the instance could have been created from a depth-first traversal
                InstanceWrapper ctx = instanceWrappers.get(component);
                if (ctx == EMPTY) {
                    ctx = new InstanceWrapperImpl(component, component.createInstance());
                    ctx.start();
                    instanceWrappers.put(component, ctx);
                }
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
