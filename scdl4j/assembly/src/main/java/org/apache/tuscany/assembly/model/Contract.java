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
package org.apache.tuscany.assembly.model;

import java.util.List;

import org.apache.tuscany.policy.model.PolicySetAttachPoint;

/**
 * Represents a contract. A contract can be either a service or a reference.
 */
public interface Contract extends AbstractContract, PolicySetAttachPoint {

    /**
     * Returns the bindings supported by this contract.
     * 
     * @return the bindings supported by this contract
     */
    List<Binding> getBindings();

    /**
     * Returns a binding of the specified type or null if there is no such
     * binding configured on this contract.
     * 
     * @param <B> the binding type
     * @param bindingClass the binding type class
     * @return the binding or null if there is no binding of the specified type
     */
    <B> B getBinding(Class<B> bindingClass);

    /**
     * Returns a callback definition of the bindings to use for callbacks.
     * 
     * @return a definition of the bindings to use for callbacks
     */
    Callback getCallback();

    /**
     * Sets a callback definition of the bindings to use for callbacks
     * 
     * @param callback a definition of the bindings to use for callbacks
     */
    void setCallback(Callback callback);

}
