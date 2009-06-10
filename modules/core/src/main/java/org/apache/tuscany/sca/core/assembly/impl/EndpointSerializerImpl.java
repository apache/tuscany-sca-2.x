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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.Endpoint2;
import org.apache.tuscany.sca.assembly.EndpointReference2;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.assembly.EndpointSerializer;

public class EndpointSerializerImpl implements EndpointSerializer {
    private XMLInputFactory inputFactory;
    private XMLOutputFactory outputFactory;
    private StAXArtifactProcessor<Endpoint2> processor;
    private StAXArtifactProcessor<EndpointReference2> refProcessor;

    public EndpointSerializerImpl(ExtensionPointRegistry registry) {
        FactoryExtensionPoint factories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        inputFactory = factories.getFactory(XMLInputFactory.class);
        outputFactory = factories.getFactory(XMLOutputFactory.class);
        StAXArtifactProcessorExtensionPoint processors =
            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        processor = processors.getProcessor(Endpoint2.class);
        refProcessor = processors.getProcessor(EndpointReference2.class);
    }

    public void readExternal(Endpoint2 endpoint, ObjectInput input) throws IOException {
        try {
            String xml = input.readUTF();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xml));
            Endpoint2 result = processor.read(reader);
            endpoint.setComponent(result.getComponent());
            endpoint.setService(result.getService());
            endpoint.setBinding(result.getBinding());
        } catch (Exception e) {
            throw new IOException(e);
        }

    }

    public void writeExternal(Endpoint2 endpoint, ObjectOutput output) throws IOException {
        StringWriter sw = new StringWriter();
        try {
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(sw);
            processor.write(endpoint, writer);
            writer.flush();
            output.writeUTF(sw.toString());
            writer.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public void readExternal(EndpointReference2 endpointReference, ObjectInput input) throws IOException {
        try {
            String xml = input.readUTF();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xml));
            EndpointReference2 result = refProcessor.read(reader);
            reader.close();
            endpointReference.setComponent(result.getComponent());
            endpointReference.setReference(result.getReference());
            endpointReference.setBinding(result.getBinding());
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public void writeExternal(EndpointReference2 endpointReference, ObjectOutput output) throws IOException {
        StringWriter sw = new StringWriter();
        try {
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(sw);
            refProcessor.write(endpointReference, writer);
            writer.flush();
            output.writeUTF(sw.toString());
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}