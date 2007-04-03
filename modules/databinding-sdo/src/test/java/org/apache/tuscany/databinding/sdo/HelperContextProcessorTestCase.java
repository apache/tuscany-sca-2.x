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

package org.apache.tuscany.databinding.sdo;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;

import javax.xml.stream.XMLInputFactory;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.Resource;
import org.easymock.EasyMock;

import commonj.sdo.helper.HelperContext;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class HelperContextProcessorTestCase extends TestCase {

    @SuppressWarnings("unchecked")
    public void testProcessor() throws Exception {
        HelperContextRegistry registry = new HelperContextRegistryImpl();
        HelperContextProcessor processor = new HelperContextProcessor(registry);
        URI id = URI.create("/composite1/");
        XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
        DeploymentContext deploymentContext = EasyMock.createMock(DeploymentContext.class);
        expect(deploymentContext.getXmlFactory()).andReturn(xmlFactory).anyTimes();
        expect(deploymentContext.getComponentId()).andReturn(id).anyTimes();
        expect(deploymentContext.getClassLoader()).andReturn(getClass().getClassLoader()).anyTimes();
        replay(deploymentContext);

        PojoComponentType componentType = new PojoComponentType(FooImpl.class);
        for (Field f : FooImpl.class.getDeclaredFields()) {
            processor.visitField(f, componentType, deploymentContext);

        }
        for (Method m : FooImpl.class.getMethods()) {
            processor.visitMethod(m, componentType, deploymentContext);
        }

        Resource<?> r1 = (Resource<?>)componentType.getResources().get("context");
        assertNotNull(r1);
        Resource<?> r2 = (Resource<?>)componentType.getResources().get("context2");
        assertNotNull(r2);
        HelperContext c1 = (HelperContext)r1.getObjectFactory().getInstance();
        HelperContext c2 = (HelperContext)r2.getObjectFactory().getInstance();
        assertSame(c1, c2);
    }

    private static class FooImpl {
        @org.apache.tuscany.databinding.sdo.api.HelperContext
        protected HelperContext context2;

        private HelperContext context;

        @org.apache.tuscany.databinding.sdo.api.HelperContext
        public void setContext(HelperContext context1) {
            this.context = context1;
        }
    }
}
