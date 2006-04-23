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
package org.apache.tuscany.container.js.integration;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.container.js.assembly.mock.HelloWorldService;
import org.apache.tuscany.container.js.builder.JavaScriptContextFactoryBuilder;
import org.apache.tuscany.container.js.builder.JavaScriptTargetWireBuilder;
import org.apache.tuscany.container.js.builder.MockInterceptorBuilder;
import org.apache.tuscany.container.js.invocation.mock.MockSyncInterceptor;
import org.apache.tuscany.container.js.mock.MockAssemblyFactory;
import org.apache.tuscany.container.js.mock.MockModuleFactory;
import org.apache.tuscany.core.builder.ContextFactoryBuilderRegistry;
import org.apache.tuscany.core.builder.impl.DefaultWireBuilder;
import org.apache.tuscany.core.builder.system.DefaultPolicyBuilderRegistry;
import org.apache.tuscany.core.builder.system.PolicyBuilderRegistry;
import org.apache.tuscany.core.client.BootstrapHelper;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.impl.CompositeContextImpl;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.runtime.RuntimeContextImpl;
import org.apache.tuscany.core.wire.jdk.JDKWireFactoryFactory;
import org.apache.tuscany.core.wire.service.DefaultWireFactoryService;
import org.apache.tuscany.core.wire.service.WireFactoryService;
import org.apache.tuscany.model.assembly.Scope;

/**
 * Integration tests for JavaScript components and aggregate contexts
 *
 * @version $Rev$ $Date$
 */
public class JSComponentContextTestCase extends TestCase {

    public void testBasicInvocation() throws Exception {
        MockSyncInterceptor mockInterceptor = new MockSyncInterceptor();
        MockInterceptorBuilder interceptorBuilder = new MockInterceptorBuilder(mockInterceptor);
        PolicyBuilderRegistry policyRegistry = new DefaultPolicyBuilderRegistry();
        policyRegistry.registerSourceBuilder(interceptorBuilder);
        WireFactoryService wireService = new DefaultWireFactoryService(new MessageFactoryImpl(), new JDKWireFactoryFactory(), policyRegistry);
        JavaScriptContextFactoryBuilder jsBuilder = new JavaScriptContextFactoryBuilder(wireService);

        ContextFactoryBuilderRegistry builderRegistry = BootstrapHelper.bootstrapContextFactoryBuilders(new NullMonitorFactory());
        builderRegistry.register(jsBuilder);
        DefaultWireBuilder defaultWireBuilder = new DefaultWireBuilder();

        RuntimeContext runtime = new RuntimeContextImpl(null, builderRegistry, defaultWireBuilder);
        runtime.addBuilder(new JavaScriptTargetWireBuilder());
        runtime.start();
        runtime.getRootContext().registerModelObject(
                MockAssemblyFactory.createSystemComponent("test.module", CompositeContextImpl.class.getName(),
                        Scope.AGGREGATE));
        CompositeContext child = (CompositeContext) runtime.getRootContext().getContext("test.module");
        child.registerModelObject(MockModuleFactory.createModule());
        child.publish(new ModuleStart(this));

        HelloWorldService source = (HelloWorldService) child.getContext("source").getInstance(new QualifiedName("./HelloWorldService"));
        Assert.assertNotNull(source);
        Assert.assertEquals("Hello foo", source.hello("foo"));
        //Assert.assertEquals(1, mockInterceptor.getCount());
        child.publish(new ModuleStop(this));
        runtime.stop();
    }

}

