package org.apache.tuscany.core.builder;

import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.context.SCAObject;
import org.apache.tuscany.spi.context.CompositeComponent;
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

    <T> void connect(SCAObject<T> source);

    <T> void connect(InboundWire<T> inboundWire, OutboundWire<T> outboundWire, boolean optimizable) throws BuilderConfigException;

    <T> void connect(OutboundWire<T> outboundWire, CompositeComponent<?> parent, Scope sourceScope) throws BuilderConfigException;

    <T> void connect(OutboundWire<T> sourceWire, InboundWire<T> targetWire, SCAObject<?> context, boolean optimizable);

}
