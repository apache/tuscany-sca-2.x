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

import org.apache.tuscany.spi.component.WorkContext;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.RequestEnd;
import org.apache.tuscany.core.component.event.RequestStart;
import org.apache.tuscany.core.implementation.system.component.SystemAtomicComponent;
import org.apache.tuscany.core.mock.component.OrderedEagerInitPojo;
import org.apache.tuscany.core.mock.component.OrderedInitPojo;
import org.apache.tuscany.core.mock.component.OrderedInitPojoImpl;
import org.apache.tuscany.core.mock.component.RequestScopeDestroyOnlyComponent;
import org.apache.tuscany.core.mock.component.RequestScopeInitDestroyComponent;
import org.apache.tuscany.core.mock.component.RequestScopeInitOnlyComponent;
import org.apache.tuscany.core.mock.factories.MockFactory;

/**
 * Lifecycle unit tests for the module scope container
 *
 * @version $Rev$ $Date$
 */
public class RequestScopeInstanceLifecycleTestCase extends TestCase {

    public void testInitDestroy() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        RequestScopeContainer scope = new RequestScopeContainer(ctx);
        scope.start();

        SystemAtomicComponent initDestroyContext = MockFactory
            .createAtomicComponent("InitDestroy", scope, RequestScopeInitDestroyComponent.class);
        initDestroyContext.start();

        SystemAtomicComponent initOnlyContext =
            MockFactory.createAtomicComponent("InitOnly", scope, RequestScopeInitOnlyComponent.class);
        initOnlyContext.start();

        SystemAtomicComponent destroyOnlyContext = MockFactory
            .createAtomicComponent("DestroyOnly", scope, RequestScopeDestroyOnlyComponent.class);
        destroyOnlyContext.start();

        scope.onEvent(new RequestStart(this));
        RequestScopeInitDestroyComponent initDestroy =
            (RequestScopeInitDestroyComponent) scope.getInstance(initDestroyContext);
        Assert.assertNotNull(initDestroy);

        RequestScopeInitOnlyComponent initOnly = (RequestScopeInitOnlyComponent) scope.getInstance(initOnlyContext);
        Assert.assertNotNull(initOnly);

        RequestScopeDestroyOnlyComponent destroyOnly =
            (RequestScopeDestroyOnlyComponent) scope.getInstance(destroyOnlyContext);
        Assert.assertNotNull(destroyOnly);

        Assert.assertTrue(initDestroy.isInitialized());
        Assert.assertTrue(initOnly.isInitialized());
        Assert.assertFalse(initDestroy.isDestroyed());
        Assert.assertFalse(destroyOnly.isDestroyed());

        // expire module
        scope.onEvent(new RequestEnd(this));

        Assert.assertTrue(initDestroy.isDestroyed());
        Assert.assertTrue(destroyOnly.isDestroyed());

        scope.stop();
    }

    public void testDestroyOrder() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        RequestScopeContainer scope = new RequestScopeContainer(ctx);
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

        scope.onEvent(new RequestStart(this));
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
        scope.onEvent(new RequestEnd(this));
        Assert.assertEquals(0, one.getNumberInstantiated());
        scope.stop();
    }

    public void testEagerInitDestroyOrder() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        RequestScopeContainer scope = new RequestScopeContainer(ctx);
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

        scope.onEvent(new RequestStart(this));
        OrderedEagerInitPojo one = (OrderedEagerInitPojo) scope.getInstance(oneCtx);
        Assert.assertNotNull(one);

        OrderedEagerInitPojo two = (OrderedEagerInitPojo) scope.getInstance(twoCtx);
        Assert.assertNotNull(two);

        OrderedEagerInitPojo three = (OrderedEagerInitPojo) scope.getInstance(threeCtx);
        Assert.assertNotNull(three);

        // expire module
        scope.onEvent(new RequestEnd(this));
        Assert.assertEquals(0, one.getNumberInstantiated());
        scope.stop();
    }

}
