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

import junit.framework.TestCase;

import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.CompositeReference;
import org.apache.tuscany.assembly.CompositeService;
import org.apache.tuscany.assembly.Multiplicity;
import org.apache.tuscany.assembly.SCABinding;
import org.apache.tuscany.assembly.impl.ComponentImpl;
import org.apache.tuscany.assembly.impl.ComponentReferenceImpl;
import org.apache.tuscany.assembly.impl.ComponentServiceImpl;
import org.apache.tuscany.assembly.impl.CompositeReferenceImpl;
import org.apache.tuscany.assembly.impl.CompositeServiceImpl;
import org.apache.tuscany.assembly.impl.SCABindingImpl;
import org.apache.tuscany.core.binding.local.LocalBindingBuilder;
import org.apache.tuscany.core.binding.local.LocalBindingDefinition;
import org.apache.tuscany.core.binding.local.LocalReferenceBinding;
import org.apache.tuscany.core.binding.local.LocalServiceBinding;
import org.apache.tuscany.core.component.ComponentManagerImpl;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
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
        CompositeService definition = new CompositeServiceImpl();
        definition.setName("foo");
        ComponentService componentService = new ComponentServiceImpl();
        componentService.setName("foo");
        definition.setPromotedService(componentService);
        SCABinding binding2 = new SCABindingImpl();
        binding2.setComponent(new ComponentImpl());
        componentService.getBindings().add(binding2);
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
        CompositeReference definition = new CompositeReferenceImpl();
        ComponentReference componentReference = new ComponentReferenceImpl();
        componentReference.setName("foo");
        definition.getPromotedReferences().add(componentReference);
        SCABinding binding2 = new SCABindingImpl();
        binding2.setComponent(new ComponentImpl());
        componentReference.getBindings().add(binding2);
        definition.setName("foo");
        definition.setMultiplicity(Multiplicity.ONE_ONE);
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
        EasyMock.expect(deploymentContext.getComponentId()).andReturn(URI.create("/default/")).anyTimes();
        parent = EasyMock.createNiceMock(Component.class);
        registry = new BuilderRegistryImpl(new ComponentManagerImpl(), null);
        registry.register(LocalBindingDefinition.class, new LocalBindingBuilder());
    }


}
