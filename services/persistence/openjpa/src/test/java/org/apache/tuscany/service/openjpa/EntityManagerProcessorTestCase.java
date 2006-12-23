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
package org.apache.tuscany.service.openjpa;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessor;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorService;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.wire.InboundWire;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * @version $Rev$ $Date$
 */
public class EntityManagerProcessorTestCase extends TestCase {

    private ImplementationProcessor processor;
    private CompositeComponent parent;

    public void testVisitMethod() throws Exception {
        Method method = Foo.class.getMethod("setBar", EntityManager.class);
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitMethod(parent, method, type, null);
        JavaMappedProperty<?> prop = type.getProperties().get("bar");
        assertEquals(EntityManagerObjectFactory.class, prop.getDefaultValueFactory().getClass());
    }

    public void testVisitBadMethod() throws Exception {
        Method method = Foo.class.getMethod("setBadBar", String.class);
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        try {
            processor.visitMethod(parent, method, type, null);
            fail();
        } catch (InvalidInjectionSite e) {
            // expected
        }
    }

    public void testVisitField() throws Exception {
        Field field = Foo.class.getDeclaredField("bar");
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitField(parent, field, type, null);
        JavaMappedProperty<?> prop = type.getProperties().get("bar");
        assertEquals(EntityManagerObjectFactory.class, prop.getDefaultValueFactory().getClass());
    }

    public void testBadVisitField() throws Exception {
        Field field = Foo.class.getDeclaredField("badField");
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        try {
            processor.visitField(null, field, type, null);
            fail();
        } catch (InvalidInjectionSite e) {
            // expected
        }
    }

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        EntityManagerFactory emf = EasyMock.createMock(EntityManagerFactory.class);
        EasyMock.replay(emf);

        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getTargetService()).andReturn(emf);
        EasyMock.replay(wire);

        parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.resolveSystemAutowire(EntityManagerFactory.class)).andReturn(wire).atLeastOnce();
        EasyMock.replay(parent);
        ImplementationProcessorService service = EasyMock.createMock(ImplementationProcessorService.class);
        service.addName(EasyMock.isA(List.class), EasyMock.eq(0), EasyMock.isA(String.class));
        EasyMock.expectLastCall().andStubAnswer(new IAnswer() {
            @SuppressWarnings({"unchecked"})
            public Object answer() throws Throwable {
                ((List<Object>) EasyMock.getCurrentArguments()[0]).add(EasyMock.getCurrentArguments()[2]);
                return null;
            }
        });
        EasyMock.replay(service);
        processor = new EntityManagerProcessor(service);
    }

    private static class Foo {


        @PersistenceContext
        protected EntityManager bar;

        @PersistenceContext
        protected String badField;

        @PersistenceContext
        public void setBar(EntityManager d) {
        }

        @PersistenceContext
        public void setBadBar(String d) {
        }

    }
}
