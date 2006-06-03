package org.apache.tuscany.core.component.scope;

import java.util.Map;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.scope.HttpSessionScopeContext;
import org.apache.tuscany.core.component.scope.ModuleScopeContext;
import org.apache.tuscany.core.component.scope.RequestScopeContext;
import org.apache.tuscany.core.component.event.HttpSessionEnd;
import org.apache.tuscany.core.component.event.CompositeStart;
import org.apache.tuscany.core.component.event.CompositeStop;
import org.apache.tuscany.core.component.event.RequestEnd;
import org.apache.tuscany.core.component.event.RequestStart;
import org.apache.tuscany.core.mock.factories.MockContextFactory;
import org.apache.tuscany.core.mock.component.OrderedDependentPojo;
import org.apache.tuscany.core.mock.component.OrderedDependentPojoImpl;
import org.apache.tuscany.core.mock.component.OrderedInitPojo;
import org.apache.tuscany.core.mock.component.OrderedInitPojoImpl;
import org.apache.tuscany.spi.context.AtomicComponent;
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
        Map<String, AtomicComponent> contexts = MockContextFactory.createWiredContexts("source", OrderedDependentPojoImpl.class,
                scopeCtx, "target", OrderedInitPojoImpl.class, scopeCtx);
        for (AtomicComponent component : contexts.values()) {
            scopeCtx.register(component);
        }
        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        scopeCtx.register(sourceComponent);
        scopeCtx.register(targetComponent);
        scopeCtx.onEvent(new CompositeStart(this, null));
        OrderedDependentPojo source = (OrderedDependentPojo)scopeCtx.getInstance(sourceComponent);
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
        ModuleScopeContext scopeCtx = new ModuleScopeContext(ctx);
        scopeCtx.start();
        Map<String, AtomicComponent> contexts = MockContextFactory.createWiredContexts("source", OrderedDependentPojoImpl.class,
                scopeCtx, "target", OrderedInitPojoImpl.class, scopeCtx);
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
        HttpSessionScopeContext scopeCtx = new HttpSessionScopeContext(ctx);
        scopeCtx.start();
        Object session = new Object();
        Map<String, AtomicComponent> contexts = MockContextFactory.createWiredContexts("source", OrderedDependentPojoImpl.class,
                scopeCtx, "target", OrderedInitPojoImpl.class, scopeCtx);
        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        scopeCtx.register(sourceComponent);
        scopeCtx.register(targetComponent);
        ctx.setIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER, session);
        OrderedDependentPojo source = (OrderedDependentPojo) scopeCtx.getInstance(sourceComponent);
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
        Map<String, AtomicComponent> contexts = MockContextFactory.createWiredContexts("source", OrderedDependentPojoImpl.class,
                scopeCtx, "target", OrderedInitPojoImpl.class, scopeCtx);
        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        ctx.setIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER, session);
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
        RequestScopeContext scopeCtx = new RequestScopeContext(ctx);
        scopeCtx.start();
        scopeCtx.onEvent(new RequestStart(this));
        Map<String, AtomicComponent> contexts = MockContextFactory.createWiredContexts("source", OrderedDependentPojoImpl.class,
                scopeCtx, "target", OrderedInitPojoImpl.class, scopeCtx);
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
