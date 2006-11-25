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


import org.apache.tuscany.spi.component.SystemAtomicComponent;
import org.apache.tuscany.spi.component.WorkContext;

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

        SystemAtomicComponent initDestroyComponent = MockFactory.createAtomicComponent("InitDestroy",
            scope,
            ModuleScopeInitDestroyComponent.class);
        initDestroyComponent.start();

        SystemAtomicComponent initOnlyComponent = MockFactory.createAtomicComponent("InitOnly",
            scope,
            ModuleScopeInitOnlyComponent.class);
        initOnlyComponent.start();

        SystemAtomicComponent destroyOnlyComponent = MockFactory.createAtomicComponent("DestroyOnly",
            scope,
            ModuleScopeDestroyOnlyComponent.class);
        destroyOnlyComponent.start();

        scope.onEvent(new CompositeStart(this, null));
        ModuleScopeInitDestroyComponent initDestroy =
            (ModuleScopeInitDestroyComponent) scope.getInstance(initDestroyComponent);
        assertNotNull(initDestroy);

        ModuleScopeInitOnlyComponent initOnly = (ModuleScopeInitOnlyComponent) scope.getInstance(initOnlyComponent);
        assertNotNull(initOnly);

        ModuleScopeDestroyOnlyComponent destroyOnly =
            (ModuleScopeDestroyOnlyComponent) scope.getInstance(destroyOnlyComponent);
        assertNotNull(destroyOnly);

        assertTrue(initDestroy.isInitialized());
        assertTrue(initOnly.isInitialized());
        assertFalse(initDestroy.isDestroyed());
        assertFalse(destroyOnly.isDestroyed());

        // expire module
        scope.onEvent(new CompositeStop(this, null));

        assertTrue(initDestroy.isDestroyed());
        assertTrue(destroyOnly.isDestroyed());

        scope.stop();
    }

    public void testDestroyOrder() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContainer scope = new ModuleScopeContainer(ctx);
        scope.start();

        SystemAtomicComponent oneComponent = MockFactory.createAtomicComponent("one",
            scope,
            OrderedInitPojoImpl.class);
        scope.register(oneComponent);
        SystemAtomicComponent twoComponent = MockFactory.createAtomicComponent("two",
            scope,
            OrderedInitPojoImpl.class);
        scope.register(twoComponent);
        SystemAtomicComponent threeComponent = MockFactory.createAtomicComponent("three",
            scope,
            OrderedInitPojoImpl.class);
        scope.register(threeComponent);

        scope.onEvent(new CompositeStart(this, null));
        OrderedInitPojo one = (OrderedInitPojo) scope.getInstance(oneComponent);
        assertNotNull(one);
        assertEquals(1, one.getNumberInstantiated());
        assertEquals(1, one.getInitOrder());

        OrderedInitPojo two = (OrderedInitPojo) scope.getInstance(twoComponent);
        assertNotNull(two);
        assertEquals(2, two.getNumberInstantiated());
        assertEquals(2, two.getInitOrder());

        OrderedInitPojo three = (OrderedInitPojo) scope.getInstance(threeComponent);
        assertNotNull(three);
        assertEquals(3, three.getNumberInstantiated());
        assertEquals(3, three.getInitOrder());

        // expire module
        scope.onEvent(new CompositeStop(this, null));
        assertEquals(0, one.getNumberInstantiated());
        scope.stop();
    }

    public void testEagerInitDestroyOrder() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContainer scope = new ModuleScopeContainer(ctx);
        scope.start();

        SystemAtomicComponent oneComponent = MockFactory.createAtomicComponent("one",
            scope,
            OrderedEagerInitPojo.class);
        scope.register(oneComponent);
        SystemAtomicComponent twoComponent = MockFactory.createAtomicComponent("two",
            scope,
            OrderedEagerInitPojo.class);
        scope.register(twoComponent);
        SystemAtomicComponent threeComponent = MockFactory.createAtomicComponent("three",
            scope,
            OrderedEagerInitPojo.class);
        scope.register(threeComponent);

        scope.onEvent(new CompositeStart(this, null));
        OrderedEagerInitPojo one = (OrderedEagerInitPojo) scope.getInstance(oneComponent);
        assertNotNull(one);

        OrderedEagerInitPojo two = (OrderedEagerInitPojo) scope.getInstance(twoComponent);
        assertNotNull(two);

        OrderedEagerInitPojo three = (OrderedEagerInitPojo) scope.getInstance(threeComponent);
        assertNotNull(three);

        // expire module
        scope.onEvent(new CompositeStop(this, null));
        assertEquals(0, one.getNumberInstantiated());
        scope.stop();
    }

}
