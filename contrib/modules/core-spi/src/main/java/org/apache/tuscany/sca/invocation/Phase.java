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

package org.apache.tuscany.sca.invocation;

/**
 * Tuscany built-in phases for the invocation chain
 * 
 * @version $Rev$ $Date$
 */
public interface Phase {
    // The first phase for outgoing invocations via a reference
    String REFERENCE = "component.reference";

    // data transformation and validation
    String REFERENCE_INTERFACE = "reference.interface";

    // reference policy handling
    String REFERENCE_POLICY = "reference.policy";

    // reference binding invoker
    String REFERENCE_BINDING = "reference.binding";

    String REFERENCE_BINDING_WIREFORMAT = "reference.binding.wireformat";
    String REFERENCE_BINDING_POLICY = "reference.binding.policy";
    String REFERENCE_BINDING_TRANSPORT = "reference.binding.transport";

    String SERVICE_BINDING_TRANSPORT = "service.binding.transport";
    String SERVICE_BINDING_OPERATION_SELECTOR = "service.binding.operationselector";
    String SERVICE_BINDING_WIREFORMAT = "service.binding.wireformat";
    String SERVICE_BINDING_POLICY = "service.binding.policy";
    
    
    // The first phase for incoming invocations via a service
    String SERVICE_BINDING = "service.binding";

    // service policy handling
    String SERVICE_POLICY = "service.policy";

    // data validation and transformation
    String SERVICE_INTERFACE = "service.interface";

    // TODO: not sure if we need to have this face
    String SERVICE = "component.service";

    // implementation policy handling
    String IMPLEMENTATION_POLICY = "implementation.policy";

    // implementation invoker
    String IMPLEMENTATION = "component.implementation";

    // String getName();
}
