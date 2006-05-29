package org.apache.tuscany.spi.wire;

import org.apache.tuscany.spi.model.Reference;
import org.apache.tuscany.spi.model.Service;
import org.apache.tuscany.spi.builder.BuilderConfigException;

/**
 * Implementations provide a system service that creates {@link org.apache.tuscany.spi.wire.ReferenceWire}s and
 * {@link org.apache.tuscany.spi.wire.ServiceWire}s. This service is typically resolved through autowire.
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
    ReferenceWire createReferenceWire(Reference reference) throws BuilderConfigException;

    /**
     * Creates a wire for a given service definition
     *
     * @param service the service to create the wire factory for
     * @throws BuilderConfigException
     */
    ServiceWire createServiceWire(Service service) throws BuilderConfigException;

}
