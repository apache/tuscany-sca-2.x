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

package org.apache.tuscany.sca.implementation.jaxrs.provider;

import javax.servlet.Servlet;
import javax.ws.rs.core.Application;

import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.implementation.jaxrs.JAXRSImplementation;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.wink.server.internal.servlet.RestServlet;
import org.apache.wink.server.utils.RegistrationUtils;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * 
 */
public class JAXRSImplementationProvider implements ImplementationProvider {
    private JAXRSImplementation implementation;
    private ServletHost host;

    /**
     * @param host
     */
    public JAXRSImplementationProvider(JAXRSImplementation implementation, ServletHost host) {
        super();
        this.implementation = implementation;
        this.host = host;
    }

    public Invoker createInvoker(RuntimeComponentService service, Operation operation) {
        return null;
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

    public void start() {
        RestServlet restServlet = new RestServlet();
        host.addServletMapping("/*", restServlet);
        Application application;
        try {
            application = (Application)implementation.getApplicationClass().newInstance();
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
        RegistrationUtils.registerApplication(application, restServlet.getServletContext());
    }

    public void stop() {
        Servlet servlet = host.removeServletMapping("/*");
        if (servlet != null) {
            servlet.destroy();
        }
    }

}
