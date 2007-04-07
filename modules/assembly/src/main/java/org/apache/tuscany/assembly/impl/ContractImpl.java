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
package org.apache.tuscany.assembly.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.assembly.AbstractContract;
import org.apache.tuscany.idl.Interface;
import org.apache.tuscany.policy.Intent;

public class ContractImpl extends BaseImpl implements AbstractContract {
    private Interface callbackInterface;
    private Interface callInterface;
    private String name;
    private List<Intent> requiredIntents = new ArrayList<Intent>();
    
    /**
     * Constructs a new contract.
     */
    public ContractImpl() {
    }
    
    /**
     * Copy constructor.
     * @param other
     */
    public ContractImpl(AbstractContract other) {
        super(other);
        callbackInterface = other.getCallbackInterface();
        callInterface = other.getInterface();
        name = other.getName();
        requiredIntents.addAll(other.getRequiredIntents());
    }

    public Interface getCallbackInterface() {
        return callbackInterface;
    }

    public Interface getInterface() {
        return callInterface;
    }

    public String getName() {
        return name;
    }

    public void setCallbackInterface(Interface callbackInterface) {
        this.callbackInterface = callbackInterface;
    }

    public void setInterface(Interface callInterface) {
        this.callInterface = callInterface;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Intent> getRequiredIntents() {
        return requiredIntents;
    }

}
