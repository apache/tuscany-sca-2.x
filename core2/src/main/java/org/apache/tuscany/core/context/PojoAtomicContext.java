package org.apache.tuscany.core.context;

import org.apache.tuscany.common.ObjectCreationException;
import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.InstanceContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.TargetException;

/**
 * @version $$Rev$$ $$Date$$
 */
public abstract class PojoAtomicContext extends AbstractContext implements AtomicContext {

    protected ScopeContext<AtomicContext> scopeContext;
    protected boolean eagerInit;
    protected EventInvoker<Object> initInvoker;
    protected EventInvoker<Object> destroyInvoker;
    protected ObjectFactory<?> objectFactory;

    public PojoAtomicContext(String name, ObjectFactory<?> objectFactory, boolean eagerInit, EventInvoker<Object> initInvoker,
                             EventInvoker<Object> destroyInvoker) {
        super(name);
        assert (objectFactory != null) : "Object factory was null";
        if (eagerInit && initInvoker == null) {
            throw new AssertionError("No intialization method found for eager init implementation");
        }
        this.objectFactory = objectFactory;
        this.eagerInit = eagerInit;
        this.initInvoker = initInvoker;
        this.destroyInvoker = destroyInvoker;
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

    public boolean isDestroyable() {
        return (destroyInvoker != null);
    }

    public Object getTargetInstance() throws TargetException {
        return scopeContext.getInstance(this);
    }

    public void addProperty(String propertyName, Object value) {

    }

    public InstanceContext createInstance() throws ObjectCreationException {
        InstanceContext ctx = new PojoInstanceContext(this, objectFactory.getInstance());
        ctx.start();
        return ctx;
    }


}
