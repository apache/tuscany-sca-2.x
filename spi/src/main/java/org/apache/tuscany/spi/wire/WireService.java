package org.apache.tuscany.spi.wire;

import org.apache.tuscany.spi.model.Reference;
import org.apache.tuscany.spi.model.Service;
import org.apache.tuscany.spi.builder.BuilderConfigException;

/**
 * Implementations provide a system service that creates {@link org.apache.tuscany.spi.wire.OutboundWire}s and
 * {@link org.apache.tuscany.spi.wire.InboundWire}s. This service is typically resolved through autowire.
 *
 * @version $$Rev$$ $$Date$$
 */
public interface WireService {

    /**
     * Creates a wire for a given reference
     *
     * @param reference the reference to create the wire factory for
     * @throws BuilderConfigException
     */
    OutboundWire createReferenceWire(Reference reference) throws BuilderConfigException;

    /**
     * Creates a wire for a given service definition
     *
     * @param service the service to create the wire factory for
     * @throws BuilderConfigException
     */
    InboundWire createServiceWire(Service service) throws BuilderConfigException;

}
