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

package echo.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.xml.PolicyAttachPointProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;

import echo.EchoBinding;
import echo.EchoBindingFactory;

/**
 * A processor for <binding.echo> elements.
 */
public class EchoBindingProcessor implements StAXArtifactProcessor<EchoBinding> {

    private QName BINDING_ECHO = new QName("http://echo", "binding.echo");
    
    private final EchoBindingFactory factory;
    private PolicyAttachPointProcessor policyProcessor;

    public EchoBindingProcessor(EchoBindingFactory factory, PolicyFactory policyFactory) {
        this.factory = factory;
        this.policyProcessor = new PolicyAttachPointProcessor(policyFactory);
    }

    public QName getArtifactType() {
        return BINDING_ECHO;
    }

    public Class<EchoBinding> getModelType() {
        return EchoBinding.class;
    }

    public EchoBinding read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        EchoBinding echoBinding = factory.createEchoBinding();

        String name = reader.getAttributeValue(null, "name");
        if (name != null) {
            echoBinding.setName(name);
        }

        String uri = reader.getAttributeValue(null, "uri");
        if (uri != null) {
            echoBinding.setURI(uri);
        }
        
        policyProcessor.readPolicies(echoBinding, reader);

        return echoBinding;
    }
    
    public void write(EchoBinding echoBinding, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {

        policyProcessor.writePolicyPrefixes(echoBinding, writer);
        writer.writeStartElement(BINDING_ECHO.getNamespaceURI(), BINDING_ECHO.getLocalPart());
        policyProcessor.writePolicyAttributes(echoBinding, writer);
        
        if (echoBinding.getName() != null) {
            writer.writeAttribute("name", echoBinding.getName());
        }
        
        if (echoBinding.getURI() != null) {
            writer.writeAttribute("uri", echoBinding.getURI());
        }
        
        writer.writeEndElement();
    }

    public void resolve(EchoBinding echoBinding, ModelResolver resolver) throws ContributionResolveException {
        PolicySetAttachPoint policySetAttachPoint = (PolicySetAttachPoint)echoBinding;
        List<Intent> requiredIntents = new ArrayList<Intent>();
        Intent resolvedIntent = null;
        for ( Intent intent : policySetAttachPoint.getRequiredIntents() ) {
            resolvedIntent = resolver.resolveModel(Intent.class, intent);
            requiredIntents.add(resolvedIntent);
        }
        policySetAttachPoint.getRequiredIntents().clear();
        policySetAttachPoint.getRequiredIntents().addAll(requiredIntents);
        
        List<PolicySet> resolvedPolicySets = new ArrayList<PolicySet>();
        PolicySet resolvedPolicySet = null;
        for ( PolicySet policySet : policySetAttachPoint.getPolicySets() ) {
            resolvedPolicySet = resolver.resolveModel(PolicySet.class, policySet);
            resolvedPolicySets.add(resolvedPolicySet);
        }
        policySetAttachPoint.getPolicySets().clear();
        policySetAttachPoint.getPolicySets().addAll(resolvedPolicySets);
    }
}
