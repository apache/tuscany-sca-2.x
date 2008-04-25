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

import static org.apache.tuscany.sca.domain.manager.impl.DomainAdminUtil.compositeQName;
import static org.apache.tuscany.sca.domain.manager.impl.DomainAdminUtil.compositeTitle;
import static org.apache.tuscany.sca.domain.manager.impl.DomainAdminUtil.contributionURI;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.implementation.data.collection.Entry;
import org.apache.tuscany.sca.implementation.data.collection.Item;
import org.apache.tuscany.sca.implementation.data.collection.ItemCollection;
import org.apache.tuscany.sca.implementation.data.collection.LocalItemCollection;
import org.apache.tuscany.sca.implementation.data.collection.NotFoundException;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * Implementation of a composite install image collection service. 
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
@Service(interfaces={ItemCollection.class, LocalItemCollection.class, Servlet.class})
public class CompositeImageCollectionImpl extends HttpServlet implements ItemCollection, LocalItemCollection, Servlet {
    private static final long serialVersionUID = 1L;

    private final static Logger logger = Logger.getLogger(CompositeImageCollectionImpl.class.getName());    

    @Reference
    public LocalItemCollection contributionCollection;
    
    @Reference
    public LocalItemCollection cloudCollection;
    
    /**
     * Initialize the component.
     */
    @Init
    public void initialize() {
    }
    
    public Entry<String, Item>[] getAll() {
        throw new UnsupportedOperationException();
    }

    public Item get(String key) throws NotFoundException {
        throw new UnsupportedOperationException();
    }

    public String post(String key, Item item) {
        throw new UnsupportedOperationException();
    }

    public void put(String key, Item item) throws NotFoundException {
        throw new UnsupportedOperationException();
    }

    public void delete(String key) throws NotFoundException {
        throw new UnsupportedOperationException();
    }
    
    public Entry<String, Item>[] query(String queryString) {
        logger.info("query " + queryString);
        
        if (queryString.startsWith("composite=")) {

            // Expecting a key in the form:
            // composite:contributionURI;namespace;localName
            int e = queryString.indexOf('=');
            String key = queryString.substring(e + 1);
            String contributionURI = contributionURI(key);
            QName qname = compositeQName(key);
            
            // Return a collection containing the following entries:
            // the resolved version of the specified composite
            // the required contributions
            List<Entry<String, Item>> entries = new ArrayList<Entry<String, Item>>();
            
            // Add the resolved composite entry
            Entry<String, Item> compositeEntry = new Entry<String, Item>();
            Item compositeItem = new Item();
            compositeItem.setTitle(compositeTitle(contributionURI, qname));
            compositeItem.setLink("/composite-resolved/" + key);
            compositeEntry.setKey(key);
            compositeEntry.setData(compositeItem);
            entries.add(compositeEntry);
            
            // Get the collection of required contributions
            Entry<String, Item>[] contributionEntries = contributionCollection.query("alldependencies=" + contributionURI);
            for (Entry<String, Item> entry: contributionEntries) {
                Item item = entry.getData();
                item.setContents(null);
                entries.add(entry);
            }

            return entries.toArray(new Entry[entries.size()]);
            
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Get the request path
        String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");
        String key = path.startsWith("/")? path.substring(1) : path;
        logger.info("get " + key);
        
        // The key contains a node name, redirect 
        // to the corresponding composite image
            
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
                    
                    // Redirect to its composite image
                    String compositeImage = "/composite-image/?composite=" + related.substring(i);
                    response.sendRedirect(compositeImage);
                    return;
                }
            }
        }
        
        // Node not found
        response.sendError(HttpServletResponse.SC_NOT_FOUND, key);
        return;
    }
}
