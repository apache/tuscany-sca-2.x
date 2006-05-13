package org.apache.tuscany.core.context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.tuscany.core.wire.jdk.JDKInvocationHandler;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.context.ServiceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.ProxyCreationException;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * The default implementation of an service context
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public class ServiceContextImpl<T extends Class> extends AbstractContext<T> implements ServiceContext<T> {

    private SourceWire<T> sourceWire;
    private InvocationHandler invocationHandler;
    // a proxy implementing the service exposed by the context backed by the invocation handler
    private T proxy;

    /**
     * Creates a new service context
     *
     * @param name              the bound service name
     * @param sourceWire the proxy factory containing the invocation chains for the service
     * @throws CoreRuntimeException if an error occurs creating the service context
     */
    public ServiceContextImpl(String name, SourceWire<T> sourceWire) throws CoreRuntimeException {
        super(name);
        assert (sourceWire != null) : "Proxy factory was null";
        this.sourceWire = sourceWire;
        invocationHandler = new JDKInvocationHandler(sourceWire.getInvocationChains());
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        return null;  //TODO implement
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

    public Object getHandler() {
        return invocationHandler;
    }

    public Class<T> getInterface() {
        return sourceWire.getBusinessInterface();
    }

}
