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
package org.apache.tuscany.core.implementation.java.integration.component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.CompositeStart;
import org.apache.tuscany.core.component.event.CompositeStop;
import org.apache.tuscany.core.component.event.HttpSessionEnd;
import org.apache.tuscany.core.component.event.HttpSessionStart;
import org.apache.tuscany.core.component.event.RequestEnd;
import org.apache.tuscany.core.component.event.RequestStart;
import org.apache.tuscany.core.component.scope.HttpSessionScopeContainer;
import org.apache.tuscany.core.component.scope.ModuleScopeContainer;
import org.apache.tuscany.core.component.scope.RequestScopeContainer;
import org.apache.tuscany.core.component.scope.StatelessScopeContainer;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.java.JavaAtomicComponent;
import org.apache.tuscany.core.implementation.java.mock.MockFactory;
import org.apache.tuscany.core.implementation.java.mock.components.Target;
import org.apache.tuscany.core.implementation.java.mock.components.TargetImpl;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.apache.tuscany.core.wire.jdk.JDKWireService;
import org.easymock.EasyMock;

/**
 * Validates wiring from a service context to Java atomic contexts by scope
 *
 * @version $$Rev$$ $$Date$$
 */
public class OutboundWireToJavaTestCase extends TestCase {
    private WorkContext workContext = new WorkContextImpl();
    private WireService wireService = new JDKWireService(new WorkContextImpl(), null);

    public void testToStatelessScope() throws Exception {
        StatelessScopeContainer scope = new StatelessScopeContainer(workContext);
        scope.start();
        final OutboundWire wire = getWire(scope);
        Target service = (Target) wireService.createProxy(wire);
        assertNotNull(service);
        service.setString("foo");
        assertEquals(null, service.getString());
        scope.stop();
    }

    public void testToRequestScope() throws Exception {
        final RequestScopeContainer scope = new RequestScopeContainer(workContext);
        scope.start();

        scope.onEvent(new RequestStart(this));

        final OutboundWire wire = getWire(scope);
        Target service = (Target) wireService.createProxy(wire);
        assertNotNull(service);
        service.setString("foo");

        // another request
        Executor executor = Executors.newSingleThreadExecutor();
        FutureTask<Void> future = new FutureTask<Void>(new Runnable() {
            public void run() {
                scope.onEvent(new RequestStart(this));
                Target service2 = (Target) wireService.createProxy(wire);
                Target target2 = (Target) wireService.createProxy(wire);
                assertEquals(null, service2.getString());
                service2.setString("bar");
                assertEquals("bar", service2.getString());
                assertEquals("bar", target2.getString());
                scope.onEvent(new RequestEnd(this));
            }
        }, null);
        executor.execute(future);
        future.get();

        assertEquals("foo", service.getString());
        scope.onEvent(new RequestEnd(this));
        scope.stop();
    }

    public void testToSessionScope() throws Exception {
        HttpSessionScopeContainer scope = new HttpSessionScopeContainer(workContext);
        scope.start();
        Object session1 = new Object();
        workContext.setIdentifier(HttpSessionScopeContainer.HTTP_IDENTIFIER, session1);
        scope.onEvent(new HttpSessionStart(this, session1));

        final OutboundWire wire = getWire(scope);
        Target service = (Target) wireService.createProxy(wire);
        Target target = (Target) wireService.createProxy(wire);
        assertNotNull(service);
        service.setString("foo");
        assertEquals("foo", service.getString());
        assertEquals("foo", target.getString());

        workContext.clearIdentifier(HttpSessionScopeContainer.HTTP_IDENTIFIER);

        //second session
        Object session2 = new Object();
        workContext.setIdentifier(HttpSessionScopeContainer.HTTP_IDENTIFIER, session2);
        scope.onEvent(new HttpSessionStart(this, session2));

        Target service2 = (Target) wireService.createProxy(wire);
        assertNotNull(service2);
        assertNull(service2.getString());
        Target target2 = (Target) wireService.createProxy(wire);
        service2.setString("bar");
        assertEquals("bar", service2.getString());
        assertEquals("bar", target2.getString());

        scope.onEvent(new HttpSessionEnd(this, session2));
        workContext.clearIdentifier(HttpSessionScopeContainer.HTTP_IDENTIFIER);

        workContext.setIdentifier(HttpSessionScopeContainer.HTTP_IDENTIFIER, session1);
        assertEquals("foo", service.getString());

        scope.onEvent(new HttpSessionEnd(this, session1));

        scope.stop();
    }

    public void testToModuleScope() throws Exception {

        ModuleScopeContainer scope = new ModuleScopeContainer(workContext);
        scope.start();
        scope.onEvent(new CompositeStart(this, null));
        final OutboundWire wire = getWire(scope);
        Target service = (Target) wireService.createProxy(wire);
        Target target = (Target) wireService.createProxy(wire);
        assertNotNull(service);
        service.setString("foo");
        assertEquals("foo", service.getString());
        assertEquals("foo", target.getString());
        scope.onEvent(new CompositeStop(this, null));
        scope.stop();
    }

    private OutboundWire getWire(ScopeContainer scope) throws NoSuchMethodException,
                                                              InvalidServiceContractException {
        ConnectorImpl connector = new ConnectorImpl();

        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);

        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setScopeContainer(scope);
        configuration.setInstanceFactory(new PojoObjectFactory<TargetImpl>(TargetImpl.class.getConstructor()));
        configuration.addServiceInterface(Target.class);
        configuration.setParent(parent);
        configuration.setWorkContext(workContext);
        configuration.setName("source");

        JavaAtomicComponent source = new JavaAtomicComponent(configuration);
        OutboundWire outboundWire = createOutboundWire(new QualifiedName("target/Target"), Target.class);
        outboundWire.setContainer(source);
        source.addOutboundWire(outboundWire);
        configuration.setName("target");
        JavaAtomicComponent target = new JavaAtomicComponent(configuration);
        InboundWire targetWire = MockFactory.createTargetWire("Target", Target.class);
        targetWire.setContainer(target);
        target.addInboundWire(targetWire);
        InboundWire inboundWire = target.getInboundWire("Target");
        inboundWire.setContainer(target);

        EasyMock.expect(parent.getChild("target")).andReturn(target);
        EasyMock.replay(parent);

        connector.connect(source);
        target.start();
        return outboundWire;
    }

    public static <T> OutboundWire createOutboundWire(QualifiedName targetName, Class<T> interfaze)
        throws InvalidServiceContractException {
        OutboundWire wire = new OutboundWireImpl();
        JavaServiceContract contract = new JavaServiceContract(interfaze);
        wire.setServiceContract(contract);
        wire.setTargetName(targetName);
        wire.addInvocationChains(createInvocationChains(interfaze));
        return wire;
    }

    private static Map<Operation<?>, OutboundInvocationChain> createInvocationChains(Class<?> interfaze)
        throws InvalidServiceContractException {
        Map<Operation<?>, OutboundInvocationChain> invocations = new HashMap<Operation<?>, OutboundInvocationChain>();
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        ServiceContract<?> contract = registry.introspect(interfaze);
        for (Operation operation : contract.getOperations().values()) {
            OutboundInvocationChain chain = new OutboundInvocationChainImpl(operation);
            invocations.put(operation, chain);
        }
        return invocations;
    }

}
