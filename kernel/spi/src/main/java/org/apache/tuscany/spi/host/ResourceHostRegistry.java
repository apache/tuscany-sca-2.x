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
package org.apache.tuscany.spi.host;

/**
 * Implementations manage a registry of resource hosts in the runtime
 *
 * @version $Rev$ $Date$
 */
public interface ResourceHostRegistry {
    /**
     * Registers a resource host for the given uri prefix
     *
     * @param uri  the uri prefix the host resolves resources for
     * @param host the resource host
     */
    void register(String uri, ResourceHost host);

    /**
     * Removes a host registered for the given uri prefix
     */
    void unregister(String uri);
}
