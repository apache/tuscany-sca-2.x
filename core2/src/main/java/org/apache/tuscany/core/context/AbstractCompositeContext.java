package org.apache.tuscany.core.context;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.tuscany.core.system.context.SystemServiceContext;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.DuplicateNameException;
import org.apache.tuscany.spi.context.IllegalTargetException;
import org.apache.tuscany.spi.context.ReferenceContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.ServiceContext;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.extension.CompositeContextExtension;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

/**
 * The base implementation of a composite context
 *
 * @version $Rev: 399348 $ $Date: 2006-05-03 09:33:22 -0700 (Wed, 03 May 2006) $
 */
@SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized", "RawUseOfParameterizedType", "NonPrivateFieldAccessedInSynchronizedContext"})
public abstract class AbstractCompositeContext<T> extends CompositeContextExtension<T> implements AutowireContext<T> {

    public static final int DEFAULT_WAIT = 1000 * 60;

    // Blocking latch to ensure the module is initialized exactly once prior to servicing requests
    protected CountDownLatch initializeLatch = new CountDownLatch(1);

    protected final Object lock = new Object();

    // Indicates whether the module context has been initialized
    protected boolean initialized;

    // a mapping of service type to component name
    protected final Map<Class, Context> autowireInternal = new ConcurrentHashMap<Class, Context>();
    protected final Map<Class, SystemServiceContext> autowireExternal = new ConcurrentHashMap<Class, SystemServiceContext>();

    protected AutowireContext<?> autowireContext;

    protected ScopeContext scopeContext;

    public AbstractCompositeContext(String name, CompositeContext parent, AutowireContext autowireContext, WireService wireService) {
        super(name, parent, wireService);
        this.autowireContext = autowireContext;
    }

    @Autowire
    public void setAutowireContext(AutowireContext context) {
        autowireContext = context;
    }

    public void setScopeContext(ScopeContext scopeContext) {
        this.scopeContext = scopeContext;
    }

    public void start() {
        synchronized (lock) {
            if (lifecycleState != UNINITIALIZED && lifecycleState != STOPPED) {
                throw new IllegalStateException("Context not in UNINITIALIZED state");
            }

            if (scopeContext != null) {
                scopeContext.start();
            }
            for (Context child : children.values()) {
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
        for (Context child : children.values()) {
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

    public void registerContext(Context child) {
        if (children.get(child.getName()) != null) {
            DuplicateNameException e = new DuplicateNameException("A context is already registered with name");
            e.setIdentifier(child.getName());
            e.addContextName(getName());
            throw e;
        }
        children.put(child.getName(), child);
        if (child instanceof ServiceContext) {
            ServiceContext serviceContext = (ServiceContext) child;
            synchronized (services) {
                services.add(serviceContext);
            }
            registerAutowire(serviceContext);
        } else if (child instanceof ReferenceContext) {
            ReferenceContext context = (ReferenceContext) child;
            synchronized (references) {
                references.add(context);
            }
            registerAutowire(context);
        } else if (child instanceof AtomicContext) {
            registerAutowire((AtomicContext) child);
        } else if (child instanceof CompositeContext) {
            CompositeContext context = (CompositeContext) child;
            if (lifecycleState == STARTED && context.getLifecycleState() == UNINITIALIZED) {
                context.start();
            }
            registerAutowire(context);
            addListener(context);
        }

    }

    public void publish(Event event) {
        checkInit();
        super.publish(event);
    }

    public <T> T resolveInstance(Class<T> instanceInterface) throws AutowireResolutionException {
        if (AutowireContext.class.equals(instanceInterface)) {
            return instanceInterface.cast(this);
        }
        Context context = autowireInternal.get(instanceInterface);
        if (context != null) {
            try {
                if (context instanceof AtomicContext || context instanceof ReferenceContext
                        || context instanceof SystemServiceContext) {
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
        SystemServiceContext context = autowireExternal.get(instanceInterface);
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

    protected void registerAutowireExternal(Class<?> interfaze, SystemServiceContext context) {
        assert interfaze != null;
        if (autowireExternal.containsKey(interfaze)) {
            return;
        }
        autowireExternal.put(interfaze, context);
    }

    protected void registerAutowireInternal(Class<?> interfaze, Context context) {
        assert interfaze != null: "Interface was null";
        if (autowireInternal.containsKey(interfaze)) {
            return;
        }
        autowireInternal.put(interfaze, context);
    }

    protected void registerAutowire(CompositeContext<?> context) {
        List<ServiceContext> services = context.getServiceContexts();
        for (ServiceContext service : services) {
            registerAutowireInternal(service.getInterface(), service);
        }
    }

    protected void registerAutowire(AtomicContext<?> context) {
        List<Class<?>> services = context.getServiceInterfaces();
        for (Class<?> service : services) {
            registerAutowireInternal(service, context);
        }
    }

    protected void registerAutowire(ReferenceContext context) {
        registerAutowireInternal(context.getInterface(), context);
    }

    protected void registerAutowire(ServiceContext context) {
        if (context instanceof SystemServiceContext) {
            SystemServiceContext systemContext = (SystemServiceContext) context;
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
                boolean success = initializeLatch.await(AbstractCompositeContext.DEFAULT_WAIT, TimeUnit.MILLISECONDS);
                if (!success) {
                    throw new ContextInitException("Timeout waiting for context to initialize");
                }
            } catch (InterruptedException e) { // should not happen
            }
        }

    }
}
