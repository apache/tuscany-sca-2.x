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

import java.lang.reflect.Member;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.implementation.java.AbstractPropertyProcessor;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorService;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.ProcessingException;
import org.apache.tuscany.spi.wire.InboundWire;


/**
 * Evaluates constructors, methods, and fields annotated with {@link PersistenceContext }, creating a mapped property
 * that will inject an {@link EntityManager} on an implementation instance.
 *
 * @version $Rev$ $Date$
 */
public class EntityManagerProcessor extends AbstractPropertyProcessor<PersistenceContext> {

    public EntityManagerProcessor(@Autowire ImplementationProcessorService service) {
        super(PersistenceContext.class, service);
    }

    protected String getName(PersistenceContext annotation) {
        String name = annotation.unitName();
        if (name == null) {
            return "_defaultJPAPersistenceUnit";
        }
        return name;
    }

    protected <T> JavaMappedProperty<T> createProperty(String name,
                                                       Class<T> javaType,
                                                       Member member) throws ProcessingException {
        if (!EntityManager.class.equals(javaType)) {
            throw new InvalidInjectionSite("Injection site must by of type " + EntityManager.class.getName(), name);
        }
        return super.createProperty(name, javaType, member);
    }

    @SuppressWarnings("unchecked")
    protected <T> void initProperty(JavaMappedProperty<T> property,
                                    PersistenceContext annotation,
                                    CompositeComponent parent,
                                    DeploymentContext context) throws ProcessingException {
        EntityManagerFactory emf;
        try {
            InboundWire wire = parent.resolveSystemAutowire(EntityManagerFactory.class);
            if (wire == null) {
                throw new EntityManagerFactoryNotConfiguredException();
            }
            Object o = wire.getTargetService();
            assert o instanceof EntityManagerFactory;
            emf = (EntityManagerFactory) o;
        } catch (TargetException e) {
            throw new ProcessingException(e);
        }
        ObjectFactory factory = new EntityManagerObjectFactory(emf);
        property.setDefaultValueFactory(factory);
    }

}
