package org.apache.tuscany.core.builder;

import org.apache.tuscany.core.wire.WireSourceConfiguration;

/**
 * Implementations order source-side policy {@link org.apache.tuscany.core.wire.Interceptor}s or
 * {@link org.apache.tuscany.core.wire.MessageHandler}s in a {@link org.apache.tuscany.core.wire.WireConfiguration}.
 *
 * @version $$Rev$$ $$Date$$
 */
public interface SourcePolicyOrderer extends PolicyOrderer{

    public void order(WireSourceConfiguration configuration);


}
