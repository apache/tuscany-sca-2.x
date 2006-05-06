/**
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.binding.jsonrpc.builder;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.binding.jsonrpc.assembly.JSONRPCBinding;
import org.apache.tuscany.binding.jsonrpc.config.JSONEntryPointContextFactory;
import org.apache.tuscany.core.extension.EntryPointBuilderSupport;
import org.apache.tuscany.core.extension.EntryPointContextFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.webapp.ServletHost;
import org.apache.tuscany.model.assembly.EntryPoint;

@Scope("MODULE")
public class JSONRPCEntryPointBuilder extends EntryPointBuilderSupport<JSONRPCBinding> {

    private ServletHost tomcatHost;

    @Autowire
    public void setTomcatHost(ServletHost tomcatHost) {
        this.tomcatHost = tomcatHost;
    }


    @Override
    protected EntryPointContextFactory createEntryPointContextFactory(EntryPoint entryPoint, MessageFactory msgFactory) {
        initServlet(entryPoint);
        return new JSONEntryPointContextFactory(entryPoint.getName(), msgFactory);
    }

    private void initServlet(EntryPoint entryPoint) {
            Servlet helloservlet = new HttpServlet() {
                public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
                    PrintWriter out = res.getWriter();
                    out.println("Hello, world!");
                    out.close();
                }
            };
            tomcatHost.registerMapping("/helloworldjsonrpc-SNAPSHOT/foo", helloservlet);
    }

}
