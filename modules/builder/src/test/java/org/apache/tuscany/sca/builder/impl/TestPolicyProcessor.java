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
package org.apache.tuscany.sca.builder.impl;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;

/**
 * A PolicyProcessor used for testing.
 *
 * @version $Rev$ $Date$
 */
public class TestPolicyProcessor implements StAXArtifactProcessor<MockPolicy> {
    public QName getArtifactType() {
        return new QName("http://schemas.xmlsoap.org/ws/2004/09/policy", "PolicyAttachment");
    }

    public MockPolicy read(XMLStreamReader arg0) throws ContributionReadException, XMLStreamException {
        return new MockPolicy();
    }

    public void write(MockPolicy arg0, XMLStreamWriter arg1) throws ContributionWriteException, XMLStreamException {
    }

    public Class<MockPolicy> getModelType() {
        return MockPolicy.class;
    }

    public void resolve(MockPolicy arg0, ModelResolver arg1) throws ContributionResolveException {

    }

}
