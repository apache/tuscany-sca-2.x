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
package org.apache.tuscany.sca.implementation.bpel;

/**
 * The service interface of the single BPEL service provided by BPEL components.
 * 
 * @version $Rev$ $Date$
 */
public interface BPEL {

    /**
     * Create a new resource.
     * @param resource
     * @return
     */
    String create(Object resource);

    /**
     * Retrieve a resource.
     * @param id
     * @return
     */
    Object retrieve(String id);

    /**
     * Update a resource.
     * @param id
     * @param resource
     * @return
     */
    Object update(String id, Object resource);

    /**
     * Delete a resource.
     * @param id
     */
    void delete(String id);

}
