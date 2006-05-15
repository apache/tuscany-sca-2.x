package org.apache.tuscany.core.context.scope;

import junit.framework.TestCase;
import org.apache.tuscany.core.context.CompositeContextImpl;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.ScopeRegistry;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.context.ScopeContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ScopeRegistryTestCase extends TestCase {

    public void testRemotableIsolation() throws Exception {
        CompositeContext context1 = new CompositeContextImpl();
        CompositeContext context2 = new CompositeContextImpl();
        WorkContext workContext = new WorkContextImpl();
        ScopeRegistry scopeRegistry = new ScopeRegistryImpl(workContext);
        scopeRegistry.registerFactory(Scope.MODULE, new ModuleScopeObjectFactory());
        workContext.setRemoteContext(context1);
        ScopeContext scope1 = scopeRegistry.getScopeContext(Scope.MODULE);
        workContext.setRemoteContext(context2);
        ScopeContext scope2 = scopeRegistry.getScopeContext(Scope.MODULE);
        assertNotSame(scope1,scope2);
        workContext.setRemoteContext(context1);
        assertSame(scope1,scopeRegistry.getScopeContext(Scope.MODULE));
        workContext.setRemoteContext(context2);
        assertSame(scope2,scopeRegistry.getScopeContext(Scope.MODULE));
    }

    public void testScopeContextCreation() throws Exception {
        WorkContext workContext = new WorkContextImpl();
        ScopeRegistry scopeRegistry = new ScopeRegistryImpl(workContext);
        scopeRegistry.registerFactory(Scope.REQUEST, new RequestScopeObjectFactory());
        scopeRegistry.registerFactory(Scope.SESSION, new HttpSessionScopeObjectFactory());
        ScopeContext request = scopeRegistry.getScopeContext(Scope.REQUEST);
        assertTrue(request instanceof RequestScopeContext);
        assertSame(request,scopeRegistry.getScopeContext(Scope.REQUEST));
        ScopeContext session = scopeRegistry.getScopeContext(Scope.SESSION);
        assertTrue(session instanceof HttpSessionScopeContext);
        assertSame(session,scopeRegistry.getScopeContext(Scope.SESSION));
        assertNotSame(request,session);
    }
}
