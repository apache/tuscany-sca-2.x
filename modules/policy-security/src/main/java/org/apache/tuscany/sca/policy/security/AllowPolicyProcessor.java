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
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;


public class AllowPolicyProcessor implements StAXArtifactProcessor<AllowPolicy> {
    private static final QName ALLOW_AUTHORIZATION_POLICY_QNAME = AllowPolicy.NAME;
    private static final String ROLES = "roles";
    
    public QName getArtifactType() {
        return ALLOW_AUTHORIZATION_POLICY_QNAME;
    }
    
    public AllowPolicyProcessor(ModelFactoryExtensionPoint modelFactories) {
    }

    
    public AllowPolicy read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        AllowPolicy policy = new AllowPolicy();
        int event = reader.getEventType();
        QName name = null;
        
        String roleNames = reader.getAttributeValue(null, ROLES);
        StringTokenizer st = new StringTokenizer(roleNames);
        while ( st.hasMoreTokens() ) {
            policy.getRoleNames().add(st.nextToken());
        }
        
        while (reader.hasNext()) {
            event = reader.getEventType();
            
            if ( event == END_ELEMENT ) {
                if ( ALLOW_AUTHORIZATION_POLICY_QNAME.equals(reader.getName()) ) {
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

    public void write(AllowPolicy policy, XMLStreamWriter writer) throws ContributionWriteException,
                                                        XMLStreamException {
        writer.writeStartElement(ALLOW_AUTHORIZATION_POLICY_QNAME.getLocalPart());
        
        StringBuffer sb = new StringBuffer();
        for ( String role : policy.getRoleNames() ) {
            sb.append(role);
        }
        
        if ( sb.length() > 0 ) {
            writer.writeAttribute(ROLES, sb.toString());
        }
       
        writer.writeEndElement();
    }

    public Class<AllowPolicy> getModelType() {
        return AllowPolicy.class;
    }

    public void resolve(AllowPolicy policy, ModelResolver resolver) throws ContributionResolveException {
        //right now nothing to resolve
       policy.setUnresolved(false);
    }
    
}
