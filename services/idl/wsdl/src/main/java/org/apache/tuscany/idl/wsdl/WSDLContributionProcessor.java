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

package org.apache.tuscany.idl.wsdl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLLocator;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.apache.tuscany.host.deployment.DeploymentException;
import org.apache.tuscany.spi.deployer.ArtifactResolverRegistry;
import org.apache.tuscany.spi.extension.ContributionProcessorExtension;
import org.apache.tuscany.spi.model.Contribution;
import org.apache.tuscany.spi.model.DeployedArtifact;
import org.osoa.sca.annotations.Reference;
import org.xml.sax.InputSource;

/**
 * The WSDL processor
 * 
 * @version $Rev$ $Date$
 */
public class WSDLContributionProcessor extends ContributionProcessorExtension {

    private final WSDLFactory wsdlFactory;

    private final ExtensionRegistry extensionRegistry;

    private final Map<String, List<Definition>> definitionsByNamespace = new HashMap<String, List<Definition>>();

    private Monitor monitor;

    private XMLSchemaRegistry schemaRegistry;

    private ArtifactResolverRegistry artifactResolverRegistry;

    public WSDLContributionProcessor() throws WSDLException {
        wsdlFactory = WSDLFactory.newInstance();
        extensionRegistry = wsdlFactory.newPopulatedExtensionRegistry();
    }

    @Reference
    public void setSchemaRegistry(XMLSchemaRegistry schemaRegistry) {
        this.schemaRegistry = schemaRegistry;
    }

    @org.apache.tuscany.api.annotation.Monitor
    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    public ExtensionRegistry getExtensionRegistry() {
        return extensionRegistry;
    }

    @SuppressWarnings("unchecked")
    public Definition loadDefinition(Contribution contribution, String namespace, URI location, InputStream inputStream)
        throws IOException, WSDLException, DeploymentException {
        if (monitor != null) {
            monitor.readingWSDL(namespace, location);
        }

        WSDLReader reader = wsdlFactory.newWSDLReader();
        reader.setFeature("javax.wsdl.verbose", false);
        reader.setExtensionRegistry(extensionRegistry);

        WSDLLocatorImpl locator = new WSDLLocatorImpl(contribution, location, inputStream);
        Definition definition = reader.readWSDL(locator);
        String definitionNamespace = definition.getTargetNamespace();
        if (namespace != null && !namespace.equals(definitionNamespace)) {
            throw new WSDLException(WSDLException.CONFIGURATION_ERROR, namespace + " != "
                                                                       + definition.getTargetNamespace());
        }

        // Load inline schemas
        registry.processModel(contribution, location, definition);
        for (Object i : definition.getImports().values()) {
            List<Import> imps = (List<Import>)i;
            for (Import imp : imps) {
                Definition imported = imp.getDefinition();
                if (imported != null) {
                    // TODO:
                    registry.processModel(contribution,
                                          URI.create(imp.getDefinition().getDocumentBaseURI()),
                                          definition);
                }
            }
        }

        if (monitor != null) {
            monitor.cachingDefinition(definitionNamespace, location);
        }
        List<Definition> definitions = definitionsByNamespace.get(definitionNamespace);
        if (definitions == null) {
            definitions = new ArrayList<Definition>();
            definitionsByNamespace.put(definitionNamespace, definitions);
        }
        definitions.add(definition);

        DeployedArtifact artifact = contribution.getArtifact(location);
        artifact.addModelObject(Definition.class, definition.getTargetNamespace(), definition);

        return definition;
    }

    public PortType getPortType(QName name) {
        String namespace = name.getNamespaceURI();
        List<Definition> definitions = definitionsByNamespace.get(namespace);
        if (definitions == null) {
            return null;
        }
        for (Definition definition : definitions) {
            PortType portType = definition.getPortType(name);
            if (portType != null) {
                return portType;
            }
        }
        return null;
    }

    public Service getService(QName name) {
        String namespace = name.getNamespaceURI();
        List<Definition> definitions = definitionsByNamespace.get(namespace);
        if (definitions == null) {
            return null;
        }
        for (Definition definition : definitions) {
            Service service = definition.getService(name);
            if (service != null) {
                return service;
            }
        }
        return null;
    }

    public static interface Monitor {
        /**
         * Monitor event emitted immediately before an attempt is made to read
         * WSDL for the supplied namespace from the supplied location.
         * 
         * @param namespace the target namespace expected in the WSDL; may be
         *            null
         * @param location the location where we will attempt to read the WSDL
         *            definition from
         */
        void readingWSDL(String namespace, URI location);

        /**
         * Monitor event emitted immediately before registering a WSDL
         * definition in the cache.
         * 
         * @param namespace the target namespace for the WSDL
         * @param location the location where the WSDL definition was read from
         */
        void cachingDefinition(String namespace, URI location);
    }

    public XMLSchemaRegistry getSchemaRegistry() {
        if (schemaRegistry == null) {
            // Default
            schemaRegistry = new XMLSchemaRegistryImpl();
        }
        return schemaRegistry;
    }

    public class WSDLLocatorImpl implements WSDLLocator {
        private Contribution contribution;
        private InputStream inputStream;
        private String baseURI;
        private URI latestImportURI;

        public WSDLLocatorImpl(Contribution contribution, URI baseURI, InputStream is) {
            this.contribution = contribution;
            this.baseURI = baseURI.toString();
            this.inputStream = is;
        }

        public void close() {
            // inputStream.close();
        }

        public InputSource getBaseInputSource() {
            return new InputSource(inputStream);
        }

        public String getBaseURI() {
            return baseURI;
        }

        public InputSource getImportInputSource(String parentLocation, String importLocation) {
            try {
                URL url = artifactResolverRegistry.resolve(contribution, null, importLocation, parentLocation);
                latestImportURI = url.toURI();
                return new InputSource(url.openStream());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public String getLatestImportURI() {
            return latestImportURI.toString();
        }

    }

    public String getContentType() {
        return "application/vnd.tuscany.wsdl";
    }

    /**
     * @param artifactResolverRegistry the artifactResolverRegistry to set
     */
    @Reference
    public void setArtifactResolverRegistry(ArtifactResolverRegistry artifactResolverRegistry) {
        this.artifactResolverRegistry = artifactResolverRegistry;
    }

    public void processContent(Contribution contribution, URI source, InputStream inputStream)
        throws DeploymentException, IOException {
        try {
            loadDefinition(contribution, null, source, inputStream);
        } catch (WSDLException e) {
            throw new InvalidWSDLContributionException(contribution.getArtifact(source).getLocation().toExternalForm(),
                                                       e);
        }
    }

    public void processModel(Contribution contribution, URI source, Object modelObject) throws DeploymentException,
        IOException {
    }

}
