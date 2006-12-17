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
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.ComponentRegistrationException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SystemAtomicComponent;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorExtension;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;
import org.apache.tuscany.spi.implementation.java.Resource;

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

    public void visitField(CompositeComponent parent,
                           Field field,
                           PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                           DeploymentContext context) throws ProcessingException {

        PersistenceUnit annotation = field.getAnnotation(PersistenceUnit.class);
        if (annotation == null) {
            return;
        }
        String unitName = annotation.unitName();

        SystemAtomicComponent component = (SystemAtomicComponent) parent.getSystemChild(unitName);
        EntityManagerFactory emf;
        if (component == null) {
            emf = builder.newEntityManagerFactory(unitName, context.getClassLoader());
            try {
                parent.registerJavaObject(unitName, EntityManagerFactory.class, emf);
            } catch (ComponentRegistrationException e) {
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
        Resource resource = new Resource();
        resource.setObjectFactory(factory);
        resource.setMember(field);
        resource.setType(field.getType());
        resource.setName(unitName);
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
