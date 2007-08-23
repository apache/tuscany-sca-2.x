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

package org.apache.tuscany.sca.binding.dwr;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.core.invocation.JDKProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.extension.helper.ComponentLifecycle;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

public class DWRService implements ComponentLifecycle {

    RuntimeComponent rc;
    RuntimeComponentService rcs;
    Binding binding;
    protected ServletHost servletHost;
    
    public static final String SERVLET_PATH = DWRServlet.AJAX_SERVLET_PATH + "/*";

    public DWRService(RuntimeComponent rc, RuntimeComponentService rcs, Binding binding, DWRBinding ab, ServletHost servletHost) {
        this.rc = rc;
        this.rcs = rcs;
        this.binding = binding;
        this.servletHost = servletHost;
    }

    public void start() {
        
        // there is no "getServlet" method on ServletHost so this has to use remove/add

        DWRServlet servlet = (DWRServlet) servletHost.removeServletMapping(SERVLET_PATH);
        if (servlet == null) {
            servlet = new DWRServlet();
        }
        
        Class<?> type = ((JavaInterface)rcs.getInterfaceContract().getInterface()).getJavaClass();

        // Create a Java proxy to the target service
        ProxyFactory proxyFactory = new JDKProxyFactory();
        Object proxy = proxyFactory.createProxy(type, rcs.getRuntimeWire(binding));

        servlet.addService(binding.getName(), type, proxy);

        servletHost.addServletMapping(SERVLET_PATH, servlet);
    }

    public void stop() {
        servletHost.removeServletMapping(SERVLET_PATH);
    }

}
