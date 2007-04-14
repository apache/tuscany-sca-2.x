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

import java.net.URI;
import java.util.List;

import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.spi.event.RuntimeEventListener;

/**
 * Responsible for tracking and managing the component tree for a runtime instance. The tree corresponds to components
 * deployed to the current runtime and hence may be sparse in comparison to the assembly component hierarchy for the SCA
 * domain as parents and children may be distributed to different runtimes.
 *
 * @version $Rev$ $Date$
 */
public interface ComponentManager extends RuntimeEventListener {

    /**
     * Registers a component which will be managed by the runtime
     *
     * @param component the component
     * @throws RegistrationException
     */
    void register(Component component) throws RegistrationException;

    /**
     * Deregisters a component
     *
     * @param component the component to deregister
     * @throws RegistrationException
     */
    void unregister(Component component) throws RegistrationException;

    /**
     * Register a simple Java Object as a system component. This is primarily intended for use by bootstrap code to
     * create the initial configuration components.
     *
     * @param uri      the uri of the resulting component
     * @param service  the service contract the component should expose
     * @param instance the Object that will become the component's implementation
     * @throws RegistrationException
     */
    <I> void registerJavaObject(URI uri, ComponentService service, I instance)
        throws RegistrationException;

    /**
     * Register a simple Java Object as a system component. This is primarily intended for use by bootstrap code to
     * create the initial configuration components.
     *
     * @param uri      the name of the resulting component
     * @param services the service contracts the component should expose
     * @param instance the Object that will become the component's implementation
     * @throws RegistrationException
     */
    <I> void registerJavaObject(URI uri, List<ComponentService> services, I instance)
        throws RegistrationException;

    /**
     * Returns the component with the given URI
     *
     * @param uri the component URI
     * @return the component or null if not found
     */
    Component getComponent(URI uri);
    
    List<SCAObject> getSCAObjects();
    void add(SCAObject object, Object model);
    <T extends SCAObject> T getSCAObject(Class<T> objectType, Object model);
    <T> T getModelObject(Class<T> modelType, SCAObject object);

}
