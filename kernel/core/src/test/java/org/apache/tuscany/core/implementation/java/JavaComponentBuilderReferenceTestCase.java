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
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Wire;
import org.apache.tuscany.spi.wire.ProxyService;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.composite.CompositeComponentImpl;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.wire.jdk.JDKProxyService;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * @version $$Rev$$ $$Date$$
 */
public class JavaComponentBuilderReferenceTestCase extends TestCase {
    private DeploymentContext deploymentContext;
    private ProxyService proxyService;
    private Constructor<SourceImpl> constructor;
    private Component parent;
    private Wire wire;
    private ScopeContainer scopeContainer;

    public void testBuildReference() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> sourceType =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        sourceType.setImplementationScope(Scope.COMPOSITE);
        JavaMappedReference reference = new JavaMappedReference();
        reference.setUri(URI.create("#target"));
        reference.setMember(SourceImpl.class.getMethod("setTarget", Target.class));
        sourceType.add(reference);
        ServiceContract<?> contract = new JavaServiceContract(Source.class);
        JavaMappedService serviceDefinition = new JavaMappedService();
        serviceDefinition.setUri(URI.create("#Source"));
        serviceDefinition.setServiceContract(contract);
        sourceType.add(serviceDefinition);
        sourceType.setConstructorDefinition(new ConstructorDefinition<SourceImpl>(constructor));
        JavaImplementation sourceImpl = new JavaImplementation(SourceImpl.class, sourceType);
        ComponentDefinition<JavaImplementation> definition = new ComponentDefinition<JavaImplementation>(sourceImpl);
        definition.setUri(URI.create("component"));
        JavaComponentBuilder builder = new JavaComponentBuilder();
        builder.setProxyService(proxyService);
        JavaAtomicComponent component = (JavaAtomicComponent) builder.build(definition, deploymentContext);
        component.setScopeContainer(scopeContainer);
        component.attachWire(wire);
        deploymentContext.getCompositeScope().start();
        component.start();

        Source source = (Source) component.getTargetInstance();
        assertNotNull(source.getTarget());
        component.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        proxyService = new JDKProxyService();
        parent = new CompositeComponentImpl(URI.create("parent"));
        constructor = SourceImpl.class.getConstructor((Class[]) null);
        createDeploymentContext();
        createWire();
    }


    private void createDeploymentContext() throws Exception {
        scopeContainer = EasyMock.createMock(ScopeContainer.class);
        scopeContainer.start();
        scopeContainer.register(EasyMock.isA(AtomicComponent.class));
        EasyMock.expectLastCall().atLeastOnce();
        EasyMock.expect(scopeContainer.getScope()).andReturn(Scope.COMPOSITE).atLeastOnce();
        scopeContainer.getWrapper(EasyMock.isA(AtomicComponent.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            private Map<AtomicComponent, InstanceWrapper> cache = new HashMap<AtomicComponent, InstanceWrapper>();

            public Object answer() throws Throwable {
                AtomicComponent component = (AtomicComponent) EasyMock.getCurrentArguments()[0];
                InstanceWrapper instance = cache.get(component);
                if (instance == null) {
                    instance = component.createInstanceWrapper();
                    cache.put(component, instance);
                }
                return instance;
            }
        }).anyTimes();
        scopeContainer.unregister(EasyMock.isA(AtomicComponent.class));
        scopeContainer.stop();
        EasyMock.replay(scopeContainer);
        deploymentContext = EasyMock.createMock(DeploymentContext.class);
        EasyMock.expect(deploymentContext.getCompositeScope()).andReturn(scopeContainer).atLeastOnce();
        EasyMock.replay(deploymentContext);
    }

    private void createWire() {
        Map<Operation<?>, InvocationChain> chains = Collections.emptyMap();
        wire = EasyMock.createMock(Wire.class);
        EasyMock.expect(wire.getSourceUri()).andReturn(URI.create("#target")).atLeastOnce();
        EasyMock.expect(wire.getInvocationChains()).andReturn(chains).atLeastOnce();
        EasyMock.expect(wire.isOptimizable()).andReturn(false);
        JavaServiceContract targetContract = new JavaServiceContract(Target.class);
        targetContract.setConversational(false);
        EasyMock.expect(wire.getSourceContract()).andReturn(targetContract).atLeastOnce();
        EasyMock.replay(wire);

    }

}
