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

package org.apache.tuscany.sca.host.http.extensibility;

import java.util.List;

/**
 * Extension Point to allow registration of different port allocators
 * @version $Rev$ $Date$
 */
public interface HttpPortAllocatorExtensionPoint {

    /**
     * Register a new http port allocator
     * @param httpPortAllocator the http port allocator
     */
    void addPortAllocators(HttpPortAllocator httpPortAllocator);

    /**
     * Unregister a http port allocator
     * @param httpPortAllocator the http port allocator
     */
    void removePortAllocators(HttpPortAllocator httpPortAllocator);

    /**
     * Get a list of all registered http port allocators
     * @return the list of http port allocators
     */
    List<HttpPortAllocator> getPortAllocators();
}
