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

import javax.persistence.EntityManagerFactory;

/**
 * Default implementation of the persistence unit builder.
 *
 */
public class DefaultPersistenceUnitBuilder implements PersistenceUnitBuilder {

    /** Persistence unit scanner */
    private PersistenceUnitScanner scanner = new PersistenceUnitScanner();
    
    /**
     * Builds the entity manager factory matching the unit name. All persistence.xml 
     * files available for the specified classloader is scanned for the specified 
     * persistence unit. The JPA provider API is used to create the entity manager 
     * factory.
     * 
     * @param unitName Persistence unit name.
     * @param classLoader Classloader.
     * @return Entity manager factory.
     */
    public EntityManagerFactory newEntityManagerFactory(String unitName, ClassLoader classLoader) {
        
        PersistenceUnitMetadata info = scanner.getPersistenceUnitInfo(unitName, classLoader);
        if(info == null) {
            throw new IllegalArgumentException("Persistence unit not found: " + unitName);
        }
        
        return null;
        
    }

}
