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
package org.apache.tuscany.core.builder.interceptor;

import javax.xml.namespace.QName;

import org.apache.tuscany.spi.builder.interceptor.InterceptorBuilder;
import org.apache.tuscany.spi.builder.interceptor.InterceptorBuilderRegistry;
import org.apache.tuscany.spi.model.physical.PhysicalInterceptorDefinition;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class InterceptorBuilderRegistryImplTestCase extends TestCase {
    private static final QName QNAME = new QName("builder");
    private static final QName INTERCEPTOR_QNAME = new QName("interceptor");

    private InterceptorBuilderRegistry registry = new InterceptorBuilderRegistryImpl();

    public void testDispatch() throws Exception {
        InterceptorBuilder builder = EasyMock.createMock(InterceptorBuilder.class);
        EasyMock.expect(builder.build(EasyMock.isA(PhysicalInterceptorDefinition.class))).andReturn(null);
        EasyMock.replay(builder);
        registry.register(QNAME, builder);
        PhysicalInterceptorDefinition definition = new PhysicalInterceptorDefinition(INTERCEPTOR_QNAME);
        definition.setBuilder(QNAME);
        registry.build(definition);
        EasyMock.verify(builder);
    }

    public void testUnregister() throws Exception {
        InterceptorBuilder builder = EasyMock.createMock(InterceptorBuilder.class);
        EasyMock.replay(builder);
        registry.register(QNAME, builder);
        registry.unregister(QNAME);
        PhysicalInterceptorDefinition definition = new PhysicalInterceptorDefinition(INTERCEPTOR_QNAME);
        definition.setBuilder(QNAME);
        try {
            registry.build(definition);
            //fail
        } catch (InterceptorBuilderNotFoundException e) {
            // expected
        }
        EasyMock.verify(builder);
    }
}
