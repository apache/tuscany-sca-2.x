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

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;

/**
 * A builder that connects an endpoint reference to an endpoint
 */
public interface WireBuilder {
    /**
     * Build the endpoint reference against the endpoint. If the endpoint is a valid target for the
     * endpoint reference, the wire buidler can populate some information from the endpoint into the 
     * endpoint reference (such as targetEndpoint, policySets or requiredIntents)
     * @param endpointReference
     * @param endpoint
     * @return true if the endpoint is a valid target for the endpoint reference
     */
    boolean build(EndpointReference endpointReference, Endpoint endpoint);
}
