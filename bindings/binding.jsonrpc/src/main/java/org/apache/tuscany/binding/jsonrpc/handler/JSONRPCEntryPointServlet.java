/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.tuscany.binding.jsonrpc.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tuscany.binding.jsonrpc.assembly.JSONRPCBinding;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.core.webapp.TuscanyServletListener;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Module;

import com.metaparadigm.jsonrpc.JSONRPCBridge;
import com.metaparadigm.jsonrpc.JSONRPCServlet;

/**
 * 
 * 
 */
public class JSONRPCEntryPointServlet extends JSONRPCServlet {
    private static final long serialVersionUID = 1L;

    private transient Map<String, Object> entryPointProxys;

    /*
     * (non-Javadoc)
     * 
     * @see com.metaparadigm.jsonrpc.JSONRPCServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ClassCastException {

        /*
         * Create a new bridge for every request to aviod all the problems with JSON-RPC-Java storing the bridge in the session
         */
        HttpSession session = request.getSession();
        try {
            session.setAttribute("JSONRPCBridge", createJSONRPCBridge());

            super.service(request, response);

        } finally {
            session.removeAttribute("JSONRPCBridge");
        }
    }

    /**
     * Creates a JSON-RPC-Java Bridge with the objects registered for all the JSON-RPC entryPoint proxys
     */
    protected JSONRPCBridge createJSONRPCBridge() {

        JSONRPCBridge json_bridge = new JSONRPCBridge();

        for (String entryPointName : entryPointProxys.keySet()) {
            Object entryPointProxy = entryPointProxys.get(entryPointName);
            json_bridge.registerObject(entryPointName, entryPointProxy);
        }

        return json_bridge;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.metaparadigm.jsonrpc.JSONRPCServlet#init(javax.servlet.ServletConfig)
     */
    @Override
    public void init(ServletConfig config) {
        super.init(config);
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        ClassLoader mycl = getClass().getClassLoader();
        try {
            if (tccl != mycl) {
                Thread.currentThread().setContextClassLoader(mycl);
            }

            initEntryPointProxys(config);

        } finally {
            if (tccl != mycl) {
                Thread.currentThread().setContextClassLoader(tccl);
            }
        }
    }

    /**
     * Initializes the entryPointProxys Map to contain the proxy objects for all the JSON-RPC entryPoints available in the SCA runtime
     * 
     * @param config
     */
    @SuppressWarnings("deprecation")
    protected void initEntryPointProxys(ServletConfig config) {

        ServletContext servletContext = config.getServletContext();
        CompositeContext moduleContext = (CompositeContext) servletContext.getAttribute(TuscanyServletListener.MODULE_COMPONENT_NAME);
        Module module = (Module) moduleContext.getComposite();

        this.entryPointProxys = new HashMap<String, Object>();

        for (EntryPoint entryPoint : module.getEntryPoints()) {
            if (hasJSONRPCBinding(entryPoint)) {
                String entryPointName = entryPoint.getName();
                EntryPointContext entryPointContext = (EntryPointContext) moduleContext.getContext(entryPointName);
                Object entryPointProxy = entryPointContext.getInstance(null);
                entryPointProxys.put(entryPointName, entryPointProxy);
            }
        }
    }

    /**
     * Tests if the EntryPoint has a JSONRPCBinding
     */
    protected boolean hasJSONRPCBinding(EntryPoint entryPoint) {
        for (Binding binding : entryPoint.getBindings()) {
            if (binding instanceof JSONRPCBinding) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the Map of entryPoint proxys
     */
    protected Map<String, Object> getEntryPointProxys() {
        return entryPointProxys;
    }

}
