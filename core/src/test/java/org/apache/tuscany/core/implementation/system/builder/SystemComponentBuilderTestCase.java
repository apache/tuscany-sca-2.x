/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.implementation.system.builder;

import java.lang.reflect.Method;
import java.net.URI;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.PropertyValue;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.OutboundWire;

import org.apache.tuscany.core.component.AutowireComponent;
import org.apache.tuscany.core.component.event.CompositeStart;
import org.apache.tuscany.core.component.event.CompositeStop;
import org.apache.tuscany.core.component.scope.ModuleScopeContainer;
import org.apache.tuscany.core.implementation.ConstructorDefinition;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.JavaServiceContract;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.system.model.SystemImplementation;
import org.apache.tuscany.core.implementation.system.wire.SystemInboundWire;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $Rev$ $Date$
 */
public class SystemComponentBuilderTestCase extends MockObjectTestCase {

    AutowireComponent parent;
    DeploymentContext deploymentContext;
    SystemComponentBuilder builder = new SystemComponentBuilder();
    ModuleScopeContainer container;

    /**
     * Verifies lifecycle callbacks are made
     */
    public void testLifecycleBuild() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        type.setEagerInit(true);
        Method initMethod = FooImpl.class.getMethod("init");
        initMethod.setAccessible(true);
        type.setInitMethod(initMethod);
        Method destroyMethod = FooImpl.class.getMethod("destroy");
        destroyMethod.setAccessible(true);
        type.setDestroyMethod(destroyMethod);
        type.setImplementationScope(Scope.MODULE);
        ConstructorDefinition<FooImpl> ctorDef = new ConstructorDefinition<FooImpl>(FooImpl.class.getConstructor());
        type.setConstructorDefinition(ctorDef);
        SystemImplementation impl = new SystemImplementation();
        impl.setComponentType(type);
        impl.setImplementationClass(FooImpl.class);
        ComponentDefinition<SystemImplementation> definition = new ComponentDefinition<SystemImplementation>(impl);
        AtomicComponent component = builder.build(parent, definition, deploymentContext);
        component.start();
        container.onEvent(new CompositeStart(this, null));
        FooImpl foo = (FooImpl) component.getServiceInstance();
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
        type.setEagerInit(true);
        Method initMethod = FooImpl.class.getMethod("init");
        initMethod.setAccessible(true);
        type.setInitMethod(initMethod);
        Method destroyMethod = FooImpl.class.getMethod("destroy");
        destroyMethod.setAccessible(true);
        type.setDestroyMethod(destroyMethod);
        type.setImplementationScope(Scope.MODULE);
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
        component.start();
        FooImpl foo = (FooImpl) component.getServiceInstance();
        assertEquals("value", foo.prop);
        container.onEvent(new CompositeStop(this, null));
    }

    /**
     * Verifies references are built properly
     */
    @SuppressWarnings("unchecked")
    public void testRefBuild() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        type.setImplementationScope(Scope.MODULE);
        JavaMappedReference mappedReference = new JavaMappedReference();
        mappedReference.setName("ref");
        Method refMethod = FooImpl.class.getMethod("setRef", Foo.class);
        refMethod.setAccessible(true);
        mappedReference.setMember(refMethod);
        JavaServiceContract contract = new JavaServiceContract(Foo.class);
        mappedReference.setServiceContract(contract);
        type.add(mappedReference);
        ConstructorDefinition<FooImpl> ctorDef = new ConstructorDefinition<FooImpl>(FooImpl.class.getConstructor());
        type.setConstructorDefinition(ctorDef);
        SystemImplementation impl = new SystemImplementation();
        impl.setComponentType(type);
        impl.setImplementationClass(FooImpl.class);
        ComponentDefinition<SystemImplementation> definition = new ComponentDefinition<SystemImplementation>(impl);
        ReferenceTarget target = new ReferenceTarget();
        target.setReferenceName("ref");
        target.addTarget(new URI("foo"));
        definition.add(target);
        AtomicComponent<?> component = builder.build(parent, definition, deploymentContext);
        OutboundWire<Foo> wire = component.getOutboundWires().get("ref").get(0);
        Mock mock = mock(SystemInboundWire.class);
        FooImpl targetFoo = new FooImpl();
        mock.expects(once()).method("getTargetService").will(returnValue(targetFoo));
        wire.setTargetWire((SystemInboundWire<Foo>) mock.proxy());
        component.start();
        FooImpl foo = (FooImpl) component.getServiceInstance();
        assertNotNull(foo.ref);
        container.onEvent(new CompositeStop(this, null));
    }

    /**
     * Verifies autowires are built properly
     */
    @SuppressWarnings("unchecked")
    public void testAutowireBuild() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        type.setImplementationScope(Scope.MODULE);
        JavaMappedReference mappedReference = new JavaMappedReference();
        mappedReference.setName("ref");
        mappedReference.setAutowire(true);
        Method refMethod = FooImpl.class.getMethod("setRef", Foo.class);
        refMethod.setAccessible(true);
        mappedReference.setMember(refMethod);
        JavaServiceContract contract = new JavaServiceContract(Foo.class);
        mappedReference.setServiceContract(contract);
        type.add(mappedReference);
        ConstructorDefinition<FooImpl> ctorDef = new ConstructorDefinition<FooImpl>(FooImpl.class.getConstructor());
        type.setConstructorDefinition(ctorDef);
        SystemImplementation impl = new SystemImplementation();
        impl.setComponentType(type);
        impl.setImplementationClass(FooImpl.class);
        ComponentDefinition<SystemImplementation> definition = new ComponentDefinition<SystemImplementation>(impl);
        Mock mock = mock(AutowireComponent.class);
        AutowireComponent autowireParent = (AutowireComponent) mock.proxy();
        FooImpl targetFoo = new FooImpl();
        mock.expects(once()).method("resolveInstance").will(returnValue(targetFoo));
        AtomicComponent<?> component = builder.build(autowireParent, definition, deploymentContext);
        component.start();
        FooImpl foo = (FooImpl) component.getServiceInstance();
        assertNotNull(foo.ref);
        container.onEvent(new CompositeStop(this, null));
    }

    /**
     * Verifies constructor-based autowiring
     */
    public void testAutowireConstructorBuild() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        type.setImplementationScope(Scope.MODULE);
        ConstructorDefinition<FooImpl2> ctorDef =
            new ConstructorDefinition<FooImpl2>(FooImpl2.class.getConstructor(Foo.class));
        ctorDef.getInjectionNames().add("ref");
        type.setConstructorDefinition(ctorDef);
        SystemImplementation impl = new SystemImplementation();
        impl.setComponentType(type);
        impl.setImplementationClass(FooImpl2.class);
        JavaMappedReference mappedReference = new JavaMappedReference();
        mappedReference.setName("ref");
        mappedReference.setAutowire(true);
        JavaServiceContract contract = new JavaServiceContract(Foo.class);
        mappedReference.setServiceContract(contract);
        type.add(mappedReference);
        ComponentDefinition<SystemImplementation> definition = new ComponentDefinition<SystemImplementation>(impl);
        Mock mock = mock(AutowireComponent.class);
        FooImpl targetFoo = new FooImpl();
        mock.expects(atLeastOnce()).method("resolveInstance").will(returnValue(targetFoo));
        AutowireComponent autowireParent = (AutowireComponent) mock.proxy();
        AtomicComponent component = builder.build(autowireParent, definition, deploymentContext);
        component.start();
        container.onEvent(new CompositeStart(this, null));
        FooImpl2 foo = (FooImpl2) component.getServiceInstance();
        assertNotNull(foo.getRef());
        container.onEvent(new CompositeStop(this, null));
    }

    protected void setUp() throws Exception {
        super.setUp();
        Mock mock = mock(AutowireComponent.class);
        parent = (AutowireComponent) mock.proxy();
        container = new ModuleScopeContainer();
        container.start();
        Mock mock2 = mock(DeploymentContext.class);
        mock2.expects(atLeastOnce()).method("getModuleScope").will(returnValue(container));
        deploymentContext = (DeploymentContext) mock2.proxy();
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

    private static class FooImpl2 implements Foo {
        private Foo ref;

        public FooImpl2(@Autowire Foo ref) {
            this.ref = ref;
        }

        public Foo getRef() {
            return ref;
        }

    }
}
