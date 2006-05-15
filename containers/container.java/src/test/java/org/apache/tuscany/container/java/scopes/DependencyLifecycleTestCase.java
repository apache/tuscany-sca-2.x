/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.container.java.scopes;

import junit.framework.TestCase;
import org.apache.tuscany.container.java.mock.MockContextFactory;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.event.HttpSessionBound;
import org.apache.tuscany.core.context.event.HttpSessionEvent;
import org.apache.tuscany.core.context.event.HttpSessionEnd;
import org.apache.tuscany.core.context.event.RequestStart;
import org.apache.tuscany.core.context.event.RequestEnd;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.context.scope.SessionScopeContext;
import org.apache.tuscany.core.context.scope.RequestScopeContext;
import org.apache.tuscany.model.assembly.Scope;

/**
 * Tests that dependencies are initalized and destroyed in the proper order (i.e. LIFO)
 *
 * @version $Rev: 393992 $ $Date: 2006-04-13 18:01:05 -0700 (Thu, 13 Apr 2006) $
 */
public class DependencyLifecycleTestCase extends TestCase {



    public void testInitDestroyOrderModuleScope() throws Exception {
        EventContext ctx = new EventContextImpl();
        ModuleScopeContext scope = new ModuleScopeContext(ctx);
        scope.registerFactories(MockContextFactory.createWiredContexts(Scope.MODULE,scope));
        scope.start();
        scope.onEvent(new ModuleStart(this));
        OrderedDependentPojo source = (OrderedDependentPojo) scope.getContext("source").getInstance(null);
        assertNotNull(source.getPojo());
        // expire module
        assertEquals(2,source.getNumberInstantiated());
        scope.onEvent(new ModuleStop(this));
        assertEquals(0,source.getNumberInstantiated());
        scope.stop();
    }

    public void testInitDestroyOrderSessionScope() throws Exception {
        EventContext ctx = new EventContextImpl();
        SessionScopeContext scope = new SessionScopeContext(ctx);
        scope.registerFactories(MockContextFactory.createWiredContexts(Scope.SESSION,scope));
        scope.start();
        Object session =  new Object();
        ctx.setIdentifier(HttpSessionEvent.HTTP_IDENTIFIER,session);
        scope.onEvent(new HttpSessionBound(this,session));
        OrderedDependentPojo source = (OrderedDependentPojo) scope.getContext("source").getInstance(null);
        assertNotNull(source.getPojo());
        // expire module
        assertEquals(2,source.getNumberInstantiated());
        scope.onEvent(new HttpSessionEnd(this,session));
        assertEquals(0,source.getNumberInstantiated());
        scope.stop();
    }


    public void testInitDestroyOrderRequestScope() throws Exception {
        EventContext ctx = new EventContextImpl();
        RequestScopeContext scope = new RequestScopeContext(ctx);
        scope.registerFactories(MockContextFactory.createWiredContexts(Scope.REQUEST,scope));
        scope.start();
        Object request =  new Object();
        scope.onEvent(new RequestStart(this,request));
        OrderedDependentPojo source = (OrderedDependentPojo) scope.getContext("source").getInstance(null);
        assertNotNull(source.getPojo());
        // expire module
        assertEquals(2,source.getNumberInstantiated());
        scope.onEvent(new RequestEnd(this,request));
        assertEquals(0,source.getNumberInstantiated());
        scope.stop();
    }

}
