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

package org.apache.tuscany.sca.contribution.resource.impl;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resource.ResourceExport;
import org.apache.tuscany.sca.contribution.resource.ResourceImportExportFactory;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * Artifact processor for Resource export
 *
 * @version $Rev$ $Date$
 */
public class ResourceExportProcessor implements StAXArtifactProcessor<ResourceExport> {

    private static final String URI = "uri";

    private final ResourceImportExportFactory factory;

    public ResourceExportProcessor(FactoryExtensionPoint modelFactories) {
        this.factory = modelFactories.getFactory(ResourceImportExportFactory.class);
    }


    /**
     * Report a error.
     *
     * @param problems
     * @param message
     * @param model
     */
     private void error(Monitor monitor, String message, Object model, Object... messageParameters) {
    	 if (monitor != null) {
	        Problem problem = monitor.createProblem(this.getClass().getName(), "contribution-resource-validation-messages", Severity.ERROR, model, message, (Object[])messageParameters);
	        monitor.problem(problem);
    	 }
     }

    public QName getArtifactType() {
        return ResourceExport.TYPE;
    }

    public Class<ResourceExport> getModelType() {
        return ResourceExport.class;
    }

    /**
     * Process <export.resource uri=""/>
     */
    public ResourceExport read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException {
    	ResourceExport resourceExport = this.factory.createResourceExport();
        QName element = null;

        try {
            while (reader.hasNext()) {
                int event = reader.getEventType();
                switch (event) {
                    case START_ELEMENT:
                        element = reader.getName();

                        // Read <export.resource>
                        if (ResourceExport.TYPE.equals(element)) {
                            String uri = reader.getAttributeValue(null, URI);
                            if (uri == null) {
                            	error(context.getMonitor(), "AttributeURIMissing", reader);
                                //throw new ContributionReadException("Attribute 'uri' is missing");
                            } else
                                resourceExport.setURI(uri);
                        }

                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        if (ResourceExport.TYPE.equals(reader.getName())) {
                            return resourceExport;
                        }
                        break;
                }

                // Read the next element
                if (reader.hasNext()) {
                    reader.next();
                }
            }
        }
        catch (XMLStreamException e) {
            ContributionReadException ex = new ContributionReadException(e);
            error(context.getMonitor(), "XMLStreamException", reader, ex);
        }

        return resourceExport;
    }

    public void write(ResourceExport resourceExport, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException, XMLStreamException {

        // Write <export.resource>
        writer.writeStartElement(ResourceExport.TYPE.getNamespaceURI(), ResourceExport.TYPE.getLocalPart());

        if (resourceExport.getURI() != null) {
            writer.writeAttribute(URI, resourceExport.getURI());
        }

        writer.writeEndElement();
    }

    public void resolve(ResourceExport resourceExport, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {

        if (resourceExport.getURI() != null)
            // Initialize the export's model resolver
            resourceExport.setModelResolver(new ResourceExportModelResolver(resourceExport, resolver));
    }
}
