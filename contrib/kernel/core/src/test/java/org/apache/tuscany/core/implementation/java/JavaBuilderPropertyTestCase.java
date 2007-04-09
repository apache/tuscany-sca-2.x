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

import java.net.URI;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.PropertyValue;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceDefinition;

import junit.framework.TestCase;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.easymock.EasyMock;

/**
 * Verifies that the java component builder handles configured properties correctly
 *
 * @version $Rev$ $Date$
 */
public class JavaBuilderPropertyTestCase extends TestCase {
    private DeploymentContext deploymentContext;
    private Component parent;
    private ScopeRegistry registry;

    @SuppressWarnings("unchecked")
    public void testPropertyHandling() throws Exception {
        JavaComponentBuilder builder = new JavaComponentBuilder();
        builder.setScopeRegistry(registry);
        PojoComponentType<ServiceDefinition, ReferenceDefinition, JavaMappedProperty<?>> type =
            new PojoComponentType<ServiceDefinition, ReferenceDefinition, JavaMappedProperty<?>>();
        JavaMappedProperty<String> property = new JavaMappedProperty<String>();
        property.setName("test");
        property.setDefaultValueFactory(new SingletonObjectFactory<String>("foo"));
        property.setMember(JavaBuilderPropertyTestCase.Foo.class.getMethod("setTest", String.class));
        type.add(property);
        type.setConstructorDefinition(new ConstructorDefinition<Foo>(Foo.class.getConstructor((Class[]) null)));
        type.setImplementationScope(Scope.STATELESS);
        JavaImplementation impl = new JavaImplementation(Foo.class, type);
        ComponentDefinition<JavaImplementation> definition = new ComponentDefinition<JavaImplementation>(impl);
        definition.setUri(URI.create("component"));
        PropertyValue propertyValue = new PropertyValue(property.getName(), property.getDefaultValueFactory());
        definition.getPropertyValues().put(property.getName(), propertyValue);
        AtomicComponent component = builder.build(definition, deploymentContext);
        JavaBuilderPropertyTestCase.Foo foo = (JavaBuilderPropertyTestCase.Foo) component.createInstance();
        assertEquals("foo", foo.getTest());
    }

    public void testIntPropertyHandling() throws Exception {
        JavaComponentBuilder builder = new JavaComponentBuilder();
        builder.setScopeRegistry(registry);
        PojoComponentType<ServiceDefinition, ReferenceDefinition, JavaMappedProperty<?>> type =
            new PojoComponentType<ServiceDefinition, ReferenceDefinition, JavaMappedProperty<?>>();
        JavaMappedProperty<Integer> property = new JavaMappedProperty<Integer>();
        property.setName("test");
        property.setDefaultValueFactory(new SingletonObjectFactory<Integer>(1));
        property.setMember(JavaBuilderPropertyTestCase.FooInt.class.getMethod("setTest", Integer.TYPE));
        type.add(property);
        type.setConstructorDefinition(new ConstructorDefinition<FooInt>(FooInt.class.getConstructor((Class[]) null)));
        type.setImplementationScope(Scope.STATELESS);
        JavaImplementation impl = new JavaImplementation(Foo.class, type);
        ComponentDefinition<JavaImplementation> definition = new ComponentDefinition<JavaImplementation>(impl);
        definition.setUri(URI.create("component"));
        ObjectFactory<Integer> defaultValueFactory = property.getDefaultValueFactory();
        PropertyValue<Integer> propertyValue = new PropertyValue<Integer>(property.getName(), defaultValueFactory);
        definition.getPropertyValues().put(property.getName(), propertyValue);
        AtomicComponent component = builder.build(definition, deploymentContext);
        FooInt foo = (FooInt) component.createInstance();
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
        EasyMock.expect(registry.getScopeContainer(EasyMock.isA(Scope.class))).andReturn(mockContainer);
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
