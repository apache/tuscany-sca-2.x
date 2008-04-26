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

import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.DEPLOYMENT_CONTRIBUTION_URI;
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.compositeKey;
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.compositeQName;
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.compositeSourceLink;
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.compositeTitle;
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.contributionURI;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.xml.CompositeProcessor;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.data.collection.Entry;
import org.apache.tuscany.sca.implementation.data.collection.Item;
import org.apache.tuscany.sca.implementation.data.collection.ItemCollection;
import org.apache.tuscany.sca.implementation.data.collection.LocalItemCollection;
import org.apache.tuscany.sca.implementation.data.collection.NotFoundException;
import org.apache.tuscany.sca.policy.PolicyFactory;
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
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
@Service(interfaces={ItemCollection.class,LocalItemCollection.class, Servlet.class})
public class DeployedCompositeCollectionImpl extends HttpServlet implements ItemCollection, LocalItemCollection {
    private static final long serialVersionUID = -3477992129462720901L;

    private final static Logger logger = Logger.getLogger(DeployedCompositeCollectionImpl.class.getName());    

    @Property
    public String compositeFile;
    
    @Property
    public String deploymentContributionDirectory;
    
    @Reference
    public LocalItemCollection deployableCollection;
    
    @Reference(required=false)
    public LocalItemCollection processCollection;

    private ModelFactoryExtensionPoint modelFactories;
    private AssemblyFactory assemblyFactory;
    private CompositeProcessor compositeProcessor;
    private XMLOutputFactory outputFactory;
    private DocumentBuilder documentBuilder;
    
    /**
     * Initialize the component.
     */
    @Init
    public void initialize() throws ParserConfigurationException {
        
        // Create factories
        modelFactories = new DefaultModelFactoryExtensionPoint();
        assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
        
        // Create composite processor
        ContributionFactory contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        PolicyFactory policyFactory = modelFactories.getFactory(PolicyFactory.class);
        compositeProcessor = new CompositeProcessor(contributionFactory, assemblyFactory, policyFactory, null);

        // Create a document builder (used to pretty print XML)
        documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }
    
    public Entry<String, Item>[] getAll() {
        logger.info("getAll");

        // Return all the composites in the domain composite
        List<Entry<String, Item>> entries = new ArrayList<Entry<String, Item>>();
        Composite compositeCollection = readCompositeCollection();
        for (Composite composite: compositeCollection.getIncludes()) {
            String contributionURI = composite.getURI();
            QName qname = composite.getName();
            String key = compositeKey(contributionURI, qname);
            Item item;
            try {
                item = deployableCollection.get(key);
            } catch (NotFoundException e) {
                item = new Item();
                item.setTitle(compositeTitle(contributionURI, qname));
                item.setLink(compositeSourceLink(contributionURI, qname));
                item.setContents("<span id=\"problem\" style=\"color: red\">Problem: Composite not found</span>");
            }
            Entry<String, Item> entry = new Entry<String, Item>();
            entry.setKey(key);
            entry.setData(item);
            entries.add(entry);
        }
        return entries.toArray(new Entry[entries.size()]);
    }

    public Item get(String key) throws NotFoundException {
        logger.info("get " + key);
        
        String contributionURI = contributionURI(key);
        QName qname = compositeQName(key);

        // Look for the specified composite in the domain composite
        List<Entry<String, Item>> entries = new ArrayList<Entry<String, Item>>();
        Composite compositeCollection = readCompositeCollection();
        for (Composite composite: compositeCollection.getIncludes()) {
            if (contributionURI.equals(composite.getURI()) && qname.equals(composite.getName())) {
                return deployableCollection.get(key);
            }
        }
        throw new NotFoundException(key);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Expect a key in the form
        // composite:contributionURI;namespace;localName
        // and return the corresponding source file
        
        // Get the request path
        String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");
        String key = path.startsWith("/")? path.substring(1) : path;
        logger.info("get " + key);
        
        // Get the item describing the composite
        Item item;
        try {
            item = deployableCollection.get(key);
        } catch (NotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, key);
            return;
        }

        // Support reading source composite file inside a JAR
        String uri = item.getAlternate();
        int e = uri.indexOf("!/"); 
        if (e != -1) {
            int s = uri.lastIndexOf('/', e - 2) +1;
            if (uri.substring(s, e).contains(".")) {
                uri = "jar:" + uri;
            } else {
                uri = uri.substring(0, e) + uri.substring(e + 1);
            }
        }
        
        // Read the composite file and write to response
        response.setContentType("text/xml");
        URLConnection connection = new URL(uri).openConnection();
        connection.setUseCaches(false);
        connection.connect();
        InputStream is = connection.getInputStream();
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

    public String post(String key, Item item) {
        logger.info("post " + key);
        
        String contributionURI = contributionURI(key);
        QName qname = compositeQName(key);

        // Adds a new composite to the domain composite
        Composite compositeCollection = readCompositeCollection();
        Composite composite = assemblyFactory.createComposite();
        composite.setName(qname);
        composite.setURI(contributionURI);
        composite.setUnresolved(true);
        compositeCollection.getIncludes().add(composite);
        
        // Optionally, write the composite contents in a new composite file
        // under the deployment composites directory, if that directory is
        // configured on this component
        if (deploymentContributionDirectory != null && item.getContents() != null) {
            File directory = new File(deploymentContributionDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, qname.getLocalPart() + ".composite");
            try {
                Writer w = new OutputStreamWriter(new FileOutputStream(file));
                w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                w.write(item.getContents());
                w.close();
            } catch (IOException e) {
                throw new ServiceRuntimeException(e);
            }
        }
        
        // Write the composite collection
        writeCompositeCollection(compositeCollection);
        
        return key;
    }

    public void put(String key, Item item) throws NotFoundException {
        logger.info("put " + key);

        String contributionURI = contributionURI(key);
        QName qname = compositeQName(key);
        
        // Update a composite already in the domain composite
        Composite compositeCollection = readCompositeCollection();
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
                writeCompositeCollection(compositeCollection);
                
                return;
            }
        }
        throw new NotFoundException(key);
    }

    public void delete(String key) throws NotFoundException {
        logger.info("delete " + key);
        
        String contributionURI = contributionURI(key);
        QName qname = compositeQName(key);
        
        // Delete/stop the corresponding process, if any
        try {
            processCollection.delete(qname.getLocalPart());
        } catch (Exception e) {}
        
        // Delete a composite from the composite collection
        Composite compositeCollection = readCompositeCollection();
        List<Composite> composites = compositeCollection.getIncludes();
        Composite deleted = null;
        for (int i = 0, n = composites.size(); i < n; i++) {
            Composite composite = composites.get(i);
            if (contributionURI.equals(composite.getURI()) && qname.equals(composite.getName())) {
                composites.remove(i);
                deleted = composite;
                
                // Write the domain composite
                writeCompositeCollection(compositeCollection);
                break;
            }
        }

        // Delete the file too if it is in the deployment contribution directory
        if (deploymentContributionDirectory != null && contributionURI.equals(DEPLOYMENT_CONTRIBUTION_URI)) {
            File file = new File(deploymentContributionDirectory, qname.getLocalPart() + ".composite");
            if (file.exists()) {
                file.delete();
            }
        }
        
        if (deleted == null) {
            throw new NotFoundException(key);
        }
    }

    public Entry<String, Item>[] query(String queryString) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Reads the domain composite.
     * 
     * @return the domain composite
     * @throws ServiceRuntimeException
     */
    private Composite readCompositeCollection() throws ServiceRuntimeException {
        Composite compositeCollection;
        File file = new File(compositeFile);
        if (file.exists()) {
            XMLInputFactory inputFactory = modelFactories.getFactory(XMLInputFactory.class);
            try {
                FileInputStream is = new FileInputStream(file);
                XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
                compositeCollection = compositeProcessor.read(reader);
            } catch (Exception e) {
                throw new ServiceRuntimeException(e);
            }
        } else {
            compositeCollection = assemblyFactory.createComposite();
            String name;
            int d = compositeFile.lastIndexOf('.');
            if (d != -1) {
                name = compositeFile.substring(0, d);
            } else {
                name = compositeFile;
            }
            compositeCollection.setName(new QName(Constants.SCA10_TUSCANY_NS, name));
        }
        return compositeCollection;
    }
    
    /**
     * Write the domain composite back to disk.
     * 
     * @param compositeCollection
     */
    private void writeCompositeCollection(Composite compositeCollection) {
        try {
            // First write to a byte stream
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(bos);
            compositeProcessor.write(compositeCollection, writer);
            
            // Parse again to pretty format the document
            Document document = documentBuilder.parse(new ByteArrayInputStream(bos.toByteArray()));
            OutputFormat format = new OutputFormat();
            format.setIndenting(true);
            format.setIndent(2);
            
            // Write to domain.composite
            FileOutputStream os = new FileOutputStream(new File(compositeFile));
            XMLSerializer serializer = new XMLSerializer(os, format);
            serializer.serialize(document);
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

}
