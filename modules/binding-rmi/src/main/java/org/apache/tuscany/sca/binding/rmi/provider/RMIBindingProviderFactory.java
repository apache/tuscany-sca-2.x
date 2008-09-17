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

package org.apache.tuscany.sca.binding.rmi.provider;

import org.apache.tuscany.sca.binding.rmi.RMIBinding;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.host.rmi.ExtensibleRMIHost;
import org.apache.tuscany.sca.host.rmi.RMIHost;
import org.apache.tuscany.sca.host.rmi.RMIHostExtensionPoint;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * 
 */
public class RMIBindingProviderFactory implements BindingProviderFactory<RMIBinding> {
    private RMIHost rmiHost;
    
    public RMIBindingProviderFactory(ExtensionPointRegistry registry) {
        super();
        RMIHostExtensionPoint hosts = registry.getExtensionPoint(RMIHostExtensionPoint.class);
        rmiHost = new ExtensibleRMIHost(hosts);
    }

    public Class<RMIBinding> getModelType() {
        return RMIBinding.class;
    }

    public ReferenceBindingProvider createReferenceBindingProvider(RuntimeComponent component,
                                                                   RuntimeComponentReference reference,
                                                                   RMIBinding binding) {
        return new RMIReferenceBindingProvider(component, reference, binding, rmiHost);
    }

    public ServiceBindingProvider createServiceBindingProvider(RuntimeComponent component,
                                                               RuntimeComponentService service,
                                                               RMIBinding binding) {
        return new RMIServiceBindingProvider(component, service, binding, rmiHost);
    }
    
}
