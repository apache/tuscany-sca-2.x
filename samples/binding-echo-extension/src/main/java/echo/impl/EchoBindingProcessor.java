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
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPoint;
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
    private PolicyFactory policyFactory;

    public EchoBindingProcessor(EchoBindingFactory factory, PolicyFactory pFactory) {
        this.factory = factory;
        this.policyFactory = pFactory;
    }

    public QName getArtifactType() {
        return BINDING_ECHO;
    }

    public Class<EchoBinding> getModelType() {
        return EchoBinding.class;
    }

    public EchoBinding read(XMLStreamReader reader) throws ContributionReadException {
        EchoBinding echoBinding = factory.createEchoBinding();

        String name = reader.getAttributeValue(null, "name");
        if (name != null) {
            echoBinding.setName(name);
        }
        String uri = reader.getAttributeValue(null, "uri");
        if (uri != null) {
            echoBinding.setURI(uri);
        }
        
        readPolicies(echoBinding, null, reader);
        return echoBinding;
    }
    
    protected QName getQNameValue(XMLStreamReader reader, String value) {
        if (value != null) {
            int index = value.indexOf(':');
            String prefix = index == -1 ? "" : value.substring(0, index);
            String localName = index == -1 ? value : value.substring(index + 1);
            String ns = reader.getNamespaceContext().getNamespaceURI(prefix);
            if (ns == null) {
                ns = "";
            }
            return new QName(ns, localName, prefix);
        } else {
            return null;
        }
    }
    
    protected void readIntents(IntentAttachPoint attachPoint, Operation operation, XMLStreamReader reader) {
        String value = reader.getAttributeValue(null, "requires");
        if (value != null) {
            List<Intent> requiredIntents = attachPoint.getRequiredIntents();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                Intent intent = policyFactory.createIntent();
                intent.setName(qname);
                if (operation != null) {
                    //intent.getOperations().add(operation);
                }
                requiredIntents.add(intent);
            }
        }
    }
    
    protected void readPolicies(PolicySetAttachPoint attachPoint, Operation operation, XMLStreamReader reader) {
        readIntents(attachPoint, operation, reader);

        String value = reader.getAttributeValue(null, "policySets");
        if (value != null) {
            List<PolicySet> policySets = attachPoint.getPolicySets();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                PolicySet policySet = policyFactory.createPolicySet();
                policySet.setName(qname);
                if (operation != null) {
                    //policySet.getOperations().add(operation);
                }
                policySets.add(policySet);
            }
        }
    }

    public void write(EchoBinding echoBinding, XMLStreamWriter writer) throws ContributionWriteException {
    }

    public void resolve(EchoBinding echoBinding, ModelResolver resolver) throws ContributionResolveException {
        List<Intent> requiredIntents = new ArrayList<Intent>();
        Intent resolvedIntent = null;
        for ( Intent intent : echoBinding.getRequiredIntents() ) {
            resolvedIntent = resolver.resolveModel(Intent.class, intent);
            requiredIntents.add(resolvedIntent);
        }
        echoBinding.getRequiredIntents().clear();
        echoBinding.getRequiredIntents().addAll(requiredIntents);
        
        List<PolicySet> resolvedPolicySets = new ArrayList<PolicySet>();
        PolicySet resolvedPolicySet = null;
        for ( PolicySet policySet : echoBinding.getPolicySets() ) {
            resolvedPolicySet = resolver.resolveModel(PolicySet.class, policySet);
            resolvedPolicySets.add(resolvedPolicySet);
        }
        echoBinding.getPolicySets().clear();
        echoBinding.getPolicySets().addAll(resolvedPolicySets);
    }
}
