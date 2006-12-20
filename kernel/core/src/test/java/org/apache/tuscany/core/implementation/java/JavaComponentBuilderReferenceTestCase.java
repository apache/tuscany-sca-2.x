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

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.InteractionScope;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.composite.CompositeComponentImpl;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.wire.jdk.JDKWireService;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * @version $$Rev$$ $$Date$$
 */
public class JavaComponentBuilderReferenceTestCase extends TestCase {
    private DeploymentContext deploymentContext;
    private WireService wireService;
    private Constructor<SourceImpl> constructor;
    private CompositeComponent parent;
    private OutboundWire wire;

    public void testBuildReference() throws Exception {

        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> sourceType =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        sourceType.setImplementationScope(Scope.COMPOSITE);
        JavaMappedReference reference = new JavaMappedReference();
        reference.setName("target");
        reference.setMember(SourceImpl.class.getMethod("setTarget", Target.class));
        sourceType.add(reference);
        ServiceContract<?> contract = new JavaServiceContract(Source.class);
        JavaMappedService serviceDefinition = new JavaMappedService();
        serviceDefinition.setName("Source");
        serviceDefinition.setServiceContract(contract);
        sourceType.add(serviceDefinition);
        sourceType.setConstructorDefinition(new ConstructorDefinition<SourceImpl>(constructor));
        JavaImplementation sourceImpl = new JavaImplementation();
        sourceImpl.setComponentType(sourceType);
        sourceImpl.setImplementationClass(SourceImpl.class);
        ComponentDefinition<JavaImplementation> definition = new ComponentDefinition<JavaImplementation>(sourceImpl);

        JavaComponentBuilder builder = new JavaComponentBuilder();
        builder.setWireService(wireService);
        JavaAtomicComponent component = (JavaAtomicComponent) builder.build(parent, definition, deploymentContext);
        component.addOutboundWire(wire);
        deploymentContext.getModuleScope().start();
        component.start();

        Source source = (Source) component.getServiceInstance();
        assertNotNull(source.getTarget());
        component.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        wireService = new JDKWireService();
        parent = new CompositeComponentImpl(null, null, null, null);
        constructor = SourceImpl.class.getConstructor((Class[]) null);
        createDeploymentContext();
        createWire();
    }


    private void createDeploymentContext() throws Exception {
        ScopeContainer scope = EasyMock.createMock(ScopeContainer.class);
        scope.start();
        scope.stop();
        scope.register(EasyMock.isA(AtomicComponent.class));
        EasyMock.expectLastCall().atLeastOnce();
        EasyMock.expect(scope.getScope()).andReturn(Scope.COMPOSITE).atLeastOnce();
        scope.getInstance(EasyMock.isA(AtomicComponent.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            private Map<AtomicComponent, Object> cache = new HashMap<AtomicComponent, Object>();

            public Object answer() throws Throwable {
                AtomicComponent component = (AtomicComponent) EasyMock.getCurrentArguments()[0];
                Object instance = cache.get(component);
                if (instance == null) {
                    instance = component.createInstance();
                    cache.put(component, instance);
                }
                return instance;
            }
        }).anyTimes();
        EasyMock.replay(scope);
        deploymentContext = EasyMock.createMock(DeploymentContext.class);
        EasyMock.expect(deploymentContext.getModuleScope()).andReturn(scope).atLeastOnce();
        EasyMock.replay(deploymentContext);
    }

    private void createWire() {
        SCAObject scaObject = EasyMock.createNiceMock(SCAObject.class);
        Map<Operation<?>, OutboundInvocationChain> chains = Collections.emptyMap();
        wire = EasyMock.createMock(OutboundWire.class);
        EasyMock.expect(wire.getReferenceName()).andReturn("target").atLeastOnce();
        EasyMock.expect(wire.getInvocationChains()).andReturn(chains);
        JavaServiceContract targetContract = new JavaServiceContract(Target.class);
        targetContract.setInteractionScope(InteractionScope.NONCONVERSATIONAL);
        EasyMock.expect(wire.getServiceContract()).andReturn(targetContract).atLeastOnce();
        EasyMock.expect(wire.getContainer()).andReturn(scaObject).atLeastOnce();
        EasyMock.replay(wire);

    }

}
