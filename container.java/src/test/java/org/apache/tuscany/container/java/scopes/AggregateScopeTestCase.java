/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.tuscany.container.java.scopes;

import java.util.List;

import junit.framework.TestCase;

import org.apache.tuscany.container.java.mock.MockAggregateAssemblyFactory;
import org.apache.tuscany.container.java.mock.MockSystemAssemblyFactory;
import org.apache.tuscany.container.java.mock.components.GenericComponent;
import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.ContextConstants;
import org.apache.tuscany.core.context.impl.AggregateContextImpl;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.AggregateScopeContext;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ExtensibleModelObject;
import junit.framework.Assert;

/**
 * Tests component nesting. This test need to be in the container.java progject since it relies on Java POJOs for scope
 * testing.
 * 
 * @version $Rev$ $Date$
 */
public class AggregateScopeTestCase extends TestCase {

    /**
     * Ensures scope events are propagated in an aggregate scope
     */
    public void testAggregateScopePropagation() throws Exception {
        EventContext ctx = new EventContextImpl();
        AggregateContext moduleComponentCtx = new AggregateContextImpl();
        moduleComponentCtx.setName("testMC");
        AggregateScopeContext scopeContainer = new AggregateScopeContext(ctx);
        Component aggregateComponent = MockSystemAssemblyFactory.createDecoratedComponent("AggregateComponent",
                AggregateContextImpl.class.getName(), ContextConstants.AGGREGATE_SCOPE_ENUM, moduleComponentCtx);
        scopeContainer.registerConfiguration((RuntimeConfiguration<InstanceContext>) aggregateComponent
                .getComponentImplementation().getRuntimeConfiguration());
        scopeContainer.start();

        AggregateContext child = (AggregateContext) scopeContainer.getContext("AggregateComponent");
        List<ExtensibleModelObject> models = MockAggregateAssemblyFactory.createAssembly(moduleComponentCtx);
        for (ExtensibleModelObject model : models) {
            child.registerModelObject(model);
        }

        scopeContainer.onEvent(EventContext.MODULE_START, null);
        Object session = new Object();
        scopeContainer.onEvent(EventContext.REQUEST_START, null);
        scopeContainer.onEvent(EventContext.SESSION_NOTIFY, session);
        AggregateContext componentCtx = (AggregateContext) scopeContainer
                .getContext("AggregateComponent");
        GenericComponent testService1 = (GenericComponent) componentCtx.locateInstance("TestService1");
        GenericComponent testService2 = (GenericComponent) componentCtx.locateInstance("TestService2");
        GenericComponent testService3 = (GenericComponent) componentCtx.locateInstance("TestService3");
        Assert.assertNotNull(testService1);
        Assert.assertNotNull(testService2);
        Assert.assertNotNull(testService3);
        scopeContainer.onEvent(EventContext.REQUEST_END, null);
        scopeContainer.onEvent(EventContext.REQUEST_START, null);
        scopeContainer.onEvent(EventContext.SESSION_NOTIFY, session);

        GenericComponent testService2a = (GenericComponent) componentCtx.locateInstance("TestService2");
        Assert.assertNotNull(testService2a);
        GenericComponent testService3a = (GenericComponent) componentCtx.locateInstance("TestService3");
        Assert.assertNotNull(testService3a);
        Assert.assertEquals(testService2, testService2a);
        Assert.assertNotSame(testService3, testService3a);
        scopeContainer.onEvent(EventContext.REQUEST_END, null);
        scopeContainer.onEvent(EventContext.SESSION_END, session);

        Object session2 = new Object();
        scopeContainer.onEvent(EventContext.REQUEST_START, null);
        scopeContainer.onEvent(EventContext.SESSION_NOTIFY, session2);
        GenericComponent testService2b = (GenericComponent) componentCtx.locateInstance("TestService2");
        Assert.assertNotNull(testService2b);
        Assert.assertNotSame(testService2, testService2b);

        scopeContainer.onEvent(EventContext.REQUEST_END, null);
        scopeContainer.onEvent(EventContext.SESSION_END, session2);

    }

    /**
     * Ensures only child entry points (and not components) are accessible from parents
     */
    public void testAggregateNoEntryPoint() throws Exception {
        EventContext ctx = new EventContextImpl();
        AggregateContext moduleComponentCtx = new AggregateContextImpl();
        moduleComponentCtx.setName("testMC");
        AggregateScopeContext scopeContainer = new AggregateScopeContext(ctx);
        Component aggregateComponent = MockSystemAssemblyFactory.createDecoratedComponent("AggregateComponent",
                AggregateContextImpl.class.getName(), ContextConstants.AGGREGATE_SCOPE_ENUM, moduleComponentCtx);
        scopeContainer.registerConfiguration((RuntimeConfiguration<InstanceContext>) aggregateComponent
                .getComponentImplementation().getRuntimeConfiguration());
        scopeContainer.start();
        AggregateContext child = (AggregateContext) scopeContainer.getContext("AggregateComponent");
        List<ExtensibleModelObject> parts = MockAggregateAssemblyFactory.createAssembly(moduleComponentCtx);
        for (ExtensibleModelObject part : parts) {
            child.registerModelObject(part);
        }

        // aggregate.onEvent(EventContext.SYSTEM_START, null);
        scopeContainer.onEvent(EventContext.MODULE_START, null);
        QualifiedName name = new QualifiedName("AggregateComponent/TestService1");
        AggregateContext componentCtx = (AggregateContext) scopeContainer
                .getContext("AggregateComponent");
    }

    /**
     * Tests adding a context before its parent has been started
     */
    public void testRegisterContextBeforeStart() throws Exception {
        EventContext ctx = new EventContextImpl();
        AggregateContext moduleComponentCtx = new AggregateContextImpl();
        moduleComponentCtx.setName("testMC");
        AggregateScopeContext scopeContainer = new AggregateScopeContext(ctx);
        Component aggregateComponent = MockSystemAssemblyFactory.createDecoratedComponent("AggregateComponent",
                AggregateContextImpl.class.getName(), ContextConstants.AGGREGATE_SCOPE_ENUM, moduleComponentCtx);
        scopeContainer.registerConfiguration((RuntimeConfiguration<InstanceContext>) aggregateComponent
                .getComponentImplementation().getRuntimeConfiguration());
        scopeContainer.start();

        scopeContainer.onEvent(EventContext.MODULE_START, null);
        QualifiedName name = new QualifiedName("AggregateComponent/TestService1");
        AggregateContext componentCtx = (AggregateContext) scopeContainer
                .getContext("AggregateComponent");
        scopeContainer.onEvent(EventContext.MODULE_STOP, null);
        scopeContainer.stop();
    }

    /**
     * Tests adding a context after its parent has been started
     */
    public void testRegisterContextAfterStart() throws Exception {
        EventContext ctx = new EventContextImpl();
        AggregateContext moduleComponentCtx = new AggregateContextImpl();
        moduleComponentCtx.setName("testMC");
        AggregateScopeContext scopeContainer = new AggregateScopeContext(ctx);
        Component aggregateComponent = MockSystemAssemblyFactory.createDecoratedComponent("AggregateComponent",
                AggregateContextImpl.class.getName(), ContextConstants.AGGREGATE_SCOPE_ENUM, moduleComponentCtx);
        scopeContainer.start();

        scopeContainer.onEvent(EventContext.MODULE_START, null);
        scopeContainer.registerConfiguration((RuntimeConfiguration<InstanceContext>) aggregateComponent
                .getComponentImplementation().getRuntimeConfiguration());
        QualifiedName name = new QualifiedName("AggregateComponent/TestService1");
        AggregateContext componentCtx = (AggregateContext) scopeContainer
                .getContext("AggregateComponent");
        scopeContainer.onEvent(EventContext.MODULE_STOP, null);
        scopeContainer.stop();
    }

}
