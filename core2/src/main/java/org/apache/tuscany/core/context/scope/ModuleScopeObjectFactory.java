package org.apache.tuscany.core.context.scope;

import org.apache.tuscany.common.ObjectCreationException;
import org.apache.tuscany.common.ObjectFactory;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ModuleScopeObjectFactory implements ObjectFactory<ModuleScopeContext> {

    public ModuleScopeContext getInstance() throws ObjectCreationException {
        return new ModuleScopeContext();
    }
}
