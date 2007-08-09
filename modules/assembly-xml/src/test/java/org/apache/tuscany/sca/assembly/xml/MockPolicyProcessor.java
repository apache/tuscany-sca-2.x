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
package org.apache.tuscany.sca.assembly.xml;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.policy.Policy;

/**
 * @author administrator
 *
 */
public class MockPolicyProcessor implements StAXArtifactProcessor<Policy> {

    public QName getArtifactType() {
        return new QName("http://schemas.xmlsoap.org/ws/2004/09/policy", "PolicyAttachment");
    }

    public Policy read(XMLStreamReader arg0) throws ContributionReadException, XMLStreamException {
        return new MockPolicyImplOne();
    }

    public void write(Policy arg0, XMLStreamWriter arg1) throws ContributionWriteException,
                                                        XMLStreamException {

    }

    public Class<Policy> getModelType() {
        // TODO Auto-generated method stub
        return Policy.class;
    }

    public void resolve(Policy arg0, ModelResolver arg1) throws ContributionResolveException {

    }

    
    public class MockPolicyImplOne implements Policy {
        public QName getSchemaName() {
            return new QName("http://schemas.xmlsoap.org/ws/2004/09/policy", "PolicyAttachment");
        }
        
    }
}
