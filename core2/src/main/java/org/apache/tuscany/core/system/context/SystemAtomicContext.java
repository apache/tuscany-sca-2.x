package org.apache.tuscany.core.system.context;

import java.util.List;
import java.util.Map;

import org.apache.tuscany.common.ObjectCreationException;
import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.core.context.PojoAtomicContext;
import org.apache.tuscany.core.context.PojoInstanceContext;
import org.apache.tuscany.core.context.event.InstanceCreated;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.context.InstanceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.SourceWireFactory;
import org.apache.tuscany.spi.wire.TargetWireFactory;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SystemAtomicContext extends PojoAtomicContext {

    public SystemAtomicContext(String name, ObjectFactory<?> objectFactory, boolean eagerInit, EventInvoker<Object> initInvoker,
                               EventInvoker<Object> destroyInvoker) {
        super(name, objectFactory, eagerInit, initInvoker, destroyInvoker);
        this.objectFactory = objectFactory;
    }

    public Object getTargetInstance() throws TargetException {
        return scopeContext.getInstance(this);
    }

    public InstanceContext createInstance() throws ObjectCreationException {
        InstanceContext ctx = new PojoInstanceContext(this, objectFactory.getInstance());
        ctx.start();
        publish(new InstanceCreated(this, ctx));
        return ctx;
    }

    public void prepare() {
    }

    public Object getInstance(QualifiedName qName) throws TargetException {
        return scopeContext.getInstance(this);
    }

    public void addTargetWireFactory(String serviceName, TargetWireFactory factory) {
        throw new UnsupportedOperationException();
    }

    public TargetWireFactory getTargetWireFactory(String serviceName) {
        throw new UnsupportedOperationException();
    }

    public Map<String, TargetWireFactory> getTargetWireFactories() {
        throw new UnsupportedOperationException();
    }

    public void addSourceWireFactory(String referenceName, SourceWireFactory factory) {
        throw new UnsupportedOperationException();
    }

    public void addSourceWireFactories(String referenceName, Class referenceInterface, List<SourceWireFactory> factory, boolean multiplicity) {
        throw new UnsupportedOperationException();
    }

    public List<SourceWireFactory> getSourceWireFactories() {
        throw new UnsupportedOperationException();
    }


}
