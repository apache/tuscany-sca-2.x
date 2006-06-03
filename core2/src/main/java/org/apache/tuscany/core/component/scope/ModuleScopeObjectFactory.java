package org.apache.tuscany.core.component.scope;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;

/**
 * Creates a new module scope context
 *
 * @version $$Rev$$ $$Date$$
 */
public class ModuleScopeObjectFactory implements ObjectFactory<ModuleScopeContext> {

    public ModuleScopeContext getInstance() throws ObjectCreationException {
        return new ModuleScopeContext();
    }
}
