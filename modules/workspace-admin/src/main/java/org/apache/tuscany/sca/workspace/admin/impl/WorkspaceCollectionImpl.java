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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.DefaultContributionFactory;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.implementation.data.collection.Entry;
import org.apache.tuscany.sca.implementation.data.collection.Item;
import org.apache.tuscany.sca.implementation.data.collection.NotFoundException;
import org.apache.tuscany.sca.workspace.DefaultWorkspaceFactory;
import org.apache.tuscany.sca.workspace.Workspace;
import org.apache.tuscany.sca.workspace.WorkspaceFactory;
import org.apache.tuscany.sca.workspace.admin.WorkspaceCollection;
import org.apache.tuscany.sca.workspace.xml.WorkspaceProcessor;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;
import org.w3c.dom.Document;

/**
 * Implementation of the contribution workspace administration service component. 
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
public class WorkspaceCollectionImpl implements WorkspaceCollection {

    private ContributionFactory contributionFactory;
    private WorkspaceFactory workspaceFactory;
    private Workspace workspace;
    private WorkspaceProcessor workspaceProcessor;
    private XMLOutputFactory outputFactory;
    private DocumentBuilder documentBuilder;
    
    /**
     * Initialize the workspace administration component.
     */
    @Init
    public void init() throws IOException, ContributionReadException, XMLStreamException, ParserConfigurationException {
        
        // Create factories
        contributionFactory = new DefaultContributionFactory();
        workspaceFactory = new DefaultWorkspaceFactory();
        outputFactory = XMLOutputFactory.newInstance();
        outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.TRUE);
        documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        
        // Read workspace.xml
        workspaceProcessor = new WorkspaceProcessor(workspaceFactory, contributionFactory, null);
        File file = new File("workspace.xml");
        if (file.exists()) {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            FileInputStream is = new FileInputStream(file);
            XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
            workspace = workspaceProcessor.read(reader);
        } else {
            workspace = workspaceFactory.createWorkspace();
        }
    }
    
    public Entry<String, Item>[] getAll() {
        // Return all the contributions
        List<Entry<String, Item>> entries = new ArrayList<Entry<String, Item>>();
        for (Contribution contribution: workspace.getContributions()) {
            Entry<String, Item> entry = new Entry<String, Item>();
            entry.setKey(contribution.getURI());
            Item item = new Item();
            item.setTitle(contribution.getURI());
            item.setLink(contribution.getLocation());
            entry.setData(item);
            entries.add(entry);
        }
        return entries.toArray(new Entry[entries.size()]);
    }

    public Item get(String key) throws NotFoundException {

        // Returns the contribution with the given URI key
        for (Contribution contribution: workspace.getContributions()) {
            if (key.equals(contribution.getURI())) {
                Item item = new Item();
                item.setTitle(contribution.getURI());
                item.setLink(contribution.getLocation());
                return item;
            }
        }
        throw new NotFoundException(key);
    }

    public String post(String key, Item item) {
        
        // Adds a new contribution to the workspace
        Contribution contribution = contributionFactory.createContribution();
        contribution.setURI(key);
        contribution.setLocation(item.getLink());
        workspace.getContributions().add(contribution);
        
        // Write the workspace
        write();
        
        return key;
    }

    public void put(String key, Item item) throws NotFoundException {
        
        // Update a contribution already in the workspace
        Contribution newContribution = contributionFactory.createContribution();
        newContribution.setURI(key);
        newContribution.setLocation(item.getLink());
        List<Contribution> contributions = workspace.getContributions();
        for (int i = 0, n = contributions.size(); i < n; i++) {
            if (contributions.get(i).getURI().equals(key)) {
                contributions.set(i, newContribution);
                
                // Write the workspace
                write();
                
                return;
            }
        }
        throw new NotFoundException(key);
    }

    public void delete(String key) throws NotFoundException {
        
        // Delete a contribution from the workspace
        List<Contribution> contributions = workspace.getContributions();
        for (int i = 0, n = contributions.size(); i < n; i++) {
            if (contributions.get(i).getURI().equals(key)) {
                contributions.remove(i);

                // Write the workspace
                write();
                
                return;
            }
        }
        throw new NotFoundException(key);
    }

    public Entry<String, Item>[] query(String queryString) {
        
        //TODO Implement later
        return null;
    }
    
    /**
     * Write the workspace back to disk
     */
    private void write() {
        try {
            // First write to a byte stream
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(bos);
            workspaceProcessor.write(workspace, writer);
            
            // Parse again to pretty format the document
            Document document = documentBuilder.parse(new ByteArrayInputStream(bos.toByteArray()));
            OutputFormat format = new OutputFormat();
            format.setIndenting(true);
            format.setIndent(2);
            
            // Write to workspace.xml
            FileOutputStream os = new FileOutputStream("workspace.xml");
            XMLSerializer serializer = new XMLSerializer(os, format);
            serializer.serialize(document);
            
        } catch (FileNotFoundException e) {
            throw new ServiceRuntimeException(e);
        } catch (ContributionWriteException e) {
            throw new ServiceRuntimeException(e);
        } catch (XMLStreamException e) {
            throw new ServiceRuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
