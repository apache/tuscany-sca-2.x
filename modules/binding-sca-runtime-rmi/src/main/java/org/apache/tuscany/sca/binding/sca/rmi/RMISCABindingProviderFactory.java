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

package org.apache.tuscany.sca.binding.sca.rmi;

import org.apache.tuscany.sca.assembly.DistributedSCABinding;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;

/**
 * The factory for the RMI based implementation of the distributed sca binding
 */
public class RMISCABindingProviderFactory implements BindingProviderFactory<DistributedSCABinding> {
    
    private ExtensionPointRegistry extensionPoints;

    public RMISCABindingProviderFactory(ExtensionPointRegistry extensionPoints) {
        this.extensionPoints = extensionPoints;
    }    

    public ReferenceBindingProvider createReferenceBindingProvider(EndpointReference endpointReference) {
        return new RMISCAReferenceBindingProvider(endpointReference, extensionPoints);
    }

    public ServiceBindingProvider createServiceBindingProvider(Endpoint endpoint) {
        return new RMISCAServiceBindingProvider(endpoint, extensionPoints);
    }

    public Class<DistributedSCABinding> getModelType() {
        return DistributedSCABinding.class;
    }  
}
