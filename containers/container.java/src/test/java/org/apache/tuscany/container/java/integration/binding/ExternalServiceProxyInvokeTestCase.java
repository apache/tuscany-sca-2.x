/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.container.java.integration.binding;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.container.java.assembly.mock.HelloWorldService;
import org.apache.tuscany.container.java.builder.MockInterceptorBuilder;
import org.apache.tuscany.container.java.invocation.mock.MockSyncInterceptor;
import org.apache.tuscany.container.java.mock.MockFactory;
import org.apache.tuscany.core.builder.system.PolicyBuilderRegistry;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.event.RequestEnd;
import org.apache.tuscany.core.context.event.RequestStart;
import org.apache.tuscany.core.runtime.RuntimeContext;

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
        HelloWorldService service1 = (HelloWorldService) child.getContext("target").getInstance(null);
        Assert.assertEquals("foo", service1.hello("foo"));

        child.publish(new RequestEnd(this, id));
        child.publish(new ModuleStop(this));
        runtime.stop();

    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
