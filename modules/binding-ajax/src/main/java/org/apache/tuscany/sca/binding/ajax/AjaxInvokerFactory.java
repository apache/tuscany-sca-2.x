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

package org.apache.tuscany.sca.binding.ajax;

import static org.apache.tuscany.sca.binding.ajax.AjaxService.SERVLET_PATH;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.spi.ComponentLifecycle;
import org.apache.tuscany.sca.spi.InvokerFactory;

public class AjaxInvokerFactory implements InvokerFactory, ComponentLifecycle {

    protected RuntimeComponent runtimeComponent;
    protected RuntimeComponentReference runtimeComponentReference;
    protected Binding binding;
    protected ServletHost servletHost;
    
    public AjaxInvokerFactory(RuntimeComponent rc, RuntimeComponentReference rcr, Binding b, AjaxBinding ab, ServletHost servletHost) {
        this.runtimeComponent = rc;
        this.runtimeComponentReference = rcr;
        this.binding = b;
        this.servletHost = servletHost;
    }

    public Invoker createInvoker(Operation operation) {
        return new AjaxInvoker(binding.getName(), operation);
    }

    public void start() {

        // there is no "getServlet" method on ServletHost so this has to use remove/add

        AjaxServlet servlet = (AjaxServlet) servletHost.removeServletMapping(SERVLET_PATH);
        if (servlet == null) {
            servlet = new AjaxServlet();
        }
        
        servlet.addReference(binding.getName());

        servletHost.addServletMapping(SERVLET_PATH, servlet);
    }

    public void stop() {
        servletHost.removeServletMapping(SERVLET_PATH);
    }

}
