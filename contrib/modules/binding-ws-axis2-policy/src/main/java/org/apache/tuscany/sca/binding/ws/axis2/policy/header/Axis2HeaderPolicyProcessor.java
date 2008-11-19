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
package org.apache.tuscany.sca.binding.ws.axis2.policy.header;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.logging.Level;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 *
 * @version $Rev$ $Date$
 */
public class Axis2HeaderPolicyProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<Axis2HeaderPolicy> {
    
    public QName getArtifactType() {
        return Axis2HeaderPolicy.AXIS2_HEADER_POLICY_QNAME;
    }
    
    public Axis2HeaderPolicyProcessor(ModelFactoryExtensionPoint modelFactories, Monitor monitor) {
    }

    
    public Axis2HeaderPolicy read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        Axis2HeaderPolicy policy = new Axis2HeaderPolicy();
        int event = reader.getEventType();
        QName name = null;
        
        while (reader.hasNext()) {
            event = reader.getEventType();
            switch (event) {
                case START_ELEMENT : {
                    name = reader.getName();
                    if ( name.equals(getArtifactType()) ) {
                        policy.setHeaderName(getQName(reader, Axis2HeaderPolicy.AXIS2_HEADER_NAME));
                    } 
                    break;
                }
            }
            
            if ( event == END_ELEMENT ) {
                if ( getArtifactType().equals(reader.getName()) ) {
                    break;
                } 
            }
            
            //Read the next element
            if (reader.hasNext()) {
                reader.next();
            }
        }
         
        return policy;
    }

    public void write(Axis2HeaderPolicy policy, XMLStreamWriter writer) 
        throws ContributionWriteException, XMLStreamException {
        String prefix = "tuscany";
        writer.writeStartElement(prefix, 
                                 getArtifactType().getLocalPart(),
                                 getArtifactType().getNamespaceURI());
        writer.writeNamespace("tuscany", Constants.SCA10_TUSCANY_NS);

        if ( policy.getHeaderName() != null ) {
            writer.writeStartElement(prefix, 
                                     Axis2HeaderPolicy.AXIS2_HEADER_NAME,
                                     getArtifactType().getNamespaceURI());
            writer.writeCharacters(policy.getHeaderName().toString());
            writer.writeEndElement();
        }      
        
        writer.writeEndElement();
    }

    public Class<Axis2HeaderPolicy> getModelType() {
        return Axis2HeaderPolicy.class;
    }

    public void resolve(Axis2HeaderPolicy arg0, ModelResolver arg1) throws ContributionResolveException {

    }
    
}
