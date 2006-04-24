package org.apache.tuscany.container.java.integration.binding;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.container.java.assembly.mock.HelloWorldService;
import org.apache.tuscany.container.java.builder.MockInterceptorBuilder;
import org.apache.tuscany.container.java.invocation.mock.MockSyncInterceptor;
import org.apache.tuscany.container.java.mock.MockFactory;
import org.apache.tuscany.core.builder.system.PolicyBuilderRegistry;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.event.RequestEnd;
import org.apache.tuscany.core.context.event.RequestStart;
import org.apache.tuscany.core.runtime.RuntimeContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @version $$Rev$$ $$Date$$
 */
public class EPtoExternalServiceTestCase extends TestCase {
    private Method hello;

    public void testEPtoESInvocation() throws Throwable {
        RuntimeContext runtime = MockFactory.registerFooBinding(MockFactory.createJavaRuntime());
        PolicyBuilderRegistry registry = (PolicyBuilderRegistry) ((CompositeContext) runtime.getSystemContext().getContext(MockFactory.SYSTEM_CHILD))
                .getContext(MockFactory.POLICY_BUILDER_REGISTRY).getInstance(null);
        MockSyncInterceptor mockInterceptor = new MockSyncInterceptor();
        MockInterceptorBuilder interceptorBuilder = new MockInterceptorBuilder(mockInterceptor, false);
        registry.registerTargetBuilder(interceptorBuilder);
        runtime.getRootContext().registerModelObject(MockFactory.createCompositeComponent("test.module"));
        CompositeContext child = (CompositeContext) runtime.getRootContext().getContext("test.module");
        child.registerModelObject(MockFactory.createModuleWithEntryPointToExternalService());
        child.publish(new ModuleStart(this));
        Object id = new Object();
        child.publish(new RequestStart(this, id));
        EntryPointContext ctx = (EntryPointContext) child.getContext("source");
        Assert.assertNotNull(ctx);
        InvocationHandler handler = (InvocationHandler) ctx.getHandler();
        Assert.assertEquals(0, mockInterceptor.getCount());
        Object response = handler.invoke(null, hello, new Object[]{"foo"});
        Assert.assertEquals("foo", response);
        Assert.assertEquals(1, mockInterceptor.getCount());

        ctx = (EntryPointContext) child.getContext("source");
        Assert.assertNotNull(ctx);
        handler = (InvocationHandler) ctx.getHandler();
        response = handler.invoke(null, hello, new Object[]{"foo"});
        Assert.assertEquals("foo", response);
        child.publish(new RequestEnd(this, id));

        // second request
        Object id2 = new Object();
        child.publish(new RequestStart(this, id2));
        ctx = (EntryPointContext) child.getContext("source");
        Assert.assertNotNull(ctx);
        handler = (InvocationHandler) ctx.getHandler();
        Assert.assertEquals(2, mockInterceptor.getCount());
        response = handler.invoke(null, hello, new Object[]{"foo"});
        Assert.assertEquals("foo", response);
        Assert.assertEquals(3, mockInterceptor.getCount());
        child.publish(new RequestEnd(this, id2));

        child.publish(new ModuleStop(this));
        runtime.stop();

    }

    protected void setUp() throws Exception {
        super.setUp();
        hello = HelloWorldService.class.getMethod("hello", String.class);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
