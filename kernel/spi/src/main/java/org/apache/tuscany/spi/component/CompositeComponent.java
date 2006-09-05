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
 * have references
 *
 * @version $Rev$ $Date$
 */
public interface CompositeComponent<T> extends Component<T>, RuntimeEventListener {
    /**
     * Registers a child of this composite.
     *
     * @param context the context to add as a child
     * @throws InvalidComponentTypeException
     */
    void register(SCAObject<?> context) throws InvalidComponentTypeException;

    /**
     * Returns the child associated with a given name
     */
    SCAObject getChild(String name);

    /**
     * Returns the children contained by the composite
     */
    List<SCAObject> getChildren();

    /**
     * Returns the services contained by the composite
     */
    List<Service> getServices();

    /**
     * Returns the service associated with the given name
     *
     * @throws ComponentNotFoundException
     */
    Service getService(String name) throws ComponentNotFoundException;

    /**
     * Returns the service instance for associated with the child registered for the given name
     */
    <T> T locateService(Class<T> serviceInterface, String serviceName);

    /**
     * Returns the references contained by the composite
     */
    List<Reference> getReferences();

    /**
     * @param scopeContainer
     */
    void setScopeContainer(ScopeContainer scopeContainer);

    /**
     * Returns the value of a Property of this composite.
     *
     * @param name the name of the Property
     * @return its value, or null if there is not such property or if it has no defined value
     */
    Document getPropertyValue(String name);
}
