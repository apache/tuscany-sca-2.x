package org.apache.tuscany.core.component.scope;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.annotation.Autowire;
import org.osoa.sca.annotations.Init;

/**
 * Creates a new stateless scope context
 *
 * @version $$Rev$$ $$Date$$
 */
public class StatelessScopeObjectFactory implements ObjectFactory<StatelessScopeContainer> {

    public StatelessScopeObjectFactory(@Autowire ScopeRegistry registry) {
        registry.registerFactory(Scope.STATELESS, this);
    }

    @Init(eager = true)
    public void init() {
    }

    public StatelessScopeContainer getInstance() throws ObjectCreationException {
        return new StatelessScopeContainer();
    }
}
