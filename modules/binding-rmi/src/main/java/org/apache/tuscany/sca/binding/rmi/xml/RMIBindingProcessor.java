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

package org.apache.tuscany.sca.binding.rmi.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.binding.rmi.RMIBinding;
import org.apache.tuscany.sca.binding.rmi.RMIBindingConstants;
import org.apache.tuscany.sca.binding.rmi.RMIBindingFactory;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;

public class RMIBindingProcessor implements
    StAXArtifactProcessor<RMIBinding>, RMIBindingConstants {

    private RMIBindingFactory rmiBindingFactory;
    private PolicyFactory policyFactory;

    public RMIBindingProcessor(AssemblyFactory assemblyFactory,
                               PolicyFactory policyFactory,
                               RMIBindingFactory rmiBindingFactory) {
        this.policyFactory = policyFactory;
        this.rmiBindingFactory = rmiBindingFactory;
    }

    public RMIBinding read(XMLStreamReader reader) throws ContributionReadException {
        try {
            RMIBinding rmiBinding = rmiBindingFactory.createRMIBinding();
            
            //Read policies
            readPolicies(rmiBinding, reader);
            
            //Read host, port and service name
            rmiBinding.setRmiHostName(reader.getAttributeValue(null, RMI_HOST));
            rmiBinding.setRmiPort(reader.getAttributeValue(null, RMI_PORT));
            rmiBinding.setRmiServiceName(reader.getAttributeValue(null, RMI_SERVICE));
            
            // Skip to end element
            while (reader.hasNext()) {
                if (reader.next() == END_ELEMENT && BINDING_RMI_QNAME.equals(reader.getName())) {
                    break;
                }
            }
            return rmiBinding;

        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }

    public void write(RMIBinding rmiBinding, XMLStreamWriter writer) throws ContributionWriteException {
        try {
            // Write a <binding.ws>
            writer.writeStartElement(Constants.SCA10_NS, BINDING_RMI);
            
            if (rmiBinding.getRmiHostName() != null) {
                writer.writeAttribute(RMIBindingConstants.RMI_HOST, rmiBinding.getRmiHostName());
            }
            
            if (rmiBinding.getRmiPort() != null) {
                writer.writeAttribute(RMIBindingConstants.RMI_PORT, rmiBinding.getRmiPort());
            }
            
            if (rmiBinding.getRmiServiceName() != null) {
                writer.writeAttribute(RMIBindingConstants.RMI_SERVICE, rmiBinding.getRmiServiceName());
            }
            writer.writeEndElement();

        } catch (XMLStreamException e) {
            throw new ContributionWriteException(e);
        }
    }

    public void resolve(RMIBinding model, ModelResolver resolver) throws ContributionResolveException {
    }

    public QName getArtifactType() {
        return RMIBindingConstants.BINDING_RMI_QNAME;
    }

    public Class<RMIBinding> getModelType() {
        return RMIBinding.class;
    }

    /**
     * Reads policy intents and policy sets.
     * @param attachPoint
     * @param reader
     */
    private void readPolicies(PolicySetAttachPoint attachPoint, XMLStreamReader reader) {
        String value = reader.getAttributeValue(null, Constants.REQUIRES);
        if (value != null) {
            List<Intent> requiredIntents = attachPoint.getRequiredIntents();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                Intent intent = policyFactory.createIntent();
                intent.setName(qname);
                requiredIntents.add(intent);
            }
        }

        value = reader.getAttributeValue(null, Constants.POLICY_SETS);
        if (value != null) {
            List<PolicySet> policySets = attachPoint.getPolicySets();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                PolicySet policySet = policyFactory.createPolicySet();
                policySet.setName(qname);
                policySets.add(policySet);
            }
        }
    }
    
    /**
     * Returns a qname from a string.  
     * @param reader
     * @param value
     * @return
     */
    private QName getQNameValue(XMLStreamReader reader, String value) {
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
