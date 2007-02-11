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
package org.apache.tuscany.spi.component;

import java.util.List;

import org.w3c.dom.Document;

import org.apache.tuscany.spi.event.RuntimeEventListener;

/**
 * The runtime instantiation of an SCA composite component. Composites may contain child components, offer services, and
 * have references. Children are contained in two namespaces, an application namespace for end-user components deployed
 * to a runtime, and a system namespace for components that provide system services.
 *
 * @version $Rev$ $Date$
 */
public interface CompositeComponent extends Component, RuntimeEventListener {

    /**
     * Returns the value of a Property of this composite.
     *
     * @param name the name of the Property
     * @return its value, or null if there is not such property or if it has no defined value
     */
    Document getPropertyValue(String name);

    /**
     * Registers a service of this composite.
     *
     * @param service the service to add as a child
     * @throws RegistrationException
     */
    void register(Service service) throws RegistrationException;

    /**
     * Registers a reference of this composite.
     *
     * @param reference the reference to add as a child
     * @throws RegistrationException
     */
    void register(Reference reference) throws RegistrationException;

    /**
     * Returns the services for the component
     *
     * @return the services for the component
     */
    List<Service> getServices();

    /**
     * Returns the references for the component
     *
     * @return the references for the component
     */
    List<Reference> getReferences();

}
