package org.apache.tuscany.spi.extension;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.context.AbstractContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.ServiceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.TargetInvocationHandler;
import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

/**
 * The default implementation of an service context
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public class ServiceContextExtension<T> extends AbstractContext<T> implements ServiceContext<T> {

    protected TargetWire<T> targetWire;
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

    public T getService() throws TargetException {
        if (target == null) {
            target = targetWire.getTargetService();
        }
        return target;
    }

    public WireInvocationHandler getHandler() {
        return new TargetInvocationHandler(targetWire.getInvocationChains());
    }

    public Class<T> getInterface() {
        return targetWire.getBusinessInterface();
    }

}
