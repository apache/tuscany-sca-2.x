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

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.container.java.assembly.mock.HelloWorldService;
import org.apache.tuscany.container.java.builder.JavaComponentContextBuilder;
import org.apache.tuscany.container.java.builder.JavaTargetWireBuilder;
import org.apache.tuscany.container.java.builder.MockInterceptorBuilder;
import org.apache.tuscany.container.java.invocation.mock.MockSyncInterceptor;
import org.apache.tuscany.container.java.mock.MockAssemblyFactory;
import org.apache.tuscany.container.java.mock.MockModuleFactory;
import org.apache.tuscany.container.java.mock.binding.foo.FooBindingBuilder;
import org.apache.tuscany.container.java.mock.binding.foo.FooBindingWireBuilder;
import org.apache.tuscany.container.java.mock.components.GenericComponent;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.builder.impl.DefaultWireBuilder;
import org.apache.tuscany.core.builder.impl.HierarchicalBuilder;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.impl.AggregateContextImpl;
import org.apache.tuscany.core.invocation.jdk.JDKProxyFactoryFactory;
import org.apache.tuscany.core.invocation.spi.ProxyFactoryFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.runtime.RuntimeContextImpl;
import org.apache.tuscany.core.system.builder.SystemComponentContextBuilder;
import org.apache.tuscany.core.system.builder.SystemEntryPointBuilder;
import org.apache.tuscany.core.system.builder.SystemExternalServiceBuilder;
import org.apache.tuscany.model.assembly.Scope;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * 
 * 
 * @version $Rev$ $Date$
 */
public class JavaToExternalServiceTestCase extends TestCase {

    public void testJavaToES() throws Exception {
        MessageFactory msgFactory = new MessageFactoryImpl();
        ProxyFactoryFactory proxyFactoryFactory =new JDKProxyFactoryFactory();

        List<RuntimeConfigurationBuilder> builders = new ArrayList();
        builders.add((new SystemComponentContextBuilder()));
        builders.add(new SystemEntryPointBuilder());
        builders.add(new SystemExternalServiceBuilder());

        JavaComponentContextBuilder javaBuilder = new JavaComponentContextBuilder();
        javaBuilder.setMessageFactory(msgFactory);
        javaBuilder.setProxyFactoryFactory(proxyFactoryFactory);

        MockSyncInterceptor mockInterceptor = new MockSyncInterceptor();
        MockInterceptorBuilder interceptorBuilder = new MockInterceptorBuilder(mockInterceptor, true);
        HierarchicalBuilder refBuilder = new HierarchicalBuilder();
        refBuilder.addBuilder(interceptorBuilder);
        javaBuilder.setReferenceBuilder(refBuilder);
        builders.add(javaBuilder);

        FooBindingBuilder fooBindingBuilder = new FooBindingBuilder();
        fooBindingBuilder.setMessageFactory(msgFactory);
        fooBindingBuilder.setProxyFactoryFactory(proxyFactoryFactory);
        builders.add(fooBindingBuilder);
        
        DefaultWireBuilder defaultWireBuilder = new DefaultWireBuilder();

        RuntimeContext runtime = new RuntimeContextImpl(null, null, builders, defaultWireBuilder);
        runtime.addBuilder(new JavaTargetWireBuilder());
        runtime.addBuilder(new FooBindingWireBuilder());
        runtime.start();
        runtime.getRootContext().registerModelObject(
                MockAssemblyFactory.createSystemComponent("test.module", AggregateContextImpl.class.getName(), Scope.AGGREGATE));
        AggregateContext child = (AggregateContext) runtime.getRootContext().getContext("test.module");
        child.registerModelObject(MockModuleFactory.createModuleWithExerntalService());
        child.fireEvent(EventContext.MODULE_START, null);
        HelloWorldService source = (HelloWorldService) child.locateInstance("source");
        Assert.assertNotNull(source);
        Assert.assertEquals(0, mockInterceptor.getCount());
        Assert.assertEquals("foo", source.hello("foo"));
        Assert.assertEquals(1, mockInterceptor.getCount());
        child.fireEvent(EventContext.MODULE_STOP, null);
        runtime.stop();
    }

}

