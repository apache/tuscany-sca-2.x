package org.apache.tuscany.spi.extension;

import java.lang.reflect.InvocationHandler;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.context.AbstractContext;
import org.apache.tuscany.spi.context.ServiceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.wire.WireInvocationHandler;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * The default implementation of an service context
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public class ServiceContextExtension<T> extends AbstractContext<T> implements ServiceContext<T> {

    protected TargetWire<T> targetWire;
    protected ObjectFactory<WireInvocationHandler> handlerFactory;
    private T target;

    public ServiceContextExtension(String name, TargetWire<T> wire, CompositeContext parent) throws CoreRuntimeException {
        super(name, parent);
        this.targetWire = wire;
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    public TargetWire<T> getTargetWire() {
        return targetWire;
    }

    public void setTargetWire(TargetWire<T> wire) {
        target = null;
        targetWire = wire;
    }

    public void setHandlerFactory(ObjectFactory<WireInvocationHandler> handlerFactory) {
        this.handlerFactory = handlerFactory;
    }


    public T getService() throws TargetException {
        if (target == null) {
            target = targetWire.getTargetService();
        }
        return target;
    }

    public InvocationHandler getHandler() {
        WireInvocationHandler invocationHandler = handlerFactory.getInstance();
        invocationHandler.setChains(targetWire.getInvocationChains());
        return invocationHandler;
    }

    public Class<T> getInterface() {
        return targetWire.getBusinessInterface();
    }

}
