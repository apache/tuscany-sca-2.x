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

package org.apache.tuscany.sca.runtime;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.context.CompositeContext;

/**
 * Start/stop a composite
 * 
 * @version $Rev$ $Date$
 */
public interface CompositeActivator {
    /**
     * Activate a composite
     * @param compositeContext The context of the Node
     * @param composite
     */
    void activate(CompositeContext compositeContext, Composite composite) throws ActivationException;

    /**
     * Activate a component reference
     * @param compositeContext The context of the Node
     * @param component
     * @param ref
     */
    void activate(CompositeContext compositeContext, RuntimeComponent component, RuntimeComponentReference ref);

    /**
     * Activate a component reference
     * @param compositeContext The context of the Node
     * @param component
     * @param ref
     */
    void activate(CompositeContext compositeContext, RuntimeComponent component, RuntimeComponentService service);
    
    /**
     * De-activate a component reference
     * @param component
     * @param ref
     */
    void deactivate(RuntimeComponent component, RuntimeComponentReference ref);

    /**
     * De-activate a component reference
     * @param component
     * @param ref
     */
    void deactivate(RuntimeComponent component, RuntimeComponentService service);

    /**
     * Stop a composite
     * @param composite
     */
    void deactivate(Composite composite) throws ActivationException;

    /**
     * Start a component reference
     * @param compositeContext The context of the Node
     * @param component
     * @param ref
     */
    void start(CompositeContext compositeContext, RuntimeComponent component, RuntimeComponentReference ref);
   

    /**
     * Start a component
     * @param component
     */
    void start(CompositeContext compositeContext, Component component) throws ActivationException;

    /**
     * Stop a component
     * @param component
     */
    void stop(CompositeContext compositeContext, Component component) throws ActivationException;

    /**
     * Start components in a composite
     * @param composite
     */
    void start(CompositeContext compositeContext, Composite composite) throws ActivationException;

    /**
     * Stop components in a composite
     * @param composite
     */
    void stop(CompositeContext compositeContext, Composite composite) throws ActivationException;

    /**
     * Activate an endpoint
     * @param compositeContext
     * @param endpoint
     */
    void activate(CompositeContext compositeContext, RuntimeEndpoint endpoint);
    
    /**
     * Activate an endpoint reference
     * @param compositeContext
     * @param endpointReference
     */
    void activate(CompositeContext compositeContext, RuntimeEndpointReference endpointReference);
    
    /**
     * Deactivate an endpoint
     * @param endpoint
     */
    void deactivate(RuntimeEndpoint endpoint);
    
    /**
     * Deactivate an endpoint reference
     * @param endpointReference
     */
    void deactivate(RuntimeEndpointReference endpointReference);
    
    /**
     * Start an endpoint
     * @param compositeContext
     * @param endpoint
     */
    void start(CompositeContext compositeContext, RuntimeEndpoint endpoint);
    
    /**
     * Start an endpoint reference
     * @param compositeContext
     * @param endpointReference
     */
    void start(CompositeContext compositeContext, RuntimeEndpointReference endpointReference);
    
    /**
     * Stop an endpoint
     * @param endpoint
     */
    void stop(RuntimeEndpoint endpoint);
    
    /**
     * Stop an endpoint reference
     * @param endpointReference
     */
    void stop(RuntimeEndpointReference endpointReference);    
}
