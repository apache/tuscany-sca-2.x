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
package org.apache.tuscany.container.spring.impl;

import java.net.URI;
import java.net.URL;

import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

import junit.framework.TestCase;
import org.apache.tuscany.container.spring.model.SpringComponentType;
import org.apache.tuscany.container.spring.model.SpringImplementation;
import org.easymock.EasyMock;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SpringCompositeBuilderTestCase extends TestCase {

    /**
     * Verifies that the builder calls back into the registry to load services and references when no
     * <code>sca:service</code> tag is specified in the Spring application.xml
     */
    public void testImplicitServiceWiring() throws Exception {
        Service service = EasyMock.createMock(Service.class);
        // Configure the mock builder registry
        BuilderRegistry registry = EasyMock.createMock(BuilderRegistry.class);
        EasyMock.expect(registry.build(
            EasyMock.isA(ServiceDefinition.class),
            EasyMock.isA(DeploymentContext.class))).andStubReturn(service);
        EasyMock.replay(registry);
        SpringCompositeBuilder builder = new SpringCompositeBuilder();
        builder.setBuilderRegistry(registry);
        ComponentDefinition<SpringImplementation> definition = createDefinitionWithService();
        Component component = builder.build(definition, null);
        assertNotNull(component);
        EasyMock.verify(registry);
    }

    /**
     * Verifies that the builder calls back into the registry to load services and references when no
     * <code>sca:reference</code> tag is specified in the Spring application.xml
     */
    public void testImplicitReferenceWiring() throws Exception {
        Service service = EasyMock.createMock(Service.class);
        // Configure the mock builder registry
        BuilderRegistry registry = EasyMock.createMock(BuilderRegistry.class);
        EasyMock.expect(registry.build(
            EasyMock.isA(ServiceDefinition.class),
            EasyMock.isA(DeploymentContext.class))).andStubReturn(service);
        EasyMock.replay(registry);
        SpringCompositeBuilder builder = new SpringCompositeBuilder();
        builder.setBuilderRegistry(registry);
        ComponentDefinition<SpringImplementation> definition = createDefinitionWithReference();
        Component component = builder.build(definition, null);
        assertNotNull(component);
        EasyMock.verify(registry);
    }

    protected ComponentDefinition<SpringImplementation> createDefinitionWithService() throws Exception {
        super.setUp();
        ClassLoader loader = getClass().getClassLoader();
        URL url = loader.getResource("META-INF/sca/testServiceContext.xml");
        Resource resource = new UrlResource(url);
        SpringComponentType<Property<?>> type = createComponentTypeWithService();
        SpringImplementation impl = new SpringImplementation(loader);
        impl.setComponentType(type);
        URI uri = URI.create("composite");
        ComponentDefinition<SpringImplementation> definition = new ComponentDefinition<SpringImplementation>(uri, impl);
        impl.setApplicationResource(resource);
        return definition;
    }

    protected ComponentDefinition<SpringImplementation> createDefinitionWithReference() throws Exception {
        super.setUp();
        ClassLoader loader = getClass().getClassLoader();
        URL url = loader.getResource("META-INF/sca/testServiceContext.xml");
        Resource resource = new UrlResource(url);
        SpringComponentType<Property<?>> type = createComponentTypeWithReference();
        SpringImplementation impl = new SpringImplementation(loader);
        impl.setComponentType(type);
        URI uri = URI.create("composite");
        ComponentDefinition<SpringImplementation> definition = new ComponentDefinition<SpringImplementation>(uri, impl);
        impl.setApplicationResource(resource);
        return definition;
    }

    private SpringComponentType<Property<?>> createComponentTypeWithService() {
        SpringComponentType<Property<?>> componentType = new SpringComponentType<Property<?>>();
        ServiceDefinition serviceDefinition = new ServiceDefinition();
        serviceDefinition.setUri(URI.create("fooService"));
        serviceDefinition.setTarget(URI.create("testBean"));
        componentType.add(serviceDefinition);
        return componentType;
    }

    private SpringComponentType<Property<?>> createComponentTypeWithReference() {
        SpringComponentType<Property<?>> componentType = new SpringComponentType<Property<?>>();
        ReferenceDefinition referenceDefinition = new ReferenceDefinition();
        referenceDefinition.setUri(URI.create("fooReference"));
        componentType.add(referenceDefinition);
        return componentType;
    }


}
