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
package org.apache.tuscany.sca.implementation.widget;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

/**
 * Servlet to handle requests for ..
 * 
 * @version $Rev$ $Date$
 */
public class WidgetReferenceServlet extends HttpServlet {
    protected transient Map<String, String> proxyRegistry = new HashMap<String, String>();
    protected transient RuntimeComponent component;

    public WidgetReferenceServlet(RuntimeComponent component) {
        proxyRegistry.put("org.apache.tuscany.sca.binding.feed.impl.AtomBindingImpl", "binding-atom.js");
        //proxyRegistry.put("org.apache.tuscany.sca.binding.feed.impl.AtomBindingImpl", "binding-jsonrpc.js");
        this.component = component;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        ServletOutputStream os = response.getOutputStream();

        os.println();
        os.println("/* Apache Tuscany - SCA Web Widget */");
        os.println();

        writeSCAWidgetCode(os, request.getServletPath());
    }

    /**
     */
    protected void writeSCAWidgetCode(ServletOutputStream out, String path) throws IOException {
        out.println();
        out.println("/* Apache Tuscany SCA Widget header */");
        out.println();
        
        // remove the leading slash '/' character
        path = path.substring(1);

        for(ComponentReference reference : component.getReferences()) {
            String referenceName = reference.getName();
            out.println("Reference::" + referenceName);
            for(Binding binding : reference.getBindings()) {
                out.println("::Bind::" + binding.getName());
                out.println("::Bind class::" + binding.getClass());
                
                String bindingProxyName = proxyRegistry.get(binding.getClass().getName());
                if(bindingProxyName != null) {
                    writeJavaScriptBindingProxy(out,bindingProxyName);
                }
            }
        }
        
        writeJavaScriptReferenceFunction(out);
        
       
        out.println();
        out.println("/** End of Apache Tuscany SCA Widget */");
        out.println();
    }

    /**
     * Retrieve the binding proxy based on the bind name
     * and embedded the javascript into this js
     */
    protected void writeJavaScriptBindingProxy(ServletOutputStream os, String bindingProxyName) throws IOException {
        
        URL url = getClass().getClassLoader().getResource(bindingProxyName); //Thread.currentThread().getContextClassLoader().getResource(bindingProxyName);
        InputStream is = url.openStream();
        int i;
        while ((i = is.read()) != -1) {
            os.write(i);
        }
        os.println();
        os.println();
    }
    
    protected void writeJavaScriptReferenceFunction (ServletOutputStream os) throws IOException {
        
        os.println("function Reference(name) {");
        os.println("    return proxy[name];");
        os.println("}");
    }

    /*
    public void addService(String serviceName) {
        serviceNames.add(serviceName);
    }

    public void removeService(String serviceName) {
        serviceNames.remove(serviceName);
    }

    public List<String> getServiceNames() {
        return serviceNames;
    }
    */

}
