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
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.contributionURI;
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.lastModified;
import static org.apache.tuscany.sca.domain.manager.impl.DomainManagerUtil.locationURL;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.DefaultModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.data.collection.Item;
import org.apache.tuscany.sca.data.collection.LocalItemCollection;
import org.apache.tuscany.sca.data.collection.NotFoundException;
import org.apache.tuscany.sca.domain.manager.impl.CompositeGeneratorServiceImpl.Cache.ContributionCache;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.workspace.processor.impl.ContributionContentProcessor;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * Implementation of a service that generates a composite from a composite model. 
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
@Service(interfaces={Servlet.class})
public class CompositeGeneratorServiceImpl extends HttpServlet implements Servlet {
    private static final long serialVersionUID = -6531448326726908269L;

    private static final Logger logger = Logger.getLogger(CompositeGeneratorServiceImpl.class.getName());

    @Reference
    public LocalItemCollection contributionCollection;
    
    @Reference
    public DomainManagerConfiguration domainManagerConfiguration;
    
    private ModelFactoryExtensionPoint modelFactories;
    private ModelResolverExtensionPoint modelResolvers;
    private URLArtifactProcessor<Contribution> contributionProcessor;
    private StAXArtifactProcessor<Composite> compositeProcessor;
    private XMLOutputFactory outputFactory;
    
    /**
     * Cache contribution models. 
     */
    static class Cache {
        static class ContributionCache {
            private Contribution contribution;
            private long contributionLastModified;
        }
        private Map<URL, ContributionCache> contributions = new HashMap<URL, ContributionCache>();
    }
    
    private Cache cache = new Cache();
    
    /**
     * Initialize the component.
     */
    @Init
    public void initialize() throws ParserConfigurationException {
        
        ExtensionPointRegistry extensionPoints = domainManagerConfiguration.getExtensionPoints();
        
        // Create a monitor
        UtilityExtensionPoint services = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = services.getUtility(MonitorFactory.class);
        Monitor monitor = monitorFactory.createMonitor();
        
        // Get model factories
        modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
        XMLInputFactory inputFactory = modelFactories.getFactory(XMLInputFactory.class);
        outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
        
        // Get and initialize artifact processors
        StAXArtifactProcessorExtensionPoint staxProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        compositeProcessor = (StAXArtifactProcessor<Composite>)staxProcessors.getProcessor(Composite.class);
        StAXArtifactProcessor<Object> staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, outputFactory, monitor);

        URLArtifactProcessorExtensionPoint urlProcessors = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        URLArtifactProcessor<Object> urlProcessor = new ExtensibleURLArtifactProcessor(urlProcessors, monitor);
        
        // Create contribution processor
        modelResolvers = extensionPoints.getExtensionPoint(ModelResolverExtensionPoint.class);
        contributionProcessor = new ContributionContentProcessor(modelFactories, modelResolvers, urlProcessor, staxProcessor, monitor);
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
        
        // Get the specified contribution info 
        String contributionURI = contributionURI(key);
        Item contributionItem;
        try {
            contributionItem = contributionCollection.get(contributionURI);
        } catch (NotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, key);
            return;
        }
        
        // Read the contribution
        Contribution contribution;
        try {
            contribution = contribution(contributionURI, contributionItem.getAlternate());
        } catch (ContributionReadException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, key);
            return;
        }

        // Find the specified deployable composite
        QName qname = compositeQName(key);
        Composite composite = null;
        for (Composite deployable: contribution.getDeployables()) {
            if (qname.equals(deployable.getName())) {
                if (!deployable.isUnresolved()) {
                    composite = deployable;
                }
                break;
            }
        }
        if (composite == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, key);
            return;
        }
        
        // Write the composite
        response.setContentType("text/xml");
        ServletOutputStream os = response.getOutputStream();
        try {
            compositeProcessor.write(composite, outputFactory.createXMLStreamWriter(os));
        } catch (ContributionWriteException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
            return;
        } catch (XMLStreamException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
            return;
        }
        os.flush();
    }

    /**
     * Returns the contribution with the given URI.
     * 
     * @param contributionURI
     * @param contributionLocation
     * @return
     * @throws NotFoundException
     */
    private Contribution contribution(String contributionURI, String contributionLocation) throws ContributionReadException {
        try {
            URI uri = URI.create(contributionURI);
            URL location = locationURL(contributionLocation);

            // Get contribution from cache
            ContributionCache contributionCache = cache.contributions.get(location);
            long lastModified = lastModified(location);
            if (contributionCache != null) {
                if (contributionCache.contributionLastModified == lastModified) {
                    return contributionCache.contribution;
                }
                
                // Reset contribution cache
                cache.contributions.remove(location);
            }
            
            Contribution contribution = (Contribution)contributionProcessor.read(null, uri, location);
            
            contributionProcessor.resolve(contribution, new DefaultModelResolver());

            // Cache contribution
            contributionCache = new ContributionCache();
            contributionCache.contribution = contribution;
            contributionCache.contributionLastModified = lastModified;
            cache.contributions.put(location, contributionCache);
            
            return contribution;

        } catch (ContributionReadException e) {
            throw e;
        } catch (MalformedURLException e) {
            throw new ContributionReadException(e);
        } catch (IOException e) {
            throw new ContributionReadException(e);
        } catch (ContributionResolveException e) {
            throw new ContributionReadException(e);
        }
    }
    
}
