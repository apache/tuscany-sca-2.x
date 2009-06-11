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

package org.apache.tuscany.sca.assembly.builder;

import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * A builder that handles the configuration of endpoint references
 * It collects together the logic so that it can be used at build time
 * or later on during late binding scenarios
 *
 * @version $Rev$ $Date$
 */
public interface EndpointReferenceBuilder {
    
    /**
     * Build an endpoint reference matching reference bindings 
     * with service bindings.
     * 
     * @param endpointReference
     * @param monitor
     */
    void build(EndpointReference endpointReference, Monitor monitor);
    
}
