package org.apache.tuscany.core.system.context;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.extension.ServiceContextExtension;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

/**
 * Default implementation for service contexts configured with the {@link org.apache.tuscany.core.system.model.SystemBinding}
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemServiceContextImpl<T> extends ServiceContextExtension<T> implements SystemServiceContext<T> {

    public SystemServiceContextImpl(String name, CompositeContext parent) throws CoreRuntimeException {
        super(name, parent);
    }

    public WireInvocationHandler getHandler() {
        // system services do not proxy
        throw new UnsupportedOperationException();
    }

}
