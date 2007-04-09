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
 * Interface implemented by host environments that allow for resolution of component implementation resources, e.g.
 * items bound in a JNDI tree.
 *
 * @version $Rev$ $Date$
 */
public interface ResourceHost {

    /**
     * Resolve a resource matching the given type
     *
     * @param type the type of the resources
     * @throws ResourceResolutionException if an error is encountered during resolution
     */
    <T> T resolveResource(Class<T> type) throws ResourceResolutionException;

    /**
     * Resolve a resource matching the given type and name
     *
     * @param type       the type of the resources
     * @param mappedName the mapped name of the resource
     * @throws ResourceResolutionException if an error is encountered during resolution
     */
    <T> T resolveResource(Class<T> type, String mappedName) throws ResourceResolutionException;

}
