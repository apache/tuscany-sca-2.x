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

import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.oasisopen.sca.ComponentContext;
import org.oasisopen.sca.ServiceReference;

/**
 * @version $Rev$ $Date$
 */
public interface RuntimeComponentContext extends ComponentContext {
    /**
     * Activate the reference (creating runtime wires)
     * @param reference
     */
    void start(RuntimeComponentReference reference);

    /**
     * Get the CallableReference for a given component reference
     * @param <B>
     * @param businessInterface The business interface
     * @param endpointReference The endpointReference to be used
     * @return A service reference representing the wire
     */
    <B> ServiceReference<B> getServiceReference(Class<B> businessInterface,
                                                RuntimeEndpointReference endpointReference);


    /**
     * Create a CallableReference for the given component service
     * @param <B>
     * @param businessInterface
     * @param component
     * @param service
     * @return
     */
    <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, RuntimeEndpoint endpoint);

    /**
     * @param <B>
     * @param businessInterface
     * @param service
     * @return
     */
    <B> ServiceReference<B> createSelfReference(Class<B> businessInterface, ComponentService service);

    ExtensionPointRegistry getExtensionPointRegistry();

    CompositeContext getCompositeContext();
}
