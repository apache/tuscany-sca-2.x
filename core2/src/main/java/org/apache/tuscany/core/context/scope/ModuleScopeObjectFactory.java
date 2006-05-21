package org.apache.tuscany.core.context.scope;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ModuleScopeObjectFactory implements ObjectFactory<ModuleScopeContext> {

    public ModuleScopeContext getInstance() throws ObjectCreationException {
        return new ModuleScopeContext();
    }
}
