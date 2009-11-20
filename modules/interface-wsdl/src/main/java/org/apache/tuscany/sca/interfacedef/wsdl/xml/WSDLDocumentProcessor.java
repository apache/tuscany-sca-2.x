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
import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.sca.common.xml.stax.StAXHelper;
import org.apache.tuscany.sca.common.xml.stax.StAXHelper.Attribute;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.xsd.XSDFactory;
import org.apache.tuscany.sca.xsd.XSDefinition;

/**
 * An ArtifactProcessor for WSDL documents.
 *
 * @version $Rev$ $Date$
 */
public class WSDLDocumentProcessor implements URLArtifactProcessor<WSDLDefinition> {

    public static final QName WSDL11 = new QName("http://schemas.xmlsoap.org/wsdl/", "definitions");
    public static final QName WSDL11_IMPORT = new QName("http://schemas.xmlsoap.org/wsdl/", "import");
    public static final QName XSD = new QName("http://www.w3.org/2001/XMLSchema", "schema");

    private XMLInputFactory inputFactory;
    private StAXHelper helper;
    private WSDLFactory factory;
    private XSDFactory xsdFactory;
    

    public WSDLDocumentProcessor(ExtensionPointRegistry registry, StAXArtifactProcessor processor) {
        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.factory = modelFactories.getFactory(WSDLFactory.class);
        this.xsdFactory = modelFactories.getFactory(XSDFactory.class);
        this.inputFactory = modelFactories.getFactory(XMLInputFactory.class);
        this.helper = StAXHelper.getInstance(registry);
    }

    /**
     * Report a exception.
     *
     * @param problems
     * @param message
     * @param model
     */
     private void error(Monitor monitor, String message, Object model, Exception ex) {
    	 if (monitor != null) {
    		 Problem problem = monitor.createProblem(this.getClass().getName(), "interface-wsdlxml-validation-messages", Severity.ERROR, model, message, ex);
    	     monitor.problem(problem);
    	 }
     }

    public WSDLDefinition read(URL contributionURL, URI artifactURI, URL artifactURL, ProcessorContext context) throws ContributionReadException {
        try {
            WSDLDefinition definition = indexRead(artifactURL);
            definition.setURI(artifactURI);
            return definition;
        } catch (Exception e) {
        	ContributionReadException ce = new ContributionReadException(e);
        	error(context.getMonitor(), "ContributionReadException", artifactURL, ce);
            //throw ce;
        	return null;
        }
    }

    public void resolve(WSDLDefinition model, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {
        if (model == null) return;
        Monitor monitor = context.getMonitor();
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
                        // FIXME: [rfeng] By the WSDL 1.1 Specification, the location attribute is required
                        // We need to resolve it by QName
                        WSDLDefinition proxy = factory.createWSDLDefinition();
                        proxy.setUnresolved(true);
                        proxy.setNamespace(imp.getNamespaceURI());
                        WSDLDefinition resolved = resolver.resolveModel(WSDLDefinition.class, proxy, context);
                        if (resolved != null && !resolved.isUnresolved()) {
                            imp.setDefinition(resolved.getDefinition());
                            if (!model.getImportedDefinitions().contains(resolved)) {
                                model.getImportedDefinitions().add(resolved);
                            }
                        }
                    } else {
                        String location = imp.getLocationURI();
                        if (location.indexOf(' ') != -1) {
                            location = location.replace(" ", "%20");
                        }
                        URI uri = URI.create(location);
                        if (uri.isAbsolute()) {
                            WSDLDefinition resolved;
                            try {
                                resolved = read(null, uri, uri.toURL(), context);
                                imp.setDefinition(resolved.getDefinition());
                                if (!model.getImportedDefinitions().contains(resolved)) {
                                    model.getImportedDefinitions().add(resolved);
                                }
                            } catch (Exception e) {
                            	ContributionResolveException ce = new ContributionResolveException(e);
                            	error(monitor, "ContributionResolveException", resolver, ce);
                                //throw ce;
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
                                    resolved = read(null, locationURI, locationURI.toURL(), context);
                                    imp.setDefinition(resolved.getDefinition());
                                    if (!model.getImportedDefinitions().contains(resolved)) {
                                        model.getImportedDefinitions().add(resolved);
                                    }
                                } catch (Exception e) {
                                	ContributionResolveException ce = new ContributionResolveException(e);
                                	error(monitor, "ContributionResolveException", resolver, ce);
                                    //throw ce;
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

        Attribute attr1 = new Attribute(WSDL11, "targetNamespace");
        Attribute attr2 = new Attribute(XSD, "targetNamespace");
        Attribute[] attrs = helper.readAttributes(doc, attr1, attr2);

        wsdlDefinition.setNamespace(attr1.getValues().get(0));
        // The definition is marked as resolved but not loaded
        wsdlDefinition.setUnresolved(false);
        wsdlDefinition.setDefinition(null);

        int index = 0;
        for (String tns : attr2.getValues()) {
            XSDefinition xsd = xsdFactory.createXSDefinition();
            xsd.setUnresolved(true);
            xsd.setNamespace(tns);
            xsd.setLocation(URI.create(doc.toURI() + "#" + index));
            index++;
            // The definition is marked as resolved but not loaded
            xsd.setUnresolved(false);
            xsd.setSchema(null);
            wsdlDefinition.getXmlSchemas().add(xsd);
        }
        return wsdlDefinition;
    }

}
