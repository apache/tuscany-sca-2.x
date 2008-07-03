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

import org.osoa.sca.annotations.Conversational;


/**
 * This is a test client that is used to test Serializing and Deserializing
 * ServiceReferences within a SCA Application where the client that the Callback
 * is referring is actually Conversational.
 * 
 * @version $Rev$ $Date$
 */
@Conversational
public interface SCAManagedConversationalClient {

    /**
     * Tests Serializing a Conversational ServiceReference.
     * 
     * @throws Exception Test failed
     */
    void testSerializeConversationalServiceReference() throws Exception;

    /**
     * Tests Serializing a Nested Conversational ServiceReference.
     * 
     * @throws Exception Test failed
     */
    void testSerializeNestedConversationalServiceReference() throws Exception;

    /**
     * Tests Serializing a Callback to a Conversational Service as managed
     * SCA code.
     * 
     * @throws Exception Test failed
     */
    void testSerializeCallbackToConversationalServiceInsideSCA() throws Exception;
}
