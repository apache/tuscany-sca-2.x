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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.binding.http.HTTPXMLWireFormat;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;

public class HTTPXMLWireFormatProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<HTTPXMLWireFormat> {
    
    public HTTPXMLWireFormatProcessor(FactoryExtensionPoint modelFactories) {
    }

    public QName getArtifactType() {
        return HTTPXMLWireFormat.WIRE_FORMAT_HTTP_XML_QNAME;
    }
    
    
    public HTTPXMLWireFormat read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException, XMLStreamException {
        HTTPXMLWireFormat wireFormat = new HTTPXMLWireFormat();
         
        return wireFormat;
    }

    public void write(HTTPXMLWireFormat wireFormat, XMLStreamWriter writer, ProcessorContext context) 
        throws ContributionWriteException, XMLStreamException {
        writer.writeStartElement("tuscany", 
                                 getArtifactType().getLocalPart(),
                                 getArtifactType().getNamespaceURI());
        writer.writeNamespace("tuscany", Constants.SCA11_TUSCANY_NS); 
        
        writer.writeEndElement();
    }

    public Class<HTTPXMLWireFormat> getModelType() {
        return HTTPXMLWireFormat.class;
    }

    public void resolve(HTTPXMLWireFormat arg0, ModelResolver arg1, ProcessorContext context) throws ContributionResolveException {

    }
    
}
