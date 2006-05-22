package org.apache.tuscany.core.context.scope;

import java.util.Map;

import junit.framework.TestCase;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.event.HttpSessionEnd;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.event.RequestStart;
import org.apache.tuscany.core.context.event.RequestEnd;
import org.apache.tuscany.core.mock.factories.MockContextFactory;
import org.apache.tuscany.core.mock.component.OrderedDependentPojo;
import org.apache.tuscany.core.mock.component.OrderedDependentPojoImpl;
import org.apache.tuscany.core.mock.component.OrderedInitPojo;
import org.apache.tuscany.core.mock.component.OrderedInitPojoImpl;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.WorkContext;

/**
 * Tests dependencies are initalized and destroyed in the proper order (i.e. LIFO)
 *
 * @version $Rev: 393992 $ $Date: 2006-04-13 18:01:05 -0700 (Thu, 13 Apr 2006) $
 */
public class DependencyLifecycleTestCase extends TestCase {

    public void testInitDestroyOrderModuleScope() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContext scopeCtx = new ModuleScopeContext(ctx);
        scopeCtx.start();
        Map<String, AtomicContext> contexts = MockContextFactory.createWiredContexts("source", OrderedDependentPojoImpl.class,
                scopeCtx, "target", OrderedInitPojoImpl.class, scopeCtx);
        for (AtomicContext context : contexts.values()) {
            scopeCtx.register(context);
        }
        AtomicContext sourceContext = contexts.get("source");
        AtomicContext targetContext = contexts.get("target");
        scopeCtx.register(sourceContext);
        scopeCtx.register(targetContext);
        scopeCtx.onEvent(new ModuleStart(this, null));
        OrderedDependentPojo source = (OrderedDependentPojo)scopeCtx.getInstance(sourceContext);
        OrderedInitPojo target = (OrderedInitPojo) scopeCtx.getInstance(targetContext);
        assertNotNull(source.getPojo());
        assertNotNull(target);
        assertEquals(2, source.getNumberInstantiated());
        scopeCtx.onEvent(new ModuleStop(this, null));
        assertEquals(0, source.getNumberInstantiated());
        scopeCtx.stop();
    }

    public void testInitDestroyOrderAfterStartModuleScope() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContext scopeCtx = new ModuleScopeContext(ctx);
        scopeCtx.start();
        Map<String, AtomicContext> contexts = MockContextFactory.createWiredContexts("source", OrderedDependentPojoImpl.class,
                scopeCtx, "target", OrderedInitPojoImpl.class, scopeCtx);
        AtomicContext sourceContext = contexts.get("source");
        AtomicContext targetContext = contexts.get("target");
        scopeCtx.onEvent(new ModuleStart(this, null));
        scopeCtx.register(sourceContext);
        scopeCtx.register(targetContext);
        OrderedDependentPojo source = (OrderedDependentPojo) scopeCtx.getInstance(sourceContext);
        OrderedInitPojo target = (OrderedInitPojo) scopeCtx.getInstance(targetContext);
        assertNotNull(source.getPojo());
        assertNotNull(target);
        assertEquals(2, source.getNumberInstantiated());
        scopeCtx.onEvent(new ModuleStop(this, null));
        assertEquals(0, source.getNumberInstantiated());
        scopeCtx.stop();
    }


    public void testInitDestroyOrderSessionScope() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        HttpSessionScopeContext scopeCtx = new HttpSessionScopeContext(ctx);
        scopeCtx.start();
        Object session = new Object();
        Map<String, AtomicContext> contexts = MockContextFactory.createWiredContexts("source", OrderedDependentPojoImpl.class,
                scopeCtx, "target", OrderedInitPojoImpl.class, scopeCtx);
        AtomicContext sourceContext = contexts.get("source");
        AtomicContext targetContext = contexts.get("target");
        scopeCtx.register(sourceContext);
        scopeCtx.register(targetContext);
        ctx.setIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER, session);
        OrderedDependentPojo source = (OrderedDependentPojo) scopeCtx.getInstance(sourceContext);
        assertNotNull(source.getPojo());
        assertEquals(2, source.getNumberInstantiated());
        scopeCtx.onEvent(new HttpSessionEnd(this, session));
        assertEquals(0, source.getNumberInstantiated());
        scopeCtx.stop();
    }


    public void testInitDestroyOrderAfterStartSessionScope() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        HttpSessionScopeContext scopeCtx = new HttpSessionScopeContext(ctx);
        scopeCtx.start();
        Object session = new Object();
        Map<String, AtomicContext> contexts = MockContextFactory.createWiredContexts("source", OrderedDependentPojoImpl.class,
                scopeCtx, "target", OrderedInitPojoImpl.class, scopeCtx);
        AtomicContext sourceContext = contexts.get("source");
        AtomicContext targetContext = contexts.get("target");
        ctx.setIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER, session);
        scopeCtx.register(sourceContext);
        scopeCtx.register(targetContext);
        OrderedDependentPojo source = (OrderedDependentPojo) scopeCtx.getInstance(sourceContext);
        assertNotNull(source.getPojo());
        assertEquals(2, source.getNumberInstantiated());
        scopeCtx.onEvent(new HttpSessionEnd(this, session));
        assertEquals(0, source.getNumberInstantiated());
        scopeCtx.stop();
    }

    public void testInitDestroyOrderRequestScope() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        RequestScopeContext scopeCtx = new RequestScopeContext(ctx);
        scopeCtx.start();
        scopeCtx.onEvent(new RequestStart(this));
        Map<String, AtomicContext> contexts = MockContextFactory.createWiredContexts("source", OrderedDependentPojoImpl.class,
                scopeCtx, "target", OrderedInitPojoImpl.class, scopeCtx);
        AtomicContext sourceContext = contexts.get("source");
        AtomicContext targetContext = contexts.get("target");
        scopeCtx.register(sourceContext);
        scopeCtx.register(targetContext);
        OrderedDependentPojo source = (OrderedDependentPojo) scopeCtx.getInstance(sourceContext);
        assertNotNull(source.getPojo());
        assertEquals(2, source.getNumberInstantiated());
        scopeCtx.onEvent(new RequestEnd(this));
        assertEquals(0, source.getNumberInstantiated());
        scopeCtx.stop();
    }

}
