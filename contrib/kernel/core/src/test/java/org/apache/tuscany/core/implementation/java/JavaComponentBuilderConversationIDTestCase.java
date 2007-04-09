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

import java.lang.reflect.Field;
import java.net.URI;

import org.osoa.sca.annotations.ConversationID;

import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.deployer.DeploymentContext;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.easymock.EasyMock;

/**
 * @version $Rev: 473859 $ $Date: 2006-11-11 22:31:55 -0500 (Sat, 11 Nov 2006) $
 */
public class JavaComponentBuilderConversationIDTestCase extends TestCase {

    @SuppressWarnings("unchecked")
    public void testResourceInjection() throws Exception {
        DeploymentContext ctx = EasyMock.createNiceMock(DeploymentContext.class);
        EasyMock.expect(ctx.getGroupId()).andStubReturn(URI.create("composite"));
        EasyMock.replay(ctx);
        ScopeContainer container = EasyMock.createNiceMock(ScopeContainer.class);
        ScopeRegistry registry = EasyMock.createMock(ScopeRegistry.class);
        EasyMock.expect(registry.getScopeContainer(Scope.STATELESS)).andReturn(container);
        EasyMock.replay(registry);
        JavaComponentBuilder builder = new JavaComponentBuilder();
        builder.setScopeRegistry(registry);
        WorkContext workContext = new WorkContextImpl();
        workContext.setIdentifier(Scope.CONVERSATION, "convID");
        builder.setWorkContext(workContext);

        ConstructorDefinition<Foo> ctorDef = new ConstructorDefinition<Foo>(Foo.class.getConstructor());
        PojoComponentType type = new PojoComponentType();
        Field field = Foo.class.getDeclaredField("conversationID");
        type.setConversationIDMember(field);
        type.setImplementationScope(Scope.STATELESS);
        type.setConstructorDefinition(ctorDef);

        JavaImplementation impl = new JavaImplementation(Foo.class, type);
        URI uri = URI.create("foo");
        ComponentDefinition<JavaImplementation> definition = new ComponentDefinition<JavaImplementation>(uri, impl);
        JavaAtomicComponent component = (JavaAtomicComponent) builder.build(definition, ctx);
        Foo foo = (Foo) component.createInstance();
        assertEquals("convID", foo.conversationID);
    }

    private static class Foo {

        @ConversationID
        protected String conversationID;

        public Foo() {
        }

    }
}


