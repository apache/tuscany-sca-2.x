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
package org.apache.tuscany.core.implementation.processor;

import java.lang.reflect.Constructor;

import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;

/**
 * Verifies constructors that have extensible annotation types, i.e. that have
 * parameters marked by annotations which are themselves processed by some other
 * implementation processor
 * 
 * @version $Rev$ $Date$
 */
public class HeutisticExtensibleConstructorTestCase extends AbstractProcessorTest {

    private HeuristicPojoProcessor processor;

    public HeutisticExtensibleConstructorTestCase() {
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        processor = new HeuristicPojoProcessor();
        processor.setInterfaceProcessorRegistry(registry);
    }

    private <T> void visitEnd(Class<T> clazz,
                              PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                              DeploymentContext context) throws ProcessingException {
        for (Constructor<T> constructor : clazz.getConstructors()) {
            visitConstructor(constructor, type, context);
        }
        processor.visitEnd(clazz, type, context);
    }
    
    /**
     * Verifies heuristic processing can be called priot to an extension
     * annotation processors being called.
     */
    public void testBarAnnotationProcessedFirst() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type 
            = new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<Foo> ctor = Foo.class.getConstructor(String.class, String.class);
        ConstructorDefinition<Foo> definition = new ConstructorDefinition<Foo>(ctor);
        type.setConstructorDefinition(definition);
        JavaMappedProperty property = new JavaMappedProperty();
        property.setName("myBar");
        definition.getParameters()[0].setName("myBar");
        type.getProperties().put("myBar", property);
        visitEnd(Foo.class, type, null);
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
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type = new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        visitEnd(Foo.class, type, null);

        // now simulate process the bar impl
        ConstructorDefinition<?> definition = type.getConstructorDefinition();
        definition.getParameters()[0].setName("myBar");
        type.getProperties().put("mybar", new JavaMappedProperty<String>());

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
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type = new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<Foo2> ctor = Foo2.class.getConstructor(String.class, String.class, String.class);
        ConstructorDefinition<Foo2> definition = new ConstructorDefinition<Foo2>(ctor);
        type.setConstructorDefinition(definition);
        // insert placeholder for first param, which would be done by a
        // processor
        definition.getParameters()[0].setName("");
        JavaMappedProperty property = new JavaMappedProperty();
        property.setJavaType(String.class);
        property.setName("myBar");
        definition.getParameters()[1].setName("myBar");
        type.getProperties().put("myBar", property);
        visitEnd(Foo2.class, type, null);
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

