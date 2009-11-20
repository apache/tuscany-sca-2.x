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

package org.apache.tuscany.sca.core.assembly;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.assembly.impl.RuntimeComponentImpl;
import org.apache.tuscany.sca.core.assembly.impl.RuntimeComponentReferenceImpl;
import org.apache.tuscany.sca.core.assembly.impl.RuntimeComponentServiceImpl;
import org.apache.tuscany.sca.core.assembly.impl.RuntimeEndpointImpl;
import org.apache.tuscany.sca.core.assembly.impl.RuntimeEndpointReferenceImpl;


/**
 * The runtime version of assembly factory
 * @version $Rev$ $Date$
 */
public class RuntimeAssemblyFactory extends DefaultAssemblyFactory implements AssemblyFactory {

    public RuntimeAssemblyFactory(ExtensionPointRegistry registry) {
        super(registry);
    }

    @Override
    public Component createComponent() {
        return new RuntimeComponentImpl();
    }

    @Override
    public ComponentReference createComponentReference() {
        return new RuntimeComponentReferenceImpl();
    }

    @Override
    public ComponentService createComponentService() {
        return new RuntimeComponentServiceImpl();
    }

    @Override
    public Endpoint createEndpoint() {
        // Create an instance of EndpointImpl that can be serialized/deserialized using the Tuscany
        // runtime extension points and extensions
        return new RuntimeEndpointImpl(registry);
    }

    @Override
    public EndpointReference createEndpointReference() {
        return new RuntimeEndpointReferenceImpl(registry);
    }

}
