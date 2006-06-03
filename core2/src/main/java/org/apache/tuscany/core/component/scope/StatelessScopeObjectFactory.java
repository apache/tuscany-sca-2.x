package org.apache.tuscany.core.component.scope;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;

/**
 * @version $$Rev$$ $$Date$$
 */
public class StatelessScopeObjectFactory implements ObjectFactory<StatelessScopeContext> {

    public StatelessScopeContext getInstance() throws ObjectCreationException {
        return new StatelessScopeContext();
    }
}
