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

import java.lang.reflect.Method;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.PropertyValue;
import org.apache.tuscany.spi.model.Scope;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.event.CompositeStart;
import org.apache.tuscany.core.component.event.CompositeStop;
import org.apache.tuscany.core.component.scope.CompositeScopeContainer;
import org.apache.tuscany.core.implementation.system.model.SystemImplementation;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class SystemComponentBuilderTestCase extends TestCase {

    CompositeComponent parent;
    DeploymentContext deploymentContext;
    SystemComponentBuilder builder = new SystemComponentBuilder();
    CompositeScopeContainer container;

    /**
     * Verifies lifecycle callbacks are made
     */
    public void testLifecycleBuild() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        type.setInitLevel(50);
        Method initMethod = FooImpl.class.getMethod("init");
        initMethod.setAccessible(true);
        type.setInitMethod(initMethod);
        Method destroyMethod = FooImpl.class.getMethod("destroy");
        destroyMethod.setAccessible(true);
        type.setDestroyMethod(destroyMethod);
        type.setImplementationScope(Scope.COMPOSITE);
        ConstructorDefinition<FooImpl> ctorDef = new ConstructorDefinition<FooImpl>(FooImpl.class.getConstructor());
        type.setConstructorDefinition(ctorDef);
        SystemImplementation impl = new SystemImplementation();
        impl.setComponentType(type);
        impl.setImplementationClass(FooImpl.class);
        ComponentDefinition<SystemImplementation> definition = new ComponentDefinition<SystemImplementation>(impl);
        AtomicComponent component = builder.build(parent, definition, deploymentContext);
        component.setScopeContainer(container);
        component.start();
        container.onEvent(new CompositeStart(this, null));
        FooImpl foo = (FooImpl) component.getTargetInstance();
        assertTrue(foo.initialized);
        container.onEvent(new CompositeStop(this, null));
        assertTrue(foo.destroyed);
    }

    /**
     * Verifies properties are built properly
     */
    public void testPropertyBuild() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        type.setInitLevel(50);
        Method initMethod = FooImpl.class.getMethod("init");
        initMethod.setAccessible(true);
        type.setInitMethod(initMethod);
        Method destroyMethod = FooImpl.class.getMethod("destroy");
        destroyMethod.setAccessible(true);
        type.setDestroyMethod(destroyMethod);
        type.setImplementationScope(Scope.COMPOSITE);
        JavaMappedProperty mappedProp = new JavaMappedProperty();
        mappedProp.setName("prop");
        Method propMethod = FooImpl.class.getMethod("setProp", String.class);
        propMethod.setAccessible(true);
        mappedProp.setMember(propMethod);
        type.add(mappedProp);
        ConstructorDefinition<FooImpl> ctorDef = new ConstructorDefinition<FooImpl>(FooImpl.class.getConstructor());
        type.setConstructorDefinition(ctorDef);
        SystemImplementation impl = new SystemImplementation();
        impl.setComponentType(type);
        impl.setImplementationClass(FooImpl.class);
        ComponentDefinition<SystemImplementation> definition = new ComponentDefinition<SystemImplementation>(impl);
        PropertyValue<String> propVal = new PropertyValue<String>();
        propVal.setName("prop");
        propVal.setValueFactory(new SingletonObjectFactory<String>("value"));
        definition.add(propVal);
        AtomicComponent component = builder.build(parent, definition, deploymentContext);
        component.setScopeContainer(container);
        component.start();
        FooImpl foo = (FooImpl) component.getTargetInstance();
        assertEquals("value", foo.prop);
        container.onEvent(new CompositeStop(this, null));
    }

    protected void setUp() throws Exception {
        super.setUp();
        parent = EasyMock.createNiceMock(CompositeComponent.class);
        container = new CompositeScopeContainer(null);
        container.start();
        deploymentContext = EasyMock.createMock(DeploymentContext.class);
        EasyMock.expect(deploymentContext.getCompositeScope()).andReturn(container).atLeastOnce();
        EasyMock.replay(deploymentContext);
    }

    private static interface Foo {

    }

    private static class FooImpl implements Foo {
        private boolean initialized;
        private boolean destroyed;
        private String prop;
        private Foo ref;

        public FooImpl() {
        }

        public void init() {
            if (initialized) {
                fail();
            }
            initialized = true;
        }

        public void destroy() {
            if (destroyed) {
                fail();
            }
            destroyed = true;
        }

        public boolean isInitialized() {
            return initialized;
        }

        public String getProp() {
            return prop;
        }

        public void setProp(String prop) {
            this.prop = prop;
        }

        public Foo getRef() {
            return ref;
        }

        public void setRef(Foo ref) {
            this.ref = ref;
        }
    }

}
