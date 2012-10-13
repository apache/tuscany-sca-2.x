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

import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.OperationSelector;
import org.apache.tuscany.sca.assembly.WireFormat;
import org.apache.tuscany.sca.binding.rest.RESTBinding;
import org.apache.tuscany.sca.binding.rest.RESTBindingFactory;
import org.apache.tuscany.sca.binding.rest.operationselector.jaxrs.JAXRSOperationSelector;
import org.apache.tuscany.sca.binding.rest.operationselector.jaxrs.JAXRSOperationSelectorFactory;
import org.apache.tuscany.sca.binding.rest.operationselector.rpc.RPCOperationSelector;
import org.apache.tuscany.sca.binding.rest.operationselector.rpc.RPCOperationSelectorFactory;
import org.apache.tuscany.sca.binding.rest.wireformat.json.JSONWireFormat;
import org.apache.tuscany.sca.binding.rest.wireformat.json.JSONWireFormatFactory;
import org.apache.tuscany.sca.binding.rest.wireformat.xml.XMLWireFormat;
import org.apache.tuscany.sca.binding.rest.wireformat.xml.XMLWireFormatFactory;
import org.apache.tuscany.sca.common.http.HTTPHeader;
import org.apache.tuscany.sca.common.http.cors.CORSConfiguration;
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
    private static final QName HEADERS_QNAME = new QName(Base.SCA11_TUSCANY_NS, "http-headers");
    private static final QName HEADER_QNAME = new QName(Base.SCA11_TUSCANY_NS, "header");
    private static final QName RESPONSE_QNAME = new QName(Base.SCA11_TUSCANY_NS, "response");

    private static final QName WIRE_FORMAT_JSON = new QName(Base.SCA11_TUSCANY_NS, "wireFormat.json");
    private static final QName WIRE_FORMAT_XML = new QName(Base.SCA11_TUSCANY_NS, "wireFormat.xml");
    
    private static final QName OPERATION_SELCTOR_RPC = new QName(Base.SCA11_TUSCANY_NS, "operationSelector.rpc");
    private static final QName OPERATION_SELCTOR_JAXRS = new QName(Base.SCA11_TUSCANY_NS, "operationSelector.jaxrs");
    
    private static final String NAME = "name";
    private static final String VALUE = "value";
    private static final String URI = "uri";
    private static final String READ_TIMEOUT = "readTimeout";
    private static final String CORS ="isCORS";

    private RESTBindingFactory restBindingFactory;
    private JSONWireFormatFactory jsonWireFormatFactory;
    private XMLWireFormatFactory xmlWireFormatFactory;
    private JAXRSOperationSelectorFactory jaxrsOperationSelectorFactory;
    private RPCOperationSelectorFactory rpcOperationSelectorFactory;
    
    private StAXArtifactProcessor<Object> extensionProcessor;

    public RESTBindingProcessor(ExtensionPointRegistry extensionPoints,
                                StAXArtifactProcessor<Object> extensionProcessor,
                                StAXAttributeProcessor<Object> extensionAttributeProcessor) {
        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        this.restBindingFactory = modelFactories.getFactory(RESTBindingFactory.class);
        this.jsonWireFormatFactory = modelFactories.getFactory(JSONWireFormatFactory.class);
        this.xmlWireFormatFactory = modelFactories.getFactory(XMLWireFormatFactory.class);
        this.jaxrsOperationSelectorFactory = modelFactories.getFactory(JAXRSOperationSelectorFactory.class);
        this.rpcOperationSelectorFactory = modelFactories.getFactory(RPCOperationSelectorFactory.class);
        
        this.extensionProcessor = (StAXArtifactProcessor<Object>)extensionProcessor;
    }

    public QName getArtifactType() {
        return RESTBinding.TYPE;
    }

    public Class<RESTBinding> getModelType() {
        return RESTBinding.class;
    }

    public RESTBinding read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException, XMLStreamException {
        RESTBinding restBinding = restBindingFactory.createRESTBinding();

        /**
         *    <tuscany:binding.rest uri="http://localhost:8085/Customer" readTimeout="60000">
         *          <tuscany:wireFormat.xml />
         *          <tuscany:operationSelector.jaxrs />
         *          <tuscany:http-headers>
         *             <tuscany:header name="Cache-Control" value="no-cache"/>
         *             <tuscany:header name="Expires" value="-1"/>
         *          </tuscany:http-headers>
         *          <tuscany:response>
         *             <tuscany:wireFormat.json />
         *          </tuscany:response>
         *   </tuscany:binding.rest>
         *
         */
        while(reader.hasNext()) {
            QName elementName = null;
            int event = reader.getEventType();
            switch (event) {
                case START_ELEMENT:
                    elementName = reader.getName();

                    if(RESTBinding.TYPE.equals(elementName)) {

                        // binding attributes
                        String name = getString(reader, NAME);
                        if(name != null) {
                            restBinding.setName(name);
                        }

                        String uri = getURIString(reader, URI);
                        if (uri != null) {
                            restBinding.setURI(uri);
                        }
                        
                        String readTimeout = getString(reader, READ_TIMEOUT);
                        if (readTimeout != null) {
                            restBinding.setReadTimeout(Integer.valueOf(readTimeout));
                        }
                        
                        Boolean isCORS = getBoolean(reader, CORS);
                        if(isCORS != null) {
                            restBinding.setCORS(isCORS);
                        } else {
                            // Default to true
                            restBinding.setCORS(Boolean.TRUE);
                        }
                        
                        break;

                    } else if (HEADERS_QNAME.equals(elementName)) {

                        // ignore wrapper element
                        break;

                    } else if (HEADER_QNAME.equals(elementName)) {

                        // header name/value pair
                        String name = getString(reader, NAME);
                        String value = getURIString(reader, VALUE);

                        if(name != null) {
                            restBinding.getHttpHeaders().add(new HTTPHeader(name, value));
                        }
                        break;

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
                                default: reader.next();
                            }
                            break;
                        }

                        // dispatch to read wire format for the response
                        //Object extension = extensionProcessor.read(reader, context);
                        Object extension = readWireFormatAndOperationSelectorExtensions(reader);
                        if (extension != null) {
                            if (extension instanceof WireFormat) {
                                restBinding.setResponseWireFormat((WireFormat)extension);
                            }
                        }
                        break;
                    } else if(WIRE_FORMAT_JSON.equals(elementName) || WIRE_FORMAT_XML.equals(elementName)  ||
                              OPERATION_SELCTOR_JAXRS.equals(elementName) || OPERATION_SELCTOR_RPC.equals(elementName)) {

                        // Read wireFormat and/or operationSelector extension elements
                        Object extension = readWireFormatAndOperationSelectorExtensions(reader);
                        if (extension != null) {
                            if (extension instanceof WireFormat) {
                                restBinding.setRequestWireFormat((WireFormat)extension);
                                restBinding.setResponseWireFormat((WireFormat)extension);
                            } else if(extension instanceof OperationSelector) {
                                restBinding.setOperationSelector((OperationSelector)extension);
                                restBinding.setCORS(true);
                            }
                        }
                        break;

                        
                    } else {
                        // Read an extension element
                        Object extension = extensionProcessor.read(reader, context);
                        if (extension != null) {
                            if (extension instanceof CORSConfiguration) {
                                restBinding.setCORSConfiguration((CORSConfiguration)extension);
                            }
                        }
                        break;
                    }

                case END_ELEMENT:
                    elementName = reader.getName();

                    if(RESTBinding.TYPE.equals(elementName)) {
                        return restBinding;
                    }
                    break;
            }



            // Read the next element
            if (reader.hasNext()) {
                reader.next();
            }
        }

        return restBinding;
    }


    public void write(RESTBinding restBinding, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException, XMLStreamException {

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
            writeWireFormatAndOperationSelectorExtensions(writer, restBinding.getOperationSelector());
        }

        // Write wire formats
        if ( restBinding.getRequestWireFormat() != null ) {
            writeWireFormatAndOperationSelectorExtensions(writer, restBinding.getRequestWireFormat());
        }

        if ( restBinding.getResponseWireFormat() != null && restBinding.getRequestWireFormat() != restBinding.getResponseWireFormat()) {
            writeStart(writer, RESPONSE_QNAME.getNamespaceURI(), RESPONSE_QNAME.getLocalPart());
            if(restBinding.getResponseWireFormat() != null) {
                writeWireFormatAndOperationSelectorExtensions(writer, restBinding.getResponseWireFormat());
            }
            writeEnd(writer);
        }


        writeEnd(writer);
    }


    public void resolve(RESTBinding model, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {
        // Should not need to do anything here for now...

    }

    private Object readWireFormatAndOperationSelectorExtensions(XMLStreamReader reader)  throws XMLStreamException {
        QName elementName = reader.getName();
        
        if(WIRE_FORMAT_JSON.equals(elementName)) {
            return this.jsonWireFormatFactory.createRESTWireFormatJSON();
        } else if(WIRE_FORMAT_XML.equals(elementName)) {
            return this.xmlWireFormatFactory.createRESTWireFormatXML();
        } else if(OPERATION_SELCTOR_JAXRS.equals(elementName)) {
            return this.jaxrsOperationSelectorFactory.createJAXRSOperationSelector();
        } else if(OPERATION_SELCTOR_RPC.equals(elementName)) {
            return this.rpcOperationSelectorFactory.createRPCOperationSelector();
        }
        
        return null;
    }
    
    private void writeWireFormatAndOperationSelectorExtensions(XMLStreamWriter writer, Object object) throws XMLStreamException {
        
        if(object instanceof JSONWireFormat) {
            writeStart(writer, WIRE_FORMAT_JSON.getNamespaceURI(), WIRE_FORMAT_JSON.getLocalPart());
            writeEnd(writer);
        } else if (object instanceof XMLWireFormat) {
            writeStart(writer, WIRE_FORMAT_XML.getNamespaceURI(), WIRE_FORMAT_XML.getLocalPart());
            writeEnd(writer);
        } else if (object instanceof JAXRSOperationSelector) {
            writeStart(writer, OPERATION_SELCTOR_JAXRS.getNamespaceURI(), OPERATION_SELCTOR_JAXRS.getLocalPart());
            writeEnd(writer);
        } else if (object instanceof RPCOperationSelector) {
            writeStart(writer, OPERATION_SELCTOR_RPC.getNamespaceURI(), OPERATION_SELCTOR_RPC.getLocalPart());
            writeEnd(writer);
        }        
    }
}
