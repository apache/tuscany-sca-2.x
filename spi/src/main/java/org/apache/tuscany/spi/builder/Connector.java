package org.apache.tuscany.spi.builder;

import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * Implementations are responsible for bridging invocation chains as an assembly is converted to runtime artifacts
 *
 * @version $$Rev$$ $$Date$$
 */
public interface Connector {

    /**
     * Connects the given artifact to a target in its composite
     *
     * @param source the source artifact to context, i.e. a <code>Service</code>, <code>Component</code>, or
     *               <code>Reference</code>
     */
    <T> void connect(SCAObject<T> source);

    /**
     * Bridges the invocation chains associated with an inbound and outbound wire.
     *
     * @param inboundWire  the wire to bridge from
     * @param outboundWire the target wire
     * @param optimizable  if the bridge may be optimized
     * @throws BuilderConfigException
     */
    <T> void connect(InboundWire<T> inboundWire, OutboundWire<T> outboundWire, boolean optimizable)
        throws BuilderConfigException;

}
