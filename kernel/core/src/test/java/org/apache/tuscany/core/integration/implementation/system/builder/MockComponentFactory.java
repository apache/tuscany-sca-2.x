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
package org.apache.tuscany.core.integration.implementation.system.builder;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;

import org.apache.tuscany.core.implementation.system.model.SystemBindingDefinition;
import org.apache.tuscany.core.implementation.system.model.SystemImplementation;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;

/**
 * @version $$Rev$$ $$Date$$
 */
public final class MockComponentFactory {

    private MockComponentFactory() {
    }

    /**
     * Creates a component named "source" with a reference to target/Target
     */
    public static ComponentDefinition<SystemImplementation> createSourceWithTargetReference()
        throws NoSuchMethodException {
        SystemImplementation impl = new SystemImplementation();
        PojoComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> componentType =
            new PojoComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        componentType.setImplementationScope(Scope.COMPOSITE);
        componentType
            .setConstructorDefinition(
                new ConstructorDefinition<SourceImpl>(SourceImpl.class.getConstructor((Class[]) null)));
        JavaMappedReference reference;
        try {
            reference = new JavaMappedReference();
            reference.setName("target");
            reference.setMember(SourceImpl.class.getMethod("setTarget", Target.class));
            JavaServiceContract contract = new JavaServiceContract();
            contract.setInterfaceClass(Target.class);
            reference.setServiceContract(contract);
            componentType.add(reference);
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
        impl.setComponentType(componentType);
        impl.setImplementationClass(SourceImpl.class);
        ComponentDefinition<SystemImplementation> sourceComponentDefinition =
            new ComponentDefinition<SystemImplementation>(impl);
        sourceComponentDefinition.setName("source");

        ReferenceTarget referenceTarget = new ReferenceTarget();
        referenceTarget.setReferenceName("target");
        try {
            referenceTarget.addTarget(new URI("target/Target"));
        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        }
        sourceComponentDefinition.add(referenceTarget);
        return sourceComponentDefinition;
    }

    /**
     * Creates a component named "source" with an autowire reference to {@link Target}
     */
    public static ComponentDefinition<SystemImplementation> createSourceWithTargetAutowire() {
        SystemImplementation impl = new SystemImplementation();
        PojoComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> componentType =
            new PojoComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        componentType.setImplementationScope(Scope.COMPOSITE);
        JavaMappedReference reference;
        try {
            reference = new JavaMappedReference();
            reference.setName("target");
            reference.setMember(SourceImpl.class.getMethod("setTarget", Target.class));
            reference.setAutowire(true);
            ServiceContract<?> contract = new JavaServiceContract();
            contract.setInterfaceClass(Target.class);
            reference.setServiceContract(contract);
            componentType.add(reference);
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
        impl.setComponentType(componentType);
        impl.setImplementationClass(SourceImpl.class);
        ComponentDefinition<SystemImplementation> sourceComponentDefinition =
            new ComponentDefinition<SystemImplementation>(impl);
        sourceComponentDefinition.setName("source");

        ReferenceTarget referenceTarget = new ReferenceTarget();
        referenceTarget.setReferenceName("target");
        sourceComponentDefinition.add(referenceTarget);
        return sourceComponentDefinition;
    }

    /**
     * Creates a component named "target" with a service named "Target"
     */
    public static ComponentDefinition<SystemImplementation> createTarget() throws NoSuchMethodException {
        SystemImplementation impl = new SystemImplementation();
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> componentType =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        componentType.setImplementationScope(Scope.COMPOSITE);
        componentType
            .setConstructorDefinition(
                new ConstructorDefinition<TargetImpl>(TargetImpl.class.getConstructor((Class[]) null)));
        JavaMappedService targetServiceDefinition = new JavaMappedService();
        targetServiceDefinition.setName("Target");
        ServiceContract<?> contract = new JavaServiceContract();
        contract.setInterfaceClass(Target.class);
        targetServiceDefinition.setServiceContract(contract);
        componentType.add(targetServiceDefinition);
        impl.setComponentType(componentType);
        impl.setImplementationClass(TargetImpl.class);
        ComponentDefinition<SystemImplementation> targetComponentDefinition =
            new ComponentDefinition<SystemImplementation>(impl);
        targetComponentDefinition.setName("target");
        return targetComponentDefinition;
    }


    public static BoundReferenceDefinition<SystemBindingDefinition> createBoundReference() {
        SystemBindingDefinition binding = new SystemBindingDefinition();
        BoundReferenceDefinition<SystemBindingDefinition> referenceDefinition = new BoundReferenceDefinition<SystemBindingDefinition>();
        referenceDefinition.setBinding(binding);
        referenceDefinition.setName("target");
        ServiceContract<?> contract = new JavaServiceContract();
        contract.setInterfaceClass(Target.class);
        referenceDefinition.setServiceContract(contract);
        return referenceDefinition;
    }

    /**
     * Creates a bound service with the name "service" that is configured to be wired to a target named "target/Target"
     */
    public static BoundServiceDefinition<SystemBindingDefinition> createBoundService() {
        SystemBindingDefinition binding = new SystemBindingDefinition();
        BoundServiceDefinition<SystemBindingDefinition> serviceDefinition = new BoundServiceDefinition<SystemBindingDefinition>();
        serviceDefinition.setBinding(binding);
        serviceDefinition.setName("serviceDefinition");
        ServiceContract<?> contract = new JavaServiceContract();
        contract.setInterfaceClass(Target.class);
        serviceDefinition.setServiceContract(contract);
        try {
            serviceDefinition.setTarget(new URI("target/Target"));
        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        }
        return serviceDefinition;
    }


}
