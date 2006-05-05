package org.apache.tuscany.core.context.scope;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.ScopeRegistry;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ScopeRegistryImpl implements ScopeRegistry {

    private final Map<CompositeContext, List<ScopeContext>> cache;
    private final List<ObjectFactory<ScopeContext>> factories;

    public ScopeRegistryImpl() {
        cache = new ConcurrentHashMap<CompositeContext, List<ScopeContext>>();
        factories = new CopyOnWriteArrayList<ObjectFactory<ScopeContext>>();
    }

    public List<ScopeContext> getScopeContexts(CompositeContext module) {
        List<ScopeContext> scopes = cache.get(module);
        if (scopes == null){
            scopes = new ArrayList<ScopeContext>();
            for (ObjectFactory<ScopeContext> factory : factories) {
                scopes.add(factory.getInstance());
            }
            cache.put(module,scopes);
        }
        return scopes;
    }

    public void registerFactory(ObjectFactory<ScopeContext> factory) {
        factories.add(factory);
    }

    public void deRegisterFactory(ObjectFactory<ScopeContext> factory) {
       factories.remove(factory);
    }


}
