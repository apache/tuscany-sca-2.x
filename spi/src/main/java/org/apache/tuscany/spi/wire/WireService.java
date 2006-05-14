package org.apache.tuscany.spi.wire;

import org.apache.tuscany.model.Reference;
import org.apache.tuscany.model.Service;
import org.apache.tuscany.spi.builder.BuilderConfigException;

/**
 * Implementations provide a system service that creates {@link org.apache.tuscany.spi.wire.SourceWire}s and
 * {@link org.apache.tuscany.spi.wire.TargetWire}s. This service is typically resolved through autowire.
 *
 * @version $$Rev$$ $$Date$$
 */
public interface WireService {

    /**
     * Creates the source-side wire for a reference
     *
     * @param reference the reference to create the wire factory for
     * @throws BuilderConfigException
     */
    public SourceWire createSourceWire(Reference reference) throws BuilderConfigException;

    /**
     * Creates a target-side wire for a service implementing a given interface
     *
     * @param service the service to create the wire factory for
     * @throws BuilderConfigException
     */
    public TargetWire createTargetWire(Service service) throws BuilderConfigException;

}
