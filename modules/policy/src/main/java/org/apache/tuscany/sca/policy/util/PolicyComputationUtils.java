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
import java.util.Iterator;
import java.util.List;

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
import org.apache.tuscany.sca.policy.PolicySet;
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
