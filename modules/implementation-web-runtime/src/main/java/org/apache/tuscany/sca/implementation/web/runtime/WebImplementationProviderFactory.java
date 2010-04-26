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
package org.apache.tuscany.sca.implementation.web.runtime;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostHelper;
import org.apache.tuscany.sca.implementation.web.WebImplementation;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.provider.ImplementationProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

public class WebImplementationProviderFactory implements ImplementationProviderFactory<WebImplementation> {

    private ServletHost servletHost;
    private ClientExtensionPoint jsClient;

    public WebImplementationProviderFactory(ExtensionPointRegistry extensionPoints) {
        this.servletHost = ServletHostHelper.getServletHost(extensionPoints);
        jsClient = extensionPoints.getExtensionPoint(ClientExtensionPoint.class);
    }

    public ImplementationProvider createImplementationProvider(RuntimeComponent component, WebImplementation implementation) {
        servletHost.setAttribute("org.apache.tuscany.sca.implementation.web.RuntimeComponent", component);
        servletHost.setAttribute("org.oasisopen.sca.ComponentContext", new ComponentContextProxy(component));

        ImplementationProvider impl;
        if (jsClient != null && implementation.getJSClient()) {
            impl = jsClient.createImplementationProvider(component, implementation);
        } else {
            impl = new ImplementationProvider() {
                
                public Invoker createInvoker(RuntimeComponentService arg0, Operation arg1) {
                    throw new UnsupportedOperationException("Components using implementation.web have no services");
                }
                public void start() {
                }
                public void stop() {
                }
                public boolean supportsOneWayInvocation() {
                    return false;
                }
            };
        }
        return impl;
    }
    
    public Class<WebImplementation> getModelType() {
        return WebImplementation.class;
    }
    
}
