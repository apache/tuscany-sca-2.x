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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.Item;
import org.apache.tuscany.sca.data.collection.NotFoundException;

/**
 * Test case for the workspace admin services.
 *
 * @version $Rev$ $Date$
 */
public class DomainManagerTestCase extends TestCase {

    private ContributionCollectionImpl contributionCollection;
    private DeployableCompositeCollectionImpl deployableCollection;
    private DomainManagerConfigurationImpl domainManagerConfiguration;
    
    private static final String WORKSPACE_XML =
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
        writer.close();
        
        // Create a workspace collection component
        domainManagerConfiguration = new DomainManagerConfigurationImpl();
        domainManagerConfiguration.initialize();
        String root = url.getFile();
        root = root.substring(0, root.lastIndexOf('/'));
        domainManagerConfiguration.setRootDirectory(root);
        
        contributionCollection = new ContributionCollectionImpl();
        contributionCollection.domainManagerConfiguration = domainManagerConfiguration;
        contributionCollection.workspaceFile = "workspace.xml";
        contributionCollection.deploymentContributionDirectory = "cloud";
        contributionCollection.domainManagerConfiguration = domainManagerConfiguration;
        deployableCollection = new DeployableCompositeCollectionImpl();
        deployableCollection.domainManagerConfiguration = domainManagerConfiguration;
        deployableCollection.contributionCollection = contributionCollection;
        contributionCollection.initialize();
        deployableCollection.initialize();
        
        // Populate the workspace with test data
        Item item = new Item();
        item.setLink(cl.getResource("contributions/store").toString());
        contributionCollection.post("store", item);
        item.setLink(cl.getResource("contributions/assets").toString());
        contributionCollection.post("assets", item);
    }
    
    public void testGetAll() {
        Entry<String, Item>[] entries = contributionCollection.getAll();
        assertEquals(2, entries.length);
        assertEquals(entries[0].getKey(), "store");
    }

    public void testGet() throws NotFoundException {
        Item item = contributionCollection.get("assets");
        assertTrue(item.getAlternate().endsWith("contributions/assets/"));
    }
    
    public void testDependencies1() {
        Entry<String, Item>[] entries = contributionCollection.query("alldependencies=store");
        assertEquals(2, entries.length);
    }
    
    public void testDependencies2() {
        Entry<String, Item>[] entries = contributionCollection.query("alldependencies=assets");
        assertEquals(1, entries.length);
        assertEquals("assets", entries[0].getKey());
    }
    
    public void testDeployables() throws NotFoundException {
        Entry<String, Item>[] entries = deployableCollection.getAll();
        assertEquals(1, entries.length);
        assertEquals("composite:store;http://store;store", entries[0].getKey());
    }
    
}
