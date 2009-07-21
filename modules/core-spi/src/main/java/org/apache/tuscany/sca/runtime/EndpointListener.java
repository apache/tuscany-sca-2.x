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

import java.util.EventListener;

import org.apache.tuscany.sca.assembly.Endpoint;

/**
 * A listener for endpoint events
 */
public interface EndpointListener extends EventListener {
    /**
     * The method is invoked when a new endpoint is added to the registry
     * @param endpoint
     */
    void endpointAdded(Endpoint endpoint);
    /**
     * The method is invoked when an endpoint is removed the registry
     * @param endpoint
     */
    void endpointRemoved(Endpoint endpoint);
    /**
     * The method is invoked when an endpoint is updated in the registry
     * @param oldEndpoint
     * @param newEndpoint
     */
    void endpointUpdated(Endpoint oldEndpoint, Endpoint newEndpoint);
}
