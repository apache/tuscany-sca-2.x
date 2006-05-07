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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tuscany.core.context.EntryPointContext;

import com.metaparadigm.jsonrpc.JSONRPCBridge;
import com.metaparadigm.jsonrpc.JSONRPCServlet;

/**
 * 
 * 
 */
public class JSONRPCEntryPointServlet extends JSONRPCServlet {
    private static final long serialVersionUID = 1L;

    private transient List<EntryPointContext> entryPoints;
    
    public JSONRPCEntryPointServlet() {
        entryPoints = new ArrayList<EntryPointContext>();
    }

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

            JSONRPCBridge jsonrpcBridge = new JSONRPCBridge();
            for (EntryPointContext epc : entryPoints) {
                jsonrpcBridge.registerObject(epc.getName(), epc.getInstance(null));
            }
            session.setAttribute("JSONRPCBridge", jsonrpcBridge);

            super.service(request, response);

        } finally {
            session.removeAttribute("JSONRPCBridge");
        }
    }

    public void addEntryPoint(EntryPointContext epc) {
        entryPoints.add(epc);
    }
}
