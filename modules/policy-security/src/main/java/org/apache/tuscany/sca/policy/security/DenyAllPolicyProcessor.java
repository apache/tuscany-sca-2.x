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


public class DenyAllPolicyProcessor implements StAXArtifactProcessor<DenyAllPolicy> {
    private static final QName DENY_ALL_AUTHORIZATION_POLICY_QNAME = DenyAllPolicy.NAME;
    
    public QName getArtifactType() {
        return DENY_ALL_AUTHORIZATION_POLICY_QNAME;
    }
    
    public DenyAllPolicyProcessor(ModelFactoryExtensionPoint modelFactories) {
    }

    
    public DenyAllPolicy read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        DenyAllPolicy policy = new DenyAllPolicy();
        return policy;
    }

    public void write(DenyAllPolicy policy, XMLStreamWriter writer) throws ContributionWriteException,
                                                        XMLStreamException {
       writer.writeStartElement(DENY_ALL_AUTHORIZATION_POLICY_QNAME.getLocalPart());
       writer.writeEndElement();
    }

    public Class<DenyAllPolicy> getModelType() {
        return DenyAllPolicy.class;
    }

    public void resolve(DenyAllPolicy policy, ModelResolver resolver) throws ContributionResolveException {
        //right now nothing to resolve
       policy.setUnresolved(false);
    }
    
}
