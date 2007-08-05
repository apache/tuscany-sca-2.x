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
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.XSDefinition;

/**
 * An ArtifactProcessor for WSDL documents.
 * 
 * @version $Rev$ $Date$
 */
public class WSDLDocumentProcessor implements URLArtifactProcessor<WSDLDefinition> {

    public static final QName WSDL11 = new QName("http://schemas.xmlsoap.org/wsdl/", "definitions");
    public static final QName XSD = new QName("http://www.w3.org/2001/XMLSchema", "schema");

    private final static XMLInputFactory inputFactory = XMLInputFactory.newInstance();

    private WSDLFactory factory;

    public WSDLDocumentProcessor(WSDLFactory factory, javax.wsdl.factory.WSDLFactory wsdlFactory) {
        this.factory = factory;
    }

    public WSDLDefinition read(URL contributionURL, URI artifactURI, URL artifactURL) throws ContributionReadException {
        try {
            return indexRead(artifactURL);
        } catch (Exception e) {
            throw new ContributionReadException(e);
        }
    }

    public void resolve(WSDLDefinition model, ModelResolver resolver) throws ContributionResolveException {
        Definition definition = model.getDefinition();
        if (definition != null) {
            for (Object imports : definition.getImports().values()) {
                List importList = (List)imports;
                for (Object i : importList) {
                    Import imp = (Import)i;
                    if (imp.getDefinition() != null) {
                        continue;
                    }
                    if (imp.getLocationURI() == null) {
                        // FIXME: [rfeng] By the WSDL 1.1 spec, the location attribute is required
                        // We need to resolve it by QName
                        WSDLDefinition proxy = factory.createWSDLDefinition();
                        proxy.setUnresolved(true);
                        proxy.setNamespace(imp.getNamespaceURI());
                        WSDLDefinition resolved = resolver.resolveModel(WSDLDefinition.class, proxy);
                        if (resolved != null && !resolved.isUnresolved()) {
                            imp.setDefinition(resolved.getDefinition());
                        }
                    } else {
                        String location = imp.getLocationURI();
                        URI uri = URI.create(location);
                        if (uri.isAbsolute()) {
                            WSDLDefinition resolved;
                            try {
                                resolved = read(null, uri, uri.toURL());
                                imp.setDefinition(resolved.getDefinition());
                            } catch (Exception e) {
                                throw new ContributionResolveException(e);
                            }
                        } else {
                            if (location.startsWith("/")) {
                                // This is a relative URI against a contribution
                                location = location.substring(1);
                                // TODO: Need to resolve it against the contribution
                            } else {
                                // This is a relative URI against the WSDL document
                                URI baseURI = URI.create(model.getDefinition().getDocumentBaseURI());
                                URI locationURI = baseURI.resolve(location);
                                WSDLDefinition resolved;
                                try {
                                    resolved = read(null, locationURI, locationURI.toURL());
                                    imp.setDefinition(resolved.getDefinition());
                                } catch (Exception e) {
                                    throw new ContributionResolveException(e);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public String getArtifactType() {
        return ".wsdl";
    }

    public Class<WSDLDefinition> getModelType() {
        return WSDLDefinition.class;
    }

    /**
     * Read the namespace for the WSDL definition and inline schemas
     * 
     * @param doc
     * @return
     * @throws IOException
     * @throws XMLStreamException
     */
    protected WSDLDefinition indexRead(URL doc) throws Exception {
        WSDLDefinition wsdlDefinition = factory.createWSDLDefinition();
        wsdlDefinition.setUnresolved(true);
        wsdlDefinition.setLocation(doc.toURI());

        InputStream is = doc.openStream();
        try {
            XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
            int eventType = reader.getEventType();
            while (true) {
                if (eventType == XMLStreamConstants.START_ELEMENT) {
                    if (WSDL11.equals(reader.getName())) {
                        String tns = reader.getAttributeValue(null, "targetNamespace");
                        wsdlDefinition.setNamespace(tns);
                        // The definition is marked as resolved but not loaded
                        wsdlDefinition.setUnresolved(false);
                        wsdlDefinition.setDefinition(null);
                    }
                    if (XSD.equals(reader.getName())) {
                        String tns = reader.getAttributeValue(null, "targetNamespace");
                        XSDefinition xsd = factory.createXSDefinition();
                        xsd.setUnresolved(true);
                        xsd.setNamespace(tns);
                        // The definition is marked as resolved but not loaded
                        xsd.setUnresolved(false);
                        xsd.setSchema(null);
                        wsdlDefinition.getXmlSchemas().add(xsd);
                    }
                }
                if (reader.hasNext()) {
                    eventType = reader.next();
                } else {
                    break;
                }
            }
            return wsdlDefinition;
        } finally {
            is.close();
        }
    }

}
