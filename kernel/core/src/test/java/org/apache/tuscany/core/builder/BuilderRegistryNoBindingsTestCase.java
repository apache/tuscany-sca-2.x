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

import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.Multiplicity;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

import junit.framework.TestCase;
import org.apache.tuscany.core.binding.local.LocalBindingBuilder;
import org.apache.tuscany.core.binding.local.LocalBindingDefinition;
import org.apache.tuscany.core.binding.local.LocalReferenceBinding;
import org.apache.tuscany.core.binding.local.LocalServiceBinding;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class BuilderRegistryNoBindingsTestCase extends TestCase {
    private DeploymentContext deploymentContext;
    private Component parent;
    private BuilderRegistry registry;

    public void testNoServiceBindings() throws Exception {
        ServiceBinding binding = EasyMock.createNiceMock(ServiceBinding.class);
        EasyMock.replay(binding);
        ServiceDefinition definition = new ServiceDefinition(URI.create("#foo"), null, false);
        definition.setTarget(new URI("foo"));
        EasyMock.replay(deploymentContext);
        EasyMock.replay(parent);

        Service service = registry.build(definition, deploymentContext);

        assertEquals(1, service.getServiceBindings().size());
        assertTrue(service.getServiceBindings().get(0) instanceof LocalServiceBinding);
        EasyMock.verify(deploymentContext);
        EasyMock.verify(parent);
    }

    public void testReferenceBindingBuilderDispatch() throws Exception {
        ReferenceBinding binding = EasyMock.createNiceMock(ReferenceBinding.class);
        EasyMock.replay(binding);
        ReferenceDefinition definition = new ReferenceDefinition(URI.create("#foo"), null, Multiplicity.ONE_ONE);
        EasyMock.replay(deploymentContext);
        EasyMock.replay(parent);

        Reference reference = registry.build(definition, deploymentContext);

        assertEquals(1, reference.getReferenceBindings().size());
        assertTrue(reference.getReferenceBindings().get(0) instanceof LocalReferenceBinding);
        EasyMock.verify(deploymentContext);
        EasyMock.verify(parent);
    }

    protected void setUp() throws Exception {
        super.setUp();
        deploymentContext = EasyMock.createMock(DeploymentContext.class);
        parent = EasyMock.createNiceMock(Component.class);
        registry = new BuilderRegistryImpl(null);
        registry.register(LocalBindingDefinition.class, new LocalBindingBuilder());
    }


}
