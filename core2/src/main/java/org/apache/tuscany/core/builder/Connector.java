package org.apache.tuscany.core.builder;

import org.apache.tuscany.spi.context.Context;

/**
 * Implementations are responsible for bridging {@link org.apache.tuscany.spi.wire.SourceInvocationChain}s and
 * {@link org.apache.tuscany.spi.wire.TargetInvocationChain}s as an assembly is converted to runtime
 * artifacts
 *
 * @version $$Rev$$ $$Date$$
 */
public interface Connector {

    <T> void connect(Context<T> source);

}
