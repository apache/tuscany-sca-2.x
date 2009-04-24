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

package org.apache.tuscany.sca.binding.rest.provider;

import org.apache.tuscany.sca.binding.rest.RESTBinding;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * REST Provider Factory
 * 
 * @version $Rev: 665897 $ $Date: 2008-06-09 22:31:03 +0100 (Mon, 09 Jun 2008) $ 
 */
public class RESTBindingProviderFactory implements BindingProviderFactory<RESTBinding> {

    private ServletHost servletHost;
    
    public RESTBindingProviderFactory(ExtensionPointRegistry extensionPoints) {
        ServletHostExtensionPoint servletHosts = extensionPoints.getExtensionPoint(ServletHostExtensionPoint.class);
        this.servletHost = servletHosts.getServletHosts().get(0);
    }
    
    public ReferenceBindingProvider createReferenceBindingProvider(RuntimeComponent component,
                                                                   RuntimeComponentReference reference,
                                                                   RESTBinding binding) {
        
        return new RESTReferenceBindingProvider(component, reference, binding);
    }

    public ServiceBindingProvider createServiceBindingProvider(RuntimeComponent component,
                                                               RuntimeComponentService service,
                                                               RESTBinding binding) {
        return new RESTServiceBindingProvider(component, service, binding, servletHost);
    }

    public Class<RESTBinding> getModelType() {
        return RESTBinding.class;
    }

}
