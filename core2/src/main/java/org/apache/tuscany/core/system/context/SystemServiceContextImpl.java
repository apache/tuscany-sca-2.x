package org.apache.tuscany.core.system.context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.extension.ServiceContextExtension;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SystemServiceContextImpl<T> extends ServiceContextExtension<T> implements SystemServiceContext<T> {

    public SystemServiceContextImpl(String name, SourceWire<T> wire, CompositeContext parent) throws CoreRuntimeException {
        this.name = name;
        this.parentContext = parent;
        this.sourceWire = wire;
    }

    public InvocationHandler getHandler() {
        throw new UnsupportedOperationException();
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        throw new UnsupportedOperationException();
    }

    public void prepare() {
        // override and do nothing since system services do not proxy
    }    

}
