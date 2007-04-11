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
package org.apache.tuscany.implementation.java.introspect.impl;

import java.lang.reflect.Constructor;

import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.implementation.java.impl.ConstructorDefinition;
import org.apache.tuscany.implementation.java.impl.JavaElement;
import org.apache.tuscany.implementation.java.impl.JavaImplementationDefinition;
import org.apache.tuscany.implementation.java.introspect.ProcessingException;
import org.apache.tuscany.interfacedef.java.introspect.DefaultJavaInterfaceIntrospector;

/**
 * Verifies constructors that have extensible annotation types, i.e. that have
 * parameters marked by annotations which are themselves processed by some other
 * implementation processor
 * 
 * @version $Rev$ $Date$
 */
public class HeutisticExtensibleConstructorTestCase extends AbstractProcessorTest {

    private org.apache.tuscany.implementation.java.introspect.impl.HeuristicPojoProcessor processor;

    public HeutisticExtensibleConstructorTestCase() {
        DefaultJavaInterfaceIntrospector introspector = new DefaultJavaInterfaceIntrospector();
        processor = new org.apache.tuscany.implementation.java.introspect.impl.HeuristicPojoProcessor();
        processor.setInterfaceVisitorExtensionPoint(introspector);
    }

    private <T> void visitEnd(Class<T> clazz, JavaImplementationDefinition type) throws ProcessingException {
        for (Constructor<T> constructor : clazz.getConstructors()) {
            visitConstructor(constructor, type);
        }
        processor.visitEnd(clazz, type);
    }

    /**
     * Verifies heuristic processing can be called priot to an extension
     * annotation processors being called.
     */
    public void testBarAnnotationProcessedFirst() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        Constructor<Foo> ctor = Foo.class.getConstructor(String.class, String.class);
        ConstructorDefinition<Foo> definition = new ConstructorDefinition<Foo>(ctor);
        type.setConstructorDefinition(definition);
        Property property = factory.createProperty();
        property.setName("myBar");
        definition.getParameters()[0].setName("myBar");
        type.getProperties().add(property);
        visitEnd(Foo.class, type);
        assertEquals(2, type.getProperties().size());
    }

    /**
     * Verifies heuristic processing can be called before an extension
     * annotation processors is called. <p/> For example, given:
     * 
     * <pre>
     *  Foo(@Bar String prop, @org.osoa.sca.annotations.Property(name = &quot;foo&quot;) String prop2)
     * </pre>
     * 
     * <p/> Heuristic evaluation of
     * @Property can occur prior to another implementation processor evaluating
     * @Bar
     * @throws Exception
     */
    public void testBarAnnotationProcessedLast() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        visitEnd(Foo.class, type);

        // now simulate process the bar impl
        ConstructorDefinition<?> definition = type.getConstructorDefinition();
        definition.getParameters()[0].setName("myBar");
        Property property = factory.createProperty();
        property.setName("myBar");
        type.getProperties().add(property);

        assertEquals(2, type.getProperties().size());
        assertEquals("foo", definition.getParameters()[1].getName());
    }

    /**
     * Verifies heuristic processing can be called before an extension
     * annotation processors is called with the extension parameter in a middle
     * position. Specifically, verifies that the heuristic processor updates
     * injection names and preserves their ordering.
     */
    public void testBarAnnotationProcessedFirstInMiddle() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        Constructor<Foo2> ctor = Foo2.class.getConstructor(String.class, String.class, String.class);
        ConstructorDefinition<Foo2> definition = new ConstructorDefinition<Foo2>(ctor);
        type.setConstructorDefinition(definition);
        // insert placeholder for first param, which would be done by a
        // processor
        definition.getParameters()[0].setName("");
        Property property = factory.createProperty();
        // Hack to add a property member
        JavaElement element = new JavaElement("myBar", String.class, null);
        type.getPropertyMembers().put("myBar", element);
        property.setName("myBar");
        definition.getParameters()[1].setName("myBar");
        type.getProperties().add(property);
        visitEnd(Foo2.class, type);
        assertEquals("baz", definition.getParameters()[0].getName());
        assertEquals(2, type.getProperties().size());
        assertEquals(1, type.getReferences().size());
    }

    public @interface Bar {

    }

    public static class Foo {
        public Foo(@Bar
        String prop, @org.osoa.sca.annotations.Property(name = "foo")
        String prop2) {
        }
    }

    public static class Foo2 {
        public Foo2(@org.osoa.sca.annotations.Reference(name = "baz")
        String prop1, @Bar
        String prop2, @org.osoa.sca.annotations.Property(name = "foo")
        String prop3) {
        }
    }

}
