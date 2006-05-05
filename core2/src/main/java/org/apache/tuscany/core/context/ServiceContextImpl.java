package org.apache.tuscany.core.context;

import java.lang.reflect.InvocationHandler;

import org.apache.tuscany.core.wire.jdk.JDKInvocationHandler;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.Lifecycle;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.context.ServiceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.ProxyCreationException;
import org.apache.tuscany.spi.wire.SourceWireFactory;

/**
 * The default implementation of an service context
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public class ServiceContextImpl<T extends Class> extends AbstractContext implements ServiceContext<T> {

    private SourceWireFactory<T> sourceWireFactory;
    private InvocationHandler invocationHandler;
    // a proxy implementing the service exposed by the context backed by the invocation handler
    private T proxy;

    /**
     * Creates a new service context
     *
     * @param name              the bound service name
     * @param sourceWireFactory the proxy factory containing the invocation chains for the service
     * @throws CoreRuntimeException if an error occurs creating the service context
     */
    public ServiceContextImpl(String name, SourceWireFactory<T> sourceWireFactory) throws CoreRuntimeException {
        super(name);
        assert (sourceWireFactory != null) : "Proxy factory was null";
        this.sourceWireFactory = sourceWireFactory;
        invocationHandler = new JDKInvocationHandler(sourceWireFactory.getConfiguration().getInvocationConfigurations());
    }

    public Object getInstance(QualifiedName qName) throws TargetException {
        if (proxy == null) {
            try {
                proxy = sourceWireFactory.createProxy();
            } catch (ProxyCreationException e) {
                TargetException te = new TargetException(e);
                te.addContextName(getName());
                throw te;
            }
        }
        return proxy;
    }

    public void start() {
        lifecycleState = Lifecycle.RUNNING;
    }

    public void stop() {
        lifecycleState = Lifecycle.STOPPED;
    }

    public Object getHandler() {
        return invocationHandler;
    }

    public T getServiceInterface() {
        return sourceWireFactory.getBusinessInterface();
    }

}
