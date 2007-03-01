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
import java.net.URI;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceProperty;
import javax.transaction.TransactionManager;

import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.RegistrationException;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.java.InterfaceJavaIntrospector;
import org.apache.tuscany.spi.implementation.java.AbstractPropertyProcessor;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorService;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.ProcessingException;
import org.apache.tuscany.spi.model.ServiceContract;

import org.apache.tuscany.core.component.ComponentManager;

/**
 * Annotation processor for injecting <code>PersistenceUnit</code> annotations on properties.
 *
 * @version $Rev$ $Date$
 */
public class PersistenceContextProcessor extends AbstractPropertyProcessor<PersistenceContext> {

    /**
     * Transaction Manager
     */
    private TransactionManager transactionManager;
    private ComponentManager componentManager;
    private InterfaceJavaIntrospector introspector;

    /**
     * Persistence unit builder
     */
    private PersistenceUnitBuilder builder = new DefaultPersistenceUnitBuilder();

    /**
     * Injects the implementation processor service.
     *
     * @param service Implementation processor service.
     */
    public PersistenceContextProcessor(@Reference ImplementationProcessorService service) {
        super(PersistenceContext.class, service);
    }

    @Reference
    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Reference
    public void setComponentManager(ComponentManager componentManager) {
        this.componentManager = componentManager;
    }

    @Reference
    public void setIntrospector(InterfaceJavaIntrospector introspector) {
        this.introspector = introspector;
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
    protected <T> void initProperty(JavaMappedProperty<T> property,
                                    PersistenceContext annotation,
                                    DeploymentContext context) throws ProcessingException {

        String unitName = annotation.unitName();
        URI unitUri = context.getComponentId().resolve(unitName);
        EntityManagerFactory emf = (EntityManagerFactory) componentManager.getComponent(unitUri);

        if (emf == null) {
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
            if (type == PersistenceContextType.TRANSACTION) {

                Properties props = new Properties();
                for (PersistenceProperty property : annotation.properties()) {
                    props.put(property.name(), property.value());
                }

                Class[] interfaces = new Class[]{EntityManager.class};
                InvocationHandler handler = new EntityManagerProxy(props, emf, transactionManager);
                EntityManager em =
                    (EntityManager) Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, handler);
                return em;

            } else {
                throw new UnsupportedOperationException("Extended persistence contexts not supported");
            }

        }

    }

}
