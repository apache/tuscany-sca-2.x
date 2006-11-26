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

import java.util.Map;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Scope;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.CompositeStart;
import org.apache.tuscany.core.component.event.CompositeStop;
import org.apache.tuscany.core.component.event.HttpSessionEnd;
import org.apache.tuscany.core.component.event.RequestEnd;
import org.apache.tuscany.core.component.event.RequestStart;
import org.apache.tuscany.core.mock.component.OrderedDependentPojo;
import org.apache.tuscany.core.mock.component.OrderedDependentPojoImpl;
import org.apache.tuscany.core.mock.component.OrderedInitPojo;
import org.apache.tuscany.core.mock.component.OrderedInitPojoImpl;

/**
 * Tests dependencies are initalized and destroyed in the proper order (i.e. LIFO)
 *
 * @version $Rev$ $Date$
 */
public class DependencyLifecycleTestCase extends TestCase {

    public void testInitDestroyOrderModuleScope() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContainer scopeCtx = new ModuleScopeContainer(ctx);
        scopeCtx.start();
        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source",
            OrderedDependentPojoImpl.class,
            scopeCtx,
            "target",
            OrderedInitPojoImpl.class,
            scopeCtx);
        for (AtomicComponent component : contexts.values()) {
            scopeCtx.register(component);
        }
        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        scopeCtx.register(sourceComponent);
        scopeCtx.register(targetComponent);
        scopeCtx.onEvent(new CompositeStart(this, null));
        OrderedDependentPojo source = (OrderedDependentPojo) scopeCtx.getInstance(sourceComponent);
        OrderedInitPojo target = (OrderedInitPojo) scopeCtx.getInstance(targetComponent);
        assertNotNull(source.getPojo());
        assertNotNull(target);
        assertEquals(2, source.getNumberInstantiated());
        scopeCtx.onEvent(new CompositeStop(this, null));
        assertEquals(0, source.getNumberInstantiated());
        scopeCtx.stop();
    }

    public void testInitDestroyOrderAfterStartModuleScope() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContainer scopeCtx = new ModuleScopeContainer(ctx);
        scopeCtx.start();
        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source",
            OrderedDependentPojoImpl.class,
            scopeCtx,
            "target",
            OrderedInitPojoImpl.class,
            scopeCtx);
        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        scopeCtx.onEvent(new CompositeStart(this, null));
        scopeCtx.register(sourceComponent);
        scopeCtx.register(targetComponent);
        OrderedDependentPojo source = (OrderedDependentPojo) scopeCtx.getInstance(sourceComponent);
        OrderedInitPojo target = (OrderedInitPojo) scopeCtx.getInstance(targetComponent);
        assertNotNull(source.getPojo());
        assertNotNull(target);
        assertEquals(2, source.getNumberInstantiated());
        scopeCtx.onEvent(new CompositeStop(this, null));
        assertEquals(0, source.getNumberInstantiated());
        scopeCtx.stop();
    }


    public void testInitDestroyOrderSessionScope() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        HttpSessionScopeContainer scopeCtx = new HttpSessionScopeContainer(ctx);
        scopeCtx.start();
        Object session = new Object();
        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source",
            OrderedDependentPojoImpl.class,
            scopeCtx,
            "target",
            OrderedInitPojoImpl.class,
            scopeCtx);
        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        scopeCtx.register(sourceComponent);
        scopeCtx.register(targetComponent);
        ctx.setIdentifier(Scope.SESSION, session);
        OrderedDependentPojo source = (OrderedDependentPojo) scopeCtx.getInstance(sourceComponent);
        assertNotNull(source.getPojo());
        assertEquals(2, source.getNumberInstantiated());
        scopeCtx.onEvent(new HttpSessionEnd(this, session));
        assertEquals(0, source.getNumberInstantiated());
        scopeCtx.stop();
    }


    public void testInitDestroyOrderAfterStartSessionScope() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        HttpSessionScopeContainer scopeCtx = new HttpSessionScopeContainer(ctx);
        scopeCtx.start();
        Object session = new Object();
        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source",
            OrderedDependentPojoImpl.class,
            scopeCtx,
            "target",
            OrderedInitPojoImpl.class,
            scopeCtx);
        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        ctx.setIdentifier(Scope.SESSION, session);
        scopeCtx.register(sourceComponent);
        scopeCtx.register(targetComponent);
        OrderedDependentPojo source = (OrderedDependentPojo) scopeCtx.getInstance(sourceComponent);
        assertNotNull(source.getPojo());
        assertEquals(2, source.getNumberInstantiated());
        scopeCtx.onEvent(new HttpSessionEnd(this, session));
        assertEquals(0, source.getNumberInstantiated());
        scopeCtx.stop();
    }

    public void testInitDestroyOrderRequestScope() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        RequestScopeContainer scopeCtx = new RequestScopeContainer(ctx);
        scopeCtx.start();
        scopeCtx.onEvent(new RequestStart(this));
        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source",
            OrderedDependentPojoImpl.class,
            scopeCtx,
            "target",
            OrderedInitPojoImpl.class,
            scopeCtx);
        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        scopeCtx.register(sourceComponent);
        scopeCtx.register(targetComponent);
        OrderedDependentPojo source = (OrderedDependentPojo) scopeCtx.getInstance(sourceComponent);
        assertNotNull(source.getPojo());
        assertEquals(2, source.getNumberInstantiated());
        scopeCtx.onEvent(new RequestEnd(this));
        assertEquals(0, source.getNumberInstantiated());
        scopeCtx.stop();
    }

}
