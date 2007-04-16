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

package org.apache.tuscany.interfacedef.wsdl.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.wsdl.Definition;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.xml.WSDLLocator;
import javax.wsdl.xml.WSDLReader;

import org.apache.tuscany.contribution.processor.URLArtifactProcessorExtension;
import org.apache.tuscany.contribution.resolver.ArtifactResolver;
import org.apache.tuscany.contribution.service.ContributionReadException;
import org.apache.tuscany.contribution.service.ContributionResolveException;
import org.apache.tuscany.contribution.service.ContributionRuntimeException;
import org.apache.tuscany.contribution.service.ContributionWireException;
import org.apache.tuscany.contribution.service.ContributionWriteException;
import org.apache.tuscany.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.interfacedef.wsdl.impl.DefaultWSDLFactory;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * An ArtifactProcessor for WSDL documents.
 *
 * @version $Rev$ $Date$
 */
public class WSDLDocumentProcessor implements URLArtifactProcessorExtension<WSDLDefinition> {

    private javax.wsdl.factory.WSDLFactory wsdlFactory;
    private ExtensionRegistry wsdlExtensionRegistry;
    private WSDLFactory factory;

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
    
    public WSDLDocumentProcessor() {
        this(new DefaultWSDLFactory(), null);
    }

    public WSDLDefinition read(URL url) throws ContributionReadException {
        try {

            // Read a WSDL document
            InputStream is = url.openStream();
            WSDLReader reader = wsdlFactory.newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", false);
            reader.setExtensionRegistry(wsdlExtensionRegistry);

            WSDLLocatorImpl locator = new WSDLLocatorImpl(url, is);
            Definition definition = reader.readWSDL(locator);
            
            WSDLDefinition wsdlDefinition = factory.createWSDLDefinition();
            wsdlDefinition.setDefinition(definition);
            
            // get base uri for any relative schema includes

            
            // Read inline schemas 
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
            
            return wsdlDefinition;
            
        } catch (WSDLException e) {
            throw new ContributionReadException(e);
        } catch (IOException e) {
            throw new ContributionReadException(e);
        }
    }
    
    public void write(WSDLDefinition wsdlDefinition, URL url) throws ContributionWriteException {
        // TODO Auto-generated method stub
    }
    
    public void resolve(WSDLDefinition model, ArtifactResolver resolver) throws ContributionResolveException {
        // TODO Auto-generated method stub
    }
    
    public void wire(WSDLDefinition model) throws ContributionWireException {
        // TODO Auto-generated method stub
    }
    
    public String getArtifactType() {
        return ".wsdl";
    }
    
    public Class<WSDLDefinition> getModelType() {
        return WSDLDefinition.class;
    }
}
