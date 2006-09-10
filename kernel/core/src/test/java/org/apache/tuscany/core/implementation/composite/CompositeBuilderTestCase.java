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
package org.apache.tuscany.core.implementation.composite;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.model.BindlessServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.apache.tuscany.core.builder.BuilderRegistryImpl;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.deployer.RootDeploymentContext;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.implementation.java.JavaComponentBuilder;
import org.apache.tuscany.core.implementation.java.JavaImplementation;
import org.apache.tuscany.core.implementation.java.mock.components.OtherTarget;
import org.apache.tuscany.core.implementation.java.mock.components.Source;
import org.apache.tuscany.core.implementation.java.mock.components.SourceImpl;
import org.apache.tuscany.core.implementation.java.mock.components.Target;
import org.apache.tuscany.core.implementation.java.mock.components.TargetImpl;
import org.apache.tuscany.core.wire.jdk.JDKWireService;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * @version $$Rev$$ $$Date$$
 */
public class CompositeBuilderTestCase extends TestCase {
    private DeploymentContext deploymentContext;

    protected void setUp() throws Exception {
        super.setUp();
        ScopeContainer mock = createMock();
        deploymentContext = new RootDeploymentContext(null, null, mock, null);
    }

    @SuppressWarnings("unchecked")
    public void testBuildConnect() throws Exception {
        CompositeComponent parent = new CompositeComponentImpl(null, null, null, null, null);

        CompositeBuilder builder = new CompositeBuilder();
        WireService wireService = new JDKWireService();
        builder.setWireService(wireService);
        BuilderRegistryImpl builderRegistry = new BuilderRegistryImpl();
        builderRegistry.setWireService(wireService);
        JavaComponentBuilder jBuilder = new JavaComponentBuilder();
        jBuilder.setWireService(wireService);
        builderRegistry.register(JavaImplementation.class, jBuilder);
        builderRegistry.register(CompositeImplementation.class, builder);
        CompositeBindlessBuilder bindlessBuilder = new CompositeBindlessBuilder();
        bindlessBuilder.setWireService(wireService);
        builderRegistry.register(bindlessBuilder);
        builder.setBuilderRegistry(builderRegistry);
        CompositeComponent component =
            (CompositeComponent) builder.build(parent, createTopComponentDef(), deploymentContext);

        ConnectorImpl connector = new ConnectorImpl();
        connector.connect(component);

        deploymentContext.getModuleScope().start();
        component.start();
        CompositeComponent sourceComponent = (CompositeComponent) component.getChild("SourceComponent");
        Source source = (Source) sourceComponent.getServiceInstance("InnerSourceService");
        assertNotNull(source);
        AtomicComponent innerSourceComponent = (AtomicComponent) sourceComponent.getChild("InnerSourceComponent");
        Source innerSourceInstance = (Source) deploymentContext.getModuleScope().getInstance(innerSourceComponent);
        assertNotNull(innerSourceInstance);
        component.stop();
    }

    private ComponentDefinition createTopComponentDef() throws Exception {

        CompositeComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> outerType =
            new CompositeComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        outerType.add(createSourceComponentDef());
        outerType.add(createTargetComponentDef());

        CompositeImplementation outerImpl = new CompositeImplementation();
        outerImpl.setComponentType(outerType);

        return new ComponentDefinition<CompositeImplementation>(outerImpl);
    }

    private ComponentDefinition<CompositeImplementation> createSourceComponentDef() throws Exception {

        CompositeComponentType<ServiceDefinition, ReferenceDefinition, JavaMappedProperty<?>> innerType =
            new CompositeComponentType<ServiceDefinition, ReferenceDefinition, JavaMappedProperty<?>>();
        innerType.add(createInnerSourceComponentDef());
        ReferenceDefinition reference = new ReferenceDefinition();
        reference.setName("TargetComponentRef");
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        JavaServiceContract targetContract = registry.introspect(Target.class);
        reference.setServiceContract(targetContract);
        innerType.add(reference);
        BindlessServiceDefinition service = new BindlessServiceDefinition();
        service.setName("InnerSourceService");
        JavaServiceContract sourceContract = registry.introspect(Source.class);
        service.setServiceContract(sourceContract);
        service.setTarget(new URI("InnerSourceComponent"));
        innerType.add(service);

        CompositeImplementation innerImpl = new CompositeImplementation();
        innerImpl.setComponentType(innerType);

        ComponentDefinition<CompositeImplementation> sourceComponentDefinition =
            new ComponentDefinition<CompositeImplementation>("SourceComponent", innerImpl);
        ReferenceTarget refTarget = new ReferenceTarget();
        refTarget.setReferenceName("TargetComponentRef");
        refTarget.addTarget(new URI("TargetComponent"));
        sourceComponentDefinition.add(refTarget);

        return sourceComponentDefinition;
    }

    private ComponentDefinition<JavaImplementation> createInnerSourceComponentDef() throws Exception {

        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> sourceType =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        sourceType.setImplementationScope(Scope.MODULE);
        JavaMappedReference reference = new JavaMappedReference();
        reference.setName("targetReference");
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        ServiceContract<?> targetContract = registry.introspect(Target.class);
        targetContract.setCallbackClass(OtherTarget.class);
        targetContract.setCallbackName("OtherTarget");
        reference.setServiceContract(targetContract);
        reference.setMember(SourceImpl.class.getMethod("setTarget", Target.class));
        sourceType.add(reference);

        ServiceContract<?> sourceContract = registry.introspect(Source.class);

        JavaMappedService sourceServiceDefinition = new JavaMappedService();
        sourceServiceDefinition.setName("Source");
        sourceServiceDefinition.setServiceContract(sourceContract);

        sourceType.add(sourceServiceDefinition);
        sourceType.setConstructorDefinition(new ConstructorDefinition<SourceImpl>(SourceImpl.class.getConstructor()));
        JavaImplementation sourceImpl = new JavaImplementation();
        sourceImpl.setComponentType(sourceType);
        sourceImpl.setImplementationClass(SourceImpl.class);
        ComponentDefinition<JavaImplementation> innerSourceComponentDefinition =
            new ComponentDefinition<JavaImplementation>("InnerSourceComponent", sourceImpl);
        ReferenceTarget refTarget = new ReferenceTarget();
        refTarget.setReferenceName("targetReference");
        refTarget.addTarget(new URI("TargetComponentRef"));
        innerSourceComponentDefinition.add(refTarget);

        return innerSourceComponentDefinition;
    }

    private ComponentDefinition<JavaImplementation> createTargetComponentDef() throws Exception {

        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> targetType =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        targetType.setImplementationScope(Scope.MODULE);

        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        ServiceContract<?> targetContract = registry.introspect(Target.class);
        targetContract.setCallbackClass(OtherTarget.class);
        targetContract.setCallbackName("OtherTarget");

        JavaMappedService serviceDefinition = new JavaMappedService();
        serviceDefinition.setName("Target");
        serviceDefinition.setServiceContract(targetContract);
        serviceDefinition.setCallbackReferenceName("otherTarget");

        targetType.add(serviceDefinition);
        targetType.setConstructorDefinition(new ConstructorDefinition<TargetImpl>(TargetImpl.class.getConstructor()));
        JavaImplementation targetImpl = new JavaImplementation();
        targetImpl.setComponentType(targetType);
        targetImpl.setImplementationClass(TargetImpl.class);
        return new ComponentDefinition<JavaImplementation>("TargetComponent", targetImpl);
    }

    private ScopeContainer createMock() {
        ScopeContainer container = EasyMock.createMock(ScopeContainer.class);
        container.start();
        container.register(EasyMock.isA(AtomicComponent.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(container.getScope()).andReturn(Scope.MODULE).anyTimes();
        EasyMock.expect(container.getInstance(EasyMock.isA(AtomicComponent.class))).andAnswer(new IAnswer<Object>() {
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
        });
        EasyMock.replay(container);
        return container;
    }
}
