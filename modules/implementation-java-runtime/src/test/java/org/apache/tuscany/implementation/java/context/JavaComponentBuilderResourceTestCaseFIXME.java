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

import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.implementation.java.context.JavaAtomicComponent;
import org.apache.tuscany.implementation.java.context.JavaComponentBuilder;
import org.apache.tuscany.implementation.java.impl.ConstructorDefinition;
import org.apache.tuscany.implementation.java.impl.JavaElement;
import org.apache.tuscany.implementation.java.impl.JavaImplementationDefinition;
import org.apache.tuscany.implementation.java.impl.Resource;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.host.ResourceHost;
import org.apache.tuscany.spi.wire.Wire;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class JavaComponentBuilderResourceTestCaseFIXME extends TestCase {

    @SuppressWarnings("unchecked")
    public void testResourceInjection() throws Exception {
        DeploymentContext ctx = EasyMock.createNiceMock(DeploymentContext.class);
        EasyMock.expect(ctx.getGroupId()).andStubReturn(URI.create("composite"));
        EasyMock.replay(ctx);
        ScopeContainer container = EasyMock.createNiceMock(ScopeContainer.class);
        ScopeRegistry registry = EasyMock.createMock(ScopeRegistry.class);
        EasyMock.expect(registry.getScopeContainer(Scope.STATELESS)).andReturn(container);
        EasyMock.replay(registry);
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.expect(host.resolveResource(EasyMock.eq(String.class))).andReturn("result");
        EasyMock.replay(host);
        JavaComponentBuilder builder = new JavaComponentBuilder();
        builder.setHost(host);
        ConstructorDefinition<Foo> ctorDef = new ConstructorDefinition<Foo>(Foo.class.getConstructor());
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        Resource resource = new Resource(new JavaElement(Foo.class.getDeclaredField("resource")));
        type.getResources().put(resource.getName(), resource);
        type.setScope(org.apache.tuscany.implementation.java.impl.Scope.STATELESS);
        type.setConstructorDefinition(ctorDef);
        type.setJavaClass(Foo.class);
        URI uri = URI.create("foo");
        Component definition = new DefaultAssemblyFactory().createComponent();
        definition.setImplementation(type);
        definition.setName("foo");
        Wire resourceWire = EasyMock.createMock(Wire.class);
        EasyMock.expect(resourceWire.getTargetInstance()).andReturn("result");
        EasyMock.replay(resourceWire);

        JavaAtomicComponent component = (JavaAtomicComponent) builder.build(definition, ctx);
        Foo foo = (Foo) component.createInstance();
        assertEquals("result", foo.resource);
    }

    private static class Foo {

        protected String resource;

        public Foo() {
        }

    }
}


