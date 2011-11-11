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

package org.apache.tuscany.sca.binding.http.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.OperationSelector;
import org.apache.tuscany.sca.assembly.WireFormat;
import org.apache.tuscany.sca.binding.http.HTTPBinding;
import org.apache.tuscany.sca.binding.http.HTTPBindingFactory;
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
 * HTTP binding artifact processor that handles the read/write of HTTP specific XML elements.
 * 
 * @version $Rev$ $Date$
*/
public class HTTPBindingProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<HTTPBinding> {
    private static final QName RESPONSE_QNAME = new QName(Base.SCA11_TUSCANY_NS, "response");

    private static final String NAME = "name";
    private static final String URI = "uri";

    private HTTPBindingFactory httpBindingFactory;
    private StAXArtifactProcessor<Object> extensionProcessor;

    public HTTPBindingProcessor(ExtensionPointRegistry extensionPoints,
                                StAXArtifactProcessor<Object> extensionProcessor,
                                StAXAttributeProcessor<Object> extensionAttributeProcessor) {
        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        this.httpBindingFactory = modelFactories.getFactory(HTTPBindingFactory.class);
        this.extensionProcessor = (StAXArtifactProcessor<Object>)extensionProcessor;
    }

    public QName getArtifactType() {
        return HTTPBinding.TYPE;
    }

    public Class<HTTPBinding> getModelType() {
        return HTTPBinding.class;
    }

    public HTTPBinding read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException,XMLStreamException {
        HTTPBinding httpBinding = httpBindingFactory.createHTTPBinding();

        /**
         *    <tuscany:binding.http uri="http://localhost:8085/Customer">
         *          <tuscany:wireFormat.json />
         *          <tuscany:operationSelector.default />
         *          <tuscany:response>
         *             <tuscany:wireFormat.xml />
         *          </tuscany:response>
         *   </tuscany:binding.http>
         *
         */
        
        while (reader.hasNext()) {
            QName elementName = null;
            int event = reader.getEventType();
            switch (event) {
                case START_ELEMENT:
                    elementName = reader.getName();

                    if (HTTPBinding.TYPE.equals(elementName)) {
                        String name = getString(reader, NAME);
                        if (name != null) {
                            httpBinding.setName(name);
                        }

                        String uri = getURIString(reader, URI);
                        if (uri != null) {
                            httpBinding.setURI(uri);
                        }
                    } else if (RESPONSE_QNAME.equals(elementName)) {

                        // skip response
                        reader.next();
                        // and position to the next start_element event
                        while (reader.hasNext()) {
                            int sub_event = reader.getEventType();
                            switch (sub_event) {
                                case START_ELEMENT:
                                    elementName = reader.getName();
                                    break;
                                default:
                                    reader.next();
                            }
                            break;
                        }

                        // dispatch to read wire format for the response
                        Object extension = extensionProcessor.read(reader, context);
                        if (extension != null) {
                            if (extension instanceof WireFormat) {
                                httpBinding.setResponseWireFormat((WireFormat)extension);
                            }
                        }
                        break;
                    } else {
                        // Read an extension element
                        Object extension = extensionProcessor.read(reader, context);
                        if (extension != null) {
                            if (extension instanceof WireFormat) {
                                httpBinding.setRequestWireFormat((WireFormat)extension);
                            } else if (extension instanceof OperationSelector) {
                                httpBinding.setOperationSelector((OperationSelector)extension);
                            }
                        }
                    }
            }

            if (event == END_ELEMENT && HTTPBinding.TYPE.equals(reader.getName())) {
                break;
            }

            // Read the next element
            if (reader.hasNext()) {
                reader.next();
            }
        }

        return httpBinding;
    }

    public void write(HTTPBinding httpBinding, XMLStreamWriter writer, ProcessorContext context)
        throws ContributionWriteException, XMLStreamException {
        // writer.writeStartElement(Constants.SCA10_NS, BINDING_HTTP);

        writeStart(writer, HTTPBinding.TYPE.getNamespaceURI(), HTTPBinding.TYPE.getLocalPart());

        // Write binding name
        if (httpBinding.getName() != null) {
            writer.writeAttribute(NAME, httpBinding.getName());
        }

        // Write binding URI
        if (httpBinding.getURI() != null) {
            writer.writeAttribute(URI, httpBinding.getURI());
        }

        // Write operation selectors
        if (httpBinding.getOperationSelector() != null) {
            extensionProcessor.write(httpBinding.getOperationSelector(), writer, context);
        }

        // Write wire formats
        if (httpBinding.getRequestWireFormat() != null) {
            extensionProcessor.write(httpBinding.getRequestWireFormat(), writer, context);
        }

        if (httpBinding.getResponseWireFormat() != null && httpBinding.getRequestWireFormat() != httpBinding
            .getResponseWireFormat()) {
            writeStart(writer, RESPONSE_QNAME.getNamespaceURI(), RESPONSE_QNAME.getLocalPart());
            extensionProcessor.write(httpBinding.getResponseWireFormat(), writer, context);
            writeEnd(writer);
        }

        writeEnd(writer);
        // writer.writeEndElement();
    }

    public void resolve(HTTPBinding model, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {
        // Should not need to do anything here for now...

    }

}
