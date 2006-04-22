package org.apache.tuscany.core.builder;

import org.apache.tuscany.core.wire.WireSourceConfiguration;

/**
 * Implementations order the set of builders that contribute policy to a wire
 *
 * @version $$Rev$$ $$Date$$
 */
public interface SourcePolicyOrderer {

    public void order(WireSourceConfiguration configuration);


}
