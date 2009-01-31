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
package bigbank.account.security;

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

/**
 * Implementation of a Policy Processor 
 *
 */
public class CheckingsDeptAuthorizationPolicyProcessor implements StAXArtifactProcessor<CheckingsDeptAuthPolicy> {
    private static final QName CHECKINGS_DEPT_AUTHORIZATION_POLICY_QNAME = new QName("http://bigbank/checkings", "AuthPolicy");
    public QName getArtifactType() {
        return CHECKINGS_DEPT_AUTHORIZATION_POLICY_QNAME;
    }
    
    public CheckingsDeptAuthorizationPolicyProcessor(ModelFactoryExtensionPoint modelFactories) {
    }

    
    public CheckingsDeptAuthPolicy read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        CheckingsDeptAuthPolicy policy = new CheckingsDeptAuthPolicy();
        return policy;
    }

    public void write(CheckingsDeptAuthPolicy policy, XMLStreamWriter writer) throws ContributionWriteException,
                                                        XMLStreamException {
        String prefix = "tuscany";
        writer.writeStartElement(prefix, 
                                 CHECKINGS_DEPT_AUTHORIZATION_POLICY_QNAME.getLocalPart(),
                                 CHECKINGS_DEPT_AUTHORIZATION_POLICY_QNAME.getNamespaceURI());
        writer.writeNamespace("chk", "http://bigbank/checkings");
        
       
        writer.writeEndElement();
    }

    public Class<CheckingsDeptAuthPolicy> getModelType() {
        return CheckingsDeptAuthPolicy.class;
    }

    public void resolve(CheckingsDeptAuthPolicy arg0, ModelResolver arg1) throws ContributionResolveException {

    }
    
}
