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

package org.apache.tuscany.sca.binding.sca;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;

/**
 * A processor for <binding.sca> elements.
 */
public class SCABindingProcessor implements StAXArtifactProcessor<SCABinding> {

    private static final QName BINDING_SCA = new QName(Constants.SCA10_NS, "binding.sca");
    private final static String NAME = "name"; 
    private final static String URI = "uri"; 

    private final SCABindingFactory factory;

    public SCABindingProcessor(SCABindingFactory factory) {
        this.factory = factory;
    }

    public QName getArtifactType() {
        return BINDING_SCA;
    }

    public Class<SCABinding> getModelType() {
        return SCABinding.class;
    }

    public SCABinding read(XMLStreamReader reader) throws ContributionReadException {
        SCABinding scaBinding = factory.createSCABinding();
        String name = reader.getAttributeValue(null, NAME);
        if (name != null) {
            scaBinding.setName(name);
        }
        String uri = reader.getAttributeValue(null, URI);
        if (uri != null) {
            scaBinding.setURI(uri);
        }
        return scaBinding;
    }

    public void write(SCABinding scaBinding, XMLStreamWriter writer) throws ContributionWriteException {
        try {
            writer.writeStartElement(BINDING_SCA.getNamespaceURI(), BINDING_SCA.getLocalPart());
            if (scaBinding.getURI() != null) {
                writer.writeAttribute("uri", scaBinding.getURI());
            }
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new ContributionWriteException(e);
        }
    }

    public void resolve(SCABinding scaBinding, ModelResolver resolver) throws ContributionResolveException {
        // TODO: Need to resolve the ComponentService by URI
    }

}
