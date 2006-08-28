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

import org.apache.tuscany.core.builder.BuilderRegistryImpl;
import org.apache.tuscany.core.deployer.RootDeploymentContext;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.core.implementation.java.JavaComponentBuilder;
import org.apache.tuscany.core.implementation.java.JavaImplementation;
import org.apache.tuscany.core.implementation.java.mock.components.Source;
import org.apache.tuscany.core.implementation.java.mock.components.SourceImpl;
import org.apache.tuscany.core.implementation.java.mock.components.Target;
import org.apache.tuscany.core.implementation.java.mock.components.TargetImpl;
import org.apache.tuscany.core.wire.jdk.JDKWireService;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
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

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $$Rev$$ $$Date$$
 */
public class CompositeBuilderTestCase extends MockObjectTestCase {
    private DeploymentContext deploymentContext;

    protected void setUp() throws Exception {
        super.setUp();
        ScopeContainer mock = createMock();
        deploymentContext = new RootDeploymentContext(null, null, mock, null);
    }

    @SuppressWarnings("unchecked")
    public void testBuild() throws Exception {
        CompositeComponent parent = new CompositeComponentImpl(null, null, null, null);
        
        CompositeBuilder builder = new CompositeBuilder();
        WireService wireService = new JDKWireService();
        builder.setWireService(wireService);
        BuilderRegistryImpl builderRegistry = new BuilderRegistryImpl();
        builderRegistry.setWireService(wireService);
        builderRegistry.register(JavaImplementation.class, new JavaComponentBuilder());
        builderRegistry.register(CompositeImplementation.class, builder);
        CompositeBindlessBuilder bindlessBuilder = new CompositeBindlessBuilder();
        bindlessBuilder.setWireService(wireService);
        builderRegistry.register(bindlessBuilder);
        builder.setBuilderRegistry(builderRegistry);
        CompositeComponent component =
            (CompositeComponent)builder.build(parent, createTopComponentDef(), deploymentContext);
        deploymentContext.getModuleScope().start();
        component.start();
        CompositeComponent sourceComponent = (CompositeComponent)component.getChild("SourceComponent");
        Source source = (Source)sourceComponent.getServiceInstance("InnerSourceService");
        assertNotNull(source);
        component.stop();
    }
    
    private ComponentDefinition createTopComponentDef() throws Exception {
        
        CompositeComponentType outerType = new CompositeComponentType();
        outerType.add(createSourceComponentDef());
        outerType.add(createTargetComponentDef());
        
        CompositeImplementation outerImpl = new CompositeImplementation();
        outerImpl.setComponentType(outerType);
        
        ComponentDefinition<CompositeImplementation> topComponentDefinition =
            new ComponentDefinition<CompositeImplementation>(outerImpl);
        
        return topComponentDefinition;
    }
    
    private ComponentDefinition createSourceComponentDef() throws Exception {
        
        CompositeComponentType innerType = new CompositeComponentType();
        innerType.add(createInnerSourceComponentDef());
        ReferenceDefinition reference = new ReferenceDefinition();
        reference.setName("targetComponentRef");
        ServiceContract targetContract = new JavaServiceContract(Target.class);
        reference.setServiceContract(targetContract);
        innerType.add(reference);
        BindlessServiceDefinition service = new BindlessServiceDefinition();
        service.setName("InnerSourceService");
        ServiceContract sourceContract = new JavaServiceContract(Source.class);
        service.setServiceContract(sourceContract);
        service.setTarget(new URI("InnerSourceComponent"));
        innerType.add(service);
        
        CompositeImplementation innerImpl = new CompositeImplementation();
        innerImpl.setComponentType(innerType);
        
        ComponentDefinition<CompositeImplementation> sourceComponentDefinition =
            new ComponentDefinition<CompositeImplementation>("SourceComponent", innerImpl);
        ReferenceTarget refTarget = new ReferenceTarget();
        refTarget.setReferenceName("targetComponentRef");
        refTarget.addTarget(new URI("TargetComponent"));
        sourceComponentDefinition.add(refTarget);
        
        return sourceComponentDefinition;
    }
    
    private ComponentDefinition createInnerSourceComponentDef() throws Exception {

        PojoComponentType sourceType = new PojoComponentType();
        sourceType.setImplementationScope(Scope.MODULE);
        JavaMappedReference reference = new JavaMappedReference();
        reference.setName("targetReference");
        ServiceContract targetContract = new JavaServiceContract(Target.class);
        reference.setServiceContract(targetContract);
        reference.setMember(SourceImpl.class.getMethod("setTarget", Target.class));
        sourceType.add(reference);

        ServiceContract sourceContract = new JavaServiceContract(Source.class);
        ServiceDefinition sourceServiceDefinition = new JavaMappedService();
        sourceServiceDefinition.setName("Source");
        sourceServiceDefinition.setServiceContract(sourceContract);

        sourceType.add(sourceServiceDefinition);
        sourceType.setConstructorDefinition(new ConstructorDefinition(SourceImpl.class.getConstructor((Class[]) null)));
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
    
    private ComponentDefinition createTargetComponentDef() throws Exception {
        
        PojoComponentType targetType = new PojoComponentType();
        targetType.setImplementationScope(Scope.MODULE);

        ServiceContract targetContract = new JavaServiceContract(Target.class);
        ServiceDefinition targetServiceDefinition = new JavaMappedService();
        targetServiceDefinition.setName("Target");
        targetServiceDefinition.setServiceContract(targetContract);

        targetType.add(targetServiceDefinition);
        targetType.setConstructorDefinition(new ConstructorDefinition(TargetImpl.class.getConstructor((Class[]) null)));
        JavaImplementation targetImpl = new JavaImplementation();
        targetImpl.setComponentType(targetType);
        targetImpl.setImplementationClass(TargetImpl.class);
        ComponentDefinition<JavaImplementation> targetComponentDefinition =
            new ComponentDefinition<JavaImplementation>("TargetComponent", targetImpl);
        
        return targetComponentDefinition;
    }

    private ScopeContainer createMock() {
        Mock mock = mock(ScopeContainer.class);
        mock.expects(once()).method("start");
        mock.expects(atLeastOnce()).method("register");
        mock.expects(atLeastOnce()).method("getScope").will(returnValue(Scope.MODULE));
        return (ScopeContainer) mock.proxy();
    }
}
