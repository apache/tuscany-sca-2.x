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

import java.net.URI;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.ComponentProperty;
import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.implementation.java.impl.DefaultJavaImplementationFactory;
import org.apache.tuscany.implementation.java.impl.JavaConstructorImpl;
import org.apache.tuscany.implementation.java.impl.JavaElementImpl;
import org.apache.tuscany.implementation.java.impl.JavaImplementationImpl;
import org.apache.tuscany.implementation.java.impl.JavaScopeImpl;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.easymock.EasyMock;

/**
 * Verifies that the java component builder handles configured properties
 * correctly
 * 
 * @version $Rev$ $Date$
 */
public class JavaBuilderPropertyTestCaseFIXME extends TestCase {
    private DeploymentContext deploymentContext;
    private Component parent;
    private ScopeRegistry registry;
    private AssemblyFactory factory = new DefaultAssemblyFactory();

    @SuppressWarnings("unchecked")
    public void testPropertyHandling() throws Exception {
        JavaComponentBuilder builder = new JavaComponentBuilder();
        JavaImplementationFactory javaImplementationFactory = new DefaultJavaImplementationFactory(factory);
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Property property = factory.createProperty();
        property.setName("test");
        // property.setDefaultValueFactory(new
        // SingletonObjectFactory<String>("foo"));
        JavaElementImpl element = new JavaElementImpl(JavaBuilderPropertyTestCaseFIXME.Foo.class.getMethod("setTest", String.class),
                                              0);
        type.getPropertyMembers().put("test", element);
        type.getProperties().add(property);
        type.setConstructor(new JavaConstructorImpl<Foo>(Foo.class.getConstructor((Class[])null)));
        type.setJavaScope(JavaScopeImpl.STATELESS);
        type.setJavaClass(Foo.class);
        org.apache.tuscany.assembly.Component definition = factory.createComponent();
        definition.setImplementation(type);
        definition.setName("component");
        ComponentProperty propertyValue = factory.createComponentProperty();
        propertyValue.setName(property.getName());
        definition.getProperties().add(propertyValue);
        AtomicComponent component = builder.build(definition, deploymentContext);
        JavaBuilderPropertyTestCaseFIXME.Foo foo = (JavaBuilderPropertyTestCaseFIXME.Foo)component.createObjectFactory().getInstance();
        assertEquals("foo", foo.getTest());
    }

    public void testIntPropertyHandling() throws Exception {
        JavaComponentBuilder builder = new JavaComponentBuilder();
        JavaImplementationFactory javaImplementationFactory = new DefaultJavaImplementationFactory(factory);
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Property property = factory.createProperty();
        property.setName("test");
        // property.setDefaultValueFactory(new
        // SingletonObjectFactory<Integer>(1));
        JavaElementImpl element = new JavaElementImpl(JavaBuilderPropertyTestCaseFIXME.Foo.class.getMethod("setTest", String.class),
                                              0);
        type.getPropertyMembers().put("test", element);
        type.getProperties().add(property);
        type.setConstructor(new JavaConstructorImpl<FooInt>(FooInt.class.getConstructor((Class[])null)));
        type.setJavaScope(JavaScopeImpl.STATELESS);
        type.setJavaClass(Foo.class);
        org.apache.tuscany.assembly.Component definition = factory.createComponent();
        definition.setImplementation(type);
        definition.setName("component");
        // ObjectFactory<Integer> defaultValueFactory =
        // property.getDefaultValueFactory();
        ComponentProperty propertyValue = factory.createComponentProperty();
        propertyValue.setName(property.getName());
        definition.getProperties().add(propertyValue);
        AtomicComponent component = builder.build(definition, deploymentContext);
        FooInt foo = (FooInt)component.createObjectFactory().getInstance();
        assertEquals(1, foo.getTest());
    }

    protected void setUp() throws Exception {
        super.setUp();
        deploymentContext = EasyMock.createMock(DeploymentContext.class);
        EasyMock.expect(deploymentContext.getGroupId()).andStubReturn(URI.create("composite"));
        EasyMock.replay(deploymentContext);
        parent = EasyMock.createNiceMock(Component.class);
        ScopeContainer mockContainer = EasyMock.createNiceMock(ScopeContainer.class);
        EasyMock.replay(mockContainer);
        registry = EasyMock.createMock(ScopeRegistry.class);
        EasyMock.expect(registry.getScopeContainer(EasyMock.isA(org.apache.tuscany.spi.Scope.class)))
            .andReturn(mockContainer);
        EasyMock.replay(registry);
    }

    private static class Foo {
        private String test;

        public Foo() {
        }

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }
    }

    private static class FooInt {
        private int test;

        public FooInt() {
        }

        public int getTest() {
            return test;
        }

        public void setTest(int test) {
            this.test = test;
        }
    }
}
