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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.container.java.builder.JavaContextFactoryBuilder;
import org.apache.tuscany.container.java.mock.MockFactory;
import org.apache.tuscany.container.java.mock.components.GenericComponent;
import org.apache.tuscany.container.java.mock.components.ModuleScopeComponentImpl;
import org.apache.tuscany.container.java.mock.components.SessionScopeComponentImpl;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.impl.AggregateContextImpl;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.AggregateScopeContext;
import org.apache.tuscany.model.assembly.Extensible;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.SimpleComponent;

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
        scopeContainer.registerFactory(MockFactory.createAggregateConfiguration("AggregateComponent", moduleComponentCtx));
        scopeContainer.start();
        AggregateContext child = (AggregateContext) scopeContainer.getContext("AggregateComponent");
        List<Extensible> models = createAssembly(moduleComponentCtx);
        for (Extensible model : models) {
            child.registerModelObject(model);
        }

        scopeContainer.onEvent(EventContext.MODULE_START, null);
        Object session = new Object();
        scopeContainer.onEvent(EventContext.REQUEST_START, null);
        scopeContainer.onEvent(EventContext.SESSION_NOTIFY, session);
        AggregateContext componentCtx = (AggregateContext) scopeContainer.getContext("AggregateComponent");
        GenericComponent testService1 = (GenericComponent) componentCtx.getContext("TestService1").getInstance(null);
        GenericComponent testService2 = (GenericComponent) componentCtx.getContext("TestService2").getInstance(null);
        GenericComponent testService3 = (GenericComponent) componentCtx.getContext("TestService3").getInstance(null);
        Assert.assertNotNull(testService1);
        Assert.assertNotNull(testService2);
        Assert.assertNotNull(testService3);
        scopeContainer.onEvent(EventContext.REQUEST_END, null);
        scopeContainer.onEvent(EventContext.REQUEST_START, null);
        scopeContainer.onEvent(EventContext.SESSION_NOTIFY, session);

        GenericComponent testService2a = (GenericComponent) componentCtx.getContext("TestService2").getInstance(null);
        Assert.assertNotNull(testService2a);
        GenericComponent testService3a = (GenericComponent) componentCtx.getContext("TestService3").getInstance(null);
        Assert.assertNotNull(testService3a);
        Assert.assertEquals(testService2, testService2a);
        Assert.assertNotSame(testService3, testService3a);
        scopeContainer.onEvent(EventContext.REQUEST_END, null);
        scopeContainer.onEvent(EventContext.SESSION_END, session);

        Object session2 = new Object();
        scopeContainer.onEvent(EventContext.REQUEST_START, null);
        scopeContainer.onEvent(EventContext.SESSION_NOTIFY, session2);
        GenericComponent testService2b = (GenericComponent) componentCtx.getContext("TestService2").getInstance(null);
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
        scopeContainer.registerFactory(MockFactory.createAggregateConfiguration("AggregateComponent", moduleComponentCtx));
        scopeContainer.start();
        AggregateContext child = (AggregateContext) scopeContainer.getContext("AggregateComponent");
        List<Extensible> parts = createAssembly(moduleComponentCtx);
        for (Extensible part : parts) {
            child.registerModelObject(part);
        }

        // aggregate.onEvent(EventContext.SYSTEM_START, null);
        scopeContainer.onEvent(EventContext.MODULE_START, null);
        QualifiedName name = new QualifiedName("AggregateComponent/TestService1");
        AggregateContext componentCtx = (AggregateContext) scopeContainer.getContext("AggregateComponent");
    }

    /**
     * Tests adding a context before its parent has been started
     */
    public void testRegisterContextBeforeStart() throws Exception {
        EventContext ctx = new EventContextImpl();
        AggregateContext moduleComponentCtx = new AggregateContextImpl();
        moduleComponentCtx.setName("testMC");
        AggregateScopeContext scopeContainer = new AggregateScopeContext(ctx);
        scopeContainer.registerFactory(MockFactory.createAggregateConfiguration("AggregateComponent", moduleComponentCtx));
        scopeContainer.start();
        scopeContainer.onEvent(EventContext.MODULE_START, null);
        QualifiedName name = new QualifiedName("AggregateComponent/TestService1");
        AggregateContext componentCtx = (AggregateContext) scopeContainer.getContext("AggregateComponent");
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
        scopeContainer.start();

        scopeContainer.onEvent(EventContext.MODULE_START, null);
        scopeContainer.registerFactory(MockFactory.createAggregateConfiguration("AggregateComponent", moduleComponentCtx));
        QualifiedName name = new QualifiedName("AggregateComponent/TestService1");
        AggregateContext componentCtx = (AggregateContext) scopeContainer.getContext("AggregateComponent");
        scopeContainer.onEvent(EventContext.MODULE_STOP, null);
        scopeContainer.stop();
    }

    /**
     * Creats an assembly containing a module-scoped component definition, a session-scoped component definition, and a
     * request-scoped component definition
     * 
     * @param ctx the parent module context
     */
    private List<Extensible> createAssembly(AggregateContext ctx) throws BuilderException {
        JavaContextFactoryBuilder builder = new JavaContextFactoryBuilder();
        SimpleComponent component = MockFactory.createComponent("TestService1", ModuleScopeComponentImpl.class, Scope.MODULE);
        SimpleComponent sessionComponent = MockFactory.createComponent("TestService2", SessionScopeComponentImpl.class,
                Scope.SESSION);
        SimpleComponent requestComponent = MockFactory.createComponent("TestService3", SessionScopeComponentImpl.class,
                Scope.REQUEST);
        builder.build(component);
        builder.build(sessionComponent);
        builder.build(requestComponent);
        List<Extensible> configs = new ArrayList();
        configs.add(component);
        configs.add(sessionComponent);
        configs.add(requestComponent);
        return configs;
    }

}
