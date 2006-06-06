package org.apache.tuscany.core.component.scope;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Scope;

/**
 * Verifies retrieval of standard scope contexts from the default scope registry
 *
 * @version $$Rev$$ $$Date$$
 */
public class ScopeRegistryTestCase extends TestCase {
    public void testScopeContextCreation() throws Exception {
        WorkContext workContext = new WorkContextImpl();
        ScopeRegistry scopeRegistry = new ScopeRegistryImpl(workContext);
        scopeRegistry.registerFactory(Scope.REQUEST, new RequestScopeObjectFactory());
        scopeRegistry.registerFactory(Scope.SESSION, new HttpSessionScopeObjectFactory());
        ScopeContainer request = scopeRegistry.getScopeContainer(Scope.REQUEST);
        assertTrue(request instanceof RequestScopeContainer);
        assertSame(request, scopeRegistry.getScopeContainer(Scope.REQUEST));
        ScopeContainer session = scopeRegistry.getScopeContainer(Scope.SESSION);
        assertTrue(session instanceof HttpSessionScopeContainer);
        assertSame(session, scopeRegistry.getScopeContainer(Scope.SESSION));
        assertNotSame(request, session);
    }
}
