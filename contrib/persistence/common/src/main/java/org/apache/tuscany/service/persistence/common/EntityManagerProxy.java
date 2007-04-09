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
import java.lang.reflect.Method;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Synchronization;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

/**
 * Proxy for entity manager.
 *
 * @version $Rev$ $Date$
 */
public class EntityManagerProxy implements InvocationHandler {
    
    // Thread local cache of entity managers
    private ThreadLocal<EntityManager> entityManagers = new ThreadLocal<EntityManager>();

    // Properties
    private Properties prop;
    
    // Entity manager factory
    private EntityManagerFactory emf;
    
    // Transaction manager
    private TransactionManager txm;
    
    /**
     * Initializes the artifacts required to create an EM.
     * 
     * @param prop EM creation porperty overrides.
     * @param emf Entity manager factory to use.
     * @param txm Transaction manager to use.
     */
    public EntityManagerProxy(Properties prop, EntityManagerFactory emf, TransactionManager txm) {
        this.prop = prop;
        this.txm = txm;
        this.emf = emf;
    }
    
    /**
     * Proxies the entity manager.
     */
    public Object invoke(Object target, Method method, Object[] parameters) throws Throwable {
        
        EntityManager entityManager = entityManagers.get();
        if(entityManager != null) {
            entityManager = emf.createEntityManager(prop);
            entityManagers.set(entityManager);
            Transaction tx = txm.getTransaction();
            if(tx != null) {
                tx.registerSynchronization(new Synchronization() {
                    public void afterCompletion(int arg0) {
                        entityManagers.set(null);
                    }
                    public void beforeCompletion() {
                    }
                });
            }
        }
        return method.invoke(entityManager, parameters);
    }

}
