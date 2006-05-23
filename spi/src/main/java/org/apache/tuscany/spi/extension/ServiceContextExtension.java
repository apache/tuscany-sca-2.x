package org.apache.tuscany.spi.extension;

import java.lang.reflect.InvocationHandler;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.context.AbstractContext;
import org.apache.tuscany.spi.context.ServiceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

/**
 * The default implementation of an service context
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public class ServiceContextExtension<T> extends AbstractContext<T> implements ServiceContext<T> {

    protected SourceWire<T> sourceWire;
    protected ObjectFactory<WireInvocationHandler> handlerFactory;
    private T target;

    public ServiceContextExtension(String name, SourceWire<T> wire, CompositeContext parent) throws CoreRuntimeException {
        super(name, parent);
        this.sourceWire = wire;
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    public SourceWire<T> getSourceWire() {
        return sourceWire;
    }

    public void setSourceWire(SourceWire<T> wire) {
        target = null;
        sourceWire = wire;
    }

    public void setHandlerFactory(ObjectFactory<WireInvocationHandler> handlerFactory) {
        this.handlerFactory = handlerFactory;
    }


    public T getService() throws TargetException {
        if (target == null) {
            target = sourceWire.getTargetService();
        }
        return target;
    }

    public InvocationHandler getHandler() {
        WireInvocationHandler invocationHandler = handlerFactory.getInstance();
        invocationHandler.setChains(sourceWire.getInvocationChains());
        return invocationHandler;
    }

    public Class<T> getInterface() {
        return sourceWire.getBusinessInterface();
    }

}
