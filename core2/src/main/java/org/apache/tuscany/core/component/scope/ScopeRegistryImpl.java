package org.apache.tuscany.core.component.scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeNotFoundException;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Scope;

/**
 * The default implementation of a scope registry
 *
 * @version $$Rev: 415032 $$ $$Date: 2006-06-17 10:28:07 -0700 (Sat, 17 Jun 2006) $$
 */
public class ScopeRegistryImpl implements ScopeRegistry {

    private final Map<Scope, ScopeContainer> scopeCache;
    private final Map<Scope, ObjectFactory<? extends ScopeContainer>> factoryCache;
    private final WorkContext workContext;

    public ScopeRegistryImpl(WorkContext workContext) {
        assert workContext != null;
        scopeCache = new ConcurrentHashMap<Scope, ScopeContainer>();
        factoryCache = new ConcurrentHashMap<Scope, ObjectFactory<? extends ScopeContainer>>();
        this.workContext = workContext;
    }

    public ScopeContainer getScopeContainer(Scope scope) {
        assert Scope.MODULE != scope : "Cannot get MODULE scope from the registry";
        ScopeContainer container = scopeCache.get(scope);
        if (container == null) {
            ObjectFactory<? extends ScopeContainer> factory = factoryCache.get(scope);
            if (factory == null) {
                ScopeNotFoundException e = new ScopeNotFoundException("Scope object factory not registered for scope");
                switch (scope) {
                    case SESSION:
                        e.setIdentifier("SESSION");
                        break;
                    case REQUEST:
                        e.setIdentifier("REQUEST");
                        break;
                    case STATELESS:
                        e.setIdentifier("STATELESS");
                        break;
                    default:
                        e.setIdentifier("UNKNOWN");
                        break;
                }
                throw e;
            }
            container = factory.getInstance();
            container.setWorkContext(workContext);
            container.start();
            scopeCache.put(scope, container);
        }
        return container;
    }

    public <T extends ScopeContainer> void registerFactory(Scope scope, ObjectFactory<T> factory) {
        factoryCache.put(scope, factory);
    }

    public void deregisterFactory(Scope scope) {
        factoryCache.remove(scope);
    }


}
