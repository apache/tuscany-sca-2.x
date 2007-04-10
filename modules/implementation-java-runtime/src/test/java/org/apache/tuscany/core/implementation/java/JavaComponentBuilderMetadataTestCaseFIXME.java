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

import java.lang.reflect.Constructor;
import java.net.URI;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.implementation.java.impl.ConstructorDefinition;
import org.apache.tuscany.implementation.java.impl.JavaElement;
import org.apache.tuscany.implementation.java.impl.JavaImplementationDefinition;
import org.apache.tuscany.implementation.java.impl.Scope;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.easymock.EasyMock;

/**
 * Verifies component type metadata is properly applied to the component
 * 
 * @version $$Rev$$ $$Date: 2007-03-29 20:36:39 -0700 (Thu, 29 Mar
 *          2007) $$
 */
public class JavaComponentBuilderMetadataTestCaseFIXME extends TestCase {
    private DeploymentContext deploymentContext;
    private Constructor<SourceImpl> constructor;
    private Component parent;
    private JavaImplementationDefinition type;
    private org.apache.tuscany.assembly.Component definition;
    private ScopeContainer scopeContainer;
    private AssemblyFactory factory = new DefaultAssemblyFactory();

    public void testMaxAge() throws Exception {
        type.setMaxAge(100);
        JavaComponentBuilder builder = new JavaComponentBuilder();
        JavaAtomicComponent component = (JavaAtomicComponent)builder.build(definition, deploymentContext);
        assertEquals(100, component.getMaxAge());
    }

    public void testMaxIdleTime() throws Exception {
        type.setMaxIdleTime(100);
        JavaComponentBuilder builder = new JavaComponentBuilder();
        JavaAtomicComponent component = (JavaAtomicComponent)builder.build(definition, deploymentContext);
        assertEquals(100, component.getMaxIdleTime());
    }

    public void testNoMaxAgeNoMaxIdleTime() throws Exception {
        JavaComponentBuilder builder = new JavaComponentBuilder();
        JavaAtomicComponent component = (JavaAtomicComponent)builder.build(definition, deploymentContext);
        assertEquals(-1, component.getMaxAge());
        assertEquals(-1, component.getMaxIdleTime());
    }

    public void testScope() throws Exception {
        JavaComponentBuilder builder = new JavaComponentBuilder();
        JavaAtomicComponent component = (JavaAtomicComponent)builder.build(definition, deploymentContext);
        component.setScopeContainer(scopeContainer);
        assertEquals(Scope.COMPOSITE, component.getScope());
    }

    protected void setUp() throws Exception {
        super.setUp();
        parent = EasyMock.createMock(Component.class);
        EasyMock.expect(parent.getUri()).andReturn(URI.create("parent")).anyTimes();
        EasyMock.replay(parent);
        constructor = SourceImpl.class.getConstructor((Class[])null);
        createDeploymentContext();
        createComponentDefinitionAndType();
    }

    private void createDeploymentContext() {
        scopeContainer = EasyMock.createMock(ScopeContainer.class);
        scopeContainer.start();
        scopeContainer.stop();
        scopeContainer.register(EasyMock.isA(AtomicComponent.class), EasyMock.isA(URI.class));
        EasyMock.expectLastCall().atLeastOnce();
        EasyMock.expect(scopeContainer.getScope()).andReturn(org.apache.tuscany.spi.Scope.COMPOSITE).atLeastOnce();
        EasyMock.replay(scopeContainer);
        deploymentContext = EasyMock.createMock(DeploymentContext.class);
        EasyMock.expect(deploymentContext.getGroupId()).andStubReturn(URI.create("composite"));
        EasyMock.replay(deploymentContext);
    }

    private void createComponentDefinitionAndType() throws Exception {
        type = new JavaImplementationDefinition();
        type.setScope(Scope.COMPOSITE);
        ComponentReference reference = factory.createComponentReference();
        reference.setName("target");
        type.getReferenceMembers().put("target",
                                       new JavaElement(SourceImpl.class.getMethod("setTarget", Target.class), 0));
        type.getReferences().add(reference);

        ComponentService serviceDefinition = ModelHelper.createService(Source.class);
        serviceDefinition.setName("Source");
        type.getServices().add(serviceDefinition);
        type.setConstructorDefinition(new ConstructorDefinition<SourceImpl>(constructor));
        type.setJavaClass(SourceImpl.class);
        definition.setImplementation(type);
        definition.setName("component");
    }

}
