package org.apache.tuscany.core.context.scope;

import junit.framework.TestCase;
import junit.framework.Assert;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.event.HttpSessionEvent;
import org.apache.tuscany.core.context.event.HttpSessionEnd;
import org.apache.tuscany.core.context.event.RequestStart;
import org.apache.tuscany.core.context.event.RequestEnd;

/**
 * Tests that dependencies are initalized and destroyed in the proper order (i.e. LIFO)
 *
 * @version $Rev: 393992 $ $Date: 2006-04-13 18:01:05 -0700 (Thu, 13 Apr 2006) $
 */
public class DependencyLifecycleTestCase extends TestCase {



    public void testInitDestroyOrderModuleScope() throws Exception {
//        WorkContext ctx = new WorkContextImpl();
//        ModuleScopeContext scope = new ModuleScopeContext(ctx);
//        scope.registerFactories(MockContextFactory.createWiredContexts(Scope.MODULE,scope));
//        scope.start();
//        scope.onEvent(new ModuleStart(this));
//        OrderedDependentPojo source = (OrderedDependentPojo) scope.getContext("source").getInstance(null);
//        assertNotNull(source.getPojo());
//        // expire module
//        Assert.assertEquals(2,source.getNumberInstantiated());
//        scope.onEvent(new ModuleStop(this));
//        Assert.assertEquals(0,source.getNumberInstantiated());
//        scope.stop();
    }

//    public void testInitDestroyOrderSessionScope() throws Exception {
//        WorkContext ctx = new WorkContextImpl();
//        SessionScopeContext scope = new SessionScopeContext(ctx);
//        scope.registerFactories(MockContextFactory.createWiredContexts(Scope.SESSION,scope));
//        scope.start();
//        Object session =  new Object();
//        ctx.setIdentifier(HttpSessionEvent.HTTP_IDENTIFIER,session);
//        scope.onEvent(new HttpSessionBound(this,session));
//        OrderedDependentPojo source = (OrderedDependentPojo) scope.getContext("source").getInstance(null);
//        assertNotNull(source.getPojo());
//        // expire module
//        Assert.assertEquals(2,source.getNumberInstantiated());
//        scope.onEvent(new HttpSessionEnd(this,session));
//        Assert.assertEquals(0,source.getNumberInstantiated());
//        scope.stop();
//    }
//
//
//    public void testInitDestroyOrderRequestScope() throws Exception {
//        WorkContext ctx = new WorkContextImpl();
//        RequestScopeContext scope = new RequestScopeContext(ctx);
//        scope.registerFactories(MockContextFactory.createWiredContexts(Scope.REQUEST,scope));
//        scope.start();
//        Object request =  new Object();
//        scope.onEvent(new RequestStart(this,request));
//        OrderedDependentPojo source = (OrderedDependentPojo) scope.getContext("source").getInstance(null);
//        assertNotNull(source.getPojo());
//        // expire module
//        Assert.assertEquals(2,source.getNumberInstantiated());
//        scope.onEvent(new RequestEnd(this,request));
//        Assert.assertEquals(0,source.getNumberInstantiated());
//        scope.stop();
//    }

}
