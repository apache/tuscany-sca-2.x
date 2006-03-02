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
package org.apache.tuscany.container.java.integration.binding;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.container.java.assembly.mock.HelloWorldService;
import org.apache.tuscany.container.java.builder.MockInterceptorBuilder;
import org.apache.tuscany.container.java.invocation.mock.MockSyncInterceptor;
import org.apache.tuscany.container.java.mock.MockFactory;
import org.apache.tuscany.container.java.mock.binding.foo.FooBindingBuilder;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.runtime.RuntimeContext;

/**
 * Tests basic entry point functionality with Java components
 * 
 * @version $Rev$ $Date$
 */
public class EntryPointToJavaTestCase extends TestCase {

    private Method hello;

    public void setUp() throws Exception {
        hello = HelloWorldService.class.getMethod("hello", new Class[] { String.class });
    }

    /**
     * Tests creation and invocation of an entry point wired to a Java component
     */
    public void testEPtoJavaInvoke() throws Throwable {
        RuntimeContext runtime = MockFactory.registerFooBinding(MockFactory.createJavaRuntime());
        FooBindingBuilder builder = (FooBindingBuilder) ((AggregateContext) runtime.getSystemContext().getContext(MockFactory.SYSTEM_CHILD))
                .getContext(MockFactory.FOO_BUILDER).getInstance(null);
        MockSyncInterceptor mockInterceptor = new MockSyncInterceptor();
        MockInterceptorBuilder interceptorBuilder = new MockInterceptorBuilder(mockInterceptor, false);
        builder.addPolicyBuilder(interceptorBuilder);
        runtime.getRootContext().registerModelObject(MockFactory.createAggregateComponent("test.module"));
        AggregateContext child = (AggregateContext) runtime.getRootContext().getContext("test.module");
        child.registerModelObject(MockFactory.createModuleWithEntryPoint());
        child.fireEvent(EventContext.MODULE_START, null);
        EntryPointContext ctx = (EntryPointContext) child.getContext("source");
        Assert.assertNotNull(ctx);
        InvocationHandler handler = (InvocationHandler) ctx.getInstance(null);
        Assert.assertEquals(0, mockInterceptor.getCount());
        Object response = handler.invoke(null, hello, new Object[] { "foo" });
        Assert.assertEquals("Hello foo", response);
        Assert.assertEquals(1, mockInterceptor.getCount());
        child.fireEvent(EventContext.MODULE_STOP, null);
        runtime.stop();
        
    }


}
