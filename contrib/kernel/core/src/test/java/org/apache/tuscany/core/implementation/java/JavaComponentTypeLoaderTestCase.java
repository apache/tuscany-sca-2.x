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
package org.apache.tuscany.core.implementation.java;

import java.net.URL;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.implementation.java.IntrospectionRegistry;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.loader.LoaderRegistry;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * @version $Rev$ $Date$
 */
public class JavaComponentTypeLoaderTestCase extends TestCase {

    @SuppressWarnings("unchecked")
    public void testPojoComponentTypeCreatedForIntrospection() throws Exception {
        IntrospectionRegistry registry = EasyMock.createMock(IntrospectionRegistry.class);
        registry.introspect(
            (Class) EasyMock.isNull(),
            EasyMock.isA(PojoComponentType.class),
            (DeploymentContext) EasyMock.isNull());
        EasyMock.expectLastCall().andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return EasyMock.getCurrentArguments()[2];
            }
        });
        EasyMock.replay(registry);
        JavaComponentTypeLoader loader = new JavaComponentTypeLoader(null, registry);
        loader.loadByIntrospection(new JavaImplementation(), null);
        EasyMock.verify(registry);
    }

    @SuppressWarnings("unchecked")
    public void testPojoComponentTypeCreatedForSideFileLoadAndReturned() throws Exception {
        LoaderRegistry registry = EasyMock.createMock(LoaderRegistry.class);
        registry.load(
            EasyMock.isA(PojoComponentType.class),
            (URL) EasyMock.isNull(),
            EasyMock.eq(PojoComponentType.class),
            (DeploymentContext) EasyMock.isNull());
        EasyMock.expectLastCall().andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return EasyMock.getCurrentArguments()[0];
            }
        });
        EasyMock.replay(registry);
        JavaComponentTypeLoader loader = new JavaComponentTypeLoader(registry, null);
        assertEquals(PojoComponentType.class, loader.loadFromSidefile(null, null).getClass());
        EasyMock.verify(registry);
    }


}
