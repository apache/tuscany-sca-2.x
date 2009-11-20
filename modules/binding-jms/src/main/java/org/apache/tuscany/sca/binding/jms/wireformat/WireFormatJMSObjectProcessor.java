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
package org.apache.tuscany.sca.binding.jms.wireformat;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;

/**
 *
 * @version $Rev$ $Date$
 */
public class WireFormatJMSObjectProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<WireFormatJMSObject> {
    
    public QName getArtifactType() {
        return WireFormatJMSObject.WIRE_FORMAT_JMS_BYTES_QNAME;
    }
    
    public WireFormatJMSObjectProcessor(FactoryExtensionPoint modelFactories) {
    }

    
    public WireFormatJMSObject read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException, XMLStreamException {
        WireFormatJMSObject wireFormat = new WireFormatJMSObject();
        
        String wrappedSingleInput = reader.getAttributeValue(null, WireFormatJMSObject.WIRE_FORMAT_JMS_OBJECT_WRAP_SINGLE_ATTR);
        if (wrappedSingleInput != null && wrappedSingleInput.length() > 0) {
            if ("true".equalsIgnoreCase(wrappedSingleInput)) {
                wireFormat.setWrappedSingleInput(true);
            } else if ("false".equalsIgnoreCase(wrappedSingleInput)) {
                wireFormat.setWrappedSingleInput(false);
            } else {
                throw new ContributionReadException(WireFormatJMSObject.WIRE_FORMAT_JMS_BYTES_QNAME.toString() + ": " + wrappedSingleInput + 
                        " is not a valid attribute value for " + WireFormatJMSObject.WIRE_FORMAT_JMS_OBJECT_WRAP_SINGLE_ATTR);
            }
        }
        return wireFormat;
    }

    public void write(WireFormatJMSObject wireFormat, XMLStreamWriter writer, ProcessorContext context) 
        throws ContributionWriteException, XMLStreamException {
        String prefix = "tuscany";
        writer.writeStartElement(prefix, 
                                 getArtifactType().getLocalPart(),
                                 getArtifactType().getNamespaceURI());
        writer.writeNamespace("tuscany", Constants.SCA11_TUSCANY_NS); 
        
        writer.writeAttribute(WireFormatJMSObject.WIRE_FORMAT_JMS_OBJECT_WRAP_SINGLE_ATTR, String.valueOf(wireFormat.isWrappedSingleInput()));
        
        writer.writeEndElement();
    }

    public Class<WireFormatJMSObject> getModelType() {
        return WireFormatJMSObject.class;
    }

    public void resolve(WireFormatJMSObject arg0, ModelResolver arg1, ProcessorContext context) throws ContributionResolveException {

    }
    
}
