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
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.BindingDefinition;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.Multiplicity;
import org.apache.tuscany.spi.model.ServiceDefinition;

import junit.framework.TestCase;
import org.apache.tuscany.core.binding.local.LocalBindingBuilder;
import org.apache.tuscany.core.binding.local.LocalBindingDefinition;
import org.apache.tuscany.core.binding.local.LocalReferenceBinding;
import org.apache.tuscany.core.binding.local.LocalServiceBinding;
import org.apache.tuscany.core.deployer.RootDeploymentContext;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class BuilderRegistryNoBindingsTestCase extends TestCase {
    private DeploymentContext deploymentContext;
    private CompositeComponent parent;
    private BuilderRegistry registry;

    public void testNoServiceBindings() throws Exception {
        ServiceBinding binding = EasyMock.createNiceMock(ServiceBinding.class);
        EasyMock.replay(binding);
        ServiceDefinition definition = new ServiceDefinition("foo", null, false);
        definition.setTarget(new URI("foo"));
        Service service = registry.build(parent, definition, deploymentContext);
        assertEquals(1, service.getServiceBindings().size());
        assertTrue(service.getServiceBindings().get(0) instanceof LocalServiceBinding);
    }

    public void testReferenceBindingBuilderDispatch() throws Exception {
        ReferenceBinding binding = EasyMock.createNiceMock(ReferenceBinding.class);
        EasyMock.replay(binding);
        List<BindingDefinition> bindingDefs = new ArrayList<BindingDefinition>();
        BoundReferenceDefinition definition =
            new BoundReferenceDefinition("foo", null, bindingDefs, Multiplicity.ONE_ONE);
        Reference reference = registry.build(parent, definition, deploymentContext);
        assertEquals(1, reference.getReferenceBindings().size());
        assertTrue(reference.getReferenceBindings().get(0) instanceof LocalReferenceBinding);
    }

    protected void setUp() throws Exception {
        super.setUp();
        deploymentContext = new RootDeploymentContext(null, null, null, null);
        parent = EasyMock.createNiceMock(CompositeComponent.class);
        EasyMock.replay(parent);
        registry = new BuilderRegistryImpl(null, null);
        registry.register(LocalBindingDefinition.class, new LocalBindingBuilder());
    }


}
