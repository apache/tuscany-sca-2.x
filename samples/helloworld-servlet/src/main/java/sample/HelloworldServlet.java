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
package sample;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osoa.sca.ComponentContext;
import org.osoa.sca.annotations.Reference;

/**
 */
public class HelloworldServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Reference
    protected HelloworldService service;

    @Override
    public void init(ServletConfig config) {
        if (service == null) {
            // The helloworldService reference will only be injected from the @Reference 
            // annotation in containers supporting SCA "deep" integration. In other 
            // environments in can be looked up from the ComponentContext.
            ComponentContext cc = (ComponentContext)config.getServletContext().getAttribute("org.osoa.sca.ComponentContext");
            service = cc.getService(HelloworldService.class, "service");
        }
    }
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {

    	String name = request.getParameter("name");
    	String greeting = service.sayHello(name);
    	
        Writer out = response.getWriter();
        out.write("<html><head><title>Apache Tuscany Helloworld Servlet Sample</title></head><body>");
        out.write("<h2>Apache Tuscany Helloworld Servlet Sample</h2>");
        out.write("<br><strong>Result: </strong>" + greeting);
        out.write("</body></html>");
        out.flush();
        out.close();
    }
}
