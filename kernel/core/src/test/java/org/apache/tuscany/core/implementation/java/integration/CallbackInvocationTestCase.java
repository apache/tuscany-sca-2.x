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
package org.apache.tuscany.core.implementation.java.integration;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;

import org.osoa.sca.NoRegisteredCallbackException;
import org.osoa.sca.annotations.Callback;

import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.scope.CompositeScopeContainer;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.implementation.java.JavaAtomicComponent;
import org.apache.tuscany.core.implementation.java.JavaComponentBuilder;
import org.apache.tuscany.core.implementation.java.JavaImplementation;
import org.apache.tuscany.core.wire.jdk.JDKWireService;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import org.easymock.IAnswer;

/**
 * Verifies callback integration scenarios with Java components.
 *
 * @version $Rev$ $Date$
 */
public class CallbackInvocationTestCase extends TestCase {
    private ScopeContainer container;
    private DeploymentContext context;
    private JavaComponentBuilder builder;
    private WireService wireService;
    private WorkScheduler scheduler;
    private WorkContext workContext;

    /**
     * Verifies callback wires are built and callback invocations are handled properly
     */
    public void testComponentToComponentCallback() throws Exception {
        ComponentDefinition<JavaImplementation> targetDefinition = createTarget();
        JavaAtomicComponent fooComponent =
            (JavaAtomicComponent) builder.build(null, targetDefinition, context);
        wireService.createWires(fooComponent, targetDefinition);
        container.register(fooComponent);

        CompositeComponent parent = createMock(CompositeComponent.class);
        parent.getChild(isA(String.class));
        expectLastCall().andReturn(fooComponent).anyTimes();
        replay(parent);

        ComponentDefinition<JavaImplementation> sourceDefinition = createSource("fooClient");
        JavaAtomicComponent clientComponent =
            (JavaAtomicComponent) builder.build(parent, sourceDefinition, context);
        wireService.createWires(clientComponent, sourceDefinition);
        container.register(clientComponent);

        Connector connector = new ConnectorImpl(new JDKWireService(), null, scheduler, workContext);

        connector.connect(clientComponent);
        FooClient client = (FooClient) clientComponent.getTargetInstance();
        client.invoke();
        assertTrue(client.invoked);
        client.invokeMultiCallback();
        assertTrue(client.count == 2);
    }

    /**
     * Verifies exception is thrown when callback is not implemented
     */
    public void testCallbackNotRegistered() throws Exception {
        ComponentDefinition<JavaImplementation> targetDefinition = createTarget();
        JavaAtomicComponent fooComponent =
            (JavaAtomicComponent) builder.build(null, targetDefinition, context);
        wireService.createWires(fooComponent, targetDefinition);
        container.register(fooComponent);

        CompositeComponent parent = createMock(CompositeComponent.class);
        parent.getChild(isA(String.class));
        expectLastCall().andReturn(fooComponent).anyTimes();
        replay(parent);

        ComponentDefinition<JavaImplementation> sourceDefinition = createPlainSource("fooPlainClient");
        JavaAtomicComponent clientComponent =
            (JavaAtomicComponent) builder.build(parent, sourceDefinition, context);
        wireService.createWires(clientComponent, sourceDefinition);
        container.register(clientComponent);

        Connector connector = new ConnectorImpl(new JDKWireService(), null, scheduler, workContext);

        connector.connect(clientComponent);
        FooPlainClient client = (FooPlainClient) clientComponent.getTargetInstance();
        try {
            client.invoke();
            fail();
        } catch (NoRegisteredCallbackException e) {
            // expected
        }
    }

    /**
     * Verifies a callback in response to an invocation from two different client components is routed back to the
     * appropriate client.
     */
    public void testTwoSourceComponentToComponentCallback() throws Exception {
        ComponentDefinition<JavaImplementation> targetDefinition = createTarget();
        JavaAtomicComponent fooComponent =
            (JavaAtomicComponent) builder.build(null, targetDefinition, context);
        wireService.createWires(fooComponent, targetDefinition);
        container.register(fooComponent);

        CompositeComponent parent = createMock(CompositeComponent.class);
        parent.getChild(isA(String.class));
        expectLastCall().andReturn(fooComponent).anyTimes();
        replay(parent);

        ComponentDefinition<JavaImplementation> sourceDefinition1 = createSource("fooCleint1");
        ComponentDefinition<JavaImplementation> sourceDefinition2 = createSource("fooCleint2");
        JavaAtomicComponent clientComponent1 =
            (JavaAtomicComponent) builder.build(parent, sourceDefinition1, context);
        wireService.createWires(clientComponent1, sourceDefinition1);
        container.register(clientComponent1);
        JavaAtomicComponent clientComponent2 =
            (JavaAtomicComponent) builder.build(parent, sourceDefinition2, context);
        wireService.createWires(clientComponent2, sourceDefinition2);
        container.register(clientComponent2);

        Connector connector = new ConnectorImpl(new JDKWireService(), null, scheduler, workContext);
        connector.connect(clientComponent1);
        connector.connect(clientComponent2);
        FooClient client1 = (FooClient) clientComponent1.getTargetInstance();
        client1.invoke();
        assertTrue(client1.invoked);
        FooClient client2 = (FooClient) clientComponent2.getTargetInstance();
        client2.invoke();
        assertTrue(client2.invoked);
    }


    private ComponentDefinition<JavaImplementation> createTarget() throws NoSuchMethodException,
                                                                          InvalidServiceContractException {
        ConstructorDefinition<FooImpl> ctorDef = new ConstructorDefinition<FooImpl>(FooImpl.class.getConstructor());
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        type.setConstructorDefinition(ctorDef);
        type.setImplementationScope(Scope.COMPOSITE);
        Method method = FooImpl.class.getMethod("setCallback", FooCallback.class);
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        ServiceContract<?> contract = registry.introspect(Foo.class);
        contract.setCallbackClass(FooCallback.class);
        contract.setCallbackName("callback");
        JavaMappedService mappedService = new JavaMappedService("Foo", contract, false, "callback", method);
        type.getServices().put("Foo", mappedService);

        JavaImplementation impl = new JavaImplementation();
        impl.setComponentType(type);
        impl.setImplementationClass(FooImpl.class);
        return new ComponentDefinition<JavaImplementation>("foo", impl);
    }

    private ComponentDefinition<JavaImplementation> createSource(String name)
        throws NoSuchMethodException, URISyntaxException, InvalidServiceContractException {
        ConstructorDefinition<FooClient> ctorDef =
            new ConstructorDefinition<FooClient>(FooClient.class.getConstructor());
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        type.setConstructorDefinition(ctorDef);
        type.setImplementationScope(Scope.COMPOSITE);
        Method method = FooClient.class.getMethod("setFoo", Foo.class);
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        ServiceContract<?> contract = registry.introspect(Foo.class);
        contract.setCallbackClass(FooCallback.class);
        contract.setCallbackName("callback");
        JavaMappedReference mappedReference = new JavaMappedReference("foo", contract, method);
        type.getReferences().put("foo", mappedReference);
        ReferenceTarget refTarget = new ReferenceTarget();
        refTarget.setReferenceName("foo");
        refTarget.getTargets().add(new URI("foo"));
        JavaImplementation impl = new JavaImplementation();
        impl.setComponentType(type);
        impl.setImplementationClass(FooClient.class);
        ComponentDefinition<JavaImplementation> def = new ComponentDefinition<JavaImplementation>(name, impl);
        def.getReferenceTargets().put("foo", refTarget);
        return def;
    }

    private ComponentDefinition<JavaImplementation> createPlainSource(String name)
        throws NoSuchMethodException, URISyntaxException, InvalidServiceContractException {
        ConstructorDefinition<FooPlainClient> ctorDef =
            new ConstructorDefinition<FooPlainClient>(FooPlainClient.class.getConstructor());
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        type.setConstructorDefinition(ctorDef);
        type.setImplementationScope(Scope.COMPOSITE);
        Method method = FooPlainClient.class.getMethod("setFoo", Foo.class);
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        ServiceContract<?> contract = registry.introspect(Foo.class);
        contract.setCallbackClass(FooCallback.class);
        contract.setCallbackName("callback");
        JavaMappedReference mappedReference = new JavaMappedReference("foo", contract, method);
        type.getReferences().put("foo", mappedReference);
        ReferenceTarget refTarget = new ReferenceTarget();
        refTarget.setReferenceName("foo");
        refTarget.getTargets().add(new URI("foo"));
        JavaImplementation impl = new JavaImplementation();
        impl.setComponentType(type);
        impl.setImplementationClass(FooPlainClient.class);
        ComponentDefinition<JavaImplementation> def = new ComponentDefinition<JavaImplementation>(name, impl);
        def.getReferenceTargets().put("foo", refTarget);
        return def;
    }

    @Callback(FooCallback.class)
    public static interface Foo {
        void call();

        void callMultiCallback();

        void callFromPlain();
    }

    public static class FooImpl implements Foo {
        private FooCallback callback;

        public FooImpl() {
        }

        @Callback
        public void setCallback(FooCallback callback) {
            this.callback = callback;
        }

        public void call() {
            callback.callback();
        }

        public void callMultiCallback() {
            callback.multiCallback();
            callback.multiCallback();
        }

        public void callFromPlain() {
            try {
                callback.callback();
                fail();
            } catch (NoRegisteredCallbackException e) {
                // expected
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static class FooClient implements FooCallback {

        private Foo foo;
        private boolean invoked;
        private int count;

        public FooClient() {
        }

        public void setFoo(Foo foo) {
            this.foo = foo;
        }

        public void callback() {
            if (invoked) {
                fail();
            }
            invoked = true;
        }

        public void multiCallback() {
            count++;
        }

        public void invoke() {
            foo.call();
        }

        public void invokeMultiCallback() {
            foo.callMultiCallback();
        }
    }

    public interface FooCallback {
        void callback();

        void multiCallback();
    }

    public static class FooPlainClient /* implements FooCallback */ { // do NOT implement the callback

        private Foo foo;

        public FooPlainClient() {
        }

        public void setFoo(Foo foo) {
            this.foo = foo;
        }

        public void invoke() {
            foo.callFromPlain();
        }

        public void callback() {

        }

        public void multiCallback() {

        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        wireService = new JDKWireService();
        container = new CompositeScopeContainer(null);
        container.start();
        context = createMock(DeploymentContext.class);
        context.getCompositeScope();
        expectLastCall().andReturn(container).anyTimes();
        replay(context);

        scheduler = createMock(WorkScheduler.class);
        scheduler.scheduleWork(isA(Runnable.class));
        expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                Runnable runnable = (Runnable) getCurrentArguments()[0];
                runnable.run();
                return null;
            }
        });
        replay(scheduler);

        builder = new JavaComponentBuilder();
        workContext = new WorkContextImpl();
        builder.setWorkContext(workContext);
        builder.setWireService(new JDKWireService(workContext, null));
        builder.setWorkScheduler(scheduler);
    }
}
