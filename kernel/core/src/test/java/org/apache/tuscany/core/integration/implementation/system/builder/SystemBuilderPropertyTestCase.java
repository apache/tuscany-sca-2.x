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
package org.apache.tuscany.core.integration.implementation.system.builder;

import java.net.URI;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.system.builder.SystemComponentBuilder;
import org.apache.tuscany.core.implementation.system.model.SystemImplementation;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.easymock.EasyMock;

/**
 * Verifies that the system builder handles configured properties correctly
 *
 * @version $Rev$ $Date$
 */
public class SystemBuilderPropertyTestCase extends TestCase {

    DeploymentContext deploymentContext;
    Component parent;

    @SuppressWarnings("unchecked")
    public void testPropertyHandling() throws Exception {
        SystemComponentBuilder builder = new SystemComponentBuilder();
        PojoComponentType<ServiceDefinition, ReferenceDefinition, JavaMappedProperty<?>> type =
            new PojoComponentType<ServiceDefinition, ReferenceDefinition, JavaMappedProperty<?>>();
        type.setConstructorDefinition(new ConstructorDefinition<Foo>(Foo.class.getConstructor((Class[]) null)));
        JavaMappedProperty<String> property = new JavaMappedProperty<String>();
        property.setName("test");
        property.setDefaultValueFactory(new SingletonObjectFactory<String>("foo"));
        property.setMember(Foo.class.getMethod("setTest", String.class));
        type.add(property);
        SystemImplementation impl = new SystemImplementation();
        impl.setComponentType(type);
        impl.setImplementationClass(Foo.class);
        ComponentDefinition<SystemImplementation> definition = new ComponentDefinition<SystemImplementation>(impl);
        definition.setUri(URI.create("component"));
        AtomicComponent component = builder.build(definition, deploymentContext);
        Foo foo = (Foo) component.createInstance();
        assertEquals("foo", foo.getTest());
    }

    protected void setUp() throws Exception {
        super.setUp();
        deploymentContext = EasyMock.createMock(DeploymentContext.class);
        EasyMock.expect(deploymentContext.getGroupId()).andStubReturn(URI.create("composite"));
        EasyMock.replay(deploymentContext);
        parent = EasyMock.createNiceMock(Component.class);
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
}
