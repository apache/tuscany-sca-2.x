package org.apache.tuscany.core.system.context;

import java.lang.reflect.InvocationHandler;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.extension.ServiceContextExtension;
import org.apache.tuscany.spi.wire.SourceWire;

/**
 * Default implementation for service contexts configured with the {@link org.apache.tuscany.core.system.model.SystemBinding}
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemServiceContextImpl<T> extends ServiceContextExtension<T> implements SystemServiceContext<T> {

    public SystemServiceContextImpl(String name, SourceWire<T> wire, CompositeContext parent) throws CoreRuntimeException {
        super(name,wire,parent);
    }

    public InvocationHandler getHandler() {
        throw new UnsupportedOperationException();
    }

}
