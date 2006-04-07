/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.container.java.integration.context;

import org.apache.tuscany.container.java.mock.MockFactory;
import org.apache.tuscany.container.java.mock.components.GenericComponent;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.model.assembly.Scope;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Tests scoping is properly handled for service references 
 * 
 * @version $Rev$ $Date$
 */
public class ScopeReferenceTestCase extends TestCase {

    /**
     * Tests a module-to-module scoped wire is setup properly by the runtime
     */
    public void testModuleToModule() throws Exception{
        RuntimeContext runtime = MockFactory.createJavaRuntime();
        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
        Assert.assertNotNull(ctx);
        runtime.getRootContext().registerModelObject(MockFactory.createAggregateComponent("test"));
        AggregateContext testCtx = (AggregateContext) runtime.getRootContext().getContext("test");
        Assert.assertNotNull(testCtx);
        testCtx.registerModelObject(MockFactory.createModule());
        testCtx.fireEvent(EventContext.MODULE_START,null);
        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source);
        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
        Assert.assertNotNull(target);
        source.getGenericComponent().getString();
   }

    /**
     * Tests a module-to-session scoped wire is setup properly by the runtime
     */
    public void testModuleToSession() throws Exception{
        RuntimeContext runtime = MockFactory.createJavaRuntime();
        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
        Assert.assertNotNull(ctx);
        runtime.getRootContext().registerModelObject(MockFactory.createAggregateComponent("test"));
        AggregateContext testCtx = (AggregateContext) runtime.getRootContext().getContext("test");
        Assert.assertNotNull(testCtx);
        testCtx.registerModelObject(MockFactory.createModule(Scope.MODULE,Scope.SESSION));
        testCtx.fireEvent(EventContext.MODULE_START,null);
        
        // first session
        Object session = new Object();
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        testCtx.fireEvent(EventContext.SESSION_NOTIFY,session);
        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source);
        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
        Assert.assertNotNull(target);
        source.getGenericComponent().setString("foo");
        Assert.assertEquals("foo",target.getString());
        testCtx.fireEvent(EventContext.REQUEST_END,session);
        
        //second session
        Object session2 = new Object();
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        testCtx.fireEvent(EventContext.SESSION_NOTIFY,session2);
        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);
        Assert.assertNotNull(target2);
        Assert.assertTrue(!"foo".equals(target2.getString()));
        
        Assert.assertTrue(!"foo".equals(source.getGenericComponent().getString()));
        source.getGenericComponent().setString("bar");
        Assert.assertEquals("bar",target2.getString());
        Assert.assertEquals("bar",source.getGenericComponent().getString());
        //testCtx.fireEvent(EventContext.SESSION_NOTIFY,session);
                
   }

    /**
     * Tests a module-to-request scoped wire is setup properly by the runtime
     */
    public void testModuleToRequest() throws Exception{
        RuntimeContext runtime = MockFactory.createJavaRuntime();
        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
        Assert.assertNotNull(ctx);
        runtime.getRootContext().registerModelObject(MockFactory.createAggregateComponent("test"));
        AggregateContext testCtx = (AggregateContext) runtime.getRootContext().getContext("test");
        Assert.assertNotNull(testCtx);
        testCtx.registerModelObject(MockFactory.createModule(Scope.MODULE,Scope.REQUEST));
        testCtx.fireEvent(EventContext.MODULE_START,null);
        
        // first request
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source);
        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
        Assert.assertNotNull(target);
        source.getGenericComponent().setString("foo");
        Assert.assertEquals("foo",target.getString());
        testCtx.fireEvent(EventContext.REQUEST_END,null);
        
        //second request
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);
        Assert.assertNotNull(target2);
        Assert.assertTrue(!"foo".equals(target2.getString()));
        
        Assert.assertTrue(!"foo".equals(source.getGenericComponent().getString()));
        source.getGenericComponent().setString("bar");
        Assert.assertEquals("bar",target2.getString());
        Assert.assertEquals("bar",source.getGenericComponent().getString());

    }
    
    /**
     * Tests a module-to-stateless scoped wire is setup properly by the runtime
     */
    public void testModuleToStateless() throws Exception{
        RuntimeContext runtime = MockFactory.createJavaRuntime();
        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
        Assert.assertNotNull(ctx);
        runtime.getRootContext().registerModelObject(MockFactory.createAggregateComponent("test"));
        AggregateContext testCtx = (AggregateContext) runtime.getRootContext().getContext("test");
        Assert.assertNotNull(testCtx);
        testCtx.registerModelObject(MockFactory.createModule(Scope.MODULE,Scope.INSTANCE));
        testCtx.fireEvent(EventContext.MODULE_START,null);
        
        // first request
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source);
        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
        Assert.assertNotNull(target);
        source.getGenericComponent().setString("foo");
        Assert.assertTrue(!"foo".equals(target.getString()));
        testCtx.fireEvent(EventContext.REQUEST_END,null);
        
        //second request
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);
        Assert.assertNotNull(target2);
        Assert.assertTrue(!"foo".equals(target2.getString()));
        
        Assert.assertTrue(!"foo".equals(source.getGenericComponent().getString()));
        source.getGenericComponent().setString("bar");
        Assert.assertTrue(!"bar".equals(target2.getString()));
   }
    
    /**
     * Tests a session-to-session scoped wire is setup properly by the runtime
     */
    public void testSessionToSession() throws Exception{
        RuntimeContext runtime = MockFactory.createJavaRuntime();
        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
        Assert.assertNotNull(ctx);
        runtime.getRootContext().registerModelObject(MockFactory.createAggregateComponent("test"));
        AggregateContext testCtx = (AggregateContext) runtime.getRootContext().getContext("test");
        Assert.assertNotNull(testCtx);
        testCtx.registerModelObject(MockFactory.createModule(Scope.SESSION,Scope.SESSION));
        testCtx.fireEvent(EventContext.MODULE_START,null);
        
        // first session
        Object session = new Object();
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        testCtx.fireEvent(EventContext.SESSION_NOTIFY,session);
        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source);
        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
        Assert.assertNotNull(target);
        source.getGenericComponent().setString("foo");
        source.getGenericComponent().setString("foo");
        Assert.assertEquals("foo",target.getString());
        testCtx.fireEvent(EventContext.REQUEST_END,session);
        
        //second session
        Object session2 = new Object();
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        testCtx.fireEvent(EventContext.SESSION_NOTIFY,session2);
        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source2);
        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);

        Assert.assertNotNull(target2);
        Assert.assertEquals(null,target2.getString());
        Assert.assertEquals(null,source2.getGenericComponent().getString());
        source2.getGenericComponent().setString("baz");
        Assert.assertEquals("baz",source2.getGenericComponent().getString());
        Assert.assertEquals("baz",target2.getString());
        
        testCtx.fireEvent(EventContext.REQUEST_END,session2);
       
    }

    
    /**
     * Tests a session-to-module scoped wire is setup properly by the runtime
     */
    public void testSessionToModule() throws Exception{
        RuntimeContext runtime = MockFactory.createJavaRuntime();
        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
        Assert.assertNotNull(ctx);
        runtime.getRootContext().registerModelObject(MockFactory.createAggregateComponent("test"));
        AggregateContext testCtx = (AggregateContext) runtime.getRootContext().getContext("test");
        Assert.assertNotNull(testCtx);
        testCtx.registerModelObject(MockFactory.createModule(Scope.SESSION,Scope.MODULE));
        testCtx.fireEvent(EventContext.MODULE_START,null);
        
        // first session
        Object session = new Object();
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        testCtx.fireEvent(EventContext.SESSION_NOTIFY,session);
        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source);
        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
        Assert.assertNotNull(target);
        source.getGenericComponent().setString("foo");
        source.getGenericComponent().setString("foo");
        Assert.assertEquals("foo",target.getString());
        testCtx.fireEvent(EventContext.REQUEST_END,session);
        
        //second session
        Object session2 = new Object();
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        testCtx.fireEvent(EventContext.SESSION_NOTIFY,session2);
        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source2);
        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);

        Assert.assertNotNull(target2);
        Assert.assertEquals("foo",target2.getString());
        Assert.assertEquals("foo",source2.getGenericComponent().getString());
        source2.getGenericComponent().setString("baz");
        Assert.assertEquals("baz",source2.getGenericComponent().getString());
        Assert.assertEquals("baz",target2.getString());
        Assert.assertEquals("baz",target.getString());
        
        testCtx.fireEvent(EventContext.REQUEST_END,session2);
       
    }

    /**
     * Tests a session-to-request scoped wire is setup properly by the runtime
     */
    public void testSessionToRequest() throws Exception{
        RuntimeContext runtime = MockFactory.createJavaRuntime();
        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
        Assert.assertNotNull(ctx);
        runtime.getRootContext().registerModelObject(MockFactory.createAggregateComponent("test"));
        AggregateContext testCtx = (AggregateContext) runtime.getRootContext().getContext("test");
        Assert.assertNotNull(testCtx);
        testCtx.registerModelObject(MockFactory.createModule(Scope.SESSION,Scope.REQUEST));
        testCtx.fireEvent(EventContext.MODULE_START,null);
        
        // first session
        Object session = new Object();
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        testCtx.fireEvent(EventContext.SESSION_NOTIFY,session);
        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source);
        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
        Assert.assertNotNull(target);
        source.getGenericComponent().setString("foo");
        Assert.assertEquals("foo",target.getString());
        testCtx.fireEvent(EventContext.REQUEST_END,session);
        
        //second session
        Object session2 = new Object();
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        testCtx.fireEvent(EventContext.SESSION_NOTIFY,session2);
        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source);
        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);

        Assert.assertNotNull(target2);
        Assert.assertEquals(null,target2.getString());
        source2.getGenericComponent().setString("baz");
        Assert.assertEquals("baz",target2.getString());
        Assert.assertEquals("baz",source2.getGenericComponent().getString());
        
        Assert.assertEquals("foo",target.getString());
        testCtx.fireEvent(EventContext.REQUEST_END,session);
        
    }
    
    
    /**
     * Tests a session-to-stateless scoped wire is setup properly by the runtime
     */
    public void testSessionToStateless() throws Exception{
        RuntimeContext runtime = MockFactory.createJavaRuntime();
        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
        Assert.assertNotNull(ctx);
        runtime.getRootContext().registerModelObject(MockFactory.createAggregateComponent("test"));
        AggregateContext testCtx = (AggregateContext) runtime.getRootContext().getContext("test");
        Assert.assertNotNull(testCtx);
        testCtx.registerModelObject(MockFactory.createModule(Scope.SESSION,Scope.INSTANCE));
        testCtx.fireEvent(EventContext.MODULE_START,null);
        
        // first session
        Object session = new Object();
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        testCtx.fireEvent(EventContext.SESSION_NOTIFY,session);
        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source);
        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
        Assert.assertNotNull(target);
        source.getGenericComponent().setString("foo");
        Assert.assertEquals(null,target.getString());
        testCtx.fireEvent(EventContext.REQUEST_END,session);
        
        //second session
        Object session2 = new Object();
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        testCtx.fireEvent(EventContext.SESSION_NOTIFY,session2);
        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source);
        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);

        Assert.assertNotNull(target2);
        Assert.assertEquals(null,target2.getString());
        source2.getGenericComponent().setString("baz");
        Assert.assertEquals(null,target2.getString()); //Note assumes no pooling
        Assert.assertEquals(null,source2.getGenericComponent().getString());
        
        Assert.assertEquals(null,target.getString()); //Note assumes no pooling
        testCtx.fireEvent(EventContext.REQUEST_END,session);
        
    }

    /**
     * Tests a request-to-request scoped wire is setup properly by the runtime
     */
    public void testRequestToRequest() throws Exception{
        RuntimeContext runtime = MockFactory.createJavaRuntime();
        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
        Assert.assertNotNull(ctx);
        runtime.getRootContext().registerModelObject(MockFactory.createAggregateComponent("test"));
        AggregateContext testCtx = (AggregateContext) runtime.getRootContext().getContext("test");
        Assert.assertNotNull(testCtx);
        testCtx.registerModelObject(MockFactory.createModule(Scope.REQUEST,Scope.REQUEST));
        testCtx.fireEvent(EventContext.MODULE_START,null);
        
        // first request
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source);
        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
        Assert.assertNotNull(target);
        source.getGenericComponent().setString("foo");
        Assert.assertEquals("foo",target.getString());
        testCtx.fireEvent(EventContext.REQUEST_END,null);
        
        //second request
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source2);
        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);

        Assert.assertNotNull(target2);
        Assert.assertEquals(null,target2.getString());
        Assert.assertEquals(null,source2.getGenericComponent().getString());
        source2.getGenericComponent().setString("baz");
        Assert.assertEquals("baz",source2.getGenericComponent().getString());
        Assert.assertEquals("baz",target2.getString());
        
        testCtx.fireEvent(EventContext.REQUEST_END,null);
       
    }

    /**
     * Tests a request-to-module scoped wire is setup properly by the runtime
     */
    public void testRequestToModule() throws Exception{
        RuntimeContext runtime = MockFactory.createJavaRuntime();
        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
        Assert.assertNotNull(ctx);
        runtime.getRootContext().registerModelObject(MockFactory.createAggregateComponent("test"));
        AggregateContext testCtx = (AggregateContext) runtime.getRootContext().getContext("test");
        Assert.assertNotNull(testCtx);
        testCtx.registerModelObject(MockFactory.createModule(Scope.REQUEST,Scope.MODULE));
        testCtx.fireEvent(EventContext.MODULE_START,null);
        
        // first request
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source);
        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
        Assert.assertNotNull(target);
        source.getGenericComponent().setString("foo");
        Assert.assertEquals("foo",target.getString());
        testCtx.fireEvent(EventContext.REQUEST_END,null);
        
        //second request
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source2);
        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);

        Assert.assertNotNull(target2);
        Assert.assertEquals("foo",target2.getString());
        Assert.assertEquals("foo",source2.getGenericComponent().getString());
        source2.getGenericComponent().setString("baz");
        Assert.assertEquals("baz",source2.getGenericComponent().getString());
        Assert.assertEquals("baz",target2.getString());
        Assert.assertEquals("baz",target.getString());
        
        testCtx.fireEvent(EventContext.REQUEST_END,null);
       
    }

    /**
     * Tests a request-to-session scoped wire is setup properly by the runtime
     */
    public void testRequestToSession() throws Exception{
        RuntimeContext runtime = MockFactory.createJavaRuntime();
        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
        Assert.assertNotNull(ctx);
        runtime.getRootContext().registerModelObject(MockFactory.createAggregateComponent("test"));
        AggregateContext testCtx = (AggregateContext) runtime.getRootContext().getContext("test");
        Assert.assertNotNull(testCtx);
        testCtx.registerModelObject(MockFactory.createModule(Scope.REQUEST,Scope.SESSION));
        testCtx.fireEvent(EventContext.MODULE_START,null);
        
        // first session
        Object session = new Object();
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        testCtx.fireEvent(EventContext.SESSION_NOTIFY,session);
        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source);
        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
        Assert.assertNotNull(target);
        source.getGenericComponent().setString("foo");
        Assert.assertEquals("foo",target.getString());
        testCtx.fireEvent(EventContext.REQUEST_END,session);

        //second request for session
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        testCtx.fireEvent(EventContext.SESSION_NOTIFY,session);
        GenericComponent targetR2 = (GenericComponent)testCtx.getContext("target").getInstance(null);
        Assert.assertEquals("foo",targetR2.getString());
        GenericComponent sourceR2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(sourceR2);
        Assert.assertEquals("foo",sourceR2.getGenericComponent().getString());
        
        testCtx.fireEvent(EventContext.REQUEST_END,session);

        //second session
        Object session2 = new Object();
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        testCtx.fireEvent(EventContext.SESSION_NOTIFY,session2);
        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source2);
        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);

        Assert.assertNotNull(target2);
        Assert.assertEquals(null,target2.getString());
        Assert.assertEquals(null,source2.getGenericComponent().getString());
        source2.getGenericComponent().setString("baz");
        Assert.assertEquals("baz",source2.getGenericComponent().getString());
        Assert.assertEquals("baz",target2.getString());
        
        testCtx.fireEvent(EventContext.REQUEST_END,session2);
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        testCtx.fireEvent(EventContext.SESSION_NOTIFY,session);
        testCtx.fireEvent(EventContext.REQUEST_END,session);
         
    }

    
    /**
     * Tests a request-to-stateless scoped wire is setup properly by the runtime
     */
    public void testRequestToStateless() throws Exception{
        RuntimeContext runtime = MockFactory.createJavaRuntime();
        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
        Assert.assertNotNull(ctx);
        runtime.getRootContext().registerModelObject(MockFactory.createAggregateComponent("test"));
        AggregateContext testCtx = (AggregateContext) runtime.getRootContext().getContext("test");
        Assert.assertNotNull(testCtx);
        testCtx.registerModelObject(MockFactory.createModule(Scope.REQUEST,Scope.INSTANCE));
        testCtx.fireEvent(EventContext.MODULE_START,null);
        
        // first request
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source);
        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
        Assert.assertNotNull(target);
        source.getGenericComponent().setString("foo");
        Assert.assertEquals(null,target.getString());
        testCtx.fireEvent(EventContext.REQUEST_END,null);
        
        //second request
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source2);
        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);

        Assert.assertNotNull(target2);
        Assert.assertEquals(null,target2.getString());
        Assert.assertEquals(null,source2.getGenericComponent().getString());
        source2.getGenericComponent().setString("baz");
        Assert.assertEquals(null,source2.getGenericComponent().getString());
        Assert.assertEquals(null,target2.getString());
        
        testCtx.fireEvent(EventContext.REQUEST_END,null);
       
    }

    
    /**
     * Tests a stateless-to-stateless scoped wire is setup properly by the runtime
     */
    public void testStatelessToStateless() throws Exception{
        RuntimeContext runtime = MockFactory.createJavaRuntime();
        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
        Assert.assertNotNull(ctx);
        runtime.getRootContext().registerModelObject(MockFactory.createAggregateComponent("test"));
        AggregateContext testCtx = (AggregateContext) runtime.getRootContext().getContext("test");
        Assert.assertNotNull(testCtx);
        testCtx.registerModelObject(MockFactory.createModule(Scope.INSTANCE,Scope.INSTANCE));
        testCtx.fireEvent(EventContext.MODULE_START,null);
        
        // first request
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source);
        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
        Assert.assertNotNull(target);
        source.getGenericComponent().setString("foo");
        Assert.assertEquals(null,target.getString());
        testCtx.fireEvent(EventContext.REQUEST_END,null);
        
        //second request
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source2);
        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);

        Assert.assertNotNull(target2);
        Assert.assertEquals(null,target2.getString());
        Assert.assertEquals(null,source2.getGenericComponent().getString());
        source2.getGenericComponent().setString("baz");
        Assert.assertEquals(null,source2.getGenericComponent().getString());
        Assert.assertEquals(null,target2.getString());
        
        testCtx.fireEvent(EventContext.REQUEST_END,null);
       
    }
    
    /**
     * Tests a stateless-to-request scoped wire is setup properly by the runtime
     */
    public void testStatelessToRequest() throws Exception{
        RuntimeContext runtime = MockFactory.createJavaRuntime();
        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
        Assert.assertNotNull(ctx);
        runtime.getRootContext().registerModelObject(MockFactory.createAggregateComponent("test"));
        AggregateContext testCtx = (AggregateContext) runtime.getRootContext().getContext("test");
        Assert.assertNotNull(testCtx);
        testCtx.registerModelObject(MockFactory.createModule(Scope.INSTANCE,Scope.REQUEST));
        testCtx.fireEvent(EventContext.MODULE_START,null);
        
        // first request
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source);
        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
        Assert.assertNotNull(target);
        source.getGenericComponent().setString("foo");
        Assert.assertEquals("foo",target.getString());
        testCtx.fireEvent(EventContext.REQUEST_END,null);

        GenericComponent targetR1 = (GenericComponent)testCtx.getContext("target").getInstance(null);
        Assert.assertNotNull(targetR1);
        Assert.assertEquals("foo",target.getString());

        //second request
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source2);
        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);

        Assert.assertNotNull(target2);
        Assert.assertEquals(null,target2.getString());
        Assert.assertEquals(null,source2.getGenericComponent().getString());
        source2.getGenericComponent().setString("baz");
        Assert.assertEquals("baz",source2.getGenericComponent().getString());
        Assert.assertEquals("baz",target2.getString());
        
        testCtx.fireEvent(EventContext.REQUEST_END,null);
       
    }

    /**
     * Tests a stateless-to-session scoped wire is setup properly by the runtime
     */
    public void testStatelessToSession() throws Exception{
        RuntimeContext runtime = MockFactory.createJavaRuntime();
        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
        Assert.assertNotNull(ctx);
        runtime.getRootContext().registerModelObject(MockFactory.createAggregateComponent("test"));
        AggregateContext testCtx = (AggregateContext) runtime.getRootContext().getContext("test");
        Assert.assertNotNull(testCtx);
        testCtx.registerModelObject(MockFactory.createModule(Scope.INSTANCE,Scope.SESSION));
        testCtx.fireEvent(EventContext.MODULE_START,null);
        
        // first session
        Object session = new Object();
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        testCtx.fireEvent(EventContext.SESSION_NOTIFY,session);
        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source);
        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
        Assert.assertNotNull(target);
        source.getGenericComponent().setString("foo");
        Assert.assertEquals("foo",target.getString());
        testCtx.fireEvent(EventContext.REQUEST_END,session);

        //second request for session
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        testCtx.fireEvent(EventContext.SESSION_NOTIFY,session);
        GenericComponent targetR2 = (GenericComponent)testCtx.getContext("target").getInstance(null);
        Assert.assertEquals("foo",targetR2.getString());
        GenericComponent sourceR2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(sourceR2);
        Assert.assertEquals("foo",sourceR2.getGenericComponent().getString());
        
        testCtx.fireEvent(EventContext.REQUEST_END,session);

        //second session
        Object session2 = new Object();
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        testCtx.fireEvent(EventContext.SESSION_NOTIFY,session2);
        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source2);
        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);

        Assert.assertNotNull(target2);
        Assert.assertEquals(null,target2.getString());
        Assert.assertEquals(null,source2.getGenericComponent().getString());
        source2.getGenericComponent().setString("baz");
        Assert.assertEquals("baz",source2.getGenericComponent().getString());
        Assert.assertEquals("baz",target2.getString());
        
        testCtx.fireEvent(EventContext.REQUEST_END,session2);
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        testCtx.fireEvent(EventContext.SESSION_NOTIFY,session);
        testCtx.fireEvent(EventContext.REQUEST_END,session);
         
    }

    
    /**
     * Tests a stateless-to-module scoped wire is setup properly by the runtime
     */
    public void testStatelessToModule() throws Exception{
        RuntimeContext runtime = MockFactory.createJavaRuntime();
        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
        Assert.assertNotNull(ctx);
        runtime.getRootContext().registerModelObject(MockFactory.createAggregateComponent("test"));
        AggregateContext testCtx = (AggregateContext) runtime.getRootContext().getContext("test");
        Assert.assertNotNull(testCtx);
        testCtx.registerModelObject(MockFactory.createModule(Scope.INSTANCE,Scope.MODULE));
        testCtx.fireEvent(EventContext.MODULE_START,null);
        
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source);
        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
        Assert.assertNotNull(target);
        source.getGenericComponent().setString("foo");
        Assert.assertEquals("foo",target.getString());
        testCtx.fireEvent(EventContext.REQUEST_END,null);

        //second session
        testCtx.fireEvent(EventContext.REQUEST_START,null);
        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
        Assert.assertNotNull(source2);
        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);

        Assert.assertNotNull(target2);
        Assert.assertEquals("foo",target2.getString());
        Assert.assertEquals("foo",source2.getGenericComponent().getString());
        source2.getGenericComponent().setString("baz");
        Assert.assertEquals("baz",source2.getGenericComponent().getString());
        Assert.assertEquals("baz",target2.getString());
        
        testCtx.fireEvent(EventContext.REQUEST_END,null);
         
    }

}

