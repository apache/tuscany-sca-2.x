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

import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.ConversationEnd;
import org.apache.tuscany.core.component.event.ConversationStart;
import org.apache.tuscany.core.mock.component.ConversationalScopeDestroyOnlyComponent;
import org.apache.tuscany.core.mock.component.ConversationalScopeInitDestroyComponent;
import org.apache.tuscany.core.mock.component.ConversationalScopeInitOnlyComponent;
import org.apache.tuscany.core.mock.component.OrderedEagerInitPojo;
import org.apache.tuscany.core.mock.component.OrderedInitPojo;
import org.apache.tuscany.core.mock.component.OrderedInitPojoImpl;
import org.apache.tuscany.core.mock.factories.MockFactory;
import org.apache.tuscany.spi.component.SystemAtomicComponent;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Scope;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Lifecycle unit tests for the conversational scope container
 *
 * @version $Rev: 451895 $ $Date: 2006-10-02 02:58:18 -0400 (Mon, 02 Oct 2006) $
 */
public class ConversationalScopeInstanceLifecycleTestCase extends TestCase {

    public void testInitDestroy() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ConversationalScopeContainer scope = new ConversationalScopeContainer(ctx);
        scope.start();

        SystemAtomicComponent initDestroyContext = MockFactory.createAtomicComponent("InitDestroy",
            scope,
            ConversationalScopeInitDestroyComponent.class);
        initDestroyContext.start();

        SystemAtomicComponent initOnlyContext = MockFactory.createAtomicComponent("InitOnly",
            scope,
            ConversationalScopeInitOnlyComponent.class);
        initOnlyContext.start();

        SystemAtomicComponent destroyOnlyContext = MockFactory.createAtomicComponent("DestroyOnly",
            scope,
            ConversationalScopeDestroyOnlyComponent.class);
        destroyOnlyContext.start();

        Object conversation = new Object();
        ctx.setIdentifier(Scope.CONVERSATIONAL, conversation);
        scope.onEvent(new ConversationStart(this, conversation));
        ConversationalScopeInitDestroyComponent initDestroy =
            (ConversationalScopeInitDestroyComponent) scope.getInstance(initDestroyContext);
        Assert.assertNotNull(initDestroy);

        ConversationalScopeInitOnlyComponent initOnly =
            (ConversationalScopeInitOnlyComponent) scope.getInstance(initOnlyContext);
        Assert.assertNotNull(initOnly);

        ConversationalScopeDestroyOnlyComponent destroyOnly =
            (ConversationalScopeDestroyOnlyComponent) scope.getInstance(destroyOnlyContext);
        Assert.assertNotNull(destroyOnly);

        Assert.assertTrue(initDestroy.isInitialized());
        Assert.assertTrue(initOnly.isInitialized());
        Assert.assertFalse(initDestroy.isDestroyed());
        Assert.assertFalse(destroyOnly.isDestroyed());

        // expire module
        scope.onEvent(new ConversationEnd(this, conversation));

        Assert.assertTrue(initDestroy.isDestroyed());
        Assert.assertTrue(destroyOnly.isDestroyed());

        scope.stop();
    }

    public void testDestroyOrder() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ConversationalScopeContainer scope = new ConversationalScopeContainer(ctx);
        scope.start();

        SystemAtomicComponent oneCtx =
            MockFactory.createAtomicComponent("one", scope, OrderedInitPojoImpl.class);
        scope.register(oneCtx);
        SystemAtomicComponent twoCtx =
            MockFactory.createAtomicComponent("two", scope, OrderedInitPojoImpl.class);
        scope.register(twoCtx);
        SystemAtomicComponent threeCtx =
            MockFactory.createAtomicComponent("three", scope, OrderedInitPojoImpl.class);
        scope.register(threeCtx);

        Object conversation = new Object();
        ctx.setIdentifier(Scope.CONVERSATIONAL, conversation);
        scope.onEvent(new ConversationStart(this, conversation));
        OrderedInitPojo one = (OrderedInitPojo) scope.getInstance(oneCtx);
        Assert.assertNotNull(one);
        Assert.assertEquals(1, one.getNumberInstantiated());
        Assert.assertEquals(1, one.getInitOrder());

        OrderedInitPojo two = (OrderedInitPojo) scope.getInstance(twoCtx);
        Assert.assertNotNull(two);
        Assert.assertEquals(2, two.getNumberInstantiated());
        Assert.assertEquals(2, two.getInitOrder());

        OrderedInitPojo three = (OrderedInitPojo) scope.getInstance(threeCtx);
        Assert.assertNotNull(three);
        Assert.assertEquals(3, three.getNumberInstantiated());
        Assert.assertEquals(3, three.getInitOrder());

        // expire module
        scope.onEvent(new ConversationEnd(this, conversation));
        Assert.assertEquals(0, one.getNumberInstantiated());
        scope.stop();
    }

    public void testEagerInitDestroyOrder() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ConversationalScopeContainer scope = new ConversationalScopeContainer(ctx);
        scope.start();

        SystemAtomicComponent oneCtx =
            MockFactory.createAtomicComponent("one", scope, OrderedEagerInitPojo.class);
        scope.register(oneCtx);
        SystemAtomicComponent twoCtx =
            MockFactory.createAtomicComponent("two", scope, OrderedEagerInitPojo.class);
        scope.register(twoCtx);
        SystemAtomicComponent threeCtx =
            MockFactory.createAtomicComponent("three", scope, OrderedEagerInitPojo.class);
        scope.register(threeCtx);

        Object conversation = new Object();
        ctx.setIdentifier(Scope.CONVERSATIONAL, conversation);
        scope.onEvent(new ConversationStart(this, conversation));
        OrderedEagerInitPojo one = (OrderedEagerInitPojo) scope.getInstance(oneCtx);
        Assert.assertNotNull(one);

        OrderedEagerInitPojo two = (OrderedEagerInitPojo) scope.getInstance(twoCtx);
        Assert.assertNotNull(two);

        OrderedEagerInitPojo three = (OrderedEagerInitPojo) scope.getInstance(threeCtx);
        Assert.assertNotNull(three);

        // expire module
        scope.onEvent(new ConversationEnd(this, conversation));
        Assert.assertEquals(0, one.getNumberInstantiated());
        scope.stop();
    }
}
