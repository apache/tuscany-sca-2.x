/**
 *
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
package org.apache.tuscany.binding.jsonrpc.config;

import org.apache.tuscany.binding.jsonrpc.handler.JSONRPCEntryPointServlet;
import org.apache.tuscany.binding.jsonrpc.handler.ScriptGetterServlet;
import org.apache.tuscany.core.builder.ContextCreationException;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.core.extension.EntryPointContextFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.webapp.ServletHost;

/**
 * @version $$Rev$$ $$Date$$
 */
public class JSONEntryPointContextFactory extends EntryPointContextFactory {

    private ServletHost tomcatHost;

    private String webAppName;

    public JSONEntryPointContextFactory(String name, MessageFactory msgFactory, String webAppName, ServletHost tomcatHost) {
        super(name, msgFactory);
        this.webAppName = webAppName;
        this.tomcatHost = tomcatHost;
    }

    public EntryPointContext createContext() throws ContextCreationException {
        EntryPointContext epc = super.createContext();
        JSONRPCEntryPointServlet jsonrpcServlet = getServlet();
        jsonrpcServlet.addEntryPoint(epc);
        return epc;
    }

    private JSONRPCEntryPointServlet getServlet() {
        String jsonrpcServletMapping = webAppName + "/SCA/jsonrpc/*";
        JSONRPCEntryPointServlet servlet;
        synchronized (tomcatHost) {
            servlet = (JSONRPCEntryPointServlet) tomcatHost.getMapping(jsonrpcServletMapping);
            if (servlet == null) {
                servlet = new JSONRPCEntryPointServlet();
                tomcatHost.registerMapping(jsonrpcServletMapping, servlet);
                tomcatHost.registerMapping(webAppName + "/SCA/scripts/*", new ScriptGetterServlet());
            }
        }
        return servlet;
    }

}
