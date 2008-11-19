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
package org.apache.tuscany.sca.binding.jms.policy.authentication.token;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.binding.jms.policy.header.JMSHeaderPolicy;
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
public class JMSTokenAuthenticationPolicyProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<JMSTokenAuthenticationPolicy> {
    
    public QName getArtifactType() {
        return JMSTokenAuthenticationPolicy.JMS_TOKEN_AUTHENTICATION_POLICY_QNAME;
    }
    
    public JMSTokenAuthenticationPolicyProcessor(ModelFactoryExtensionPoint modelFactories, Monitor monitor) {
    }

    
    public JMSTokenAuthenticationPolicy read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        JMSTokenAuthenticationPolicy policy = new JMSTokenAuthenticationPolicy();
        int event = reader.getEventType();
        QName name = null;
        
        while (reader.hasNext()) {
            event = reader.getEventType();
            switch (event) {
                case START_ELEMENT : {
                    name = reader.getName();
                    if ( name.equals(getArtifactType()) ) {
                        policy.setTokenName(getQName(reader, JMSTokenAuthenticationPolicy.JMS_TOKEN_AUTHENTICATION_TOKEN_NAME));
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

    public void write(JMSTokenAuthenticationPolicy policy, XMLStreamWriter writer) 
        throws ContributionWriteException, XMLStreamException {
        String prefix = "tuscany";
        writer.writeStartElement(prefix, 
                                 getArtifactType().getLocalPart(),
                                 getArtifactType().getNamespaceURI());
        writer.writeNamespace("tuscany", Constants.SCA10_TUSCANY_NS);

        if ( policy.getTokenName() != null ) {
            writer.writeAttribute(prefix,
                                  getArtifactType().getNamespaceURI(),
                                  JMSTokenAuthenticationPolicy.JMS_TOKEN_AUTHENTICATION_TOKEN_NAME, 
                                  policy.getTokenName().toString());
        }      
        
        writer.writeEndElement();
    }

    public Class<JMSTokenAuthenticationPolicy> getModelType() {
        return JMSTokenAuthenticationPolicy.class;
    }

    public void resolve(JMSTokenAuthenticationPolicy arg0, ModelResolver arg1) throws ContributionResolveException {

    }
    
}
