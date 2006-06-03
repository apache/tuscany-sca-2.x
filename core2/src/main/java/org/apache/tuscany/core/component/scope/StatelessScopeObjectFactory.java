package org.apache.tuscany.core.component.scope;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;

/**
 * Creates a new stateless scope context
 *
 * @version $$Rev$$ $$Date$$
 */
public class StatelessScopeObjectFactory implements ObjectFactory<StatelessScopeContext> {

    public StatelessScopeContext getInstance() throws ObjectCreationException {
        return new StatelessScopeContext();
    }
}
