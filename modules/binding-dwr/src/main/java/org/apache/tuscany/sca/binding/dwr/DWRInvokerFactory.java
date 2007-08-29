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

import static org.apache.tuscany.sca.binding.dwr.DWRService.SERVLET_PATH;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.extension.helper.ComponentLifecycle;
import org.apache.tuscany.sca.extension.helper.InvokerFactory;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;

public class DWRInvokerFactory implements InvokerFactory, ComponentLifecycle {

    private Binding binding;
    private ServletHost servletHost;
    
    public DWRInvokerFactory(RuntimeComponent rc, RuntimeComponentReference rcr, Binding b, DWRBinding ab, ServletHost servletHost) {
        this.binding = b;
        this.servletHost = servletHost;
    }

    public Invoker createInvoker(Operation operation) {
        return new DWRInvoker(binding.getName(), operation);
    }

    public void start() {

        DWRServlet servlet = (DWRServlet) servletHost.getServletMapping(SERVLET_PATH);
        if (servlet == null) {
            servlet = new DWRServlet();
            servletHost.addServletMapping(SERVLET_PATH, servlet);
        }
        
        servlet.addReference(binding.getName());
    }

    public void stop() {
        servletHost.removeServletMapping(SERVLET_PATH);
    }

}
