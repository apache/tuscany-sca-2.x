/**
 *
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.tuscany.tomcat.integration;

import java.io.IOException;
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.osoa.sca.CurrentModuleContext;
import org.osoa.sca.ModuleContext;

/**
 * @version $Rev$ $Date$
 */
@SuppressWarnings({"serial"})
public class TestServlet extends GenericServlet {

    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        Object runtime = getServletContext().getAttribute("org.apache.tuscany.core.runtime.RuntimeContext");
        if (runtime == null || "org.apache.tuscany.core.runtime.RuntimeContext".equals(runtime.getClass().getName())) {
            throw new ServletException("Runtime not bound to org.apache.tuscany.core.runtime.RuntimeContext");
        }

        Object module = getServletContext().getAttribute("org.apache.tuscany.core.webapp.ModuleComponentContext");
        if (module == null || "org.apache.tuscany.core.context.CompositeContext".equals(module.getClass().getName())) {
            throw new ServletException("Module composite not bound to org.apache.tuscany.core.webapp.ModuleComponentContext");
        }

        ModuleContext moduleContext = CurrentModuleContext.getContext();
        if (moduleContext == null) {
            throw new ServletException("No module context returned");
        }
        String name = moduleContext.getName();
        if (!"testContext".equals(name)) {
            throw new ServletException("Invalid module context name: " + name);
        }

        HelloWorldService helloService = (HelloWorldService) moduleContext.locateService("HelloWorld");
        String greetings = helloService.getGreetings("World");
        if (!"Hello World".equals(greetings)) {
            throw new ServletException("Serivce returned " + greetings);
        }
    }
}
