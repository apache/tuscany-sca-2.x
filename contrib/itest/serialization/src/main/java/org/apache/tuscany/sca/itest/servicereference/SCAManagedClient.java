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
package org.apache.tuscany.sca.itest.servicereference;


/**
 * This is a test client that is used to test Serializing and Deserializing
 * ServiceReferences within a SCA Application
 * 
 * @version $Date$ $Revision$
 */
public interface SCAManagedClient {

    /**
     * Tests Serializing a Stateless ServiceReference
     * 
     * @throws Exception Test failed
     */
    void testSerializeStatelessServiceReference() throws Exception;

    /**
     * Tests Serializing a Nested Stateless ServiceReference.
     * 
     * @throws Exception Test failed
     */
    void testSerializeNestedStatelessServiceReference() throws Exception;

    /**
     * Tests Serializing a Callback to a Stateless Service as managed
     * SCA code
     * 
     * @throws Exception Test failed
     */
    void testSerializeCallbackToStatelessServiceInsideSCA() throws Exception;
}
