package org.apache.tuscany.core.context;

import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.context.event.InstanceCreated;
import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.InstanceContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.SourceWireFactory;
import org.apache.tuscany.spi.wire.TargetWireFactory;

/**
 * @version $$Rev$$ $$Date$$
 */
public class PojoAtomicContext extends AbstractContext implements AtomicContext {

    private ScopeContext scopeContext;

    private boolean eagerInit;

    private EventInvoker<Object> initInvoker;

    private EventInvoker<Object> destroyInvoker;


    private ObjectFactory<InstanceContext> objectFactory;

    public PojoAtomicContext(String name, ScopeContext scopeContext, ObjectFactory<InstanceContext> objectFactory, boolean eagerInit, EventInvoker<Object> initInvoker,
                             EventInvoker<Object> destroyInvoker) {
        super(name);
        assert (scopeContext != null) : "Scope context was null";
        assert (objectFactory != null) : "Object factory was null";
        if (eagerInit && initInvoker == null) {
            throw new AssertionError("No intialization method found for eager init implementation");
        }
        this.scopeContext = scopeContext;
        this.objectFactory = objectFactory;
        this.eagerInit = eagerInit;
        this.initInvoker = initInvoker;
        this.destroyInvoker = destroyInvoker;
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

    public Scope getScope() {
        return scopeContext.getScope();
    }

    public void addProperty(String propertyName, Object value) {

    }

    public void addTargetWireFactory(String serviceName, TargetWireFactory factory) {

    }

    public TargetWireFactory getTargetWireFactory(String serviceName) {
        return null;
    }

    public Map<String, TargetWireFactory> getTargetWireFactories() {
        return null;
    }

    public void addSourceWireFactory(String referenceName, SourceWireFactory factory) {

    }

    public void addSourceWireFactories(String referenceName, Class referenceInterface, List<SourceWireFactory> factory, boolean multiplicity) {

    }

    public List<SourceWireFactory> getSourceWireFactories() {
        return null;
    }

    public void prepare(CompositeContext parent) {

    }

    public InstanceContext createInstance() throws ObjectCreationException {
        InstanceContext ctx = objectFactory.getInstance();
        publish(new InstanceCreated(this,ctx));
        return ctx;
    }


    public Object getInstance(QualifiedName qName) throws TargetException {
        return scopeContext.getInstance(this);
    }



}
