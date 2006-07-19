package org.apache.tuscany.core.component.scope;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;

/**
 * Creates a new module scope context
 *
 * @version $$Rev: 412208 $$ $$Date: 2006-06-06 13:47:19 -0700 (Tue, 06 Jun 2006) $$
 */
public class ModuleScopeObjectFactory implements ObjectFactory<ModuleScopeContainer> {

    public ModuleScopeContainer getInstance() throws ObjectCreationException {
        return new ModuleScopeContainer();
    }
}
