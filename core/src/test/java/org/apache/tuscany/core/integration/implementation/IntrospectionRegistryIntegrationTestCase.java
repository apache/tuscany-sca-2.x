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
package org.apache.tuscany.core.integration.implementation;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import static org.apache.tuscany.spi.model.Scope.MODULE;

import junit.framework.TestCase;
import org.apache.tuscany.core.idl.java.InterfaceJavaIntrospectorImpl;
import org.apache.tuscany.core.implementation.IntrospectionRegistryImpl;
import org.apache.tuscany.core.implementation.processor.DestroyProcessor;
import org.apache.tuscany.core.implementation.processor.ImplementationProcessorService;
import org.apache.tuscany.core.implementation.processor.ImplementationProcessorServiceImpl;
import org.apache.tuscany.core.implementation.processor.InitProcessor;
import org.apache.tuscany.core.implementation.processor.PropertyProcessor;
import org.apache.tuscany.core.implementation.processor.ReferenceProcessor;
import org.apache.tuscany.core.implementation.processor.ScopeProcessor;
import org.apache.tuscany.core.monitor.NullMonitorFactory;

/**
 * Sanity check of the <code>IntegrationRegistry</code> to verify operation with processors
 *
 * @version $Rev$ $Date$
 */
public class IntrospectionRegistryIntegrationTestCase extends TestCase {

    private IntrospectionRegistryImpl registry;

    public void testSimpleComponentTypeParsing() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        registry.introspect(null, Foo.class, type, null);
        assertEquals(Foo.class.getMethod("init"), type.getInitMethod());
        assertEquals(Foo.class.getMethod("destroy"), type.getDestroyMethod());
        assertEquals(MODULE, type.getImplementationScope());
        assertEquals(Foo.class.getMethod("setBar", String.class), type.getProperties().get("bar").getMember());
        assertEquals(Foo.class.getMethod("setTarget", Foo.class), type.getReferences().get("target").getMember());
    }

    protected void setUp() throws Exception {
        super.setUp();
        registry = new IntrospectionRegistryImpl();
        registry.setMonitor(new NullMonitorFactory().getMonitor(IntrospectionRegistryImpl.Monitor.class));
        registry.registerProcessor(new DestroyProcessor());
        registry.registerProcessor(new InitProcessor());
        registry.registerProcessor(new ScopeProcessor());
        InterfaceJavaIntrospectorImpl introspector = new InterfaceJavaIntrospectorImpl();
        ImplementationProcessorService service = new ImplementationProcessorServiceImpl(introspector);
        registry.registerProcessor(new PropertyProcessor(service));
        registry.registerProcessor(new ReferenceProcessor(introspector));
    }

    @Scope("MODULE")
    private static class Foo {
        protected Foo target;
        protected String bar;

        private boolean initialized;
        private boolean destroyed;


        @Init
        public void init() {
            if (initialized) {
                fail();
            }
            initialized = true;
        }

        @Destroy
        public void destroy() {
            if (destroyed) {
                fail();
            }
            destroyed = true;
        }

        public Foo getTarget() {
            return target;
        }

        @Reference
        public void setTarget(Foo target) {
            this.target = target;
        }

        public String getBar() {
            return bar;
        }

        @Property
        public void setBar(String bar) {
            this.bar = bar;
        }

    }
}
