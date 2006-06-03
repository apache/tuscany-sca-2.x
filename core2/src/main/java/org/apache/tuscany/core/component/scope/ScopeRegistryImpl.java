package org.apache.tuscany.core.component.scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.ScopeNotFoundException;
import org.apache.tuscany.spi.context.ScopeRegistry;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.model.Scope;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ScopeRegistryImpl implements ScopeRegistry {

    private final Map<Scope, ScopeContext> scopeCache;
    private final Map<Scope, ObjectFactory<? extends ScopeContext>> factoryCache;
    private final WorkContext workContext;

    public ScopeRegistryImpl(WorkContext workContext) {
        assert(workContext != null);
        scopeCache = new ConcurrentHashMap<Scope, ScopeContext>();
        factoryCache = new ConcurrentHashMap<Scope, ObjectFactory<? extends ScopeContext>>();
        this.workContext = workContext;
    }

    public ScopeContext getScopeContext(Scope scope) {
        assert Scope.MODULE != scope: "Cannot get MODULE scope from the registry";
        ScopeContext context = scopeCache.get(scope);
        if (context == null) {
            ObjectFactory<? extends ScopeContext> factory = factoryCache.get(scope);
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
            context = factory.getInstance();
            context.setWorkContext(workContext);
            context.start();
            scopeCache.put(scope,context);
        }
        return context;
    }

    public <T extends ScopeContext> void registerFactory(Scope scope, ObjectFactory<T> factory) {
        factoryCache.put(scope, factory);
    }

    public void deregisterFactory(Scope scope) {
        factoryCache.remove(scope);
    }


}
