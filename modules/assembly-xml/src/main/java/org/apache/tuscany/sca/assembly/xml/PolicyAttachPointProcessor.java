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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPoint;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;

public class PolicyAttachPointProcessor extends BaseStAXArtifactProcessor implements Constants {
    
    private PolicyFactory policyFactory;
    
    public PolicyAttachPointProcessor(PolicyFactory policyFactory) {
        this.policyFactory = policyFactory;
    }

    /**
     * Read policy intents associated with an operation.
     * @param attachPoint
     * @param operation
     * @param reader
     */
    private void readIntents(Object attachPoint, Operation operation, XMLStreamReader reader) {
        if (!(attachPoint instanceof IntentAttachPoint))
            return;
        IntentAttachPoint intentAttachPoint = (IntentAttachPoint)attachPoint;
        String value = reader.getAttributeValue(null, REQUIRES);
        if (value != null) {
            List<Intent> requiredIntents = intentAttachPoint.getRequiredIntents();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                Intent intent = policyFactory.createIntent();
                intent.setName(qname);
                if (operation != null) {
                    //FIXME Don't we need to handle intent specification
                    // on an operation basis?
                    //intent.getOperations().add(operation);
                }
                requiredIntents.add(intent);
            }
        }
    }

    /**
     * Reads policy intents and policy sets associated with an operation.
     * @param attachPoint
     * @param operation
     * @param reader
     */
    public void readPolicies(Object attachPoint, Operation operation, XMLStreamReader reader) {
        readIntents(attachPoint, operation, reader);
        readPolicySets(attachPoint, operation, reader);
    }

    /**
     * Reads policy intents and policy sets.
     * @param attachPoint
     * @param reader
     */
    public void readPolicies(Object attachPoint, XMLStreamReader reader) {
        readPolicies(attachPoint, null, reader);
    }

    /**
     * Reads policy sets associated with an operation.
     * @param attachPoint
     * @param operation
     * @param reader
     */
    private void readPolicySets(Object attachPoint, Operation operation, XMLStreamReader reader) {
        if (!(attachPoint instanceof PolicySetAttachPoint)) {
            return;
        }
        PolicySetAttachPoint policySetAttachPoint = (PolicySetAttachPoint)attachPoint;
        String value = reader.getAttributeValue(null, POLICY_SETS);
        if (value != null) {
            List<PolicySet> policySets = policySetAttachPoint.getPolicySets();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                PolicySet policySet = policyFactory.createPolicySet();
                policySet.setName(qname);
                if (operation != null) {
                    //FIXME Don't we need to handle policySet specification
                    // on an operation basis?
                    //policySet.getOperations().add(operation);
                }
                policySets.add(policySet);
            }
        }
    }
    
    /**
     * Write policies
     * @param attachPoint
     * @return
     */
    XAttr writePolicies(Object attachPoint) throws XMLStreamException {
        return writePolicies(attachPoint, (Operation)null);
    }

    /**
     * Write policies
     * @param attachPoint
     * @return
     */
    public void writePolicyAttributes(Object attachPoint, XMLStreamWriter writer) throws XMLStreamException {
        writePolicyAttributes(attachPoint, (Operation)null, writer);
    }

    /**
     * Write policies
     * @param attachPoint
     * @return
     */
    public void writePolicyPrefixes(Object attachPoint, XMLStreamWriter writer) throws XMLStreamException {
        writePolicyPrefixes(attachPoint, (Operation)null, writer);
    }

    /**
     * Write policies associated with an operation
     * @param attachPoint
     * @param operation
     * @return
     */
    XAttr writePolicies(Object attachPoint, Operation operation) {
        List<XAttr> attrs =new ArrayList<XAttr>();
        attrs.add(writeIntents(attachPoint, operation));
        attrs.add(writePolicySets(attachPoint, operation));
        return new XAttr(null, attrs);
    }

    /**
     * Write policies
     * @param attachPoint
     * @return
     */
    public void writePolicyAttributes(Object attachPoint, Operation operation, XMLStreamWriter writer) throws XMLStreamException {
        XAttr attr = writePolicies(attachPoint, operation);
        attr.write(writer);
    }

    /**
     * Write policies
     * @param attachPoint
     * @return
     */
    public void writePolicyPrefixes(Object attachPoint, Operation operation, XMLStreamWriter writer) throws XMLStreamException {
        XAttr attr = writePolicies(attachPoint, operation);
        attr.writePrefix(writer);
    }

    /**
     * Write policy intents associated with an operation.
     * @param attachPoint
     * @param operation
     */
    private XAttr writeIntents(Object attachPoint, Operation operation) {
        if (!(attachPoint instanceof IntentAttachPoint)) {
            return null;
        }
        IntentAttachPoint intentAttachPoint = (IntentAttachPoint)attachPoint;
        List<QName> qnames = new ArrayList<QName>();
        for (Intent intent: intentAttachPoint.getRequiredIntents()) {
            qnames.add(intent.getName());
        }
        return new XAttr(Constants.REQUIRES, qnames);
    }

    /**
     * Write policy sets associated with an operation.
     * @param attachPoint
     * @param operation
     */
    private XAttr writePolicySets(Object attachPoint, Operation operation) {
        if (!(attachPoint instanceof PolicySetAttachPoint)) {
            return null;
        }
        PolicySetAttachPoint policySetAttachPoint = (PolicySetAttachPoint)attachPoint;
        List<QName> qnames = new ArrayList<QName>();
        for (PolicySet policySet: policySetAttachPoint.getPolicySets()) {
            qnames.add(policySet.getName());
        }
        return new XAttr(Constants.POLICY_SETS, qnames);
    }
    
    public void resolvePolicies(Object attachPoint, ModelResolver resolver) {
        if ( attachPoint instanceof PolicySetAttachPoint ) {
            PolicySetAttachPoint policySetAttachPoint = (PolicySetAttachPoint)attachPoint;
            
            List<Intent> requiredIntents = new ArrayList<Intent>();
            Intent resolvedIntent = null;
            
            if ( policySetAttachPoint instanceof Binding ) {
                if ( policySetAttachPoint.getType().isUnresolved() ) {
                    IntentAttachPointType resolved = 
                        resolver.resolveModel(IntentAttachPointType.class, policySetAttachPoint.getType());
                    policySetAttachPoint.setType(resolved);
                }
            }
            
            if ( policySetAttachPoint.getRequiredIntents() != null && policySetAttachPoint.getRequiredIntents().size() > 0 ) {
                for ( Intent intent : policySetAttachPoint.getRequiredIntents() ) {
                    resolvedIntent = resolver.resolveModel(Intent.class, intent);
                    requiredIntents.add(resolvedIntent);
                }
                policySetAttachPoint.getRequiredIntents().clear();
                policySetAttachPoint.getRequiredIntents().addAll(requiredIntents);
            }
            
            if ( policySetAttachPoint.getPolicySets() != null && policySetAttachPoint.getPolicySets().size() > 0 ) {
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
    }
}
