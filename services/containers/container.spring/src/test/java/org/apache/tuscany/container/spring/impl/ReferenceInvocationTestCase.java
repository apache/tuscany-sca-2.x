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

import org.apache.tuscany.spi.component.Reference;

import junit.framework.TestCase;
import org.apache.tuscany.container.spring.mock.TestBean;
import org.apache.tuscany.container.spring.mock.TestBeanImpl;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

/**
 * Verifies wiring from a Spring bean to an SCA composite reference
 *
 * @version $$Rev$$ $$Date$$
 */
public class ReferenceInvocationTestCase extends TestCase {

    public void testInvocation() throws Exception {
        AbstractApplicationContext ctx = createSpringContext();
        SpringCompositeComponent parent = new SpringCompositeComponent("spring", ctx, null, null, null);
        parent.start();
        TestBean referenceTarget = new TestBeanImpl();
        Reference reference = createMock(Reference.class);
        expect(reference.getName()).andReturn("bar").anyTimes();
        expect(reference.getInterface()).andStubReturn(TestBean.class);
        expect(reference.getServiceInstance()).andStubReturn(referenceTarget);
        replay(reference);
        parent.register(reference);
        ctx.getBean("foo");
    }

    private AbstractApplicationContext createSpringContext() {
        StaticApplicationContext beanFactory = new StaticApplicationContext();
        RootBeanDefinition definition = new RootBeanDefinition(TestBeanImpl.class);
        //REVIEW we need to figure out how to handle eager init components
        definition.setLazyInit(true);
        RuntimeBeanReference ref = new RuntimeBeanReference("bar");
        PropertyValue val = new PropertyValue("bean", ref);
        definition.getPropertyValues().addPropertyValue(val);
        beanFactory.registerBeanDefinition("foo", definition);
        return beanFactory;
    }
}
