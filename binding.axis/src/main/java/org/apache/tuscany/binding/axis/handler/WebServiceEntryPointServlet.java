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
package org.apache.tuscany.binding.axis.handler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.http.AxisServlet;

import org.apache.tuscany.binding.axis.config.AxisEngineConfigurationFactory;
import org.apache.tuscany.core.context.webapp.TuscanyWebAppRuntime;

/**
 */
public class WebServiceEntryPointServlet extends AxisServlet {

    /**
     *
     */
    public WebServiceEntryPointServlet() {
        super();
    }

    /**
     * @see org.apache.axis.transport.http.AxisServletBase#getEngine()
     */
    public AxisServer getEngine() throws AxisFault {
        AxisServer engine = null;
        ServletContext context = getServletContext();
        synchronized (context) {
            engine = (AxisServer) getServletContext().getAttribute(getServletName() + ".WebServiceEntryPointAxisEngine");
            if (engine == null) {
                Map environment = new HashMap();
                environment.put(AxisEngine.ENV_SERVLET_CONTEXT, context);
                String webInfPath = context.getRealPath("/WEB-INF");
                if (webInfPath != null)
                    environment.put(AxisEngine.ENV_SERVLET_REALPATH, webInfPath + File.separator + "attachments");
                TuscanyWebAppRuntime tuscanyRuntime = (TuscanyWebAppRuntime) context.getAttribute(TuscanyWebAppRuntime.class.getName());
                EngineConfiguration config = new AxisEngineConfigurationFactory(tuscanyRuntime).getServerEngineConfig();
                if (config != null) {
                    environment.put(EngineConfiguration.PROPERTY_NAME, config);
                }
                engine = AxisServer.getServer(environment);
                engine.setName(getServletName());
                context.setAttribute(getServletName() + ".WebServiceEntryPointAxisEngine", engine);
            }
        }
        return engine;
    }

    /**
     * @see org.apache.axis.transport.http.AxisServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doPost(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
        super.doPost(arg0, arg1);
    }

}
