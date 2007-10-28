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
package org.apache.tuscany.sca.implementation.widget.provider;

import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.implementation.widget.WidgetImplementation;
import org.apache.tuscany.sca.implementation.widget.WidgetReferenceServlet;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * The model representing a resource implementation in an SCA assembly model.
 */
class WidgetImplementationProvider implements ImplementationProvider {
    private RuntimeComponent component;
    private WidgetImplementation implementation;

    private ServletHost servletHost;

    /**
     * Constructs a new resource implementation provider.
     */
    WidgetImplementationProvider(RuntimeComponent component, WidgetImplementation implementation, ServletHost servletHost) {
        this.component = component;
        this.implementation = implementation;
        
        this.servletHost = servletHost;
    }

    public Invoker createInvoker(RuntimeComponentService service, Operation operation) {
        WidgetImplementationInvoker invoker = new WidgetImplementationInvoker(implementation.getLocationURL());
        return invoker;
    }
    
    public boolean supportsOneWayInvocation() {
        return false;
    }

    public void start() {

        WidgetReferenceServlet widgetServlet = new WidgetReferenceServlet(component);
        
        String uri = component.getURI() + "/" + component.getURI() + ".js";
        servletHost.addServletMapping(uri, widgetServlet);        
    }

    public void stop() {
    }

}
