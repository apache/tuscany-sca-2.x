/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.core.component.scope;

import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeNotFoundException;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Scope;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;

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
