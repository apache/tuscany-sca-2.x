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
package org.apache.tuscany.sca.console.handler.scdl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tuscany.spi.host.ServletHost;
import org.osoa.sca.annotations.EagerInit;

/**
 * Super class for all the Tuscany servlets.
 * 
 * @version $Revision$ $Date$
 *
 */
@SuppressWarnings("serial")
@EagerInit
public abstract class TuscanyServlet extends HttpServlet {

    /**
     * Injects the servlet host and path mapping.
     * 
     * @param servletHost Servlet host to use.
     * @param path Path mapping for the servlet.
     */
    public TuscanyServlet(ServletHost servletHost, String path) {
        servletHost.registerMapping(path, this);
    }
    
    /**
     * Processes the request.
     * 
     * @param req Servlet request.
     * @param res Servlet response.
     * @throws ServletException Servlet exception.
     * @throws IOException IO Exception.
     */
    protected abstract void process(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException;
    
    /**
     * Handles get requests.
     */
    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        process(req, res);
    }

    /**
     * Handles post request.
     */
    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        process(req, res);
    }

}
