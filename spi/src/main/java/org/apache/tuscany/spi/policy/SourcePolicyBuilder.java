package org.apache.tuscany.spi.policy;

import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * Implementations contribute {@link org.apache.tuscany.spi.wire.Interceptor}s or {@link
 * org.apache.tuscany.spi.wire.MessageHandler}s that handle source-side policy on a wire.
 *
 * @version $$Rev$$ $$Date$$
 */
public interface SourcePolicyBuilder {

    public void build(ReferenceDefinition referenceDefinition, OutboundWire wire) throws BuilderException;

}
