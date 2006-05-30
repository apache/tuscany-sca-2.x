package org.apache.tuscany.core.builder;

import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.model.Scope;

/**
 * Implementations are responsible for bridging invocation chains as an assembly is converted to runtime
 * artifacts
 *
 * @version $$Rev$$ $$Date$$
 */
public interface Connector {

    <T> void connect(Context<T> source);

    public <T> void connect(InboundWire<T> inboundWire, OutboundWire<T> outboundWire, boolean optimizable) throws BuilderConfigException;

    public <T> void connect(OutboundWire<T> outboundWire, CompositeContext<?> parent, Scope sourceScope) throws BuilderConfigException;
    public <T> void connect(OutboundWire<T> sourceWire, InboundWire<T> targetWire, Context<?> context, boolean optimizable);

}
