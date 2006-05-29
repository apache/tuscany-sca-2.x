package org.apache.tuscany.core.builder;

import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.wire.ServiceWire;

/**
 * Implementations are responsible for bridging invocation chains as an assembly is converted to runtime
 * artifacts
 *
 * @version $$Rev$$ $$Date$$
 */
public interface Connector {

    <T> void connect(Context<T> source);

    public <T> void connect(ServiceWire<T> serviceWire, Context<?> targetContext) throws BuilderConfigException;


}
