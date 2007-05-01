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

import java.lang.reflect.Field;
import java.net.URI;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.implementation.java.impl.DefaultJavaImplementationFactory;
import org.apache.tuscany.implementation.java.impl.JavaConstructorImpl;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.easymock.EasyMock;
import org.osoa.sca.annotations.ConversationID;

/**
 * @version $Rev$ $Date$
 */
public class JavaComponentBuilderConversationIDTestCaseFIXME extends TestCase {

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
        WorkContext workContext = new WorkContextImpl();
        workContext.setIdentifier(Scope.CONVERSATION, "convID");
        builder.setWorkContext(workContext);
        
        AssemblyFactory assemblyFactory = new DefaultAssemblyFactory();

        JavaConstructorImpl<Foo> ctorDef = new JavaConstructorImpl<Foo>(Foo.class.getConstructor());
        JavaImplementationFactory javaImplementationFactory = new DefaultJavaImplementationFactory(assemblyFactory);
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Field field = Foo.class.getDeclaredField("conversationID");
        type.setConversationIDMember(field);
        type.setJavaScope(org.apache.tuscany.implementation.java.impl.JavaScopeImpl.STATELESS);
        type.setConstructor(ctorDef);
        type.setJavaClass(Foo.class);

        Component definition = assemblyFactory.createComponent();
        definition.setName("foo");
        definition.setImplementation(type);
        JavaAtomicComponent component = (JavaAtomicComponent)builder.build(definition, ctx);
        Foo foo = (Foo)component.createInstance();
        assertEquals("convID", foo.conversationID);
    }

    private static class Foo {

        @ConversationID
        protected String conversationID;

        public Foo() {
        }

    }
}
