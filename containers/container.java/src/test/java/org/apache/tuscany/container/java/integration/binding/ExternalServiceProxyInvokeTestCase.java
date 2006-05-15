package org.apache.tuscany.container.java.integration.binding;

import junit.framework.TestCase;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ExternalServiceProxyInvokeTestCase extends TestCase {

    /**
     * Tests that an external service can be invoked by locating a proxy to it as opposed to invoking it over a wire from another
     * source such as an entry point or external service.
     *
     * @throws Throwable
     */
    public void testProxyInvocation() throws Throwable {
//        RuntimeContext runtime = MockContextFactory.registerFooBinding(MockContextFactory.createJavaRuntime());
//        PolicyBuilderRegistry builderRegistry = (PolicyBuilderRegistry) ((CompositeContext) runtime.getSystemContext().getContext(MockContextFactory.SYSTEM_CHILD))
//                .getContext(MockContextFactory.POLICY_BUILDER_REGISTRY).getInstance(null);
//        MockSyncInterceptor mockInterceptor = new MockSyncInterceptor();
//        MockInterceptorBuilder interceptorBuilder = new MockInterceptorBuilder(mockInterceptor, false);
//        builderRegistry.registerTargetBuilder(interceptorBuilder);
//        runtime.getRootContext().registerModelObject(MockContextFactory.createCompositeComponent("test.module"));
//        CompositeContext child = (CompositeContext) runtime.getRootContext().getContext("test.module");
//        child.registerModelObject(MockContextFactory.createModuleWithEntryPointToExternalService());
//        child.publish(new ModuleStart(this));
//        Object id = new Object();
//        child.publish(new RequestStart(this, id));
//        HelloWorldService service1 = (HelloWorldService) child.getContext("target").getInstance(null);
//        Assert.assertEquals("foo", service1.hello("foo"));
//
//        child.publish(new RequestEnd(this, id));
//        child.publish(new ModuleStop(this));
//        runtime.stop();

    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
