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
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.RequestStart;
import org.apache.tuscany.core.context.event.HttpSessionBound;
import org.apache.tuscany.core.context.event.RequestEnd;
import org.apache.tuscany.core.context.event.HttpSessionEnd;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.impl.CompositeContextImpl;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.CompositeScopeContext;
import org.apache.tuscany.model.assembly.Extensible;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.AtomicComponent;

/**
 * Tests component nesting. This test needs to be in the container.java progject since it relies on Java POJOs for scope
 * testing.
 * 
 * @version $Rev$ $Date$
 */
public class CompositeScopeTestCase extends TestCase {

    /**
     * Ensures scope events are propagated in an composite scope
     */
    public void testCompositeScopePropagation() throws Exception {
        EventContext ctx = new EventContextImpl();
        CompositeContext moduleComponentCtx = new CompositeContextImpl();
        moduleComponentCtx.setName("testMC");
        moduleComponentCtx.start();
        CompositeScopeContext scopeContainer = new CompositeScopeContext(ctx);
        scopeContainer.registerFactory(MockFactory.createCompositeConfiguration("CompositeComponent"));
        scopeContainer.start();
        CompositeContext child = (CompositeContext) scopeContainer.getContext("CompositeComponent");
        List<Extensible> models = createAssembly();
        for (Extensible model : models) {
            child.registerModelObject(model);
        }

        scopeContainer.onEvent(new ModuleStart(this));
        Object session = new Object();
        Object id = new Object();
        //ctx.setIdentifier(EventContext.SESSION,session);
        scopeContainer.onEvent(new RequestStart(this,id));
        scopeContainer.onEvent(new HttpSessionBound(this,session));
        CompositeContext componentCtx = (CompositeContext) scopeContainer.getContext("CompositeComponent");
        GenericComponent testService1 = (GenericComponent) componentCtx.getContext("TestService1").getInstance(null);
        GenericComponent testService2 = (GenericComponent) componentCtx.getContext("TestService2").getInstance(null);
        GenericComponent testService3 = (GenericComponent) componentCtx.getContext("TestService3").getInstance(null);
        Assert.assertNotNull(testService1);
        Assert.assertNotNull(testService2);
        Assert.assertNotNull(testService3);
        scopeContainer.onEvent(new RequestEnd(this,id));
        scopeContainer.onEvent(new RequestStart(this,id));
        scopeContainer.onEvent(new HttpSessionBound(this,session));

        GenericComponent testService2a = (GenericComponent) componentCtx.getContext("TestService2").getInstance(null);
        Assert.assertNotNull(testService2a);
        GenericComponent testService3a = (GenericComponent) componentCtx.getContext("TestService3").getInstance(null);
        Assert.assertNotNull(testService3a);
        Assert.assertEquals(testService2, testService2a);
        Assert.assertNotSame(testService3, testService3a);
        scopeContainer.onEvent(new RequestEnd(this,id));
        scopeContainer.onEvent(new HttpSessionEnd(this,session));

        Object session2 = new Object();
        Object id2 = new Object();
        scopeContainer.onEvent(new RequestStart(this,id2));
        scopeContainer.onEvent(new HttpSessionBound(this,session2));
        GenericComponent testService2b = (GenericComponent) componentCtx.getContext("TestService2").getInstance(null);
        Assert.assertNotNull(testService2b);
        Assert.assertNotSame(testService2, testService2b);

        scopeContainer.onEvent(new RequestEnd(this,id2));
        scopeContainer.onEvent(new HttpSessionEnd(this,session2));

    }

    /**
     * Ensures only child entry points (and not components) are accessible from parents
     */
    public void testCompositeNoEntryPoint() throws Exception {
        EventContext ctx = new EventContextImpl();
        CompositeContext moduleComponentCtx = new CompositeContextImpl();
        moduleComponentCtx.setName("testMC");
        CompositeScopeContext scopeContainer = new CompositeScopeContext(ctx);
        scopeContainer.registerFactory(MockFactory.createCompositeConfiguration("CompositeComponent"));
        scopeContainer.start();
        CompositeContext child = (CompositeContext) scopeContainer.getContext("CompositeComponent");
        List<Extensible> parts = createAssembly();
        for (Extensible part : parts) {
            child.registerModelObject(part);
        }
        scopeContainer.onEvent(new ModuleStart(this));
        scopeContainer.getContext("CompositeComponent");
    }

    /**
     * Tests adding a context before its parent has been started
     */
    public void testRegisterContextBeforeStart() throws Exception {
        EventContext ctx = new EventContextImpl();
        CompositeContext moduleComponentCtx = new CompositeContextImpl();
        moduleComponentCtx.setName("testMC");
        CompositeScopeContext scopeContainer = new CompositeScopeContext(ctx);
        scopeContainer.registerFactory(MockFactory.createCompositeConfiguration("CompositeComponent"));
        scopeContainer.start();
        scopeContainer.onEvent(new ModuleStart(this));
        scopeContainer.getContext("CompositeComponent");
        scopeContainer.onEvent(new ModuleStop(this));
        scopeContainer.stop();
    }

    /**
     * Tests adding a context after its parent has been started
     */
    public void testRegisterContextAfterStart() throws Exception {
        EventContext ctx = new EventContextImpl();
        CompositeContext moduleComponentCtx = new CompositeContextImpl();
        moduleComponentCtx.setName("testMC");
        CompositeScopeContext scopeContainer = new CompositeScopeContext(ctx);
        scopeContainer.start();

        scopeContainer.onEvent(new ModuleStart(this));
        scopeContainer.registerFactory(MockFactory.createCompositeConfiguration("CompositeComponent"));
        scopeContainer.getContext("CompositeComponent");
        scopeContainer.onEvent(new ModuleStop(this));
        scopeContainer.stop();
    }

    /**
     * Creats an assembly containing a module-scoped component definition, a session-scoped component definition, and a
     * request-scoped component definition
     *
     */
    private List<Extensible> createAssembly() throws BuilderException {
        JavaContextFactoryBuilder builder = new JavaContextFactoryBuilder();
        AtomicComponent component = MockFactory.createComponent("TestService1", ModuleScopeComponentImpl.class, Scope.MODULE);
        AtomicComponent sessionComponent = MockFactory.createComponent("TestService2", SessionScopeComponentImpl.class,
                Scope.SESSION);
        AtomicComponent requestComponent = MockFactory.createComponent("TestService3", SessionScopeComponentImpl.class,
                Scope.REQUEST);
        builder.build(component);
        builder.build(sessionComponent);
        builder.build(requestComponent);
        List<Extensible> configs = new ArrayList<Extensible>();
        configs.add(component);
        configs.add(sessionComponent);
        configs.add(requestComponent);
        return configs;
    }

}
