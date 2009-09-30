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

import static javax.xml.XMLConstants.DEFAULT_NS_PREFIX;
import static javax.xml.XMLConstants.XMLNS_ATTRIBUTE;
import static javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.common.xml.dom.DOMHelper;
import org.apache.tuscany.sca.common.xml.stax.StAXHelper;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySubject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A builder that attaches policy sets to the domain composite using the xpath defined by
 * the attachTo attribute. It first creates a DOM model for the composite so that the xpath
 * expression can be evaluated. For the nodes selected by the xpath, add the policySets attribute
 * to the subject element. Then reload the patched DOM into a Composite model again.  
 *
 * @version $Rev$ $Date$
 */
public class PolicyAttachmentBuilderImpl implements CompositeBuilder {
    private StAXHelper staxHelper;
    private DOMHelper domHelper;
    private StAXArtifactProcessor<Composite> processor;

    public PolicyAttachmentBuilderImpl(ExtensionPointRegistry registry) {
        domHelper = DOMHelper.getInstance(registry);
        staxHelper = StAXHelper.getInstance(registry);
        StAXArtifactProcessorExtensionPoint processors =
            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        processor = processors.getProcessor(Composite.class);
    }

    public String getID() {
        return "org.apache.tuscany.sca.policy.builder.PolicyAttachmentBuilder";
    }

    public Composite build(Composite composite, Definitions definitions, Monitor monitor)
        throws CompositeBuilderException {
        try {
            Composite patched = applyXPath(composite, definitions, monitor);
            return patched;
        } catch (Exception e) {
            throw new CompositeBuilderException(e);
        }
    }

    /**
     * Apply the attachTo XPath against the composite model
     * @param composite The orginal composite
     * @param definitions SCA definitions that contain the policy sets
     * @param monitor The monitor
     * @return A reloaded composite
     * @throws Exception
     */
    private Composite applyXPath(Composite composite, Definitions definitions, Monitor monitor) throws Exception {
        if (definitions == null || definitions.getPolicySets().isEmpty()) {
            return composite;
        }
        // Recursively apply the xpath against the composites referenced by <implementation.composite>
        for (Component component : composite.getComponents()) {
            Implementation impl = component.getImplementation();
            if (impl instanceof Composite) {
                Composite patched = applyXPath((Composite)impl, definitions, monitor);
                if (patched != impl) {
                    component.setImplementation(patched);
                }
            }
        }
        Document document = null;

        for (PolicySet ps : definitions.getPolicySets()) {
            // First calculate the applicable nodes
            Set<Node> applicableNodes = null;
            /*
            XPathExpression appliesTo = ps.getAppliesToXPathExpression();
            if (appliesTo != null) {
                applicableNodes = new HashSet<Node>();
                NodeList nodes = (NodeList)appliesTo.evaluate(document, XPathConstants.NODESET);
                for (int i = 0; i < nodes.getLength(); i++) {
                    applicableNodes.add(nodes.item(i));
                }
            }
            */
            XPathExpression exp = ps.getAttachToXPathExpression();
            if (exp != null) {
                if (document == null) {
                    document = saveAsDOM(composite);
                }
                NodeList nodes = (NodeList)exp.evaluate(document, XPathConstants.NODESET);
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    if (applicableNodes == null || applicableNodes.contains(node)) {
                        // The node can be a component, service, reference or binding
                        String index = getStructuralURI(node);
                        PolicySubject subject = lookup(composite, index);
                        if (subject != null) {
                            subject.getPolicySets().add(ps);
                        }
                    }
                }
            }
        }

        return composite;
    }

    private Document saveAsDOM(Composite composite) throws XMLStreamException, ContributionWriteException, IOException,
        SAXException {
        // First write the composite into a DOM document so that we can apply the xpath
        StringWriter sw = new StringWriter();
        XMLStreamWriter writer = staxHelper.createXMLStreamWriter(sw);
        // Write the composite into a DOM document
        processor.write(composite, writer);
        writer.close();

        Document document = domHelper.load(sw.toString());
        return document;
    }

    private static final QName COMPONENT = new QName(Base.SCA11_NS, "component");
    private static final QName SERVICE = new QName(Base.SCA11_NS, "service");
    private static final QName REFERENCE = new QName(Base.SCA11_NS, "reference");

    private static String getStructuralURI(Node node) {
        if (node != null) {
            QName name = new QName(node.getNamespaceURI(), node.getLocalName());
            if (COMPONENT.equals(name)) {
                Element element = (Element)node;
                return element.getAttributeNS(null, "uri");
            } else if (SERVICE.equals(name)) {
                Element component = (Element)node.getParentNode();
                String uri = component.getAttributeNS(null, "uri");
                String service = ((Element)node).getAttributeNS(null, "name");
                return uri + "#service(" + service + ")";
            } else if (REFERENCE.equals(name)) {
                Element component = (Element)node.getParentNode();
                String uri = component.getAttributeNS(null, "uri");
                String reference = ((Element)node).getAttributeNS(null, "name");
                return uri + "#reference(" + reference + ")";
            } else {
                String localName = node.getLocalName();
                if (localName.startsWith("binding.")) {
                    String bindingName = ((Element)node).getAttributeNS(null, "name");
                    Element contract = (Element)node.getParentNode();
                    String contractName = contract.getAttributeNS(null, "name");
                    Element component = (Element)node.getParentNode().getParentNode();
                    String uri = component.getAttributeNS(null, "uri");
                    return uri + "#" + contract.getLocalName() + "(" + contractName + "/" + bindingName + ")";
                } else if (localName.startsWith("implementation.")) {
                    Element component = (Element)node.getParentNode();
                    String uri = component.getAttributeNS(null, "uri");
                    return uri + "#implementation()";
                }
            }
        }
        return null;
    }

    private Binding getBinding(Contract contract, String name) {
        for (Binding binding : contract.getBindings()) {
            if (name.equals(binding.getName())) {
                return binding;
            }
        }
        return null;
    }

    private PolicySubject lookup(Composite composite, String structuralURI) {
        if (structuralURI == null) {
            return null;
        }
        int index = structuralURI.indexOf('#');
        String componentURI = structuralURI;
        String service = null;
        String reference = null;
        String binding = null;
        boolean impl = false;

        if (index != -1) {
            componentURI = structuralURI.substring(0, index);
            String fragment = structuralURI.substring(index + 1);
            int begin = fragment.indexOf('(');
            int end = fragment.indexOf(')');
            if (begin != -1 && end != -1) {
                String path = fragment.substring(begin + 1, end).trim();
                String prefix = fragment.substring(0, begin).trim();
                if (prefix.equals("implementation")) {
                    impl = true;
                } else {
                    int pos = path.indexOf('/');
                    if (pos != -1) {
                        binding = path.substring(pos + 1);
                        path = path.substring(0, index);
                        if ("service-binding".equals(prefix)) {
                            service = path;
                        } else if ("reference-binding".equals(prefix)) {
                            reference = path;
                        }
                    }
                    if ("service".equals(prefix)) {
                        service = path;
                    } else if ("reference".equals(prefix)) {
                        reference = path;
                    }
                }
            }
        }
        for (Component component : composite.getComponents()) {
            if (component.getURI().equals(componentURI)) {
                if (service != null) {
                    ComponentService componentService = component.getService(service);
                    if (binding != null) {
                        Binding b = getBinding(componentService, binding);
                        if (b instanceof PolicySubject) {
                            return (PolicySubject)b;
                        }
                    } else {
                        return componentService;
                    }
                } else if (reference != null) {
                    ComponentReference componentReference = component.getReference(reference);
                    if (binding != null) {
                        Binding b = getBinding(componentReference, binding);
                        if (b instanceof PolicySubject) {
                            return (PolicySubject)b;
                        }
                    } else {
                        return componentReference;
                    }
                } else if (impl) {
                    return component.getImplementation();
                }
                return component;
            } else if (structuralURI.startsWith(component.getURI() + "/")) {
                Implementation implementation = component.getImplementation();
                if (implementation instanceof Composite) {
                    return lookup((Composite)implementation, structuralURI);
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Attach the policySet to the given DOM node 
     * @param node The DOM node (should be an element)
     * @param policySet The policy set to be attached
     * @return true if the element is changed, false if the element already contains the same policy set
     * and no change is made
     */
    private boolean attach(Node node, PolicySet policySet) {
        Element element = (Element)node;
        Document document = element.getOwnerDocument();

        QName qname = policySet.getName();
        String prefix = DOMHelper.getPrefix(element, qname.getNamespaceURI());
        if (prefix == null) {
            // Find the a non-conflicting prefix
            int i = 0;
            while (true) {
                prefix = "ns" + i;
                String ns = DOMHelper.getNamespaceURI(element, prefix);
                if (ns == null) {
                    break;
                }
            }
            // Declare the namespace
            Attr nsAttr = document.createAttributeNS(XMLNS_ATTRIBUTE_NS_URI, XMLNS_ATTRIBUTE + ":" + prefix);
            nsAttr.setValue(qname.getNamespaceURI());
            element.setAttributeNodeNS(nsAttr);
        }
        // Form the value as a qualified name
        String qvalue = null;
        if (DEFAULT_NS_PREFIX.equals(prefix)) {
            qvalue = qname.getLocalPart();
        } else {
            qvalue = prefix + ":" + qname.getLocalPart();
        }

        // Check if the attribute exists
        Attr attr = element.getAttributeNode("policySets");
        if (attr == null) {
            // Create the policySets attr
            attr = document.createAttributeNS(null, "policySets");
            attr.setValue(qvalue);
            element.setAttributeNodeNS(attr);
            return true;
        } else {
            // Append to the existing value
            boolean duplicate = false;
            String value = attr.getValue();
            StringTokenizer tokenizer = new StringTokenizer(value);
            while (tokenizer.hasMoreTokens()) {
                String ps = tokenizer.nextToken();
                int index = ps.indexOf(':');
                String ns = null;
                String localName = null;
                if (index == -1) {
                    ns = DOMHelper.getNamespaceURI(element, DEFAULT_NS_PREFIX);
                    localName = ps;
                } else {
                    ns = DOMHelper.getNamespaceURI(element, ps.substring(0, index));
                    localName = ps.substring(index + 1);
                }
                QName psName = new QName(ns, localName);
                if (qname.equals(psName)) {
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) {
                // REVIEW: [rfeng] How to comply to POL40012?
                value = value + " " + qvalue;
                attr.setValue(value.trim());
                return true;
            }
            return false;
        }
    }

}
