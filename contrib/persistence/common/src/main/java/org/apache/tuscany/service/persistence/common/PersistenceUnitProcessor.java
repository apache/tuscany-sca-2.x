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
package org.apache.tuscany.service.persistence.common;

import java.lang.reflect.Field;
import java.net.URI;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.RegistrationException;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.java.InterfaceJavaIntrospector;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorExtension;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;
import org.apache.tuscany.spi.implementation.java.Resource;
import org.apache.tuscany.spi.model.ServiceContract;

import org.apache.tuscany.core.component.ComponentManager;

/**
 * Annotation processor for injecting <code>PersistenceUnit</code> annotations on properties.
 *
 * @version $Rev$ $Date$
 */
public class PersistenceUnitProcessor extends ImplementationProcessorExtension {
    /**
     * Persistence unit builder
     */
    private PersistenceUnitBuilder builder = new DefaultPersistenceUnitBuilder();
    private ComponentManager componentManager;
    private InterfaceJavaIntrospector introspector;

    @Reference
    public void setComponentManager(ComponentManager componentManager) {
        this.componentManager = componentManager;
    }

    @Reference
    public void setIntrospector(InterfaceJavaIntrospector introspector) {
        this.introspector = introspector;
    }


    @SuppressWarnings({"unchecked"})
    public void visitField(Field field,
                           PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                           DeploymentContext context) throws ProcessingException {

        PersistenceUnit annotation = field.getAnnotation(PersistenceUnit.class);
        if (annotation == null) {
            return;
        }
        String unitName = annotation.unitName();
        URI unitUri = context.getComponentId().resolve(unitName);
        AtomicComponent component = (AtomicComponent) componentManager.getComponent(unitUri);
        EntityManagerFactory emf;
        if (component == null) {
            emf = builder.newEntityManagerFactory(unitName, context.getClassLoader());
            try {
                ServiceContract<EntityManagerFactory> contract =
                    (ServiceContract) introspector.introspect(EntityManagerFactory.class);
                componentManager.registerJavaObject(unitUri, contract, emf);
            } catch (RegistrationException e) {
                throw new ProcessingException(e);
            } catch (InvalidServiceContractException e) {
                throw new ProcessingException(e);
            }
        } else {
            try {
                emf = (EntityManagerFactory) component.getTargetInstance();
            } catch (TargetException e) {
                throw new ProcessingException(e);
            }
        }

        ObjectFactory factory = new EmfObjectFactory(emf);
        Resource resource = new Resource(unitName, field.getType(), field);
        resource.setObjectFactory(factory);
        type.add(resource);
    }

    private class EmfObjectFactory implements ObjectFactory<EntityManagerFactory> {

        private EntityManagerFactory emf;

        public EmfObjectFactory(EntityManagerFactory emf) {
            this.emf = emf;
        }

        public EntityManagerFactory getInstance() {
            return emf;
        }

    }

}
