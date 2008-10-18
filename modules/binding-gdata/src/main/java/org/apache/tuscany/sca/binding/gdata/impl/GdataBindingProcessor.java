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
package org.apache.tuscany.sca.binding.gdata.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

//import org.apache.tuscany.sca.assembly.xml.PolicyAttachPointProcessor;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.impl.IntentAttachPointTypeFactoryImpl;

import org.apache.tuscany.sca.binding.gdata.GdataBinding;
import org.apache.tuscany.sca.binding.gdata.GdataBindingFactory;

/**
 * A processor for <binding.gdata> elements.
 */
public class GdataBindingProcessor implements StAXArtifactProcessor<GdataBinding> {

    private QName BINDING_GDATA = new QName("http://tuscany.apache.org/xmlns/sca/1.0", "binding.gdata");
    
    private final GdataBindingFactory factory;
    
    //private PolicyAttachPointProcessor policyProcessor;

    public GdataBindingProcessor(ModelFactoryExtensionPoint modelFactories) {
        this.factory = modelFactories.getFactory(GdataBindingFactory.class);
        System.out.println("[Debug Info]GdataBindingProcessor reached");
        //this.policyProcessor = new PolicyAttachPointProcessor(policyFactory);
    }

    public QName getArtifactType() {
        return BINDING_GDATA;
    }

    public Class<GdataBinding> getModelType() {
        return GdataBinding.class;
    }

    public GdataBinding read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        GdataBinding gdataBinding = factory.createGdataBinding();
                IntentAttachPointType bindingType = new IntentAttachPointTypeFactoryImpl().createBindingType();
        bindingType.setName(getArtifactType());
        bindingType.setUnresolved(true);
        ((PolicySetAttachPoint)gdataBinding).setType(bindingType);

        String name = reader.getAttributeValue(null, "name");
        if (name != null) {
            gdataBinding.setName(name);
        }

        String uri = reader.getAttributeValue(null, "uri");
        if (uri != null) {
            gdataBinding.setURI(uri);
        }
        
        String serviceType = reader.getAttributeValue(null, "serviceType");
        if (serviceType != null) {
            gdataBinding.setServiceType(serviceType);
        }
        
        String username = reader.getAttributeValue(null, "username");
        if (username != null) {
            gdataBinding.setUsername(username);
        }
        
        String password = reader.getAttributeValue(null, "password");
        if (password != null) {
            gdataBinding.setPassword(password);
        }        
                
        //policyProcessor.readPolicies(echoBinding, reader);

        return gdataBinding;
    }
    
    public void write(GdataBinding gdataBinding, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {

        //policyProcessor.writePolicyPrefixes(gdataBinding, writer);
        writer.writeStartElement(BINDING_GDATA.getNamespaceURI(), BINDING_GDATA.getLocalPart());
        //policyProcessor.writePolicyAttributes(gdataBinding, writer);
        
        if (gdataBinding.getName() != null) {
            writer.writeAttribute("name", gdataBinding.getName());
        }
        
        if (gdataBinding.getURI() != null) {
            writer.writeAttribute("uri", gdataBinding.getURI());
        }
        
        if (gdataBinding.getServiceType() != null) {
            writer.writeAttribute("serviceType", gdataBinding.getServiceType());
        }
        
        if (gdataBinding.getUsername() != null) {
            writer.writeAttribute("username", gdataBinding.getUsername());
        }
        
        if (gdataBinding.getPassword() != null) {
            writer.writeAttribute("password", gdataBinding.getPassword());
        }
        
        writer.writeEndElement();
    }

    
    //FIXME: Resolve the attached policySet, might not needed (the echo binding implementation example)
    public void resolve(GdataBinding gdataBinding, ModelResolver resolver) throws ContributionResolveException {
        PolicySetAttachPoint policySetAttachPoint = (PolicySetAttachPoint)gdataBinding;
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
