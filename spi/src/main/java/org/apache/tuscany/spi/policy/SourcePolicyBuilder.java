package org.apache.tuscany.spi.policy;

import java.util.List;

import org.apache.tuscany.model.ReferenceTarget;
import org.apache.tuscany.model.Reference;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.builder.BuilderException;

/**
 * Implementations contribute {@link org.apache.tuscany.spi.wire.Interceptor}s or {@link
 * org.apache.tuscany.spi.wire.MessageHandler}s that handle source-side policy on a wire.
 *
 * @version $$Rev$$ $$Date$$
 */
public interface SourcePolicyBuilder {

    public void build(Reference reference, SourceWire wire) throws BuilderException;

}
