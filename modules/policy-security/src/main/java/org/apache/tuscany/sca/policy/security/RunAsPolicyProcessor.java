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
package org.apache.tuscany.sca.policy.security;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

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


public class RunAsPolicyProcessor implements StAXArtifactProcessor<RunAsPolicy> {
    private static final QName RUNAS_AUTHORIZATION_POLICY_QNAME = RunAsPolicy.NAME;
    private static final String ROLE = "role";
    
    public QName getArtifactType() {
        return RUNAS_AUTHORIZATION_POLICY_QNAME;
    }
    
    public RunAsPolicyProcessor(ModelFactoryExtensionPoint modelFactories) {
    }

    
    public RunAsPolicy read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        RunAsPolicy policy = new RunAsPolicy();
        int event = reader.getEventType();
        QName name = null;
        
        String role = reader.getAttributeValue(null, ROLE);
        policy.setRole(role);
        
        while (reader.hasNext()) {
            event = reader.getEventType();
            
            if ( event == END_ELEMENT ) {
                if ( RUNAS_AUTHORIZATION_POLICY_QNAME.equals(reader.getName()) ) {
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

    public void write(RunAsPolicy policy, XMLStreamWriter writer) throws ContributionWriteException,
                                                        XMLStreamException {
        writer.writeStartElement(RUNAS_AUTHORIZATION_POLICY_QNAME.getLocalPart());
        writer.writeAttribute(ROLE, policy.getRole());
        writer.writeEndElement();
    }

    public Class<RunAsPolicy> getModelType() {
        return RunAsPolicy.class;
    }

    public void resolve(RunAsPolicy policy, ModelResolver resolver) throws ContributionResolveException {
        //right now nothing to resolve
       policy.setUnresolved(false);
    }
    
}
