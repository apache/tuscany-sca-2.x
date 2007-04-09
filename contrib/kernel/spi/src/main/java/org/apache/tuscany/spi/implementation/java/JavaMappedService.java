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
package org.apache.tuscany.spi.implementation.java;

import java.lang.reflect.Member;
import java.net.URI;

import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * A ServiceDefinition definition that is mapped to a Java interface. The mapped interface is not required to be the
 * same as the interface specified in the service contract. This is to allow the service contract to be specified using
 * different interface definition languages or, in the case were the IDL is Java, to allow the service definition to be
 * loaded from a different classloader.
 *
 * @version $Rev$ $Date$
 */
public class JavaMappedService extends ServiceDefinition {
    private Class<?> serviceInterface;
    private Member callbackMember;

    public JavaMappedService() {
    }

    public JavaMappedService(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public JavaMappedService(URI name, ServiceContract contract, boolean remotable) {
        super(name, contract, remotable);
    }

    public JavaMappedService(URI name,
                             ServiceContract contract,
                             boolean remotable,
                             String callbackRefName,
                             Member callbackMember) {
        super(name, contract, remotable, callbackRefName);
        this.callbackMember = callbackMember;
    }

    /**
     * Returns the Java interface for this service. This may be different from the interface that defines the service
     * contract.
     *
     * @return the Java interface for this service
     */
    public Class<?> getServiceInterface() {
        return serviceInterface;
    }

    /**
     * Sets the Java interface for this service. This may be different from the interface used to define the service
     * contract.
     *
     * @param serviceInterface the Java interface for this service
     */
    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public Member getCallbackMember() {
        return callbackMember;
    }

    public void setCallbackMember(Member callbackMember) {
        this.callbackMember = callbackMember;
    }

}
