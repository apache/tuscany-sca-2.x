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

package org.apache.tuscany.sca.workspace.admin.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.tuscany.sca.implementation.data.collection.Entry;
import org.apache.tuscany.sca.implementation.data.collection.Item;
import org.apache.tuscany.sca.implementation.data.collection.NotFoundException;

/**
 * Test case for the workspace admin services.
 *
 * @version $Rev$ $Date$
 */
public class AdminTestCase extends TestCase {
    
    private WorkspaceCollectionImpl workspaceCollection;
    
    private final static String WORKSPACE_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<workspace xmlns=\"http://tuscany.apache.org/xmlns/sca/1.0\" " +
        "xmlns:ns1=\"http://tuscany.apache.org/xmlns/sca/1.0\">\n" +
        "</workspace>";
    
    @Override
    protected void setUp() throws Exception {
        ClassLoader cl = getClass().getClassLoader();

        // Make sure we start with a clean workspace.xml file
        URL url = cl.getResource("workspace.xml");
        FileOutputStream os = new FileOutputStream(new File(url.toURI()));
        Writer writer = new OutputStreamWriter(os);
        writer.write(WORKSPACE_XML);
        writer.flush();
        
        // Create a workspace collection component
        workspaceCollection = new WorkspaceCollectionImpl();
        workspaceCollection.workspaceFileName = url.toString();
        workspaceCollection.init();
        
        // Populate the workspace with test data
        Item item = new Item();
        item.setLink(cl.getResource("contributions/store").toString());
        workspaceCollection.post("store", item);
        item.setLink(cl.getResource("contributions/assets").toString());
        workspaceCollection.post("assets", item);
    }
    
    public void testGetAll() {
        Entry<String, Item>[] entries = workspaceCollection.getAll();
        assertEquals(2, entries.length);
        assertEquals(entries[0].getKey(), "store");
    }

    public void testGet() throws NotFoundException {
        Item item = workspaceCollection.get("assets");
        assertTrue(item.getLink().endsWith("contributions/assets"));
    }
    
    public void testQuery() {
        Entry<String, Item>[] entries = workspaceCollection.query("importedBy=store");
        for (Entry<String, Item> entry: entries) {
            System.out.println(entry.getKey());
        }
    }
    
}
