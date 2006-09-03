/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.container.spring.impl;

import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.extension.ServiceExtension;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;

import junit.framework.TestCase;
import org.apache.tuscany.container.spring.mock.TestBean;
import org.apache.tuscany.container.spring.mock.TestBeanImpl;
import org.apache.tuscany.test.ArtifactFactory;
import static org.apache.tuscany.test.ArtifactFactory.createWireService;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

/**
 * Tests a simple invocation through a service to a Spring bean
 *
 * @version $$Rev$$ $$Date$$
 */
public class ServiceInvocationTestCase extends TestCase {

    public void testInvocation() throws InvalidServiceContractException {
        AbstractApplicationContext springContext = createSpringContext();
        SpringCompositeComponent<?> context = new SpringCompositeComponent("parent", springContext, null, null, null);
        InboundWire<TestBean> inboundWire = ArtifactFactory.createInboundWire("fooService", TestBean.class);
        OutboundWire<TestBean> outboundWire = ArtifactFactory.createOutboundWire("fooService", TestBean.class);
        ArtifactFactory.terminateWire(outboundWire);
        Service<TestBean> service =
            new ServiceExtension<TestBean>("fooService", TestBean.class, context, createWireService());
        service.setInboundWire(inboundWire);
        service.setOutboundWire(outboundWire);
        Connector connector = ArtifactFactory.createConnector();
        connector.connect(inboundWire, outboundWire, true);
        for (InboundInvocationChain chain : inboundWire.getInvocationChains().values()) {
            chain.setTargetInvoker(context.createTargetInvoker("foo", chain.getOperation()));
        }
        context.register(service);
        TestBean serviceInstance = (TestBean) context.getService("fooService").getServiceInstance();
        assertEquals("bar", serviceInstance.echo("bar"));
    }


    private AbstractApplicationContext createSpringContext() {
        StaticApplicationContext beanFactory = new StaticApplicationContext();
        RootBeanDefinition definition = new RootBeanDefinition(TestBeanImpl.class);
        definition.setLazyInit(true);
        beanFactory.registerBeanDefinition("foo", definition);
        return beanFactory;
    }

}
