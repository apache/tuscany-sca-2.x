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

package org.apache.tuscany.sca.binding.rest.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.OperationSelector;
import org.apache.tuscany.sca.assembly.WireFormat;
import org.apache.tuscany.sca.binding.rest.RESTBinding;
import org.apache.tuscany.sca.binding.rest.RESTBindingFactory;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXAttributeProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;

/**
 * REST Binding Artifact Processor
 * 
 * @version $Rev$ $Date$
 */
public class RESTBindingProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<RESTBinding> {
    private static final String NAME = "name";
    private static final String URI = "uri";

    private RESTBindingFactory httpBindingFactory;
    private StAXArtifactProcessor<Object> extensionProcessor;

    public RESTBindingProcessor(ExtensionPointRegistry extensionPoints, 
                                StAXArtifactProcessor<Object> extensionProcessor,
                                StAXAttributeProcessor<Object> extensionAttributeProcessor) {
        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        this.httpBindingFactory = modelFactories.getFactory(RESTBindingFactory.class);
        this.extensionProcessor = (StAXArtifactProcessor<Object>)extensionProcessor;
    }

    public QName getArtifactType() {
        return RESTBinding.TYPE;
    }

    public Class<RESTBinding> getModelType() {
        return RESTBinding.class;
    }

    public RESTBinding read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException, XMLStreamException {
        RESTBinding httpBinding = httpBindingFactory.createRESTBinding();

        while(reader.hasNext()) {
            QName elementName = null;
            int event = reader.getEventType();
            switch (event) {
                case START_ELEMENT:
                    elementName = reader.getName();

                    if (RESTBinding.TYPE.equals(elementName)) {
                        String name = getString(reader, NAME);
                        if(name != null) {
                            httpBinding.setName(name);
                        }

                        String uri = getURIString(reader, URI);
                        if (uri != null) {
                            httpBinding.setURI(uri);
                        }
                    } else {
                        // Read an extension element
                        Object extension = extensionProcessor.read(reader, context);
                        if (extension != null) {
                            if (extension instanceof WireFormat) {
                                httpBinding.setRequestWireFormat((WireFormat)extension);
                            } else if(extension instanceof OperationSelector) {
                                httpBinding.setOperationSelector((OperationSelector)extension);
                            }
                        }
                    }
            }

            if (event == END_ELEMENT && RESTBinding.TYPE.equals(reader.getName())) {
                break;
            }

            // Read the next element
            if (reader.hasNext()) {
                reader.next();
            }
        }

        return httpBinding;
    }

    public void write(RESTBinding restBinding, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException, XMLStreamException {
        //writer.writeStartElement(Constants.SCA10_NS, BINDING_HTTP);

        writeStart(writer, RESTBinding.TYPE.getNamespaceURI(), RESTBinding.TYPE.getLocalPart());

        // Write binding name
        if (restBinding.getName() != null) {
            writer.writeAttribute(NAME, restBinding.getName());
        }

        // Write binding URI
        if (restBinding.getURI() != null) {
            writer.writeAttribute(URI, restBinding.getURI());
        }
        
        // Write operation selectors
        if ( restBinding.getOperationSelector() != null ) {
            extensionProcessor.write(restBinding.getOperationSelector(), writer, context);
        }
        
        // Write wire formats
        if ( restBinding.getRequestWireFormat() != null ) {
            extensionProcessor.write(restBinding.getRequestWireFormat(), writer, context);
        }

        writeEnd(writer);
        //writer.writeEndElement();
    }


    public void resolve(RESTBinding model, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {
        // Should not need to do anything here for now... 

    }

}
