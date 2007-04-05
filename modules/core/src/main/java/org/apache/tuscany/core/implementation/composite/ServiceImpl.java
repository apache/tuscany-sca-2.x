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
package org.apache.tuscany.core.implementation.composite;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tuscany.assembly.Contract;
import org.apache.tuscany.spi.component.AbstractSCAObject;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;

/**
 * The default implementation of a {@link Service}
 *
 * @version $Rev$ $Date$
 */
public class ServiceImpl extends AbstractSCAObject implements Service {
    private Contract serviceContract;
    private List<ServiceBinding> bindings = new ArrayList<ServiceBinding>();
    private URI targetUri;

    public ServiceImpl(URI name, Contract contract) {
        this(name, contract, null);
    }

    public ServiceImpl(URI name, Contract contract, URI targetUri) {
        super(name);
        this.serviceContract = contract;
        this.targetUri = targetUri;
    }

    public Contract getServiceContract() {
        return serviceContract;
    }

    public URI getTargetUri() {
        return targetUri;
    }

    public List<ServiceBinding> getServiceBindings() {
        return Collections.unmodifiableList(bindings);
    }

    public void addServiceBinding(ServiceBinding binding) {
        bindings.add(binding);
    }

    public void start() {
        super.start();
        for (ServiceBinding binding : bindings) {
            binding.start();
        }
    }

    public void stop() {
        super.stop();
        for (ServiceBinding binding : bindings) {
            binding.stop();
        }
    }

}
