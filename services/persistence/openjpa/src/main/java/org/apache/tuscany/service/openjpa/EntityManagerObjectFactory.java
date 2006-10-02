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

import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;

/**
 * Creates a JPA <code>EntityManager</code> to be injected on a component implementation instance.
 *
 * @version $Rev$ $Date$
 */
public class EntityManagerObjectFactory implements ObjectFactory<EntityManager> {

    private EntityManagerFactory emf;
    private Map props;

    public EntityManagerObjectFactory(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManagerObjectFactory(EntityManagerFactory emf, Map props) {
        this.emf = emf;
        this.props = props;
    }

    public EntityManager getInstance() throws ObjectCreationException {
        if (props == null) {
            return emf.createEntityManager();
        } else {
            return emf.createEntityManager(props);
        }
    }
}
