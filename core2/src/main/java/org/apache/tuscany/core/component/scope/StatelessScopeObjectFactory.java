package org.apache.tuscany.core.component.scope;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;

/**
 * Creates a new stateless scope context
 *
 * @version $$Rev: 412208 $$ $$Date: 2006-06-06 13:47:19 -0700 (Tue, 06 Jun 2006) $$
 */
public class StatelessScopeObjectFactory implements ObjectFactory<StatelessScopeContainer> {

    public StatelessScopeContainer getInstance() throws ObjectCreationException {
        return new StatelessScopeContainer();
    }
}
