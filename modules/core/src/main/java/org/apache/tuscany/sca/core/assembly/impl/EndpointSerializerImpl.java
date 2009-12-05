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

package org.apache.tuscany.sca.core.assembly.impl;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.runtime.EndpointSerializer;
import org.oasisopen.sca.ServiceRuntimeException;

public class EndpointSerializerImpl implements EndpointSerializer {
    private ExtensionPointRegistry registry;
    private XMLInputFactory inputFactory;
    private XMLOutputFactory outputFactory;
    private StAXArtifactProcessor<Endpoint> processor;
    private StAXArtifactProcessor<EndpointReference> refProcessor;

    public EndpointSerializerImpl(ExtensionPointRegistry registry) {
        this.registry = registry;
        FactoryExtensionPoint factories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        inputFactory = factories.getFactory(XMLInputFactory.class);
        outputFactory = factories.getFactory(XMLOutputFactory.class);
        StAXArtifactProcessorExtensionPoint processors =
            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        processor = processors.getProcessor(Endpoint.class);
        refProcessor = processors.getProcessor(EndpointReference.class);
    }

    public Endpoint readEndpoint(String xml) {
        try {
            XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xml));
            Endpoint result = processor.read(reader, new ProcessorContext(registry));
            result.setRemote(true);
            reader.close();
            return result;
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public String write(Endpoint endpoint) {
        StringWriter sw = new StringWriter();
        try {
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(sw);
            processor.write(endpoint, writer, new ProcessorContext(registry));
            writer.flush();
            writer.close();
            return sw.toString();
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public EndpointReference readEndpointReference(String xml) {
        try {
            XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xml));
            EndpointReference result = refProcessor.read(reader, new ProcessorContext(registry));
            result.setRemote(true);
            reader.close();
            return result;
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public String write(EndpointReference endpointReference) {
        StringWriter sw = new StringWriter();
        try {
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(sw);
            refProcessor.write(endpointReference, writer, new ProcessorContext(registry));
            writer.flush();
            writer.close();
            return sw.toString();
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }
}
