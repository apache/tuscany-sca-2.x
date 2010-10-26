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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySubject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A builder that checks that policy sets apply to the elements to which they are attached. 
 * Any that don't are removed. It first creates a DOM model for the composite so that the xpath
 * expression can be evaluated. For each element that holds a policy set is calculates the 
 * appliesTo nodes and checks that the current element is in the set. If not the policySet is
 * removed from the element   
 *
 * @version $Rev$ $Date$
 */
public class PolicyAppliesToBuilderImpl extends PolicyAttachmentBuilderImpl {

    public PolicyAppliesToBuilderImpl(ExtensionPointRegistry registry) {
        super(registry);
    }

    public String getID() {
        return "org.apache.tuscany.sca.policy.builder.PolicyAppliesToBuilder";
    }

    public Composite build(Composite composite, BuilderContext context)
        throws CompositeBuilderException {
        try {
            // create a DOM for the Domain Composite Infoset
            Document document = saveAsDOM(composite);
            
            // create a cache of evaluated node against each policy set so we don't
            // have to keep evaluating policy sets that appear in multiple places
            Map<PolicySet, List<PolicySubject>> appliesToSubjects = new HashMap<PolicySet, List<PolicySubject>>();
            
            // for all implementations, endpoint and endpoint references check that 
            // the policy sets validly apply
            return checkAppliesTo(document, appliesToSubjects, composite, context);
           
        } catch (Exception e) {
            throw new CompositeBuilderException(e);
        }
    }
    
    private Composite checkAppliesTo(Document document, Map<PolicySet, List<PolicySubject>> appliesToSubjects, Composite topComposite, BuilderContext context) throws Exception {
 
    	for ( Component component : topComposite.getComponents() ) {
    		if ( component.getImplementation() instanceof Composite ) {
    			Composite nested = (Composite) component.getImplementation();
    			checkAppliesTo(saveAsDOM(nested),new HashMap<PolicySet, List<PolicySubject>>(), nested, context );
    		}
    	}

    	for (Component component : topComposite.getComponents()) {

    		for (ComponentService componentService : component.getServices()) {
    			for (Endpoint ep : componentService.getEndpoints()) {
    			    List<PolicySet> policySetsToRemove = checkAppliesToSubject(document, appliesToSubjects, topComposite, (PolicySubject)ep.getService(), ep.getService().getPolicySets());
    			    ep.getPolicySets().removeAll(policySetsToRemove);
    				if (ep.getBinding() instanceof PolicySubject) {
    				    policySetsToRemove = checkAppliesToSubject(document, appliesToSubjects, topComposite, (PolicySubject)ep.getBinding(), ((PolicySubject)ep.getBinding()).getPolicySets());
    				    ep.getPolicySets().removeAll(policySetsToRemove);
    				}
    			}
    		}

    		for (ComponentReference componentReference : component.getReferences()) {
    			for (EndpointReference epr : componentReference.getEndpointReferences()) {
    			    List<PolicySet> policySetsToRemove = checkAppliesToSubject(document, appliesToSubjects, topComposite, (PolicySubject)epr.getReference(), epr.getReference().getPolicySets());
    			    epr.getPolicySets().removeAll(policySetsToRemove);
    				if (epr.getBinding() instanceof PolicySubject) {
    				    policySetsToRemove = checkAppliesToSubject(document, appliesToSubjects, topComposite, (PolicySubject)epr.getBinding(), ((PolicySubject)epr.getBinding()).getPolicySets());
    				    epr.getPolicySets().removeAll(policySetsToRemove);
    				} 
    			}
    		}

    		Implementation implementation = component.getImplementation();
    		if (implementation != null && 
    				implementation instanceof PolicySubject) {
    			checkAppliesToSubject(document, appliesToSubjects, topComposite, implementation, implementation.getPolicySets());
    		}
    	}

        return topComposite;
    }
    

	/**
     * Checks that all the provided policy sets apply to the provided policy subject
     * 
     * @param document
     * @param appliesToSubjects
     * @param policySubject
     * @param policySets
     * @return
     * @throws Exception
     */
    private List<PolicySet> checkAppliesToSubject(Document document, Map<PolicySet, List<PolicySubject>> appliesToSubjects, Composite composite, PolicySubject policySubject, List<PolicySet> policySets) throws Exception {
        List<PolicySet> policySetsToRemove = new ArrayList<PolicySet>();
        
        for (PolicySet policySet : policySets){
            List<PolicySubject> subjects = appliesToSubjects.get(policySet);
            
            if (subjects == null){
                XPathExpression appliesTo = policySet.getAppliesToXPathExpression();
                if (appliesTo != null) {
                    NodeList nodes = (NodeList)appliesTo.evaluate(document, XPathConstants.NODESET);
                    
                    if (nodes.getLength() > 0){
                        subjects = new ArrayList<PolicySubject>();
                        appliesToSubjects.put(policySet, subjects);
                    }
                    
                    for (int i = 0; i < nodes.getLength(); i++) {
                        Node node = nodes.item(i);
                        String index = getStructuralURI(node);
                        PolicySubject subject = lookup(composite, index);
                        if ( subject != null ) 
                        	subjects.add(subject);
                    }
                }
            }
            
            if (subjects != null){
                if (!subjects.contains(policySubject)){
                    policySetsToRemove.add(policySet);
                }
            } 
            
            // TODO - If no "appliesTo" is provided does the policy set apply to 
            //        everything or nothing?

        }
        
        policySets.removeAll(policySetsToRemove); 
        return policySetsToRemove;
    }    
}
