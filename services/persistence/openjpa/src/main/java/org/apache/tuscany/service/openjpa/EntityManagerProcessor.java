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
package org.apache.tuscany.service.openjpa;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Member;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorExtension;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;

/**
 * @version $Rev$ $Date$
 */
public class EntityManagerProcessor extends ImplementationProcessorExtension {

    public void visitMethod(CompositeComponent parent,
                            Method method,
                            PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                            DeploymentContext context) throws ProcessingException {

        if (method.getAnnotation(PersistenceContext.class) == null) {
            return;
        }
        // TODO process other JPA annotations
        addProperty(parent, method, type);
    }

    public void visitField(CompositeComponent parent, Field field,
                           PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                           DeploymentContext context) throws ProcessingException {
        if (field.getAnnotation(PersistenceContext.class) == null) {
            return;
        }
        // TODO process other JPA annotations
        addProperty(parent, field, type);
    }

    private void addProperty(CompositeComponent parent,
                             Member member,
                             PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type)
        throws ProcessingException {
        EntityManagerFactory emf = parent.resolveInstance(EntityManagerFactory.class);
        if (emf == null) {
            ProcessingException e = new ProcessingException("EntityManagerFactory not configured in composite");
            e.setIdentifier(parent.getName());
            throw e;
        }
        JavaMappedProperty<EntityManager> prop = new JavaMappedProperty<EntityManager>();
        prop.setMember(member);
        prop.setName(member.getName());
        prop.setDefaultValueFactory(new EntityManagerObjectFactory(emf));
        type.add(prop);
    }
}
