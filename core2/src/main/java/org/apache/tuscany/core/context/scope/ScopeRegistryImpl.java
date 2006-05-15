package org.apache.tuscany.core.context.scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.ScopeNotFoundException;
import org.apache.tuscany.spi.context.ScopeRegistry;
import org.apache.tuscany.spi.context.WorkContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ScopeRegistryImpl implements ScopeRegistry {

    private final Map<CompositeContext, ScopeContext> moduleScopeCache;
    private final Map<Scope, ScopeContext> scopeCache;
    private final Map<Scope, ObjectFactory<? extends ScopeContext>> factoryCache;
    private final WorkContext workContext;

    public ScopeRegistryImpl(WorkContext workContext) {
        assert(workContext != null);
        moduleScopeCache = new ConcurrentHashMap<CompositeContext, ScopeContext>();
        scopeCache = new ConcurrentHashMap<Scope, ScopeContext>();
        factoryCache = new ConcurrentHashMap<Scope, ObjectFactory<? extends ScopeContext>>();
        this.workContext = workContext;
    }

    public ScopeContext getScopeContext(Scope scope) {
        if (Scope.MODULE == scope) {
            CompositeContext remoteContext = workContext.getRemoteContext();
            assert(remoteContext != null): "Remote composite context next set";
            ScopeContext moduleScope = moduleScopeCache.get(remoteContext);
            if (moduleScope == null) {
                ObjectFactory<? extends ScopeContext> factory = factoryCache.get(scope);
                if (factory == null) {
                    ScopeNotFoundException e = new ScopeNotFoundException("Scope object factor not registered for scope");
                    e.setIdentifier("MODULE");
                    throw e;
                }
                moduleScope = factory.getInstance();
                moduleScope.setWorkContext(workContext);
                moduleScope.start();
                moduleScopeCache.put(remoteContext, moduleScope);
            }
            return moduleScope;
        } else {
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
    }

    public <T extends ScopeContext> void registerFactory(Scope scope, ObjectFactory<T> factory) {
        factoryCache.put(scope, factory);
    }

    public void deregisterFactory(Scope scope) {
        factoryCache.remove(scope);
    }


}
