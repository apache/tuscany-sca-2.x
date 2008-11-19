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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.ComponentReference;

public class JSONRPCScripProcessor implements ContextScriptProcessor {

    public void scriptInit(HttpServletRequest req, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        InputStream is = getClass().getClassLoader().getResourceAsStream("jsonrpc.js");
        if (is != null) {
            int i;
            while ((i = is.read()) != -1) {
                out.write(i);
            }           
        }
        
        out.println();
    }

    public void scriptReference(ComponentReference cr, HttpServletRequest req, HttpServletResponse response) throws IOException {
        for (Binding b : cr.getBindings()) {
            if ("org.apache.tuscany.sca.binding.jsonrpc.JSONRPCBinding".equals(b.getClass().getName())) {
               PrintWriter out = response.getWriter();
               out.println("SCA.componentContext.serviceNames.push('" + cr.getName() + "');");
               out.println("SCA.componentContext.serviceProxys.push(new JSONRpcClient('" + cr.getReference().getTargets().get(0).getName() + "').Service);");
            }
        }
    }

}
