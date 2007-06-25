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

package org.apache.tuscany.sca.binding.feed.provider;

import org.apache.tuscany.sca.binding.feed.RSSBinding;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * Implementation of the RSS binding provider factory.
 */
public class RSSBindingProviderFactory implements BindingProviderFactory<RSSBinding> {

    MessageFactory messageFactory;
    ServletHost servletHost;

    public RSSBindingProviderFactory(ServletHost servletHost, MessageFactory messageFactory) {
        this.servletHost = servletHost;
        this.messageFactory = messageFactory;
    }

    public ReferenceBindingProvider createReferenceBindingProvider(RuntimeComponent component,
                                                                   RuntimeComponentReference reference,
                                                                   RSSBinding binding) {
        return new RSSReferenceBindingProvider(component, reference, binding);
    }

    public ServiceBindingProvider createServiceBindingProvider(RuntimeComponent component,
                                                               RuntimeComponentService service,
                                                               RSSBinding binding) {
        return new RSSServiceBindingProvider(component, service, binding, servletHost, messageFactory);
    }

    public Class<RSSBinding> getModelType() {
        return RSSBinding.class;
    }
}
