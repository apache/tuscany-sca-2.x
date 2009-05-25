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

package org.apache.tuscany.sca.war;

import java.io.File;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InstallerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private transient ServletConfig servletConfig;
    private transient Installer installer;

    public void init(ServletConfig servletConfig) throws ServletException {
        this.servletConfig = servletConfig;
        String path = servletConfig.getServletContext().getRealPath("/");
        File tuscanyWarDir = null;
        if (path != null) {
            tuscanyWarDir = new File(path);
        }
        File tomcatBase = new File(System.getProperty("catalina.base"));
        installer = new Installer(tuscanyWarDir, tomcatBase);
    }

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        doIt(httpServletRequest, httpServletResponse);
    }

    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        doIt(httpServletRequest, httpServletResponse);
    }

    protected void doIt(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        if ("Install".equalsIgnoreCase(req.getParameter("action"))) {
            installer.install();
        } else if ("Uninstall".equalsIgnoreCase(req.getParameter("action"))) {
            installer.uninstall();
        } 

        req.setAttribute("installer", installer);
        RequestDispatcher rd = servletConfig.getServletContext().getRequestDispatcher("/installer.jsp");
        rd.forward(req,res);
    }

}
