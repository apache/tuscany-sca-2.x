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
package org.apache.tuscany.sca.binding.ws.axis2;

import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.util.PolicyHandlerDefinitionsLoader;
import org.apache.tuscany.sca.policy.util.PolicyHandlerTuple;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.osoa.sca.ServiceRuntimeException;

/**
 * Axis2BindingProviderFactory
 *
 * @version $Rev$ $Date$
 */

public class Axis2BindingProviderFactory implements BindingProviderFactory<WebServiceBinding> {

    private MessageFactory messageFactory;
    private ServletHost servletHost;
    private Map<ClassLoader, List<PolicyHandlerTuple>> policyHandlerClassnames = null;

    public Axis2BindingProviderFactory(ExtensionPointRegistry extensionPoints) {
        ServletHostExtensionPoint servletHosts = extensionPoints.getExtensionPoint(ServletHostExtensionPoint.class);
        List<ServletHost> hosts = servletHosts.getServletHosts();
        if (hosts.isEmpty()) {
            throw new ServiceRuntimeException("No Servlet host is avaible for HTTP web services");
        } else {
            this.servletHost = hosts.get(0);
        }
        ModelFactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
        this.messageFactory = modelFactories.getFactory(MessageFactory.class); 
        policyHandlerClassnames = PolicyHandlerDefinitionsLoader.loadPolicyHandlerClassnames();
    }

    public ReferenceBindingProvider createReferenceBindingProvider(RuntimeComponent component, RuntimeComponentReference reference, WebServiceBinding binding) {
        return new Axis2ReferenceBindingProvider(component, reference, binding, servletHost, messageFactory, policyHandlerClassnames);
    }

    public ServiceBindingProvider createServiceBindingProvider(RuntimeComponent component, RuntimeComponentService service, WebServiceBinding binding) {
        return new Axis2ServiceBindingProvider(component, service, binding, servletHost, messageFactory, policyHandlerClassnames);
    }
    
    public Class<WebServiceBinding> getModelType() {
        return WebServiceBinding.class;
    }
}
