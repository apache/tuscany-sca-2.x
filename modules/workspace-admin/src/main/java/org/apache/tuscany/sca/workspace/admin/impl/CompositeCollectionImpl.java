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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.xml.CompositeProcessor;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.implementation.data.collection.Entry;
import org.apache.tuscany.sca.implementation.data.collection.Item;
import org.apache.tuscany.sca.implementation.data.collection.NotFoundException;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.workspace.admin.CompositeCollection;
import org.apache.tuscany.sca.workspace.admin.LocalCompositeCollection;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;
import org.w3c.dom.Document;

/**
 * Implementation of a composite collection service. 
 *
 * @version $Rev: 632617 $ $Date: 2008-03-01 08:24:33 -0800 (Sat, 01 Mar 2008) $
 */
@Scope("COMPOSITE")
@Service(CompositeCollection.class)
public class CompositeCollectionImpl implements CompositeCollection {
    
    @Property
    public String compositeFileName;
    
    @Reference
    public LocalCompositeCollection deployableCompositeCollection;

    private AssemblyFactory assemblyFactory;
    private Composite compositeCollection;
    private CompositeProcessor compositeProcessor;
    private XMLOutputFactory outputFactory;
    
    /**
     * Initialize the component.
     */
    @Init
    public void initialize() throws IOException, ContributionReadException, XMLStreamException, ParserConfigurationException {
        
        // Create factories
        ModelFactoryExtensionPoint modelFactories = new DefaultModelFactoryExtensionPoint();
        assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
        
        // Read domain.composite
        ContributionFactory contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        PolicyFactory policyFactory = modelFactories.getFactory(PolicyFactory.class);
        compositeProcessor = new CompositeProcessor(contributionFactory, assemblyFactory, policyFactory, null);
        File file = new File(compositeFileName);
        if (file.exists()) {
            XMLInputFactory inputFactory = modelFactories.getFactory(XMLInputFactory.class);
            FileInputStream is = new FileInputStream(file);
            XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
            compositeCollection = compositeProcessor.read(reader);
        } else {
            compositeCollection = assemblyFactory.createComposite();
            compositeCollection.setName(new QName(Constants.SCA10_TUSCANY_NS, compositeFileName));
        }
    }
    
    public Entry<String, Item>[] getAll() {
        // Return all the composites in the domain composite
        List<Entry<String, Item>> entries = new ArrayList<Entry<String, Item>>();
        for (Composite composite: compositeCollection.getIncludes()) {
            Entry<String, Item> entry = new Entry<String, Item>();
            String contributionURI = composite.getURI();
            QName qname = composite.getName();
            String key = key(contributionURI, qname); 
            entry.setKey(key);
            Item item = new Item();
            item.setTitle(title(contributionURI, qname));
            item.setLink(compositeLink(contributionURI, qname));
            entry.setData(item);
            entries.add(entry);
        }
        return entries.toArray(new Entry[entries.size()]);
    }

    public Item get(String key) throws NotFoundException {
        String contributionURI = uri(key);
        QName qname = qname(key);

        // Returns the composite with the given name key
        for (Composite composite: compositeCollection.getIncludes()) {
            if (contributionURI.equals(composite.getURI()) && qname.equals(composite.getName())) {
                Item item = new Item();
                item.setTitle(title(contributionURI, qname));
                item.setLink(compositeLink(composite.getURI(), qname));
                return item;
            }
        }
        throw new NotFoundException(key);
    }

    public String post(String key, Item item) {
        String contributionURI = uri(key);
        QName qname = qname(key);
        
        // Adds a new composite to the domain composite
        Composite composite = assemblyFactory.createComposite();
        composite.setName(qname);
        composite.setURI(contributionURI);
        composite.setUnresolved(true);
        compositeCollection.getIncludes().add(composite);
        
        // Write the domain composite
        write();
        
        return key;
    }

    public void put(String key, Item item) throws NotFoundException {
        String contributionURI = uri(key);
        QName qname = qname(key);
        
        // Update a composite already in the domain composite
        Composite newComposite = assemblyFactory.createComposite();
        newComposite.setName(qname);
        newComposite.setURI(contributionURI);
        newComposite.setUnresolved(true);
        List<Composite> composites = compositeCollection.getIncludes();
        for (int i = 0, n = composites.size(); i < n; i++) {
            Composite composite = composites.get(i);
            if (contributionURI.equals(composite.getURI()) && qname.equals(composite.getName())) {
                composites.set(i, newComposite);
                
                // Write the domain composite
                write();
                
                return;
            }
        }
        throw new NotFoundException(key);
    }

    public void delete(String key) throws NotFoundException {
        String contributionURI = uri(key);
        QName qname = qname(key);
        
        // Delete a composite from the domain composite
        List<Composite> composites = compositeCollection.getIncludes();
        for (int i = 0, n = composites.size(); i < n; i++) {
            Composite composite = composites.get(i);
            if (contributionURI.equals(composite.getURI()) && qname.equals(composite.getName())) {
                composites.remove(i);

                // Write the domain composite
                write();
                
                return;
            }
        }
        throw new NotFoundException(key);
    }

    public Entry<String, Item>[] query(String queryString) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Write the domain composite back to disk
     */
    private void write() {
        try {
            // First write to a byte stream
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(bos);
            compositeProcessor.write(compositeCollection, writer);
            
            // Parse again to pretty format the document
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(new ByteArrayInputStream(bos.toByteArray()));
            OutputFormat format = new OutputFormat();
            format.setIndenting(true);
            format.setIndent(2);
            
            // Write to domain.composite
            FileOutputStream os = new FileOutputStream(new File(compositeFileName));
            XMLSerializer serializer = new XMLSerializer(os, format);
            serializer.serialize(document);
            
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }
    
    private String compositeLink(String contributionURI, QName qname) {
        String key = key(contributionURI, qname);
        Entry<String, Item>[] entries = deployableCompositeCollection.query("contribution=" + contributionURI);
        for (Entry<String, Item> entry: entries) {
            if (key.equals(entry.getKey())) {
                return entry.getData().getLink();
            }
        }
        return null;
    }
    
    /**
     * Extracts a qname from a key expressed as contributionURI;namespace;localpart.
     * @param key
     * @return
     */
    private static QName qname(String key) {
        int i = key.indexOf(';');
        key = key.substring(i + 1);
        i = key.indexOf(';');
        return new QName(key.substring(0, i), key.substring(i + 1));
    }
    
    /**
     * Extracts a contribution uri from a key expressed as contributionURI;namespace;localpart.
     * @param key
     * @return
     */
    private static String uri(String key) {
        int i = key.indexOf(';');
        return key.substring("composite:".length(), i);
    }
    
    /**
     * Returns a composite key expressed as contributionURI;namespace;localpart.
     * @param qname
     * @return
     */
    private static String key(String uri, QName qname) {
        return "composite:" + uri + ';' + qname.getNamespaceURI() + ';' + qname.getLocalPart();
    }

    /**
     * Returns a composite title expressed as contributionURI - namespace;localpart.
     * @param qname
     * @return
     */
    private static String title(String uri, QName qname) {
        return uri + " - " + qname.getNamespaceURI() + ';' + qname.getLocalPart();
    }

}
