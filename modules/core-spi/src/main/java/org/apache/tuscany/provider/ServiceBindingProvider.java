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

package org.apache.tuscany.provider;

import org.apache.tuscany.interfacedef.InterfaceContract;

/**
 * A service binding can optionally implement this interface
 * 
 * @version $Rev$ $Date$
 */
public interface ServiceBindingProvider<M> {
    /**
     * This method will be invoked when the component service binding
     * is activated.
     */
    void start();

    /**
     * This method will be invoked when the component service binding
     * is deactivated.
     */
    void stop();

    /**
     * Get the effective interface contract imposed by the binding. For example,
     * it will be interface contract introspected from the WSDL portType used by
     * the endpoint for a WebService binding.
     * 
     * @param service The component service that owns the service binding
     * @return The effective interface contract, if null is returned, the interface contract
     * for the component service will be used
     */
    InterfaceContract getBindingInterfaceContract();
}
