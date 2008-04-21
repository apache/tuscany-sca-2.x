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

package org.apache.tuscany.sca.policy.util;

import static javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;

import java.io.StringWriter;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.ProfileIntent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility methods used during computation of PolicyIntents and PolicySets sets
 */
public class PolicyComputationUtils {
    private static String POLICYSET_PREFIX = "tp_";
    private static String APPLICABLE_POLICYSET_ATTR_NS = "http://tuscany.apache.org/xmlns/sca/1.0"; 
    private static String APPLICABLE_POLICYSET_ATTR = "applicablePolicySets"; 
    private static String POLICY_SETS_ATTR = "policySets"; 
    private static String APPLICABLE_POLICYSET_ATTR_PREFIX = "tuscany";
    private static String SCA10_NS = "http://www.osoa.org/xmlns/sca/1.0";

    /**
     * This method unconditionally adds intents from the source list to the target list.
     * It is used for intermediate intent inheritance between promotion levels
     * (e.g. between a composite service and a component service).  It does not check
     * whether there are conflicting (mutually exclusive) intents.  This is because
     * promotion cannot override intents.  If the resulting target list has conflicting
     * intents, this will be detected later during policy computation.  
     */
    public static void addInheritedIntents(List<Intent> sourceList, List<Intent> targetList) {
        if (sourceList != null) {
            targetList.addAll(sourceList);
        }
    }

    public static void addInheritedPolicySets(List<PolicySet> sourceList,
                                              List<PolicySet> targetList,
                                              boolean checkOverrides) {
        // check overrides is true when policysets are to be copied from
        // componentType to component level
        if (checkOverrides) {
            // aggregate all the provided intents present in the target
            List<Intent> targetProvidedIntents = new ArrayList<Intent>();
            for (PolicySet policySet : targetList) {
                targetProvidedIntents.addAll(policySet.getProvidedIntents());
            }

            // for every policy set in the source check if it provides one of
            // the intents that is
            // already provided by the policysets in the destination and do not
            // copy them.
            for (PolicySet policySet : sourceList) {
                for (Intent sourceProvidedIntent : policySet.getProvidedIntents()) {
                    if (!targetProvidedIntents.contains(sourceProvidedIntent)) {
                        targetList.add(policySet);
                    }
                }
            }
        } else {
            targetList.addAll(sourceList);
        }
    }

    /**
     * This method is used to inherit intents and policy sets between hierarchical levels
     * within the same composite (e.g. between a component and its services and references).
     * In this case the source intents and policy sets provide defaults which are inherited
     * into the target lists only when there is no conflict.  For example consider a component
     * with 3 references.  The component level requires intent 'propagatesTransaction'.
     * Reference 1 and 2 do not specify an intent, but reference 3 requires 'suspendsTransaction'.
     * In this case the 'propagatesTransaction' intent is inherited by reference 1 and 2
     * but not by reference 3.
     */
    public static void addDefaultPolicies(List<Intent> sourceIntents,
                                          List<PolicySet> sourcePolicySets,
                                          List<Intent> targetIntents,
                                          List<PolicySet> targetPolicySets)
    {
        // form a list of all intents required by the target
        List<Intent> combinedTargetIntents = new ArrayList<Intent>();
        combinedTargetIntents.addAll(findAndExpandProfileIntents(targetIntents));
        for (PolicySet targetPolicySet : targetPolicySets) {
            combinedTargetIntents.addAll(findAndExpandProfileIntents(targetPolicySet.getProvidedIntents()));
        }

        // inherit intents in the source list that do not conflict with intents already in the target list
        for (Intent sourceIntent : findAndExpandProfileIntents(sourceIntents)) {
            boolean conflict = false;
            for (Intent excluded : sourceIntent.getExcludedIntents()) {
                if (combinedTargetIntents.contains(excluded)) {
                    conflict = true;
                    break;
                }
            }
            if (!conflict) {
                targetIntents.add(sourceIntent);
            }
        }

        // inherit policy sets in the source list that do not conflict with policy sets or intents
        // in the target list
        for (PolicySet sourcePolicySet : sourcePolicySets) {
            boolean conflict = false;
            List<Intent> providedIntents = findAndExpandProfileIntents(sourcePolicySet.getProvidedIntents());
            checkConflict: for (Intent intent : providedIntents) {
                for (Intent excluded : intent.getExcludedIntents()) {
                    if (combinedTargetIntents.contains(excluded)) {
                        conflict = true;
                        break checkConflict;
                    }
                }
            }
            if (!conflict)
                targetPolicySets.add(sourcePolicySet);
        }

    }

    public static void checkForMutuallyExclusiveIntents(
                         List<Intent> intents,
                         List<PolicySet> policySets,
                         IntentAttachPointType intentAttachPointType,
                         String id) throws PolicyValidationException
    {
        // gather all intents (keeping track of where they come from)
        Map<Intent, PolicySet> combinedIntents = new HashMap<Intent,PolicySet>();
        for (PolicySet policySet : policySets) {
            for (Intent providedIntent : findAndExpandProfileIntents(policySet.getProvidedIntents())) {
                combinedIntents.put(providedIntent, policySet);
            }
        }
        for (Intent intent : intents) {
            combinedIntents.put(intent, null);
        }

        // check for conflicts
        for (Intent intent : combinedIntents.keySet()) {
            for (Intent excluded : intent.getExcludedIntents()) {
                if (combinedIntents.keySet().contains(excluded)) {
                    String sIntent1, sIntent2;
                    if (combinedIntents.get(intent) == null)
                        sIntent1 = intent.getName().toString();
                    else
                        sIntent1 = intent.getName().toString() + " in policy set " + combinedIntents.get(intent).getName().toString();
                    if (combinedIntents.get(excluded) == null)
                        sIntent2 = excluded.getName().toString();
                    else
                        sIntent2 = excluded.getName().toString() + " in policy set " + combinedIntents.get(excluded).getName().toString();
                    throw new PolicyValidationException(
                        intentAttachPointType.getName() + " for " + id +
                        " uses mutually-exclusive intents " + sIntent1 + " and " + sIntent2);
                }
            }
        }
    }

    public static void expandProfileIntents(List<Intent> intents) {
        List<Intent> expandedIntents = null;
        if ( intents.size() > 0 ) {
            expandedIntents = findAndExpandProfileIntents(intents);
            intents.clear();
            intents.addAll(expandedIntents);
        }
    }

    public static List<Intent> findAndExpandProfileIntents(List<Intent> intents) {
        List<Intent> expandedIntents = new ArrayList<Intent>();
        for ( Intent intent : intents ) {
            if ( intent instanceof ProfileIntent ) {
                ProfileIntent profileIntent = (ProfileIntent)intent;
                List<Intent> requiredIntents = profileIntent.getRequiredIntents();
                expandedIntents.addAll(findAndExpandProfileIntents(requiredIntents));
            } else {
                expandedIntents.add(intent);
            }
        }
        return expandedIntents;
    }

    private static byte[] addApplicablePolicySets(Document doc, Collection<PolicySet> policySets)
        throws XPathExpressionException, TransformerConfigurationException, TransformerException {
        
        int prefixCount = 1;

        for (PolicySet policySet : policySets) {
            if (policySet.getAppliesTo() != null) {
                addApplicablePolicySets(policySet, doc, prefixCount);
            }

            if (policySet.getAlwaysAppliesTo() != null) {
                addAlwaysApplicablePolicySets(policySet, doc, prefixCount);
            }
        }

        StringWriter sw = new StringWriter();
        final Source domSource = new DOMSource(doc);
        final Result finalResult = new StreamResult(sw);
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        // transformer.setOutputProperty("omit-xml-declaration", "yes");
        // Allow priviledged access to let transformers read property files. Requires
        // PropertyPermission in security policy.
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                public Object run() throws TransformerException {
                    transformer.transform(domSource, finalResult);
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw (TransformerException)e.getException();
        }
        
        return sw.toString().getBytes();
    }

    private static void addAlwaysApplicablePolicySets(PolicySet policySet,
                                                      Document doc,
                                                      int prefixCount) throws XPathExpressionException {
        XPathExpression expression = policySet.getAlwaysAppliesToXPathExpression();
        NodeList result = (NodeList)expression.evaluate(doc, XPathConstants.NODESET);

        if (result != null) {
            for (int counter = 0; counter < result.getLength(); ++counter) {
                Node aResultNode = result.item(counter);

                String alwaysApplicablePolicySets = null;
                String policySetPrefix = POLICYSET_PREFIX + prefixCount++;
                String policySetsAttrPrefix = "sca";

                policySetPrefix =
                    declareNamespace((Element)aResultNode, policySetPrefix, policySet.getName()
                        .getNamespaceURI());
                policySetsAttrPrefix =
                    declareNamespace((Element)aResultNode, policySetsAttrPrefix, SCA10_NS);
                if (aResultNode.getAttributes().getNamedItem(POLICY_SETS_ATTR) != null) {
                    alwaysApplicablePolicySets =
                        aResultNode.getAttributes().getNamedItem(POLICY_SETS_ATTR).getNodeValue();
                }

                if (alwaysApplicablePolicySets != null && alwaysApplicablePolicySets.length() > 0) {
                    alwaysApplicablePolicySets =
                        alwaysApplicablePolicySets + " "
                            + policySetPrefix
                            + ":"
                            + policySet.getName().getLocalPart();
                } else {
                    alwaysApplicablePolicySets =
                        policySetPrefix + ":" + policySet.getName().getLocalPart();
                }

                ((Element)aResultNode).setAttribute(POLICY_SETS_ATTR, alwaysApplicablePolicySets);
            }
        }
    }

    private static void addApplicablePolicySets(PolicySet policySet,
                                         Document doc,
                                         int prefixCount) throws XPathExpressionException {
        XPathExpression expression = policySet.getAppliesToXPathExpression();
        NodeList result = (NodeList)expression.evaluate(doc, XPathConstants.NODESET);

        if (result != null) {
            for (int counter = 0; counter < result.getLength(); ++counter) {
                Node aResultNode = result.item(counter);

                String applicablePolicySets = null;
                String policySetPrefix = POLICYSET_PREFIX + prefixCount++;
                String appPolicyAttrPrefix = APPLICABLE_POLICYSET_ATTR_PREFIX;

                policySetPrefix =
                    declareNamespace((Element)aResultNode, policySetPrefix, policySet.getName()
                        .getNamespaceURI());
                appPolicyAttrPrefix =
                    declareNamespace((Element)aResultNode,
                                     appPolicyAttrPrefix,
                                     APPLICABLE_POLICYSET_ATTR_NS);
                if (aResultNode.getAttributes().getNamedItemNS(APPLICABLE_POLICYSET_ATTR_NS,
                                                               APPLICABLE_POLICYSET_ATTR) != null) {
                    applicablePolicySets =
                        aResultNode.getAttributes().getNamedItemNS(APPLICABLE_POLICYSET_ATTR_NS,
                                                                   APPLICABLE_POLICYSET_ATTR)
                            .getNodeValue();
                }

                if (applicablePolicySets != null && applicablePolicySets.length() > 0) {
                    applicablePolicySets =
                        applicablePolicySets + " "
                            + policySetPrefix
                            + ":"
                            + policySet.getName().getLocalPart();
                } else {
                    applicablePolicySets =
                        policySetPrefix + ":" + policySet.getName().getLocalPart();
                }

                ((Element)aResultNode).setAttributeNS(APPLICABLE_POLICYSET_ATTR_NS,
                                                      appPolicyAttrPrefix + ":"
                                                          + APPLICABLE_POLICYSET_ATTR,
                                                      applicablePolicySets);
            }
        }
    }

    public static byte[] addApplicablePolicySets(URL artifactUrl, Collection<PolicySet> domainPolicySets) throws Exception {
        DocumentBuilderFactory dbFac = DocumentBuilderFactory.newInstance();
        dbFac.setNamespaceAware(true);
        DocumentBuilder db = dbFac.newDocumentBuilder();
        Document doc = db.parse(artifactUrl.toURI().toString());
        return addApplicablePolicySets(doc, domainPolicySets);
    }
    
    private static class DOMNamespaceContext implements NamespaceContext {
        private Node node;

        /**
         * @param node
         */
        public DOMNamespaceContext(Node node) {
            super();
            this.node = node;
        }

        public String getNamespaceURI(String prefix) {
            return node.lookupNamespaceURI(prefix);
        }

        public String getPrefix(String namespaceURI) {
            return node.lookupPrefix(namespaceURI);
        }

        public Iterator<?> getPrefixes(String namespaceURI) {
            return null;
        }

    }
    
    private static String declareNamespace(Element element, String prefix, String ns) {
        if (ns == null) {
            ns = "";
        }
        if (prefix == null) {
            prefix = "";
        }
        String qname = null;
        if ("".equals(prefix)) {
            qname = "xmlns";
        } else {
            qname = "xmlns:" + prefix;
        }
        Node node = element;
        boolean declared = false;
        while (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
            if ( node.lookupPrefix(ns) != null ) {
                prefix = node.lookupPrefix(ns);
                declared = true;
                break;
            } else {
                /*NamedNodeMap attrs = node.getAttributes();
                if (attrs == null) {
                    break;
                }
                Node attr = attrs.getNamedItem(qname);
                if (attr != null) {
                    declared = ns.equals(attr.getNodeValue());
                    break;
                }*/
                node = node.getParentNode();
            }
        }
        if (!declared) {
            org.w3c.dom.Attr attr = element.getOwnerDocument().createAttributeNS(XMLNS_ATTRIBUTE_NS_URI, qname);
            attr.setValue(ns);
            element.setAttributeNodeNS(attr);
        }
        return prefix;
    }

}
