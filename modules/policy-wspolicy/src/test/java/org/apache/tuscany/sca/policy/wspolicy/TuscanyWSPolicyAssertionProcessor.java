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
package org.apache.tuscany.sca.policy.wspolicy;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;

/**
 *
 * @version $Rev$ $Date$
 */
public class TuscanyWSPolicyAssertionProcessor implements StAXArtifactProcessor<TuscanyWSPolicyAssertion> {

    public QName getArtifactType() {
        return new QName(Base.SCA11_TUSCANY_NS, "tuscanyWSPolicyAssertion");
    }

    public TuscanyWSPolicyAssertion read(XMLStreamReader arg0, ProcessorContext context) throws ContributionReadException, XMLStreamException {
        return new TuscanyWSPolicyAssertion();
    }

    public void write(TuscanyWSPolicyAssertion arg0, XMLStreamWriter arg1, ProcessorContext context) throws ContributionWriteException,
                                                        XMLStreamException {
    }

    public Class<TuscanyWSPolicyAssertion> getModelType() {
        // TODO Auto-generated method stub
        return TuscanyWSPolicyAssertion.class;
    }

    public void resolve(TuscanyWSPolicyAssertion arg0, ModelResolver arg1, ProcessorContext context) throws ContributionResolveException {

    }

}
