package org.apache.tuscany.core.builder;

import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.CompositeContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public interface Connector {

    void connect(Context<?> source, CompositeContext parent);

}
