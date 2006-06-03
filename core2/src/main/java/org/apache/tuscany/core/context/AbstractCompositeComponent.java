package org.apache.tuscany.core.context;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.tuscany.core.system.context.SystemService;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.context.AtomicComponent;
import org.apache.tuscany.spi.context.CompositeComponent;
import org.apache.tuscany.spi.context.SCAObject;
import org.apache.tuscany.spi.context.DuplicateNameException;
import org.apache.tuscany.spi.context.IllegalTargetException;
import org.apache.tuscany.spi.context.Reference;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.Service;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.extension.CompositeComponentExtension;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

/**
 * The base implementation of a composite context
 *
 * @version $Rev: 399348 $ $Date: 2006-05-03 09:33:22 -0700 (Wed, 03 May 2006) $
 */
@SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized", "RawUseOfParameterizedType", "NonPrivateFieldAccessedInSynchronizedContext"})
public abstract class AbstractCompositeComponent<T> extends CompositeComponentExtension<T> implements AutowireComponent<T> {

    public static final int DEFAULT_WAIT = 1000 * 60;

    // Blocking latch to ensure the module is initialized exactly once prior to servicing requests
    protected CountDownLatch initializeLatch = new CountDownLatch(1);

    protected final Object lock = new Object();

    // Indicates whether the module context has been initialized
    protected boolean initialized;

    // a mapping of service type to component name
    protected final Map<Class, SCAObject> autowireInternal = new ConcurrentHashMap<Class, SCAObject>();
    protected final Map<Class, SystemService> autowireExternal = new ConcurrentHashMap<Class, SystemService>();

    protected AutowireComponent<?> autowireContext;

    protected ScopeContext scopeContext;

    public AbstractCompositeComponent(String name, CompositeComponent parent, AutowireComponent autowireContext, WireService wireService) {
        super(name, parent, wireService);
        this.autowireContext = autowireContext;
    }

    @Autowire
    public void setAutowireContext(AutowireComponent context) {
        autowireContext = context;
    }

    public void setScopeContext(ScopeContext scopeContext) {
        this.scopeContext = scopeContext;
    }

    public void start() {
        synchronized (lock) {
            if (lifecycleState != UNINITIALIZED && lifecycleState != STOPPED) {
                throw new IllegalStateException("SCAObject not in UNINITIALIZED state");
            }

            if (scopeContext != null) {
                scopeContext.start();
            }
            for (SCAObject child : children.values()) {
                child.start();
            }
            initializeLatch.countDown();
            initialized = true;
            lifecycleState = INITIALIZED;
        }
    }

    public void stop() {
        if (lifecycleState == STOPPED) {
            return;
        }
        for (SCAObject child : children.values()) {
            child.stop();
        }
        if (scopeContext != null) {
            scopeContext.stop();
        }

        // need to block a start until reset is complete
        initializeLatch = new CountDownLatch(2);
        lifecycleState = STOPPING;
        initialized = false;
        // allow initialized to be called
        initializeLatch.countDown();
        lifecycleState = STOPPED;
    }

    public void register(SCAObject child) {
        if (children.get(child.getName()) != null) {
            DuplicateNameException e = new DuplicateNameException("A context is already registered with name");
            e.setIdentifier(child.getName());
            e.addContextName(getName());
            throw e;
        }
        children.put(child.getName(), child);
        if (child instanceof Service) {
            Service service = (Service) child;
            synchronized (services) {
                services.add(service);
            }
            registerAutowire(service);
        } else if (child instanceof Reference) {
            Reference context = (Reference) child;
            synchronized (references) {
                references.add(context);
            }
            registerAutowire(context);
        } else if (child instanceof AtomicComponent) {
            registerAutowire((AtomicComponent) child);
        } else if (child instanceof CompositeComponent) {
            CompositeComponent component = (CompositeComponent) child;
            if (lifecycleState == RUNNING && component.getLifecycleState() == UNINITIALIZED) {
                component.start();
            }
            registerAutowire(component);
            addListener(component);
        }

    }

    public void publish(Event event) {
        checkInit();
        super.publish(event);
    }

    public <T> T resolveInstance(Class<T> instanceInterface) throws AutowireResolutionException {
        if (AutowireComponent.class.equals(instanceInterface)) {
            return instanceInterface.cast(this);
        }
        SCAObject context = autowireInternal.get(instanceInterface);
        if (context != null) {
            try {
                if (context instanceof AtomicComponent || context instanceof Reference
                        || context instanceof SystemService) {
                    return instanceInterface.cast(context.getService());
                } else {
                    IllegalTargetException e = new IllegalTargetException("Autowire target must be a system service, atomic, or reference context");
                    e.setIdentifier(instanceInterface.getName());
                    e.addContextName(getName());
                    throw e;
                }
            } catch (CoreRuntimeException e) {
                e.addContextName(getName());
                throw e;
            }
        } else if (autowireContext != null) {
            try {
                // resolve to parent
                return autowireContext.resolveInstance(instanceInterface);
            } catch (AutowireResolutionException e) {
                e.addContextName(getName());
                throw e;
            }
        } else {
            return null;
        }
    }

    public <T> T resolveExternalInstance(Class<T> instanceInterface) throws AutowireResolutionException {
        SystemService context = autowireExternal.get(instanceInterface);
        if (context != null) {
            try {
                return instanceInterface.cast(context.getService());
            } catch (CoreRuntimeException e) {
                e.addContextName(getName());
                throw e;
            }
        } else {
            return null;
        }
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        return null;// new BridgingInvoker(serviceName, operation, this);
    }

    protected void registerAutowireExternal(Class<?> interfaze, SystemService context) {
        assert interfaze != null;
        if (autowireExternal.containsKey(interfaze)) {
            return;
        }
        autowireExternal.put(interfaze, context);
    }

    protected void registerAutowireInternal(Class<?> interfaze, SCAObject context) {
        assert interfaze != null: "Interface was null";
        if (autowireInternal.containsKey(interfaze)) {
            return;
        }
        autowireInternal.put(interfaze, context);
    }

    protected void registerAutowire(CompositeComponent<?> component) {
        List<Service> services = component.getServices();
        for (Service service : services) {
            registerAutowireInternal(service.getInterface(), service);
        }
    }

    protected void registerAutowire(AtomicComponent<?> component) {
        List<Class<?>> services = component.getServiceInterfaces();
        for (Class<?> service : services) {
            registerAutowireInternal(service, component);
        }
    }

    protected void registerAutowire(Reference context) {
        registerAutowireInternal(context.getInterface(), context);
    }

    protected void registerAutowire(Service context) {
        if (context instanceof SystemService) {
            SystemService systemContext = (SystemService) context;
            registerAutowireExternal(systemContext.getInterface(), systemContext);
        }
    }

    /**
     * Blocks until the module context has been initialized
     */
    protected void checkInit() {
        if (!initialized) {
            try {
                /* block until the module has initialized */
                boolean success = initializeLatch.await(AbstractCompositeComponent.DEFAULT_WAIT, TimeUnit.MILLISECONDS);
                if (!success) {
                    throw new ComponentInitException("Timeout waiting for context to initialize");
                }
            } catch (InterruptedException e) { // should not happen
            }
        }

    }
}
