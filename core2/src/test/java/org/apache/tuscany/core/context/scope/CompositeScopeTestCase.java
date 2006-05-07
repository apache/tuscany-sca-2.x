package org.apache.tuscany.core.context.scope;

import junit.framework.TestCase;

/**
 * Tests component nesting. This test needs to be in the container.java progject since it relies on Java POJOs
 * for scope testing.
 *
 * @version $Rev: 396284 $ $Date: 2006-04-23 08:27:42 -0700 (Sun, 23 Apr 2006) $
 */
public class CompositeScopeTestCase extends TestCase {

    /**
     * Ensures scope events are propagated in an composite scope
     */
    public void testCompositeScopePropagation() throws Exception {
//        WorkContext ctx = new WorkContextImpl();
//        CompositeContext moduleComponentCtx = new CompositeContextImpl();
//        moduleComponentCtx.setName("testMC");
//        moduleComponentCtx.start();
//        CompositeScopeContext scopeContainer = new CompositeScopeContext(ctx);
//        //scopeContainer.registerFactory(MockFactory.createCompositeConfiguration("CompositeComponent"));
//        scopeContainer.start();
//        CompositeContext child = (CompositeContext) scopeContainer.getContext("CompositeComponent");
//
//        scopeContainer.onEvent(new ModuleStart(this));
//        Object session = new Object();
//        Object id = new Object();
//        //ctx.setIdentifier(EventContext.SESSION,session);
//        scopeContainer.onEvent(new RequestStart(this,id));
//        scopeContainer.onEvent(new HttpSessionBound(this,session));
//        CompositeContext componentCtx = (CompositeContext) scopeContainer.getContext("CompositeComponent");
//        GenericComponent testService1 = (GenericComponent) componentCtx.getContext("TestService1").getInstance(null);
//        GenericComponent testService2 = (GenericComponent) componentCtx.getContext("TestService2").getInstance(null);
//        GenericComponent testService3 = (GenericComponent) componentCtx.getContext("TestService3").getInstance(null);
//        Assert.assertNotNull(testService1);
//        Assert.assertNotNull(testService2);
//        Assert.assertNotNull(testService3);
//        scopeContainer.onEvent(new RequestEnd(this,id));
//        scopeContainer.onEvent(new RequestStart(this,id));
//        scopeContainer.onEvent(new HttpSessionBound(this,session));
//
//        GenericComponent testService2a = (GenericComponent) componentCtx.getContext("TestService2").getInstance(null);
//        Assert.assertNotNull(testService2a);
//        GenericComponent testService3a = (GenericComponent) componentCtx.getContext("TestService3").getInstance(null);
//        Assert.assertNotNull(testService3a);
//        Assert.assertEquals(testService2, testService2a);
//        Assert.assertNotSame(testService3, testService3a);
//        scopeContainer.onEvent(new RequestEnd(this,id));
//        scopeContainer.onEvent(new HttpSessionEnd(this,session));
//
//        Object session2 = new Object();
//        Object id2 = new Object();
//        scopeContainer.onEvent(new RequestStart(this,id2));
//        scopeContainer.onEvent(new HttpSessionBound(this,session2));
//        GenericComponent testService2b = (GenericComponent) componentCtx.getContext("TestService2").getInstance(null);
//        Assert.assertNotNull(testService2b);
//        Assert.assertNotSame(testService2, testService2b);
//
//        scopeContainer.onEvent(new RequestEnd(this,id2));
//        scopeContainer.onEvent(new HttpSessionEnd(this,session2));

    }

    /**
     * Ensures only child entry points (and not components) are accessible from parents
     */
    public void testCompositeNoEntryPoint() throws Exception {
//        EventContext ctx = new EventContextImpl();
//        CompositeContext moduleComponentCtx = new CompositeContextImpl();
//        moduleComponentCtx.setName("testMC");
//        CompositeScopeContext scopeContainer = new CompositeScopeContext(ctx);
//        scopeContainer.registerFactory(MockFactory.createCompositeConfiguration("CompositeComponent"));
//        scopeContainer.start();
//        CompositeContext child = (CompositeContext) scopeContainer.getContext("CompositeComponent");
//        List<Extensible> parts = createAssembly();
//        for (Extensible part : parts) {
//            child.registerModelObject(part);
//        }
//        scopeContainer.onEvent(new ModuleStart(this));
//        scopeContainer.getContext("CompositeComponent");
    }

    /**
     * Tests adding a context before its parent has been started
     */
    public void testRegisterContextBeforeStart() throws Exception {
//        EventContext ctx = new EventContextImpl();
//        CompositeContext moduleComponentCtx = new CompositeContextImpl();
//        moduleComponentCtx.setName("testMC");
//        CompositeScopeContext scopeContainer = new CompositeScopeContext(ctx);
//        scopeContainer.registerFactory(MockFactory.createCompositeConfiguration("CompositeComponent"));
//        scopeContainer.start();
//        scopeContainer.onEvent(new ModuleStart(this));
//        scopeContainer.getContext("CompositeComponent");
//        scopeContainer.onEvent(new ModuleStop(this));
//        scopeContainer.stop();
    }

    /**
     * Tests adding a context after its parent has been started
     */
    public void testRegisterContextAfterStart() throws Exception {
//        EventContext ctx = new EventContextImpl();
//        CompositeContext moduleComponentCtx = new CompositeContextImpl();
//        moduleComponentCtx.setName("testMC");
//        CompositeScopeContext scopeContainer = new CompositeScopeContext(ctx);
//        scopeContainer.start();
//
//        scopeContainer.onEvent(new ModuleStart(this));
//        scopeContainer.registerFactory(MockFactory.createCompositeConfiguration("CompositeComponent"));
//        scopeContainer.getContext("CompositeComponent");
//        scopeContainer.onEvent(new ModuleStop(this));
//        scopeContainer.stop();
    }


}
