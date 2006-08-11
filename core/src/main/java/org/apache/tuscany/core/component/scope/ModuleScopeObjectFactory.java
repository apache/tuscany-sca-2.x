package org.apache.tuscany.core.component.scope;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.annotation.Autowire;
import org.osoa.sca.annotations.Init;

/**
 * Creates a new module scope context
 *
 * @version $$Rev$$ $$Date$$
 */
public class ModuleScopeObjectFactory implements ObjectFactory<ModuleScopeContainer> {

    public ModuleScopeObjectFactory(@Autowire ScopeRegistry registry) {
        registry.registerFactory(Scope.MODULE, this);
    }

    @Init(eager = true)
    public void init() {
    }

    public ModuleScopeContainer getInstance() throws ObjectCreationException {
        return new ModuleScopeContainer();
    }
}
