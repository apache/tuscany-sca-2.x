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
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.extension.SystemAtomicComponentExtension;

/**
 * A system service that manages a JPA <code>EntityManagerFactory</code>. This service is used by {@link
 * EntityManagerProcessor} to inject an <code>EntityManager<code> on a constructor parameter, field, or method of an
 * implementation instance. Since a JPA persistence context is specific to an application, this system service is scoped
 * to the application composite. That is, it is contained as a system child of the application composite.
 *
 * @version $Rev$ $Date$
 */
public class JPAAtomicComponent extends SystemAtomicComponentExtension {
    private EntityManagerFactory factory;
    private String persistenceUnit;
    private Map<Object, Object> configProps;

    public JPAAtomicComponent(String name,
                              CompositeComponent parent,
                              ScopeContainer scopeContainer,
                              String persistenceUnit,
                              Map<Object, Object> configProps,
                              int initLevel) {
        super(name, parent, initLevel);
        assert persistenceUnit != null;
        this.persistenceUnit = persistenceUnit;
        this.configProps = configProps;
    }

    public Object createInstance() throws ObjectCreationException {
        if (configProps != null) {
            factory = Persistence.createEntityManagerFactory(persistenceUnit, configProps);
        } else {
            factory = Persistence.createEntityManagerFactory(persistenceUnit);
        }
        return factory;
    }

    public Object getTargetInstance() throws TargetResolutionException {
        return factory;
    }


}
