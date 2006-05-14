package org.apache.tuscany.core.context;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.common.ObjectCreationException;
import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.context.AbstractContext;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.InstanceWrapper;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.TargetException;

/**
 * Base implementation of an {@link AtomicContext} whose implementation type is a Java class
 *
 * @version $$Rev$$ $$Date$$
 */
public abstract class PojoAtomicContext<T> extends AbstractContext<T> implements AtomicContext<T> {

    protected ScopeContext<AtomicContext> scopeContext;
    protected boolean eagerInit;
    protected EventInvoker<Object> initInvoker;
    protected EventInvoker<Object> destroyInvoker;
    protected ObjectFactory<?> objectFactory;
    protected List<Class<?>> serviceInterfaces;
    protected List<Injector> injectors;

    public PojoAtomicContext(String name, Class<?> serviceInterface, ObjectFactory<?> objectFactory, boolean eagerInit, EventInvoker<Object> initInvoker,
                             EventInvoker<Object> destroyInvoker, List<Injector> injectors) {
        super(name);
        List<Class<?>> serviceInterfaces = new ArrayList<Class<?>>();
        serviceInterfaces.add(serviceInterface);
        assert (objectFactory != null) : "Object factory was null";
        if (eagerInit && initInvoker == null) {
            throw new AssertionError("No intialization method found for eager init implementation");
        }
        this.objectFactory = objectFactory;
        this.eagerInit = eagerInit;
        this.initInvoker = initInvoker;
        this.destroyInvoker = destroyInvoker;
        this.serviceInterfaces = serviceInterfaces;
        this.injectors = (injectors == null) ? new ArrayList<Injector>() : injectors;
    }

    public PojoAtomicContext(String name, List<Class<?>> serviceInterfaces, ObjectFactory<?> objectFactory, boolean eagerInit, EventInvoker<Object> initInvoker,
                             EventInvoker<Object> destroyInvoker,List<Injector> injectors) {
        super(name);
        assert (objectFactory != null) : "Object factory was null";
        if (eagerInit && initInvoker == null) {
            throw new AssertionError("No intialization method found for eager init implementation");
        }
        this.objectFactory = objectFactory;
        this.eagerInit = eagerInit;
        this.initInvoker = initInvoker;
        this.destroyInvoker = destroyInvoker;
        this.serviceInterfaces = serviceInterfaces;
        this.injectors = (injectors == null) ? new ArrayList<Injector>() : injectors;
    }

    public List<Class<?>> getServiceInterfaces() {
        return serviceInterfaces;
    }

    public Scope getScope() {
        if (scopeContext == null) {
            return null;
        }
        return scopeContext.getScope();
    }

    public void setScopeContext(ScopeContext<AtomicContext> scopeContext) {
        this.scopeContext = scopeContext;
        scopeContext.register(this);
    }

    public boolean isEagerInit() {
        return eagerInit;
    }

    public void init(Object instance) throws TargetException {
        if (initInvoker != null) {
            initInvoker.invokeEvent(instance);
        }
    }

    public void destroy(Object instance) throws TargetException {
        if (destroyInvoker != null) {
            destroyInvoker.invokeEvent(instance);
        }
    }

    @SuppressWarnings("unchecked")
    public T getTargetInstance() throws TargetException {
        return (T) scopeContext.getInstance(this);
    }

    public InstanceWrapper createInstance() throws ObjectCreationException {
        Object instance = objectFactory.getInstance();
        InstanceWrapper ctx = new PojoInstanceWrapper(this, instance);
        // inject the instance with properties and references
        for (Injector<Object> injector : injectors) {
            injector.inject(instance);
        }
        ctx.start();
        return ctx;
    }


}
