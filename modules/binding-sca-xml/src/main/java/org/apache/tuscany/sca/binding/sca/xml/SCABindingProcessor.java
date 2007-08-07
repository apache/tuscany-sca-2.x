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

package org.apache.tuscany.sca.binding.sca.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
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

/**
 * A processor to read the XML that describes the SCA binding.
 */

public class SCABindingProcessor implements StAXArtifactProcessor<SCABinding>, Constants{
    
    protected AssemblyFactory assemblyFactory;
    protected PolicyFactory policyFactory;       
    private SCABindingFactory scaBindingFactory;

    protected static final String BINDING_SCA = "binding.sca";
    protected static final QName BINDING_SCA_QNAME = new QName(Constants.SCA10_NS, BINDING_SCA);

    public SCABindingProcessor(ModelFactoryExtensionPoint modelFactories) {
        this.assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        this.policyFactory = modelFactories.getFactory(PolicyFactory.class);
        this.scaBindingFactory = modelFactories.getFactory(SCABindingFactory.class);
    }
    
    public SCABindingProcessor(AssemblyFactory assemblyFactory,
                               PolicyFactory policyFactory,
                               SCABindingFactory scaBindingFactory) {
    	this.assemblyFactory = assemblyFactory;
    	this.policyFactory = policyFactory;
        this.scaBindingFactory = scaBindingFactory;
    }

    public QName getArtifactType() {
        return BINDING_SCA_QNAME;
    }

    public Class<SCABinding> getModelType() {
        return SCABinding.class;
    }

    public SCABinding read(XMLStreamReader reader) throws ContributionReadException {
        try {
            SCABinding scaBinding = scaBindingFactory.createSCABinding();
            
            // Read policies
            readPolicies(scaBinding, reader);
            
            // Read binding name
            String name = reader.getAttributeValue(null, NAME);
            if (name != null) {
                scaBinding.setName(name);
            }

            // Read binding URI
            String uri = reader.getAttributeValue(null, URI);
            if (uri != null) {
                scaBinding.setURI(uri);
            }

            // Skip to end element
            while (reader.hasNext()) {
                if (reader.next() == END_ELEMENT && BINDING_SCA_QNAME.equals(reader.getName())) {
                    break;
                }
            }
            return scaBinding;

        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }
    
    public void resolve(SCABinding model, ModelResolver resolver) throws ContributionResolveException {
    }    

    public void write(SCABinding scaBinding, XMLStreamWriter writer) throws ContributionWriteException {
        try {
            // Write a <binding.sca>
            writer.writeStartElement(Constants.SCA10_NS, BINDING_SCA);

            // Write binding URI
            if (scaBinding.getURI() != null) {
                writer.writeAttribute(URI, scaBinding.getURI());
            }
            
            writer.writeEndElement();

        } catch (XMLStreamException e) {
            throw new ContributionWriteException(e);
        }
    }
    
    /**
     * The following are copied from BaseArtefactProcessor as that class is
     * abstract
     */
     
    /**
     * Reads policy intents and policy sets.
     * @param attachPoint
     * @param reader
     */
    protected void readPolicies(PolicySetAttachPoint attachPoint, XMLStreamReader reader) {
        readPolicies(attachPoint, null, reader);
    }

    /**
     * Reads policy intents and policy sets associated with an operation.
     * @param attachPoint
     * @param operation
     * @param reader
     */
    protected void readPolicies(PolicySetAttachPoint attachPoint, Operation operation, XMLStreamReader reader) {
        readIntents(attachPoint, operation, reader);

        String value = reader.getAttributeValue(null, Constants.POLICY_SETS);
        if (value != null) {
            List<PolicySet> policySets = attachPoint.getPolicySets();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                PolicySet policySet = policyFactory.createPolicySet();
                policySet.setName(qname);
                if (operation != null) {
                    policySet.getOperations().add(operation);
                }
                policySets.add(policySet);
            }
        }
    }  
    
    /**
     * Read policy intents associated with an operation.
     * @param attachPoint
     * @param operation
     * @param reader
     */
    protected void readIntents(IntentAttachPoint attachPoint, Operation operation, XMLStreamReader reader) {
        String value = reader.getAttributeValue(null, Constants.REQUIRES);
        if (value != null) {
            List<Intent> requiredIntents = attachPoint.getRequiredIntents();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                Intent intent = policyFactory.createIntent();
                intent.setName(qname);
                if (operation != null) {
                    intent.getOperations().add(operation);
                }
                requiredIntents.add(intent);
            }
        }
    }
    
    /**
     * Returns a qname from a string.  
     * @param reader
     * @param value
     * @return
     */
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
    
    

}
