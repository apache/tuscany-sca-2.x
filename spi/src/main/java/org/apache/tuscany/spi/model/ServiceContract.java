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
package org.apache.tuscany.spi.model;

import java.util.Map;

/**
 * Base class representing service contract information
 *
 * @version $Rev$ $Date$
 */
public abstract class ServiceContract extends ModelObject {
    private InteractionScope interactionScope;
    private Class<?> interfaceClass;
    private String interfaceName;
    private String callbackName;
    private Class<?> callbackClass;
    private Map<String, Operation> operations;
    private Map<String, Operation> callbacksOperations;

    protected ServiceContract() {
    }

    protected ServiceContract(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    protected ServiceContract(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    /**
     * Returns the class used to represent the service contract
     */
    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    /**
     * Sets the class used to represent the service contract
     */
    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    /**
     * Returns the service interaction scope
     */
    public InteractionScope getInteractionScope() {
        return interactionScope;
    }

    /**
     * Sets the service interaction scope
     */
    public void setInteractionScope(InteractionScope interactionScope) {
        this.interactionScope = interactionScope;
    }

    /**
     * Returns the name of the callback or null if the contract is unidirectional
     */
    public String getCallbackName() {
        return callbackName;
    }

    /**
     * Sets the name of the callback service
     */
    public void setCallbackName(String callbackName) {
        this.callbackName = callbackName;
    }

    /**
     * Returns the name of the callback service
     */
    public Class<?> getCallbackClass() {
        return callbackClass;
    }

    public void setCallbackClass(Class<?> callbackClass) {
        this.callbackClass = callbackClass;
    }

    public Map<String, Operation> getOperations() {
        return operations;
    }

    public void setOperations(Map<String, Operation> operations) {
        this.operations = operations;
    }

    public Map<String, Operation> getCallbacksOperations() {
        return callbacksOperations;
    }

    public void setCallbacksOperations(Map<String, Operation> callbacksOperations) {
        this.callbacksOperations = callbacksOperations;
    }
}
