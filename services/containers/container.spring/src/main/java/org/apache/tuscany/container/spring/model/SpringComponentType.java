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
package org.apache.tuscany.container.spring.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

import org.springframework.context.support.AbstractApplicationContext;

/**
 * Component type information for a Spring composite component implementation type. A component type is associated with
 * a Spring application context
 *
 * @version $Rev$ $Date$
 */
public class SpringComponentType<S extends ServiceDefinition,
    R extends ReferenceDefinition,
    P extends Property<?>> extends CompositeComponentType<S, R, P> {

    private AbstractApplicationContext applicationContext;
    private Map<String, Class<?>> serviceTypes = new HashMap<String, Class<?>>();
    private boolean exposeAllBeans;

    public SpringComponentType(AbstractApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public SpringComponentType() {
    }

    // FIXME andyp@bea.com -- this is a component type it should NOT contain bean instances!

    /**
     * Returns the application context for the component type
     */
    public AbstractApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(AbstractApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Returns a collection of service types defined by <code>sca:service</code> elements in a Spring configuration.
     * Service types define beans that can be targets of services defined in the SCDL Spring composite declaration. For
     * each service type, there must be a corresponding service definition as part of the Spring composite declaration
     * per the SCA specification.
     */
    public Map<String, Class<?>> getServiceTypes() {
        return serviceTypes;
    }

    /**
     * Adds a service type to the component declaration defined by <code>sca:service</code> elements in a Spring
     * configuration.
     *
     * @param name the name of the service
     * @param type the interface type of the target bean
     */
    public void addServiceType(String name, Class<?> type) {
        this.serviceTypes.put(name, type);
    }

    /**
     * Returns true if all beans in the Spring application context may be service targets or false if service types are
     * defined
     */
    public boolean isExposeAllBeans() {
        return exposeAllBeans;
    }

    /**
     * Sets if all beans in the Spring application context may be service targets or false if service types are defined
     */
    public void setExposeAllBeans(boolean exposeAllBeans) {
        this.exposeAllBeans = exposeAllBeans;
    }

}
