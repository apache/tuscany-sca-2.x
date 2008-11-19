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

import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.compositeQName;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.logging.Logger;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.Item;
import org.apache.tuscany.sca.data.collection.LocalItemCollection;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * Implementation of a service that returns a node configuration. 
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
@Service(Servlet.class)
public class NodeConfigurationServiceImpl extends HttpServlet implements Servlet {
    private static final long serialVersionUID = 6913769467386954463L;

    private static final Logger logger = Logger.getLogger(NodeConfigurationServiceImpl.class.getName());

    @Reference
    public LocalItemCollection contributionCollection;
    
    @Reference
    public LocalItemCollection cloudCollection;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Get the request path
        String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");
        String key = path.startsWith("/")? path.substring(1) : path;
        logger.fine("get " + key);
        
        // The key contains a node name, redirect 
        // to the corresponding composite config
            
        // Get the collection of cloud composites
        Entry<String, Item>[] cloudEntries = cloudCollection.getAll();

        // Find the specified node
        for (Entry<String, Item> cloudEntry: cloudEntries) {
            QName qname = compositeQName(cloudEntry.getKey());
            if (qname.getLocalPart().equals(key)) {
                
                // Found the specified node
                String related = cloudEntry.getData().getRelated();
                int i = related.indexOf("composite:");
                if (i != -1) {
                    
                    // Redirect to its composite config
                    String compositeConfiguration = "/composite-config/?composite=" + related.substring(i);
                    response.sendRedirect(compositeConfiguration);
                    return;
                }
            }
        }
        
        // Node not found
        response.sendError(HttpServletResponse.SC_NOT_FOUND, key);
        return;
    }
}
