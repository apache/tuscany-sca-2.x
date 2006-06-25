package org.apache.tuscany.spi.builder;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * Implementations are responsible for bridging invocation chains as an assembly is converted to runtime artifacts
 *
 * @version $$Rev$$ $$Date$$
 */
public interface Connector {

    <T> void connect(SCAObject<T> source);

    <T> void connect(InboundWire<T> inboundWire, OutboundWire<T> outboundWire, boolean optimizable)
        throws BuilderConfigException;

    <T> void connect(OutboundWire<T> outboundWire, CompositeComponent<?> parent, Scope sourceScope)
        throws BuilderConfigException;

    <T> void connect(OutboundWire<T> sourceWire, InboundWire<T> targetWire, SCAObject<?> context, boolean optimizable);

}
