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

import java.net.URL;

import org.apache.tuscany.spi.component.Reference;

import junit.framework.TestCase;
import org.apache.tuscany.container.spring.mock.TestBean;
import org.apache.tuscany.container.spring.mock.TestBeanImpl;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

/**
 * Verifies wiring from a Spring bean to an SCA composite reference
 *
 * @version $$Rev$$ $$Date$$
 */
public class ReferenceInvocationTestCase extends TestCase {

    public void testInvocation() throws Exception {
        URL url = getClass().getClassLoader().getResource("META-INF/sca/testReferenceContext.xml");
        Resource resource = new UrlResource(url);
        SpringCompositeComponent parent = new SpringCompositeComponent("spring", resource, null, null, null);
        TestBean referenceTarget = new TestBeanImpl();
        Reference reference = createMock(Reference.class);
        expect(reference.getName()).andReturn("bar").anyTimes();
        expect(reference.isSystem()).andReturn(false).atLeastOnce();
        expect(reference.getInterface()).andStubReturn(TestBean.class);
        expect(reference.getServiceInstance()).andStubReturn(referenceTarget);
        reference.start();
        replay(reference);
        parent.register(reference);
        parent.start();
        parent.locateService(TestBean.class, "testBean");
    }

}
