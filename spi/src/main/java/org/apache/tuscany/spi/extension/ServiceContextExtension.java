package org.apache.tuscany.spi.extension;

import java.lang.reflect.InvocationHandler;

import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.context.AbstractContext;
import org.apache.tuscany.spi.context.ServiceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.ProxyCreationException;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

/**
 * The default implementation of an service context
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public abstract class ServiceContextExtension<T> extends AbstractContext<T> implements ServiceContext<T> {

    private SourceWire<T> sourceWire;

    protected ObjectFactory<WireInvocationHandler> handlerFactory;
    // a proxy implementing the service exposed by the context backed by the invocation handler
    private T proxy;

    /**
     * Creates a new service context
     */
    public ServiceContextExtension() throws CoreRuntimeException {
    }

    public void setSourceWire(SourceWire<T> sourceWire) {
        this.sourceWire = sourceWire;
    }

    public void setHandlerFactory(ObjectFactory<WireInvocationHandler> handlerFactory) {
        this.handlerFactory = handlerFactory;
    }


    public T getService() throws TargetException {
        if (proxy == null) {
            try {
                proxy = sourceWire.createProxy();
            } catch (ProxyCreationException e) {
                TargetException te = new TargetException(e);
                te.addContextName(getName());
                throw te;
            }
        }
        return proxy;
    }

    public InvocationHandler getHandler() {
        WireInvocationHandler invocationHandler = handlerFactory.getInstance();
        invocationHandler.setConfiguration(sourceWire.getInvocationChains());
        return invocationHandler;
    }

    public Class<T> getInterface() {
        return sourceWire.getBusinessInterface();
    }

}
