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
package org.apache.tuscany.core.implementation.system.generator;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.generator.ComponentGenerator;
import org.apache.tuscany.spi.generator.GenerationException;
import org.apache.tuscany.spi.generator.GeneratorContext;
import org.apache.tuscany.spi.generator.GeneratorRegistry;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireSourceDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireTargetDefinition;

import org.apache.tuscany.core.implementation.system.model.SystemImplementation;
import org.apache.tuscany.core.implementation.system.model.SystemPhysicalComponentDefinition;
import org.apache.tuscany.core.model.physical.instancefactory.InjectionSiteMapping;
import org.apache.tuscany.core.model.physical.instancefactory.InjectionSource;
import org.apache.tuscany.core.model.physical.instancefactory.MemberSite;
import org.apache.tuscany.core.model.physical.instancefactory.ReflectiveIFProviderDefinition;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalWireSourceDefinition;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalWireTargetDefinition;

/**
 * @version $Rev$ $Date$
 */
@EagerInit
public class SystemPhysicalComponentGenerator implements ComponentGenerator<ComponentDefinition<SystemImplementation>> {


    public SystemPhysicalComponentGenerator(@Reference GeneratorRegistry registry) {
        registry.register(SystemImplementation.class, this);
    }

    @SuppressWarnings({"unchecked"})
    public void generate(ComponentDefinition<SystemImplementation> definition, GeneratorContext context) {
        SystemImplementation implementation = definition.getImplementation();
        // TODO not a safe cast
        PojoComponentType<JavaMappedService, JavaMappedReference, Property<?>> type = implementation.getComponentType();
        SystemPhysicalComponentDefinition pDefinition = new SystemPhysicalComponentDefinition();
        pDefinition.setComponentId(definition.getUri());
        pDefinition.setScope(type.getImplementationScope());
        // TODO get classloader id
        ReflectiveIFProviderDefinition provider = new ReflectiveIFProviderDefinition();
        Method destroyMethod = type.getDestroyMethod();
        if (destroyMethod != null) {
            provider.setDestroyMethod(destroyMethod.toString());
        }
        Method initMethod = type.getInitMethod();
        if (initMethod != null) {
            provider.setInitMethod(initMethod.toString());
        }
        provider.setImplementationClass(implementation.getImplementationClass().getName());
        // TODO ctor arguments
        // TODO set CDI source for ref, props, and callbacks
        Map<String, JavaMappedReference> references = type.getReferences();
        for (Map.Entry<String, JavaMappedReference> entry : references.entrySet()) {
            JavaMappedReference reference = entry.getValue();
            Member member = reference.getMember();
            InjectionSource source = new InjectionSource();
            source.setName(entry.getKey());
            source.setValueType(InjectionSource.ValueSourceType.REFERENCE);
            MemberSite memberSite = new MemberSite();
            memberSite.setName(member.getName());
            if (member instanceof Method) {
                memberSite.setElementType(ElementType.METHOD);
            } else if (member instanceof Field) {
                memberSite.setElementType(ElementType.FIELD);
            } else {
                throw new AssertionError("Illegal injection type");
            }

            InjectionSiteMapping mapping = new InjectionSiteMapping();
            mapping.setSource(source);
            mapping.setSite(memberSite);
            provider.addInjectionSite(mapping);
        }

        pDefinition.setInstanceFactoryProviderDefinition(provider);
        context.getPhysicalChangeSet().addComponentDefinition(pDefinition);
    }

    public PhysicalWireSourceDefinition generateWireSource(ComponentDefinition<SystemImplementation> definition,
                                                           ReferenceDefinition serviceDefinition,
                                                           GeneratorContext context)
        throws GenerationException {
        JavaPhysicalWireSourceDefinition wireDefinition = new JavaPhysicalWireSourceDefinition();
        wireDefinition.setUri(definition.getUri());
        wireDefinition.setOptimizable(true);
        return wireDefinition;
    }

    public PhysicalWireTargetDefinition generateWireTarget(ComponentDefinition<SystemImplementation> definition,
                                                           ServiceDefinition serviceDefinition,
                                                           GeneratorContext context)
        throws GenerationException {
        JavaPhysicalWireTargetDefinition wireDefinition = new JavaPhysicalWireTargetDefinition();
        wireDefinition.setUri(definition.getUri());
        return wireDefinition;
    }

}
