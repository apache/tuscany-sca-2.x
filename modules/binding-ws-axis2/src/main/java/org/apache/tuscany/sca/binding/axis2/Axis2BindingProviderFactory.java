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
package org.apache.tuscany.sca.binding.axis2;

import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * Axis2BindingProviderFactory
 *
 * @version $Rev$ $Date$
 */

public class Axis2BindingProviderFactory implements BindingProviderFactory<WebServiceBinding> {

    private MessageFactory messageFactory;
    private ServletHost servletHost;

    public Axis2BindingProviderFactory(ServletHost servletHost, MessageFactory messageFactory) {
        this.servletHost = servletHost;
        this.messageFactory = messageFactory;
    }

    public ReferenceBindingProvider createReferenceBindingProvider(RuntimeComponent component, RuntimeComponentReference reference, WebServiceBinding binding) {
        return new Axis2ReferenceBindingProvider(component, reference, binding, messageFactory);
    }

    public ServiceBindingProvider createServiceBindingProvider(RuntimeComponent component, RuntimeComponentService service, WebServiceBinding binding) {
        return new Axis2ServiceBindingProvider(component, service, binding, servletHost, messageFactory);
    }
    
    public Class<WebServiceBinding> getModelType() {
        return WebServiceBinding.class;
    }
}
