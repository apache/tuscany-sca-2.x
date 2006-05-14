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
    private final Map<Scope, ObjectFactory<ScopeContext>> factoryCache;
    private final WorkContext workContext;

    public ScopeRegistryImpl(WorkContext workContext) {
        assert(workContext != null);
        moduleScopeCache = new ConcurrentHashMap<CompositeContext, ScopeContext>();
        factoryCache = new ConcurrentHashMap<Scope, ObjectFactory<ScopeContext>>();
        this.workContext = workContext;
    }

    public ScopeContext getScopeContext(Scope scope) {
        CompositeContext remoteContext = workContext.getRemoteContext();
        assert(remoteContext != null): "Remote composite context next set";
        if (Scope.MODULE == scope) {
            ScopeContext moduleScope = moduleScopeCache.get(remoteContext);
            if (moduleScope == null) {
                ObjectFactory<ScopeContext> factory = factoryCache.get(scope);
                if (factory == null) {
                    ScopeNotFoundException e = new ScopeNotFoundException("Scope object factor not registered for scope");
                    e.setIdentifier("MODULE");
                    throw e;
                }
                moduleScope = factory.getInstance();
                moduleScopeCache.put(remoteContext, moduleScope);
            }
            return moduleScope;
        }
        ObjectFactory<ScopeContext> factory = factoryCache.get(scope);
        if (factory == null) {
            ScopeNotFoundException e = new ScopeNotFoundException("Scope object factor not registered for scope");
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

        return null;
    }

    public void registerFactory(Scope scope, ObjectFactory<ScopeContext> factory) {
        factoryCache.put(scope, factory);
    }

    public void deregisterFactory(Scope scope) {
        factoryCache.remove(scope);
    }


}
