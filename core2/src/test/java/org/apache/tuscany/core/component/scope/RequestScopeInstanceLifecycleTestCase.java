package org.apache.tuscany.core.component.scope;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.RequestEnd;
import org.apache.tuscany.core.component.event.RequestStart;
import org.apache.tuscany.core.mock.component.OrderedEagerInitPojo;
import org.apache.tuscany.core.mock.component.OrderedInitPojo;
import org.apache.tuscany.core.mock.component.OrderedInitPojoImpl;
import org.apache.tuscany.core.mock.component.RequestScopeDestroyOnlyComponent;
import org.apache.tuscany.core.mock.component.RequestScopeInitDestroyComponent;
import org.apache.tuscany.core.mock.component.RequestScopeInitOnlyComponent;
import org.apache.tuscany.core.mock.factories.MockContextFactory;
import org.apache.tuscany.core.system.component.SystemAtomicComponent;
import org.apache.tuscany.spi.component.WorkContext;

/**
 * Lifecycle unit tests for the module scope container
 *
 * @version $Rev: 398107 $ $Date: 2006-04-29 01:38:27 -0700 (Sat, 29 Apr 2006) $
 */
public class RequestScopeInstanceLifecycleTestCase extends TestCase {

    public void testInitDestroy() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        RequestScopeContext scope = new RequestScopeContext(ctx);
        scope.start();

        SystemAtomicComponent initDestroyContext = MockContextFactory.createSystemAtomicContext("InitDestroy", scope, RequestScopeInitDestroyComponent.class);
        initDestroyContext.start();

        SystemAtomicComponent initOnlyContext = MockContextFactory.createSystemAtomicContext("InitOnly", scope, RequestScopeInitOnlyComponent.class);
        initOnlyContext.start();

        SystemAtomicComponent destroyOnlyContext = MockContextFactory.createSystemAtomicContext("DestroyOnly", scope, RequestScopeDestroyOnlyComponent.class);
        destroyOnlyContext.start();

        scope.onEvent(new RequestStart(this));
        RequestScopeInitDestroyComponent initDestroy = (RequestScopeInitDestroyComponent) scope.getInstance(initDestroyContext);
        Assert.assertNotNull(initDestroy);

        RequestScopeInitOnlyComponent initOnly = (RequestScopeInitOnlyComponent) scope.getInstance(initOnlyContext);
        Assert.assertNotNull(initOnly);

        RequestScopeDestroyOnlyComponent destroyOnly = (RequestScopeDestroyOnlyComponent) scope.getInstance(destroyOnlyContext);
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
        RequestScopeContext scope = new RequestScopeContext(ctx);
        scope.start();

        SystemAtomicComponent oneCtx = MockContextFactory.createSystemAtomicContext("one", scope, OrderedInitPojoImpl.class);
        scope.register(oneCtx);
        SystemAtomicComponent twoCtx = MockContextFactory.createSystemAtomicContext("two", scope, OrderedInitPojoImpl.class);
        scope.register(twoCtx);
        SystemAtomicComponent threeCtx = MockContextFactory.createSystemAtomicContext("three", scope, OrderedInitPojoImpl.class);
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
        RequestScopeContext scope = new RequestScopeContext(ctx);
        scope.start();

        SystemAtomicComponent oneCtx = MockContextFactory.createSystemAtomicContext("one", scope, OrderedEagerInitPojo.class);
        scope.register(oneCtx);
        SystemAtomicComponent twoCtx = MockContextFactory.createSystemAtomicContext("two", scope, OrderedEagerInitPojo.class);
        scope.register(twoCtx);
        SystemAtomicComponent threeCtx = MockContextFactory.createSystemAtomicContext("three", scope, OrderedEagerInitPojo.class);
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
