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

package org.apache.tuscany.sca.implementation.web;

import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.extension.helper.ImplementationActivator;
import org.apache.tuscany.sca.extension.helper.InvokerFactory;
import org.apache.tuscany.sca.extension.helper.utils.PropertyValueObjectFactory;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

import static org.apache.tuscany.sca.implementation.web.ComponentContextServlet.COMPONENT_CONTEXT_SCRIPT_URI;

public class WebImplementationActivator implements ImplementationActivator<WebImplementation> {

    protected ServletHost servletHost;
    // TODO: seems wrong to need PropertyValueObjectFactory, could it be on Property somehow? 
    protected PropertyValueObjectFactory propertyFactory;

    public WebImplementationActivator(ServletHost servletHost, PropertyValueObjectFactory propertyFactory) {
        this.servletHost = servletHost;
        this.propertyFactory = propertyFactory;
    }

    public Class<WebImplementation> getImplementationClass() {
        return WebImplementation.class;
    }

    public InvokerFactory createInvokerFactory(RuntimeComponent rc, ComponentType ct, WebImplementation implementation) {

        initServlet(servletHost);

        WebSingleton.INSTANCE.setRuntimeComponent(rc);

        return new InvokerFactory() {
            public Invoker createInvoker(Operation arg0) {
                throw new IllegalStateException("can't invoke an implementation.web component");
            }
        };
    }

    private void initServlet(ServletHost servletHost) {
        if (servletHost.getServletMapping(COMPONENT_CONTEXT_SCRIPT_URI) == null) {
            servletHost.addServletMapping(COMPONENT_CONTEXT_SCRIPT_URI, new ComponentContextServlet());
        }
    }

}
