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
package echo.provider.policy;

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
public class WSPolicyProcessor implements StAXArtifactProcessor<WSPolicy> {

    public QName getArtifactType() {
        return new QName("http://schemas.xmlsoap.org/ws/2004/09/policy", "PolicyAttachment");
    }

    public WSPolicy read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        //FIXME Implement this method
        return new WSPolicy();
    }

    public void write(WSPolicy wsPolicy, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {
        //FIXME Implement this method
    }

    public Class<WSPolicy> getModelType() {
        return WSPolicy.class;
    }

    public void resolve(WSPolicy wsPolicy, ModelResolver modelResolver) throws ContributionResolveException {

    }
}
