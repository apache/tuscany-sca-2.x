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

package org.apache.tuscany.sca.domain.manager.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.logging.Logger;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.data.collection.Item;
import org.apache.tuscany.sca.data.collection.LocalItemCollection;
import org.apache.tuscany.sca.data.collection.NotFoundException;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * Implementation of a service that returns the source of a deployable composite. 
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
@Service(Servlet.class)
public class DeployableCompositeServiceImpl extends HttpServlet implements Servlet {
    private static final long serialVersionUID = -3477992129462720902L;

    private static final Logger logger = Logger.getLogger(DeployableCompositeServiceImpl.class.getName());

    @Reference
    public DomainManagerConfiguration domainManagerConfiguration;
    
    @Reference
    public LocalItemCollection deployableCollection;
    
    /**
     * Initialize the component.
     */
    @Init
    public void initialize() throws ParserConfigurationException {
        
        ExtensionPointRegistry extensionPoints = domainManagerConfiguration.getExtensionPoints();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Expect a key in the form
        // composite:contributionURI;namespace;localName
        // and return the corresponding source file
        
        // Get the request path
        String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");
        String key = path.startsWith("/")? path.substring(1) : path;
        logger.fine("get " + key);
        
        // Get the item describing the composite
        Item item;
        try {
            item = deployableCollection.get(key);
        } catch (NotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, key);
            return;
        }

        // Redirect if there is no composite file
        String uri = item.getAlternate();
        if (uri == null) {
            response.sendRedirect("/composite-generated/" + key);
            return;
        }
        
        // Read the composite file and write to response
        InputStream is;
        try {
            URLConnection connection = new URL(uri).openConnection();
            connection.setUseCaches(false);
            connection.connect();
            is = connection.getInputStream();
        } catch (FileNotFoundException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, key);
            return;
        }

        response.setContentType("text/xml");
        ServletOutputStream os = response.getOutputStream();
        byte[] buffer = new byte[4096];
        for (;;) {
            int n = is.read(buffer);
            if (n < 0) {
                break;
            }
            os.write(buffer, 0, n);
        }
        is.close();
        os.flush();
    }

}
