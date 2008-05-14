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

import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.compositeKey;
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.compositeQName;
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.contributionURI;
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.nodeURI;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tuscany.sca.implementation.data.collection.Entry;
import org.apache.tuscany.sca.implementation.data.collection.Item;
import org.apache.tuscany.sca.implementation.data.collection.LocalItemCollection;
import org.apache.tuscany.sca.implementation.data.collection.NotFoundException;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * Implementation of a component that provides a quick start path for a
 * composite in a contribution. 
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
@Service(interfaces={Servlet.class})
public class QuickStartServiceImpl extends HttpServlet {
    private static final long serialVersionUID = -3477992129462720901L;

    private static final Logger logger = Logger.getLogger(QuickStartServiceImpl.class.getName());

    @Reference
    public LocalItemCollection contributionCollection;
    
    @Reference
    public LocalItemCollection deployableCollection;

    @Reference 
    public LocalItemCollection domainCompositeCollection;
    
    @Reference
    public LocalItemCollection cloudCollection;
    
    @Reference
    public LocalItemCollection processCollection;
    
    /**
     * Initialize the component.
     */
    @Init
    public void initialize() throws ParserConfigurationException {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            
            // Get the request path
            String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            
            // Get the request parameters
            String contributionURI = request.getParameter("contribution");
            String contributionLocation = request.getParameter("location");
            String compositeURI = request.getParameter("composite");
            String start = request.getParameter("start");
    
            logger.fine("Composite Quick Start.");
            logger.fine("Contribution URI: " + contributionURI);
            logger.fine("Contribution location: " + contributionLocation);
            logger.fine("Composite URI: " + compositeURI);
            
            // Look for the contribution in the workspace
            Entry<String, Item>[] contributionEntries = contributionCollection.getAll();
            Entry<String, Item> contributionEntry = null;
            for (Entry<String, Item> entry: contributionEntries) {
                if (contributionURI.equals(entry.getKey())) {
                    contributionEntry = entry;
                    break;
                }
            }
            
            // Add the contribution if necessary
            if (contributionEntry == null) {
                Item item = new Item();
                item.setLink(contributionLocation);
                contributionCollection.post(contributionURI, item);
            }
            
            // Look for the specified deployable composite in the contribution
            String compositeKey = null;
            Entry<String, Item>[] deployableEntries = deployableCollection.query("contribution=" + contributionURI);
            for (Entry<String, Item> entry: deployableEntries) {
                Item item = entry.getData();
                if (contributionURI.equals(contributionURI(entry.getKey())) && item.getAlternate().endsWith(compositeURI)) {
                    compositeKey = entry.getKey();
                    break;
                }
            }
            
            if (compositeKey == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, compositeURI);
                return;
            }
            
            // Look for the deployable composite in the domain composite
            try {
                domainCompositeCollection.get(compositeKey);
            } catch (NotFoundException e) {
    
                // Add the deployable composite to the domain composite
                Item item = new Item();
                domainCompositeCollection.post(compositeKey, item);
            }
    
            // Check if the deployable composite is already assigned a node
            Entry<String, Item>[] nodeEntries = cloudCollection.getAll();
            String nodeName = null;
            for (Entry<String, Item> entry: nodeEntries) {
                Item item = entry.getData();
                String related = item.getRelated();
                if (related != null) {
                    int c = related.indexOf("composite:");
                    related = related.substring(c);
                    if (compositeKey.equals(related)) {
                        nodeName = compositeQName(entry.getKey()).getLocalPart();
                    }
                }
            }
            
            // Create a new node for the composite if necessary
            if (nodeName == null) {
                
                // Construct node name and key
                QName compositeName = compositeQName(compositeKey); 
                nodeName = compositeName.getLocalPart() + "Node"; 
                String nodeKey = compositeKey("http://tuscany.apache.org/cloud", new QName("http://tuscany.apache.org/cloud", nodeName));
                
                // Find a free node port
                Set<Integer> nodePorts = new HashSet<Integer>(); 
                for (Entry<String, Item> entry: nodeEntries) {
                    Item item = entry.getData();
                    String uri = nodeURI(item.getContents());
                    if (uri != null) {
                        URI u = URI.create(uri);
                        int port = u.getPort();
                        if (port != -1) {
                            nodePorts.add(port);
                        }
                    }
                }
                String nodeURI = null;
                for (int port = 8100; port<8200; port++) {
                    if (!nodePorts.contains(port)) {
                        nodeURI = "http://localhost:" + port;
                        break;
                    }
                }
                if (nodeURI == null) {
                    throw new RuntimeException("Couldn't find a free port for new node: " + nodeName);
                }
                
                // Build the entry describing the node
                Item item = new Item();
                String content = 
                                "<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\"\n" +
                                "       xmlns:t=\"http://tuscany.apache.org/xmlns/sca/1.0\"\n" +
                                "       targetNamespace=\"http://tuscany.apache.org/cloud\"\n" +
                                "       xmlns:c=\"" + compositeName.getNamespaceURI() + "\"\n" +
                                "       name=\"" + nodeName + "\">\n" +
                                "\n" +
                                "       <component name=\"" + nodeName + "\">\n" +
                                "               <t:implementation.node uri=\"" + contributionURI + "\" composite=\"c:" + compositeName.getLocalPart() + "\"/>\n" +
                                "               <service name=\"Node\">\n" +
                                "                       <binding.ws uri=\"" + nodeURI + "\"/>\n" +
                                "                       <t:binding.http uri=\"" + nodeURI + "\"/>\n" +
                                "                       <t:binding.jsonrpc uri=\"" + nodeURI + "\"/>\n" +
                                "                       <t:binding.atom uri=\"" + nodeURI + "\"/>\n" +
                                "               </service>\n" +
                                "       </component>\n" + 
                                "</composite>";
                item.setContents(content);
    
                // Create the new node
                cloudCollection.post(nodeKey, item);
            }
            
            // Finally, start the node
            if ("true".equals(start)) {
                processCollection.post(nodeName, new Item());
            }
            
            response.getWriter().print("<html><body>Node <span id=\"node\">" + nodeName + "</span> OK.</body></html>");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not start composite", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
        }
    }
    
}
