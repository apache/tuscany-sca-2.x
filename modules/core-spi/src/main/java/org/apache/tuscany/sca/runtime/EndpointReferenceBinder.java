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

import org.apache.tuscany.sca.assembly.EndpointReference;

/**
 * A utility responsible for resolving the endpoint reference against a matching endpoint published
 * to the EndpointRegistry
 */
public interface EndpointReferenceBinder {
    
    /**
     * @param endpointRegistry
     * @param endpointReference
     * @return
     */
    boolean bindBuildTime(EndpointRegistry endpointRegistry, EndpointReference endpointReference);

    
    /**
     * @param endpointRegistry
     * @param endpointReference
     * @return
     */
    boolean bindRunTime(EndpointRegistry endpointRegistry, EndpointReference endpointReference);
    
    /**
     * 
     * @param endpointRegistry
     * @param endpointReference
     * @return
     */
    boolean isOutOfDate(EndpointRegistry endpointRegistry, EndpointReference endpointReference);
}
