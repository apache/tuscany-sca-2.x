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
package org.apache.tuscany.core.implementation.system.builder;

import java.lang.reflect.Field;
import java.net.URI;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.host.ResourceHost;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.Resource;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.system.model.SystemImplementation;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class SystemComponentBuilderResourceTestCase extends TestCase {

    @SuppressWarnings("unchecked")
    public void testResourceInjection() throws Exception {
        ScopeContainer container = EasyMock.createNiceMock(ScopeContainer.class);
        DeploymentContext ctx = EasyMock.createNiceMock(DeploymentContext.class);
        URI groupId = URI.create("composite");
        EasyMock.expect(ctx.getGroupId()).andStubReturn(groupId);
        EasyMock.replay(ctx);
        ScopeRegistry registry = EasyMock.createMock(ScopeRegistry.class);
        EasyMock.expect(registry.getScopeContainer(Scope.STATELESS)).andReturn(container);
        EasyMock.replay(registry);
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.expect(host.resolveResource(EasyMock.eq(String.class))).andReturn("result");
        EasyMock.replay(host);
        SystemComponentBuilder builder = new SystemComponentBuilder();
        builder.setScopeRegistry(registry);
        builder.setHost(host);
        ConstructorDefinition<Foo> ctorDef = new ConstructorDefinition<SystemComponentBuilderResourceTestCase.Foo>(
            SystemComponentBuilderResourceTestCase.Foo.class.getConstructor());
        PojoComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> type =
            new PojoComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        Field member = Foo.class.getDeclaredField("resource");
        Resource<String> resource = new Resource<String>("resource", String.class, member);
        type.add(resource);
        type.setImplementationScope(Scope.STATELESS);
        type.setConstructorDefinition(ctorDef);
        SystemImplementation impl = new SystemImplementation();
        impl.setImplementationClass(SystemComponentBuilderResourceTestCase.Foo.class);
        impl.setComponentType(type);
        ComponentDefinition<SystemImplementation> definition =
            new ComponentDefinition<SystemImplementation>(URI.create("foo"), impl);

        Wire wire = EasyMock.createMock(Wire.class);
        EasyMock.expect(wire.getTargetInstance()).andReturn("result");
        EasyMock.replay(wire);

        AtomicComponent component = builder.build(definition, ctx);
        SystemComponentBuilderResourceTestCase.Foo foo =
            (SystemComponentBuilderResourceTestCase.Foo) component.createInstance();
        assertEquals("result", foo.resource);
    }

    private static class Foo {

        protected String resource;

        public Foo() {
        }

    }
}
