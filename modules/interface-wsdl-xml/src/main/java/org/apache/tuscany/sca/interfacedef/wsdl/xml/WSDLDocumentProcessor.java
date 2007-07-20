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

package org.apache.tuscany.sca.interfacedef.wsdl.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.xml.WSDLLocator;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionRuntimeException;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * An ArtifactProcessor for WSDL documents.
 *
 * @version $Rev$ $Date$
 */
public class WSDLDocumentProcessor implements URLArtifactProcessor<WSDLDefinition> {

    private javax.wsdl.factory.WSDLFactory wsdlFactory;
    private ExtensionRegistry wsdlExtensionRegistry;
    private WSDLFactory factory;
    
    private Map<String, WSDLDefinition> loadedDefinitions = new Hashtable<String, WSDLDefinition>();

    /**
     * Implementation of a WSDL locator.
     */
    private class WSDLLocatorImpl implements WSDLLocator {
        private InputStream inputStream;
        private URL base;
        private String latestImportURI;

        public WSDLLocatorImpl(URL base, InputStream is) {
            this.base = base;
            this.inputStream = is;
        }

        public void close() {
        }

        public InputSource getBaseInputSource() {
            return new InputSource(inputStream);
        }

        public String getBaseURI() {
            return base.toString();
        }

        public InputSource getImportInputSource(String parentLocation, String importLocation) {
            try {
                URL url = new URL(new URL(parentLocation), importLocation);
                latestImportURI = url.toString();
                return new InputSource(url.openStream());
            } catch (Exception e) {
                throw new ContributionRuntimeException(e);
            }
        }

        public String getLatestImportURI() {
            return latestImportURI;
        }

    }

    /**
     * URI resolver implementation for xml schema
     */
    private class URIResolverImpl implements URIResolver {

        public org.xml.sax.InputSource resolveEntity(java.lang.String targetNamespace,
                                                     java.lang.String schemaLocation,
                                                     java.lang.String baseUri) {
            try {
                URL url = new URL(new URL(baseUri), schemaLocation);
                return new InputSource(url.openStream());
            } catch (IOException e) {
                return null;
            }
        }
    }

    public WSDLDocumentProcessor(WSDLFactory factory, javax.wsdl.factory.WSDLFactory wsdlFactory) {
        this.factory = factory;
        
        if (wsdlFactory != null) {
            this.wsdlFactory = wsdlFactory;
        } else {
            try {
                this.wsdlFactory = javax.wsdl.factory.WSDLFactory.newInstance();
            } catch (WSDLException e) {
                throw new ContributionRuntimeException(e);
            }
        }
        
        wsdlExtensionRegistry = this.wsdlFactory.newPopulatedExtensionRegistry();
    }
    
    private void readInlineSchemas(Definition definition, WSDLDefinition wsdlDefinition) {
        Types types = definition.getTypes();
        if (types != null) {
            wsdlDefinition.getInlinedSchemas().setSchemaResolver(new URIResolverImpl());
            for (Object ext : types.getExtensibilityElements()) {
                if (ext instanceof Schema) {
                    Element element = ((Schema)ext).getElement();

                    // TODO: fix to make includes in imported
                    //       schema work. The XmlSchema library was crashing
                    //       because the base uri was not set. This doesn't
                    //       affect imports. Need to check that this
                    //       is the right approach for XSDs included by a
                    //       XSD which is itself imported inline in a WSDL
                    XmlSchemaCollection schemaCollection = wsdlDefinition.getInlinedSchemas();            
                    schemaCollection.setBaseUri(((Schema)ext).getDocumentBaseURI());

                    wsdlDefinition.getInlinedSchemas().read(element, element.getBaseURI());
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private void mergeDefinition(Definition existingDefn, Definition definition) {
        if (existingDefn != null ) {
            //merge into existing defn
            existingDefn.getImports().putAll(definition.getImports());
            existingDefn.getMessages().putAll(definition.getMessages());
            existingDefn.getPortTypes().putAll(definition.getPortTypes());
            existingDefn.getBindings().putAll(definition.getBindings());
            existingDefn.getServices().putAll(definition.getServices());
        } 
    }
    
    @SuppressWarnings("unchecked")
    public WSDLDefinition read(URL contributionURL, URI artifactURI, URL artifactURL) throws ContributionReadException {
        try {

            // Read a WSDL document
            InputStream is = artifactURL.openStream();
            WSDLReader reader = wsdlFactory.newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", false);
            reader.setExtensionRegistry(wsdlExtensionRegistry);

            WSDLLocatorImpl locator = new WSDLLocatorImpl(artifactURL, is);
            Definition definition = reader.readWSDL(locator);
            
            WSDLDefinition wsdlDefinition = loadedDefinitions.get(definition.getTargetNamespace());
            if ( wsdlDefinition != null ) {
                mergeDefinition(wsdlDefinition.getDefinition(), definition);
            } else {
                wsdlDefinition = factory.createWSDLDefinition();
                wsdlDefinition.setDefinition(definition);
                loadedDefinitions.put(definition.getTargetNamespace(), wsdlDefinition);
            }
            
            //Read inline schemas 
            readInlineSchemas(definition, wsdlDefinition);
             
            //read the inline schemas for wsdl imports
            if ( definition.getImports().size() > 0 ) {
                Iterator<Vector<Import>> importsIterator = definition.getImports().values().iterator();
                Vector<Import> imports = null;
                Import anImport = null;
                while (importsIterator.hasNext()) {
                    imports = importsIterator.next();
                    for ( int count = 0 ; count < imports.size() ; ++count ) {
                        anImport = imports.elementAt(count);
//                      Read inline schemas 
                        readInlineSchemas(anImport.getDefinition(), wsdlDefinition);
                    }
                }
            }
            
            return wsdlDefinition;
            
        } catch (WSDLException e) {
            throw new ContributionReadException(e);
        } catch (IOException e) {
            throw new ContributionReadException(e);
        }
    }
    
    public void resolve(WSDLDefinition model, ModelResolver resolver) throws ContributionResolveException {
    }
    
    public String getArtifactType() {
        return ".wsdl";
    }
    
    public Class<WSDLDefinition> getModelType() {
        return WSDLDefinition.class;
    }
}
