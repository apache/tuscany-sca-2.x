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

package org.apache.tuscany.sca.core.assembly;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.runtime.ReferenceParameters;

/**
 * Artifact processor for reference parameters.
 * 
 * @version $Rev$ $Date$
 */
public class ReferenceParameterProcessor implements StAXArtifactProcessor<ReferenceParameters> {
    private static final QName REFERENCE_PARAMETERS =
        new QName("http://tuscany.apache.org/xmlns/sca/1.0", "referenceParameters", "tuscany");
    
    /**
     * Constructs a new processor.
     * 
     * @param modelFactories
     */
    public ReferenceParameterProcessor(ModelFactoryExtensionPoint modelFactories, Monitor monitor) {
    }

    /**
     * @see org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor#getArtifactType()
     */
    public QName getArtifactType() {
        return REFERENCE_PARAMETERS;
    }

    /**
     * @see org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor#read(javax.xml.stream.XMLStreamReader)
     */
    public ReferenceParameters read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        ReferenceParameters parameters = new ReferenceParametersImpl();
        parameters.setConversationID(reader.getAttributeValue(null, "conversationID"));
        parameters.setCallbackID(reader.getAttributeValue(null, "callbackID"));
        return parameters;
    }

    /**
     * @see org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor#write(java.lang.Object, javax.xml.stream.XMLStreamWriter)
     */
    public void write(ReferenceParameters model, XMLStreamWriter writer) throws ContributionWriteException,
        XMLStreamException {
        writer.writeStartElement(REFERENCE_PARAMETERS.getPrefix(),
                                 REFERENCE_PARAMETERS.getLocalPart(),
                                 REFERENCE_PARAMETERS.getNamespaceURI());
        writer.writeNamespace(REFERENCE_PARAMETERS.getPrefix(), REFERENCE_PARAMETERS.getNamespaceURI());
        if (model.getConversationID() != null) {
            writer.writeAttribute("conversationID", model.getConversationID().toString());
        }
        if (model.getCallbackID() != null) {
            writer.writeAttribute("callbackID", model.getCallbackID().toString());
        }
        writer.writeEndElement();
    }

    /**
     * @see org.apache.tuscany.sca.contribution.processor.ArtifactProcessor#getModelType()
     */
    public Class<ReferenceParameters> getModelType() {
        return ReferenceParameters.class;
    }

    /**
     * @see org.apache.tuscany.sca.contribution.processor.ArtifactProcessor#resolve(java.lang.Object, org.apache.tuscany.sca.contribution.resolver.ModelResolver)
     */
    public void resolve(ReferenceParameters model, ModelResolver resolver) throws ContributionResolveException {
    }

}
