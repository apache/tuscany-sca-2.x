package org.apache.tuscany.spi.extension;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.context.AbstractContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.ServiceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.ServiceInvocationHandler;
import org.apache.tuscany.spi.wire.ServiceWire;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

/**
 * The default implementation of an service context
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public class ServiceContextExtension<T> extends AbstractContext<T> implements ServiceContext<T> {

    protected ServiceWire<T> serviceWire;
    private T target;

    public ServiceContextExtension(String name, ServiceWire<T> wire, CompositeContext parent) throws CoreRuntimeException {
        super(name, parent);
        this.serviceWire = wire;
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    public ServiceWire<T> getWire() {
        return serviceWire;
    }

    public void setWire(ServiceWire<T> wire) {
        target = null;
        serviceWire = wire;
    }

    public T getService() throws TargetException {
        if (target == null) {
            target = serviceWire.getTargetService();
        }
        return target;
    }

    public WireInvocationHandler getHandler() {
        return new ServiceInvocationHandler(serviceWire.getInvocationChains());
    }

    public Class<T> getInterface() {
        return serviceWire.getBusinessInterface();
    }

}
