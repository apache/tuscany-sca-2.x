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
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.tuscany.contribution.processor.URLArtifactProcessorExtension;
import org.apache.tuscany.contribution.resolver.ArtifactResolver;
import org.apache.tuscany.contribution.service.ContributionReadException;
import org.apache.tuscany.contribution.service.ContributionResolveException;
import org.apache.tuscany.contribution.service.ContributionWireException;
import org.apache.tuscany.contribution.service.ContributionWriteException;
import org.apache.tuscany.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.interfacedef.wsdl.XSDefinition;
import org.apache.tuscany.interfacedef.wsdl.impl.DefaultWSDLFactory;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.xml.sax.InputSource;

/**
 * An ArtifactProcessor for XSD documents.
 *
 * @version $Rev$ $Date$
 */
public class XSDDocumentProcessor implements URLArtifactProcessorExtension<XSDefinition> {

    private WSDLFactory factory;

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

    public XSDDocumentProcessor(WSDLFactory factory) {
        this.factory = factory;
    }
    
    public XSDDocumentProcessor() {
        this(new DefaultWSDLFactory());
    }

    public XSDefinition read(URL url) throws ContributionReadException {
        try {

            // Read an XSD document
            InputStream is = url.openStream();
            try {
    
                XmlSchemaCollection collection = new XmlSchemaCollection();
                collection.setSchemaResolver(new URIResolverImpl());
                XmlSchema schema = collection.read(new InputStreamReader(is), null);
    
                XSDefinition xsDefinition = factory.createXSDefinition();
                xsDefinition.setSchema(schema);
                
                return xsDefinition;
            } finally {
                is.close();
            }
            
        } catch (IOException e) {
            throw new ContributionReadException(e);
        }
    }
    
    public void write(XSDefinition wsdlDefinition, URL url) throws ContributionWriteException {
        // TODO Auto-generated method stub
    }
    
    public void resolve(XSDefinition model, ArtifactResolver resolver) throws ContributionResolveException {
        // TODO Auto-generated method stub
    }
    
    public void wire(XSDefinition model) throws ContributionWireException {
        // TODO Auto-generated method stub
    }
    
    public String getArtifactType() {
        return ".xsd";
    }
    
    public Class<XSDefinition> getModelType() {
        return XSDefinition.class;
    }
}
