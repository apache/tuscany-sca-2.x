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

package org.apache.tuscany.sca.binding.sca.corba.impl;

import org.apache.tuscany.sca.binding.sca.DistributedSCABinding;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.host.corba.CorbaHost;
import org.apache.tuscany.sca.host.corba.CorbaHostExtensionPoint;
import org.apache.tuscany.sca.host.corba.ExtensibleCorbaHost;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * Binding provider factory for SCA default binding over CORBA binding
 */
public class CorbaSCABindingProviderFactory implements BindingProviderFactory<DistributedSCABinding> {

    private CorbaHostExtensionPoint chep;
    private CorbaHost host;
    private ExtensionPointRegistry extensions;

    public CorbaSCABindingProviderFactory(ExtensionPointRegistry extensions) {
        this.extensions = extensions;
        chep = extensions.getExtensionPoint(CorbaHostExtensionPoint.class);
        host = new ExtensibleCorbaHost(chep);
    }

    public ReferenceBindingProvider createReferenceBindingProvider(RuntimeComponent component,
                                                                   RuntimeComponentReference reference,
                                                                   DistributedSCABinding binding) {
        return new CorbaSCAReferenceBindingProvider(binding.getSCABinding(), host, component, reference, extensions);
    }

    public ServiceBindingProvider createServiceBindingProvider(RuntimeComponent component,
                                                               RuntimeComponentService service,
                                                               DistributedSCABinding binding) {
        return new CorbaSCAServiceBindingProvider(binding.getSCABinding(), host, component, service, extensions);
    }

    public Class<DistributedSCABinding> getModelType() {
        return DistributedSCABinding.class;
    }
}
