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
package org.apache.tuscany.implementation.java.context;

import java.lang.reflect.Constructor;
import java.net.URI;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.implementation.java.impl.DefaultJavaImplementationFactory;
import org.apache.tuscany.implementation.java.impl.JavaConstructorImpl;
import org.apache.tuscany.implementation.java.impl.JavaElementImpl;
import org.apache.tuscany.implementation.java.impl.JavaScopeImpl;
import org.apache.tuscany.implementation.java.mock.Source;
import org.apache.tuscany.implementation.java.mock.SourceImpl;
import org.apache.tuscany.implementation.java.mock.Target;
import org.apache.tuscany.interfacedef.java.JavaFactory;
import org.apache.tuscany.interfacedef.java.impl.DefaultJavaFactory;
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
    private JavaImplementation type;
    private org.apache.tuscany.assembly.Component definition;
    private ScopeContainer scopeContainer;
    private AssemblyFactory factory = new DefaultAssemblyFactory();
    private JavaFactory javaFactory = new DefaultJavaFactory();

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
        assertEquals(JavaScopeImpl.COMPOSITE, component.getScope());
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
        JavaImplementationFactory javaImplementationFactory = new DefaultJavaImplementationFactory(factory);
        type = javaImplementationFactory.createJavaImplementation();
        type.setJavaScope(JavaScopeImpl.COMPOSITE);
        ComponentReference reference = factory.createComponentReference();
        reference.setName("target");
        type.getReferenceMembers().put("target",
                                       new JavaElementImpl(SourceImpl.class.getMethod("setTarget", Target.class), 0));
        type.getReferences().add(reference);

        ComponentService serviceDefinition = ModelHelper.createService(factory, javaFactory, Source.class);
        serviceDefinition.setName("Source");
        type.getServices().add(serviceDefinition);
        type.setConstructor(new JavaConstructorImpl<SourceImpl>(constructor));
        type.setJavaClass(SourceImpl.class);
        definition.setImplementation(type);
        definition.setName("component");
    }

}
