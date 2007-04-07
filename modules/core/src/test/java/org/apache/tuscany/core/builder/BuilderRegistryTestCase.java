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
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.CompositeReference;
import org.apache.tuscany.assembly.CompositeService;
import org.apache.tuscany.assembly.Implementation;
import org.apache.tuscany.assembly.Multiplicity;
import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.assembly.impl.BindingImpl;
import org.apache.tuscany.assembly.impl.ComponentImpl;
import org.apache.tuscany.assembly.impl.ComponentServiceImpl;
import org.apache.tuscany.assembly.impl.ComponentTypeImpl;
import org.apache.tuscany.assembly.impl.CompositeImpl;
import org.apache.tuscany.assembly.impl.CompositeReferenceImpl;
import org.apache.tuscany.assembly.impl.CompositeServiceImpl;
import org.apache.tuscany.core.component.scope.ScopeRegistryImpl;
import org.apache.tuscany.core.component.scope.StatelessScopeContainer;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.builder.BindingBuilder;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class BuilderRegistryTestCase extends TestCase {
    private DeploymentContext deploymentContext;
    private ScopeContainer scopeContainer;
    private Map<URI, Component> components;

    //private BuilderRegistryImpl registry;
    private Component parent;

    @SuppressWarnings({"unchecked"})
    public void testRegistration() throws Exception {
        URI componentId = URI.create("sca://localhost/component");
        Composite implementation = new CompositeImpl();
        org.apache.tuscany.assembly.Component componentDefinition =
            new ComponentImpl();
        componentDefinition.setImplementation(implementation);

        Component component = EasyMock.createMock(Component.class);
        // FIXME:
        Map<String, Property> properties = new HashMap<String, Property>();
        for (Property p : componentDefinition.getProperties()) {
            properties.put(p.getName(), p);
        }
        component.setDefaultPropertyValues(properties);
        component.setScopeContainer(scopeContainer);
        EasyMock.expect(component.getUri()).andReturn(componentId);
        EasyMock.replay(component);

        EasyMock.expect(deploymentContext.getCompositeScope()).andReturn(scopeContainer);
        EasyMock.expect(deploymentContext.getComponents()).andReturn(components);
        EasyMock.replay(deploymentContext);

        EasyMock.expect(components.put(componentId, component)).andReturn(null);
        EasyMock.replay(components);

        ComponentBuilder builder = EasyMock.createMock(ComponentBuilder.class);
        EasyMock.expect(builder.build(componentDefinition, deploymentContext)).andReturn(component);
        EasyMock.replay(builder);

        ScopeRegistry scopeRegistry = new ScopeRegistryImpl();
        scopeRegistry.register(scopeContainer);
        BuilderRegistry registry = new BuilderRegistryImpl(scopeRegistry);
        registry.register(Composite.class, builder);

        assertSame(component, registry.build(componentDefinition, deploymentContext));
        EasyMock.verify(builder);
    }

    @SuppressWarnings({"unchecked"})
    public void testServiceBindingBuilderDispatch() throws Exception {
        BuilderRegistry registry = new BuilderRegistryImpl(null);
        ServiceBinding binding = EasyMock.createNiceMock(ServiceBinding.class);
        EasyMock.replay(binding);
        BindingBuilder<MockBindingDefinition> builder = EasyMock.createMock(BindingBuilder.class);
        EasyMock.expect(builder.build(
            EasyMock.isA(CompositeService.class),
            EasyMock.isA(MockBindingDefinition.class),
            EasyMock.isA(DeploymentContext.class))).andReturn(binding).times(2);
        EasyMock.replay(builder);
        registry.register(MockBindingDefinition.class, builder);
        CompositeService definition = new CompositeServiceImpl();
        definition.setName("foo");
        definition.getBindings().add(new MockBindingDefinition());
        definition.getBindings().add(new MockBindingDefinition());
        ComponentService target = new ComponentServiceImpl();
        target.setName("foo");
        definition.setPromotedService(target);
        Service service = registry.build(definition, deploymentContext);
        assertEquals(2, service.getServiceBindings().size());
    }

    @SuppressWarnings({"unchecked"})
    public void testReferenceBindingBuilderDispatch() throws Exception {
        BuilderRegistry registry = new BuilderRegistryImpl(null);
        ReferenceBinding binding = EasyMock.createNiceMock(ReferenceBinding.class);
        EasyMock.replay(binding);
        BindingBuilder<MockBindingDefinition> builder = EasyMock.createMock(BindingBuilder.class);
        EasyMock.expect(builder.build(
            EasyMock.isA(CompositeReference.class),
            EasyMock.isA(MockBindingDefinition.class),
            EasyMock.isA(DeploymentContext.class))).andReturn(binding).times(2);
        EasyMock.replay(builder);
        registry.register(MockBindingDefinition.class, builder);
        CompositeReference definition = new CompositeReferenceImpl();
        definition.setName("foo");
        definition.setMultiplicity(Multiplicity.ONE_ONE);
        definition.getBindings().add(new MockBindingDefinition());
        definition.getBindings().add(new MockBindingDefinition());
        Reference reference = registry.build(definition, deploymentContext);
        assertEquals(2, reference.getReferenceBindings().size());
    }

    /*
    @SuppressWarnings({"unchecked"})
    public void testNoConversationalContract() throws Exception {
        ScopeRegistry scopeRegistry = EasyMock.createMock(ScopeRegistry.class);
        ScopeContainer scopeContainer = EasyMock.createNiceMock(ScopeContainer.class);
        EasyMock.expect(scopeRegistry.getScopeContainer(EasyMock.isA(Scope.class))).andReturn(scopeContainer);
        EasyMock.replay(scopeRegistry);
        BuilderRegistry registry = new BuilderRegistryImpl(scopeRegistry);

        URI uri = URI.create("foo");
        AtomicComponent component = EasyMock.createNiceMock(AtomicComponent.class);
        EasyMock.expect(component.getUri()).andReturn(uri);
        EasyMock.replay(component);
        ComponentBuilder builder = EasyMock.createMock(ComponentBuilder.class);
        EasyMock.expect(builder.build(
            EasyMock.isA(org.apache.tuscany.assembly.Component.class),
            EasyMock.isA(DeploymentContext.class))).andReturn(component);
        EasyMock.replay(builder);
        registry.register(FooImplementation.class, builder);
        
        FooImplementation impl = new FooImplementation();
        // FIXME:
        // impl.setImplementationScope(Scope.CONVERSATION);
        org.apache.tuscany.assembly.Component definition = new ComponentImpl();
        definition.setImplementation(impl);

        EasyMock.expect(deploymentContext.getComponents()).andReturn(components);
        EasyMock.replay(deploymentContext);

        EasyMock.expect(components.put(uri, component)).andReturn(null);
        EasyMock.replay(components);
        try {
            registry.build(definition, deploymentContext);
            fail("Should throw NoConversationalContractException");
        } catch (NoConversationalContractException e) {
            // expected
        }
    }
    */

    @SuppressWarnings({"unchecked"})
    protected void setUp() throws Exception {
        super.setUp();
        deploymentContext = EasyMock.createMock(DeploymentContext.class);
        parent = EasyMock.createNiceMock(Component.class);
        scopeContainer = EasyMock.createMock(ScopeContainer.class);
        EasyMock.expect(scopeContainer.getScope()).andReturn(Scope.STATELESS).anyTimes();
        EasyMock.replay(scopeContainer);
        components = EasyMock.createMock(Map.class);
    }

    private class MockBuilder implements ComponentBuilder {
        public Component build(
            org.apache.tuscany.assembly.Component componentDefinition,
            DeploymentContext deploymentContext) throws BuilderConfigException {
            return null;
        }
    }

    private class MockBindingDefinition extends BindingImpl {

    }

    private class FooImplementation extends ComponentTypeImpl implements Implementation {

    }


}
