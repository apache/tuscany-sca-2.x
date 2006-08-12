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
package org.apache.tuscany.container.spring;

import org.apache.tuscany.container.spring.mock.TestBean;
import org.apache.tuscany.container.spring.mock.TestBeanImpl;
import org.apache.tuscany.spi.component.Reference;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Verifies wiring from a Spring bean to an SCA composite reference
 *
 * @version $$Rev$$ $$Date$$
 */
public class ReferenceInvocationTestCase extends MockObjectTestCase {

    public void testInvocation() throws Exception {
        ConfigurableApplicationContext ctx = createSpringContext();
        SpringCompositeComponent parent = new SpringCompositeComponent("spring", ctx, null, null);
        parent.start();
        TestBean referenceTarget = new TestBeanImpl();
        Mock mock = mock(Reference.class);
        mock.stubs().method("getName").will(returnValue("bar"));
        mock.stubs().method("getInterface").will(returnValue(TestBean.class));
        mock.expects(atLeastOnce()).method("getServiceInstance").will(returnValue(referenceTarget));
        Reference reference = (Reference) mock.proxy();
        parent.register(reference);
        ctx.getBean("foo");
    }

    private ConfigurableApplicationContext createSpringContext() {
        StaticApplicationContext beanFactory = new StaticApplicationContext();
        BeanDefinition definition = new RootBeanDefinition(TestBeanImpl.class);
        RuntimeBeanReference ref = new RuntimeBeanReference("bar");
        PropertyValue val = new PropertyValue("bean", ref);
        definition.getPropertyValues().addPropertyValue(val);
        beanFactory.registerBeanDefinition("foo", definition);
        return beanFactory;
    }
}
