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

package org.apache.tuscany.sca.binding.atom.provider;

import org.apache.tuscany.sca.binding.atom.AtomBinding;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

/**
 * Implementation of a Binding provider factory for the Atom binding.
 *
 * @version $Rev$ $Date$
 */
public class AtomBindingProviderFactory implements BindingProviderFactory<AtomBinding> {

    private MessageFactory messageFactory;
    private Mediator mediator;
    private ServletHost servletHost;

    public AtomBindingProviderFactory(ExtensionPointRegistry extensionPoints) {
        ServletHostExtensionPoint servletHosts = extensionPoints.getExtensionPoint(ServletHostExtensionPoint.class);
        this.servletHost = servletHosts.getServletHosts().get(0);
        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        this.messageFactory = modelFactories.getFactory(MessageFactory.class);
        this.mediator = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class).getUtility(Mediator.class);
    }

    public ReferenceBindingProvider createReferenceBindingProvider(RuntimeEndpointReference endpointReference) {
        return new AtomReferenceBindingProvider(endpointReference, mediator);
    }

    public ServiceBindingProvider createServiceBindingProvider(RuntimeEndpoint endpoint) {
        return new AtomServiceBindingProvider(endpoint, messageFactory, mediator, servletHost);
    }

    public Class<AtomBinding> getModelType() {
        return AtomBinding.class;
    }
}
