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
package org.apache.tuscany.core.builder;

import java.net.URI;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.interceptor.InterceptorBuilder;
import org.apache.tuscany.spi.builder.interceptor.InterceptorBuilderRegistry;
import org.apache.tuscany.spi.model.physical.PhysicalInterceptorDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireSourceDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireTargetDefinition;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;
import org.apache.tuscany.core.builder.interceptor.InterceptorBuilderRegistryImpl;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ConnectorImplCreateWireTestCase extends TestCase {
    private static final QName QNAME = new QName("test");

    public void testCreateWire() throws Exception {
        TestConnector connector = new TestConnector();
        PhysicalWireDefinition definition = new PhysicalWireDefinition();
        PhysicalWireSourceDefinition sourceDefinition = new PhysicalWireSourceDefinition();
        sourceDefinition.setUri(URI.create("source"));
        PhysicalWireTargetDefinition targetDefinition = new PhysicalWireTargetDefinition();
        targetDefinition.setUri(URI.create("target"));
        definition.setSource(sourceDefinition);
        definition.setTarget(targetDefinition);
        PhysicalOperationDefinition operation = new PhysicalOperationDefinition();
        operation.setName("operation");
        definition.addOperation(operation);
        PhysicalOperationDefinition callback = new PhysicalOperationDefinition();
        callback.setName("callback");
        callback.setCallback(true);
        definition.addOperation(callback);
        Wire wire = connector.createWire(definition);
        assertEquals(2, wire.getPhysicalInvocationChains().size());
    }

    public void testDispatchToBuilder() throws Exception {
        QName qName = new QName("interceptor");
        InterceptorBuilder builder = EasyMock.createMock(InterceptorBuilder.class);
        EasyMock.expect(builder.build(EasyMock.isA(PhysicalInterceptorDefinition.class))).andReturn(null).times(2);
        EasyMock.replay(builder);
        InterceptorBuilderRegistryImpl regisry = new InterceptorBuilderRegistryImpl();
        regisry.register(qName, builder);
        TestConnector connector = new TestConnector(regisry);
        PhysicalWireDefinition definition = new PhysicalWireDefinition();
        PhysicalWireSourceDefinition sourceDefinition = new PhysicalWireSourceDefinition();
        sourceDefinition.setUri(URI.create("source"));
        PhysicalWireTargetDefinition targetDefinition = new PhysicalWireTargetDefinition();
        targetDefinition.setUri(URI.create("target"));
        definition.setSource(sourceDefinition);
        definition.setTarget(targetDefinition);
        PhysicalInterceptorDefinition interceptorDefinition = new PhysicalInterceptorDefinition(QNAME);
        interceptorDefinition.setBuilder(qName);

        PhysicalOperationDefinition operation = new PhysicalOperationDefinition();
        operation.setName("operation");
        operation.addInterceptor(interceptorDefinition);
        definition.addOperation(operation);
        PhysicalOperationDefinition callback = new PhysicalOperationDefinition();
        callback.setName("callback");
        callback.setCallback(true);
        callback.addInterceptor(interceptorDefinition);
        definition.addOperation(callback);
        connector.createWire(definition);
        EasyMock.verify(builder);
    }

    private class TestConnector extends ConnectorImpl {

        public TestConnector() {
            super(null);
        }

        public TestConnector(InterceptorBuilderRegistry regisry) {
            super(regisry, null, null, null, null, null);
        }


        public Wire createWire(PhysicalWireDefinition definition) throws BuilderException {
            return super.createWire(definition);
        }
    }
}
