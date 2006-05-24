package org.apache.tuscany.core.context.scope;


import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.mock.factories.MockContextFactory;
import org.apache.tuscany.core.mock.component.ModuleScopeDestroyOnlyComponent;
import org.apache.tuscany.core.mock.component.ModuleScopeInitDestroyComponent;
import org.apache.tuscany.core.mock.component.ModuleScopeInitOnlyComponent;
import org.apache.tuscany.core.mock.component.OrderedInitPojoImpl;
import org.apache.tuscany.core.mock.component.OrderedInitPojo;
import org.apache.tuscany.core.mock.component.OrderedEagerInitPojo;
import org.apache.tuscany.core.system.context.SystemAtomicContext;
import org.apache.tuscany.spi.context.WorkContext;

/**
 * Lifecycle unit tests for the module scope container
 *
 * @version $Rev: 398107 $ $Date: 2006-04-29 01:38:27 -0700 (Sat, 29 Apr 2006) $
 */
public class ModuleScopeInstanceLifecycleTestCase extends TestCase {

    public void testInitDestroy() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContext scope = new ModuleScopeContext(ctx);
        scope.start();

        SystemAtomicContext initDestroyContext = MockContextFactory.createSystemAtomicContext("InitDestroy", scope, ModuleScopeInitDestroyComponent.class);
        initDestroyContext.start();

        SystemAtomicContext initOnlyContext = MockContextFactory.createSystemAtomicContext("InitOnly", scope, ModuleScopeInitOnlyComponent.class);
        initOnlyContext.start();

        SystemAtomicContext destroyOnlyContext = MockContextFactory.createSystemAtomicContext("DestroyOnly", scope, ModuleScopeDestroyOnlyComponent.class);
        destroyOnlyContext.start();
        
        scope.onEvent(new ModuleStart(this, null));
        ModuleScopeInitDestroyComponent initDestroy = (ModuleScopeInitDestroyComponent) scope.getInstance(initDestroyContext);
        Assert.assertNotNull(initDestroy);

        ModuleScopeInitOnlyComponent initOnly = (ModuleScopeInitOnlyComponent) scope.getInstance(initOnlyContext);
        Assert.assertNotNull(initOnly);

        ModuleScopeDestroyOnlyComponent destroyOnly = (ModuleScopeDestroyOnlyComponent) scope.getInstance(destroyOnlyContext);
        Assert.assertNotNull(destroyOnly);

        Assert.assertTrue(initDestroy.isInitialized());
        Assert.assertTrue(initOnly.isInitialized());
        Assert.assertFalse(initDestroy.isDestroyed());
        Assert.assertFalse(destroyOnly.isDestroyed());

        // expire module
        scope.onEvent(new ModuleStop(this, null));

        Assert.assertTrue(initDestroy.isDestroyed());
        Assert.assertTrue(destroyOnly.isDestroyed());

        scope.stop();
    }

    public void testDestroyOrder() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContext scope = new ModuleScopeContext(ctx);
        scope.start();

        SystemAtomicContext oneCtx = MockContextFactory.createSystemAtomicContext("one", scope, OrderedInitPojoImpl.class);
        scope.register(oneCtx);
        SystemAtomicContext twoCtx = MockContextFactory.createSystemAtomicContext("two", scope, OrderedInitPojoImpl.class);
        scope.register(twoCtx);
        SystemAtomicContext threeCtx = MockContextFactory.createSystemAtomicContext("three", scope, OrderedInitPojoImpl.class);
        scope.register(threeCtx);

        scope.onEvent(new ModuleStart(this,null));
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
        scope.onEvent(new ModuleStop(this,null));
        Assert.assertEquals(0, one.getNumberInstantiated());
        scope.stop();
    }

    public void testEagerInitDestroyOrder() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContext scope = new ModuleScopeContext(ctx);
        scope.start();

        SystemAtomicContext oneCtx = MockContextFactory.createSystemAtomicContext("one", scope, OrderedEagerInitPojo.class);
        scope.register(oneCtx);
        SystemAtomicContext twoCtx = MockContextFactory.createSystemAtomicContext("two", scope, OrderedEagerInitPojo.class);
        scope.register(twoCtx);
        SystemAtomicContext threeCtx = MockContextFactory.createSystemAtomicContext("three", scope, OrderedEagerInitPojo.class);
        scope.register(threeCtx);

        scope.onEvent(new ModuleStart(this,null));
        OrderedEagerInitPojo one = (OrderedEagerInitPojo) scope.getInstance(oneCtx);
        Assert.assertNotNull(one);

        OrderedEagerInitPojo two = (OrderedEagerInitPojo) scope.getInstance(twoCtx);
        Assert.assertNotNull(two);

        OrderedEagerInitPojo three = (OrderedEagerInitPojo) scope.getInstance(threeCtx);
        Assert.assertNotNull(three);

        // expire module
        scope.onEvent(new ModuleStop(this,null));
        Assert.assertEquals(0, one.getNumberInstantiated());
        scope.stop();
    }

}
