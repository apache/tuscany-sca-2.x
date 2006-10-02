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
import org.apache.tuscany.spi.component.SystemAtomicComponent;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.CompositeStart;
import org.apache.tuscany.core.component.event.CompositeStop;
import org.apache.tuscany.core.mock.component.ModuleScopeDestroyOnlyComponent;
import org.apache.tuscany.core.mock.component.ModuleScopeInitDestroyComponent;
import org.apache.tuscany.core.mock.component.ModuleScopeInitOnlyComponent;
import org.apache.tuscany.core.mock.component.OrderedEagerInitPojo;
import org.apache.tuscany.core.mock.component.OrderedInitPojo;
import org.apache.tuscany.core.mock.component.OrderedInitPojoImpl;
import org.apache.tuscany.core.mock.factories.MockFactory;

/**
 * Lifecycle unit tests for the module scope container
 *
 * @version $Rev$ $Date$
 */
public class ModuleScopeInstanceLifecycleTestCase extends TestCase {

    public void testInitDestroy() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContainer scope = new ModuleScopeContainer(ctx);
        scope.start();

        SystemAtomicComponent initDestroyContext = MockFactory.createAtomicComponent("InitDestroy",
            scope,
            ModuleScopeInitDestroyComponent.class);
        initDestroyContext.start();

        SystemAtomicComponent initOnlyContext = MockFactory.createAtomicComponent("InitOnly",
            scope,
            ModuleScopeInitOnlyComponent.class);
        initOnlyContext.start();

        SystemAtomicComponent destroyOnlyContext = MockFactory.createAtomicComponent("DestroyOnly",
            scope,
            ModuleScopeDestroyOnlyComponent.class);
        destroyOnlyContext.start();

        scope.onEvent(new CompositeStart(this, null));
        ModuleScopeInitDestroyComponent initDestroy =
            (ModuleScopeInitDestroyComponent) scope.getInstance(initDestroyContext);
        Assert.assertNotNull(initDestroy);

        ModuleScopeInitOnlyComponent initOnly = (ModuleScopeInitOnlyComponent) scope.getInstance(initOnlyContext);
        Assert.assertNotNull(initOnly);

        ModuleScopeDestroyOnlyComponent destroyOnly =
            (ModuleScopeDestroyOnlyComponent) scope.getInstance(destroyOnlyContext);
        Assert.assertNotNull(destroyOnly);

        Assert.assertTrue(initDestroy.isInitialized());
        Assert.assertTrue(initOnly.isInitialized());
        Assert.assertFalse(initDestroy.isDestroyed());
        Assert.assertFalse(destroyOnly.isDestroyed());

        // expire module
        scope.onEvent(new CompositeStop(this, null));

        Assert.assertTrue(initDestroy.isDestroyed());
        Assert.assertTrue(destroyOnly.isDestroyed());

        scope.stop();
    }

    public void testDestroyOrder() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContainer scope = new ModuleScopeContainer(ctx);
        scope.start();

        SystemAtomicComponent oneCtx = MockFactory.createAtomicComponent("one",
            scope,
            OrderedInitPojoImpl.class);
        scope.register(oneCtx);
        SystemAtomicComponent twoCtx = MockFactory.createAtomicComponent("two",
            scope,
            OrderedInitPojoImpl.class);
        scope.register(twoCtx);
        SystemAtomicComponent threeCtx = MockFactory.createAtomicComponent("three",
            scope,
            OrderedInitPojoImpl.class);
        scope.register(threeCtx);

        scope.onEvent(new CompositeStart(this, null));
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
        scope.onEvent(new CompositeStop(this, null));
        Assert.assertEquals(0, one.getNumberInstantiated());
        scope.stop();
    }

    public void testEagerInitDestroyOrder() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContainer scope = new ModuleScopeContainer(ctx);
        scope.start();

        SystemAtomicComponent oneCtx = MockFactory.createAtomicComponent("one",
            scope,
            OrderedEagerInitPojo.class);
        scope.register(oneCtx);
        SystemAtomicComponent twoCtx = MockFactory.createAtomicComponent("two",
            scope,
            OrderedEagerInitPojo.class);
        scope.register(twoCtx);
        SystemAtomicComponent threeCtx = MockFactory.createAtomicComponent("three",
            scope,
            OrderedEagerInitPojo.class);
        scope.register(threeCtx);

        scope.onEvent(new CompositeStart(this, null));
        OrderedEagerInitPojo one = (OrderedEagerInitPojo) scope.getInstance(oneCtx);
        Assert.assertNotNull(one);

        OrderedEagerInitPojo two = (OrderedEagerInitPojo) scope.getInstance(twoCtx);
        Assert.assertNotNull(two);

        OrderedEagerInitPojo three = (OrderedEagerInitPojo) scope.getInstance(threeCtx);
        Assert.assertNotNull(three);

        // expire module
        scope.onEvent(new CompositeStop(this, null));
        Assert.assertEquals(0, one.getNumberInstantiated());
        scope.stop();
    }

}
