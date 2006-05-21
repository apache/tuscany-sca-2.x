package org.apache.tuscany.core.context.scope;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.ObjectCreationException;

/**
 * @version $$Rev$$ $$Date$$
 */
public class StatelessScopeObjectFactory implements ObjectFactory<StatelessScopeContext> {

    public StatelessScopeContext getInstance() throws ObjectCreationException {
        return new StatelessScopeContext();
    }
}
