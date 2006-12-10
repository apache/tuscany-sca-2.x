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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceProperty;
import javax.transaction.TransactionManager;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.implementation.java.AbstractPropertyProcessor;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorService;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;

/**
 * Annotation processor for injecting <code>PersistenceUnit</code> 
 * annotations on properties.
 *
 */
public class PersistenceContextProcessor extends AbstractPropertyProcessor<PersistenceContext> {
    
    /** Transaction Manager */
    @Autowire
    private TransactionManager transactionManager;

    /** Persistence unit builder */
    private PersistenceUnitBuilder builder = new DefaultPersistenceUnitBuilder();

    /**
     * Injects the implementation processor service.
     * @param service Implementation processor service.
     */
    public PersistenceContextProcessor(@Autowire ImplementationProcessorService service) {
        super(PersistenceContext.class, service);
    }

    /**
     * Defaults to the field name.
     */
    @Override
    protected String getName(PersistenceContext persistenceContext) {
        return null;
    }

    /**
     * Initializes the property.
     */
    @SuppressWarnings("unchecked")
    protected <T> void initProperty(JavaMappedProperty<T> property, PersistenceContext annotation, CompositeComponent parent, DeploymentContext context) {

        String unitName = annotation.unitName();
        EntityManagerFactory emf = (EntityManagerFactory) parent.getSystemChild(unitName);

        if (emf == null) {
            emf = builder.newEntityManagerFactory(unitName, context.getClassLoader());
            parent.registerJavaObject(unitName, EntityManagerFactory.class, emf);
        }
        ObjectFactory factory = new EmObjectFactory(emf, annotation);
        property.setDefaultValueFactory(factory);

    }

    private class EmObjectFactory implements ObjectFactory<EntityManager> {
        
        private EntityManagerFactory emf;
        private PersistenceContext annotation;

        public EmObjectFactory(EntityManagerFactory emf, PersistenceContext annotation) {
            this.emf = emf;
            this.annotation = annotation;
        }

        public EntityManager getInstance() {
            
            PersistenceContextType type = annotation.type();
            if(type == PersistenceContextType.TRANSACTION) {
                
                Properties props = new Properties();
                for(PersistenceProperty property : annotation.properties()) {
                    props.put(property.name(), property.value());
                }
                
                Class[] interfaces = new Class[] {EntityManager.class};
                InvocationHandler handler = new EntityManagerProxy(props, emf, transactionManager);
                EntityManager em = (EntityManager)Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, handler);
                return em;
                
            } else {
                throw new UnsupportedOperationException("Extended persistence contexts not supported");
            }
            
        }
        
    }

}
