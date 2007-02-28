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
 * Represents a component reference
 *
 * @version $Rev$ $Date$
 */
public class ReferenceDefinition extends ModelObject {
    private URI uri;
    private ServiceContract serviceContract;
    private Multiplicity multiplicity;
    private boolean required;
    private List<BindingDefinition> bindings;

    public ReferenceDefinition() {
        multiplicity = Multiplicity.ONE_ONE;
        bindings = new ArrayList<BindingDefinition>();
    }

    public ReferenceDefinition(URI uri, ServiceContract serviceContract) {
        this.uri = uri;
        this.serviceContract = serviceContract;
        bindings = new ArrayList<BindingDefinition>();
        multiplicity = Multiplicity.ONE_ONE;
    }

    public ReferenceDefinition(URI uri, ServiceContract serviceContract, Multiplicity multiplicity) {
        this.uri = uri;
        this.serviceContract = serviceContract;
        this.multiplicity = multiplicity;
        bindings = new ArrayList<BindingDefinition>();
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public ServiceContract<?> getServiceContract() {
        return serviceContract;
    }

    public void setServiceContract(ServiceContract serviceContract) {
        this.serviceContract = serviceContract;
    }

    public Multiplicity getMultiplicity() {
        return multiplicity;
    }

    public void setMultiplicity(Multiplicity multiplicity) {
        this.multiplicity = multiplicity;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public List<BindingDefinition> getBindings() {
        return Collections.unmodifiableList(bindings);
    }

    public void addBinding(BindingDefinition binding) {
        this.bindings.add(binding);
    }
}
