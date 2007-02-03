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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a service offered by a component
 *
 * @version $Rev$ $Date$
 */
public class ServiceDefinition extends ModelObject {
    private URI uri;
    private ServiceContract serviceContract;
    private boolean remotable;
    private String callbackRefName;
    private List<BindingDefinition> bindings;
    private URI target;

    public ServiceDefinition() {
        bindings = new ArrayList<BindingDefinition>();
    }

    public ServiceDefinition(URI uri, ServiceContract serviceContract, boolean remotable) {
        bindings = new ArrayList<BindingDefinition>();
        this.uri = uri;
        this.serviceContract = serviceContract;
        this.remotable = remotable;
    }

    public ServiceDefinition(URI uri, ServiceContract serviceContract, boolean remotable, String callbackRefName) {
        bindings = new ArrayList<BindingDefinition>();
        this.uri = uri;
        this.serviceContract = serviceContract;
        this.remotable = remotable;
        this.callbackRefName = callbackRefName;
    }

    /**
     * Returns the service name
     *
     * @return the service name
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Sets the service name
     *
     * @param uri the service name
     */
    public void setUri(URI uri) {
        this.uri = uri;
    }

    /**
     * Returns the service contract
     *
     * @return the service contract
     */
    public ServiceContract<?> getServiceContract() {
        return serviceContract;
    }

    /**
     * Sets the service contract
     *
     * @param contract the service contract
     */
    public void setServiceContract(ServiceContract contract) {
        this.serviceContract = contract;
    }

    /**
     * Returns true if the service is remotable
     *
     * @return true if the service is remotable
     */
    public boolean isRemotable() {
        return remotable;
    }

    /**
     * Sets if the service is remotable
     *
     * @param remotable if the service is remotable
     */
    public void setRemotable(boolean remotable) {
        this.remotable = remotable;
    }

    /**
     * Returns the callback name.
     */
    public String getCallbackReferenceName() {
        return callbackRefName;
    }

    /**
     * Sets the callback name
     */
    public void setCallbackReferenceName(String name) {
        this.callbackRefName = name;
    }

    /**
     * Returns the bindings configured for the service
     *
     * @return the bindings configured for the service
     */
    public List<BindingDefinition> getBindings() {
        return Collections.unmodifiableList(bindings);
    }

    /**
     * Configures the service with a binding
     *
     * @param binding the binding
     */
    public void addBinding(BindingDefinition binding) {
        this.bindings.add(binding);
    }

    /**
     * Returns the target URI the service is wired to
     *
     * @return the target URI the service is wired to
     */
    public URI getTarget() {
        return target;
    }

    /**
     * Sets the target URI the service is wired to
     *
     * @param target the target URI the service is wired to
     */
    public void setTarget(URI target) {
        this.target = target;
    }


}
