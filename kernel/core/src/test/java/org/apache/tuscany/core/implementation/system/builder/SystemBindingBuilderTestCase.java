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
package org.apache.tuscany.core.implementation.system.builder;

import java.net.URI;

import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ServiceContract;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.system.model.SystemBindingDefinition;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class SystemBindingBuilderTestCase extends TestCase {

    public void testBuild() throws Exception {
        SystemBindingBuilder builder = new SystemBindingBuilder();
        AtomicComponent componet = EasyMock.createMock(AtomicComponent.class);
        EasyMock.replay(componet);
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getSystemChild("foo")).andReturn(componet);
        EasyMock.replay(parent);
        DeploymentContext context = EasyMock.createMock(DeploymentContext.class);
        EasyMock.replay(context);
        BoundServiceDefinition definition = new BoundServiceDefinition();
        definition.setName("bar");
        definition.setTarget(new URI("foo"));
        ServiceContract<Object> contract = new ServiceContract<Object>(Object.class) {
        };
        definition.setServiceContract(contract);
        ServiceBinding serviceBinding = builder.build(parent, definition, null, context);
        assertEquals("bar", serviceBinding.getName());
        assertEquals(contract, serviceBinding.getBindingServiceContract());
        assertEquals(parent, serviceBinding.getParent());
    }

    public void testRegister() {
        BuilderRegistry registry = EasyMock.createMock(BuilderRegistry.class);
        registry.register(EasyMock.eq(SystemBindingDefinition.class), EasyMock.isA(SystemBindingBuilder.class));
        EasyMock.replay(registry);
        SystemBindingBuilder builder = new SystemBindingBuilder();
        builder.setBuilderRegistry(registry);
        builder.init();
        EasyMock.verify(registry);
    }
}
