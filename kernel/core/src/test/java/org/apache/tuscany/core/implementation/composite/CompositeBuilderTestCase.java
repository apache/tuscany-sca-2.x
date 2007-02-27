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
import java.util.Map;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ScopeContainerMonitor;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;

import junit.framework.TestCase;
import org.apache.tuscany.core.binding.local.LocalBindingBuilder;
import org.apache.tuscany.core.binding.local.LocalBindingDefinition;
import org.apache.tuscany.core.builder.BuilderRegistryImpl;
import org.apache.tuscany.core.component.scope.CompositeScopeContainer;
import org.apache.tuscany.core.deployer.RootDeploymentContext;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.implementation.java.JavaComponentBuilder;
import org.apache.tuscany.core.implementation.java.JavaImplementation;
import org.apache.tuscany.core.mock.component.OtherTarget;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.easymock.EasyMock;

/**
 * @version $$Rev$$ $$Date$$
 */
public class CompositeBuilderTestCase extends TestCase {
    private static final URI TOP_COMPONENT = URI.create("Top");
    private static final URI SOURCE_COMPONENT = URI.create("Top/Parent/SourceComponent");
    private static final URI TARGET_COMPONENT = URI.create("Top/Parent/TargetComponent");
    private static final URI INNER_SOURCE_COMPONENT = URI.create("Top/Parent/SourceComponent/InnerSourceComponent");

    private DeploymentContext deploymentContext;

    @SuppressWarnings("unchecked")
    public void testBuild() throws Exception {
        CompositeBuilder builder = new CompositeBuilder();
        BuilderRegistryImpl builderRegistry = new BuilderRegistryImpl(null);
        JavaComponentBuilder jBuilder = new JavaComponentBuilder();
        builderRegistry.register(JavaImplementation.class, jBuilder);
        builderRegistry.register(CompositeImplementation.class, builder);
        builderRegistry.register(LocalBindingDefinition.class, new LocalBindingBuilder());
        builder.setBuilderRegistry(builderRegistry);

        Component component =
            builder.build(createTopComponentDef(), deploymentContext);
        Map<URI, Component> components = deploymentContext.getComponents();
        Component sourceComponent = components.get(SOURCE_COMPONENT);
        assertNotNull(sourceComponent.getService("InnerSourceService"));
        AtomicComponent innerSourceComponent = (AtomicComponent) components.get(INNER_SOURCE_COMPONENT);
        assertNotNull(innerSourceComponent);
    }

    protected void setUp() throws Exception {
        super.setUp();
        ScopeContainerMonitor monitor = EasyMock.createNiceMock(ScopeContainerMonitor.class);
        CompositeScopeContainer container = new CompositeScopeContainer(monitor);
        container.start();
        deploymentContext = new RootDeploymentContext(null, null, TOP_COMPONENT, null, container, false);
    }

    private ComponentDefinition createTopComponentDef() throws Exception {
        CompositeComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> outerType =
            new CompositeComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        outerType.add(createSourceComponentDef());
        outerType.add(createTargetComponentDef());

        CompositeImplementation outerImpl = new CompositeImplementation();
        outerImpl.setComponentType(outerType);

        ComponentDefinition def = new ComponentDefinition<CompositeImplementation>(outerImpl);
        def.setUri(TOP_COMPONENT);
        return def;
    }

    private ComponentDefinition<CompositeImplementation> createSourceComponentDef() throws Exception {

        CompositeComponentType<ServiceDefinition, ReferenceDefinition, JavaMappedProperty<?>> innerType =
            new CompositeComponentType<ServiceDefinition, ReferenceDefinition, JavaMappedProperty<?>>();
        innerType.add(createInnerSourceComponentDef());
        ReferenceDefinition reference = new ReferenceDefinition();
        reference.setUri(URI.create("#TargetComponentRef"));
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        JavaServiceContract targetContract = registry.introspect(Target.class);
        reference.setServiceContract(targetContract);
        innerType.add(reference);
        ServiceDefinition service = new ServiceDefinition();
        service.setUri(URI.create("#InnerSourceService"));
        JavaServiceContract sourceContract = registry.introspect(Source.class);
        service.setServiceContract(sourceContract);
        service.setTarget(INNER_SOURCE_COMPONENT);
        innerType.add(service);

        CompositeImplementation innerImpl = new CompositeImplementation();
        innerImpl.setComponentType(innerType);

        ComponentDefinition<CompositeImplementation> sourceComponentDefinition =
            new ComponentDefinition<CompositeImplementation>(SOURCE_COMPONENT, innerImpl);
        ReferenceTarget refTarget = new ReferenceTarget();
        refTarget.setReferenceName(URI.create("#TargetComponentRef"));
        refTarget.addTarget(TARGET_COMPONENT);
        sourceComponentDefinition.add(refTarget);

        return sourceComponentDefinition;
    }

    private ComponentDefinition<JavaImplementation> createInnerSourceComponentDef() throws Exception {

        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> sourceType =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        sourceType.setImplementationScope(Scope.COMPOSITE);
        JavaMappedReference reference = new JavaMappedReference();
        reference.setUri(URI.create("#targetReference"));
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        ServiceContract<?> targetContract = registry.introspect(Target.class);
        targetContract.setCallbackClass(OtherTarget.class);
        targetContract.setCallbackName("OtherTarget");
        reference.setServiceContract(targetContract);
        reference.setMember(SourceImpl.class.getMethod("setTarget", Target.class));
        sourceType.add(reference);

        ServiceContract<?> sourceContract = registry.introspect(Source.class);

        JavaMappedService sourceServiceDefinition = new JavaMappedService();
        sourceServiceDefinition.setUri(URI.create("#Source"));
        sourceServiceDefinition.setServiceContract(sourceContract);

        sourceType.add(sourceServiceDefinition);
        sourceType.setConstructorDefinition(new ConstructorDefinition<SourceImpl>(SourceImpl.class.getConstructor()));
        JavaImplementation sourceImpl = new JavaImplementation(SourceImpl.class, sourceType);
        ComponentDefinition<JavaImplementation> innerSourceComponentDefinition =
            new ComponentDefinition<JavaImplementation>(INNER_SOURCE_COMPONENT, sourceImpl);
        ReferenceTarget refTarget = new ReferenceTarget();
        refTarget.setReferenceName(URI.create("#targetReference"));
        refTarget.addTarget(new URI("#TargetComponentRef"));
        innerSourceComponentDefinition.add(refTarget);

        return innerSourceComponentDefinition;
    }

    private ComponentDefinition<JavaImplementation> createTargetComponentDef() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> targetType =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        targetType.setImplementationScope(Scope.COMPOSITE);

        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        ServiceContract<?> targetContract = registry.introspect(Target.class);
        targetContract.setCallbackClass(OtherTarget.class);
        targetContract.setCallbackName("OtherTarget");

        JavaMappedService serviceDefinition = new JavaMappedService();
        serviceDefinition.setUri(URI.create("Target"));
        serviceDefinition.setServiceContract(targetContract);
        serviceDefinition.setCallbackReferenceName("otherTarget");

        targetType.add(serviceDefinition);
        targetType.setConstructorDefinition(new ConstructorDefinition<TargetImpl>(TargetImpl.class.getConstructor()));
        JavaImplementation targetImpl = new JavaImplementation(TargetImpl.class, targetType);
        //URI uri = URI.create("TargetComponent");
        return new ComponentDefinition<JavaImplementation>(TARGET_COMPONENT, targetImpl);
    }

}
