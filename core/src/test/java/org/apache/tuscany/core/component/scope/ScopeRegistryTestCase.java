package org.apache.tuscany.core.component.scope;

import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeNotFoundException;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Scope;

import org.apache.tuscany.core.component.WorkContextImpl;
import org.jmock.MockObjectTestCase;

/**
 * Verifies retrieval of standard scope contexts from the default scope registry
 *
 * @version $$Rev: 415162 $$ $$Date: 2006-06-18 11:19:43 -0700 (Sun, 18 Jun 2006) $$
 */
public class ScopeRegistryTestCase extends MockObjectTestCase {
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

    public void testDeregisterFactory() throws Exception {
        WorkContext workContext = new WorkContextImpl();
        ScopeRegistry scopeRegistry = new ScopeRegistryImpl(workContext);
        RequestScopeObjectFactory factory = new RequestScopeObjectFactory();
        scopeRegistry.registerFactory(Scope.REQUEST, factory);
        scopeRegistry.deregisterFactory(Scope.REQUEST);
        try {
            scopeRegistry.getScopeContainer(Scope.REQUEST);
            fail();
        } catch (ScopeNotFoundException e) {
            // expected
        }
    }

    public void testScopeNotRegistered() throws Exception {
        WorkContext workContext = new WorkContextImpl();
        ScopeRegistry scopeRegistry = new ScopeRegistryImpl(workContext);
        try {
            scopeRegistry.getScopeContainer(Scope.REQUEST);
            fail();
        } catch (ScopeNotFoundException e) {
            // expected
        }
        try {
            scopeRegistry.getScopeContainer(Scope.SESSION);
            fail();
        } catch (ScopeNotFoundException e) {
            // expected
        }
        try {
            scopeRegistry.getScopeContainer(Scope.STATELESS);
            fail();
        } catch (ScopeNotFoundException e) {
            // expected
        }
    }


}
