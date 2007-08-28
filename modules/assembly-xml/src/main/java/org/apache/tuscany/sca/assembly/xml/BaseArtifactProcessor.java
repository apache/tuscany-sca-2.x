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

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
import static javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AbstractContract;
import org.apache.tuscany.sca.assembly.AbstractProperty;
import org.apache.tuscany.sca.assembly.AbstractReference;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPoint;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * A base class with utility methods for the other artifact processors in this module. 
 * 
 * @version $Rev$ $Date$
 */
abstract class BaseArtifactProcessor implements Constants {

    protected ContributionFactory contributionFactory;
    protected AssemblyFactory assemblyFactory;
    protected PolicyFactory policyFactory;
    protected StAXArtifactProcessor<Object> extensionProcessor;

    private static final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
    static {
        domFactory.setNamespaceAware(true);
    }

    /**
     * Construcst a new BaseArtifactProcessor.
     * @param contribFactory
     * @param factory
     * @param policyFactory
     */
    @SuppressWarnings("unchecked")
    public BaseArtifactProcessor(ContributionFactory contribFactory,
                                 AssemblyFactory factory,
                                 PolicyFactory policyFactory,
                                 StAXArtifactProcessor extensionProcessor) {
        this.assemblyFactory = factory;
        this.policyFactory = policyFactory;
        this.extensionProcessor = (StAXArtifactProcessor<Object>)extensionProcessor;
        this.contributionFactory = contribFactory;
    }

    /**
     * Construcst a new BaseArtifactProcessor.
     * @param factory
     * @param policyFactory
     */
    @SuppressWarnings("unchecked")
    public BaseArtifactProcessor(AssemblyFactory factory,
                                 PolicyFactory policyFactory,
                                 StAXArtifactProcessor extensionProcessor) {
        this.assemblyFactory = factory;
        this.policyFactory = policyFactory;
        this.extensionProcessor = (StAXArtifactProcessor<Object>)extensionProcessor;
    }

    /**
     * Returns the string value of an attribute.
     * @param reader
     * @param name
     * @return
     */
    protected String getString(XMLStreamReader reader, String name) {
        return reader.getAttributeValue(null, name);
    }

    /**
     * Returns the qname value of an attribute.
     * @param reader
     * @param name
     * @return
     */
    protected QName getQName(XMLStreamReader reader, String name) {
        String qname = reader.getAttributeValue(null, name);
        return getQNameValue(reader, qname);
    }

    /**
     * Returns the value of xsi:type attribute
     * @param reader The XML stream reader
     * @return The QName of the type, if the attribute is not present, null is
     *         returned.
     */
    protected QName getXSIType(XMLStreamReader reader) {
        String qname = reader.getAttributeValue(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type");
        return getQNameValue(reader, qname);
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

    /**
     * Returns the boolean value of an attribute.
     * @param reader
     * @param name
     * @return
     */
    protected boolean getBoolean(XMLStreamReader reader, String name) {
        String value = reader.getAttributeValue(null, name);
        if (value == null) {
            value = Boolean.toString(false);
        }
        return Boolean.valueOf(value);
    }

    /**
     * Returns the value of an attribute as a list of qnames.
     * @param reader
     * @param name
     * @return
     */
    protected List<QName> getQNames(XMLStreamReader reader, String name) {
        String value = reader.getAttributeValue(null, name);
        if (value != null) {
            List<QName> qnames = new ArrayList<QName>();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                qnames.add(getQName(reader, tokens.nextToken()));
            }
            return qnames;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Read policy intents.
     * @param attachPoint
     * @param reader
     */
    protected void readIntents(IntentAttachPoint attachPoint, XMLStreamReader reader) {
        readIntents(attachPoint, null, reader);
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
                    //intent.getOperations().add(operation);
                }
                requiredIntents.add(intent);
            }
        }
    }

    /**
     * Reads policy intents and policy sets.
     * @param attachPoint
     * @param reader
     */
    protected void readPolicies(Object attachPoint, XMLStreamReader reader) {
        if (attachPoint instanceof PolicySetAttachPoint) {
            readPolicies((PolicySetAttachPoint)attachPoint, null, reader);
        }
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
                    //policySet.getOperations().add(operation);
                }
                policySets.add(policySet);
            }
        }
    }

    /**
     * Read list of reference targets
     * @param reference
     * @param reader
     */
    protected void readTargets(Reference reference, XMLStreamReader reader) {
        String value = reader.getAttributeValue(null, Constants.TARGET);
        ComponentService target = null;
        if (value != null) {
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                target = assemblyFactory.createComponentService();
                target.setUnresolved(true);
                target.setName(tokens.nextToken());
                reference.getTargets().add(target);
            }
        }
    }

    /**
     * Read a multiplicity attribute.
     * @param reference
     * @param reader
     */
    protected void readMultiplicity(AbstractReference reference, XMLStreamReader reader) {
        String value = reader.getAttributeValue(null, MULTIPLICITY);
        if (ZERO_ONE.equals(value)) {
            reference.setMultiplicity(Multiplicity.ZERO_ONE);
        } else if (ONE_N.equals(value)) {
            reference.setMultiplicity(Multiplicity.ONE_N);
        } else if (ZERO_N.equals(value)) {
            reference.setMultiplicity(Multiplicity.ZERO_N);
        }
    }

    /**
     * Returns the value of a constrainingType attribute.
     * @param reader
     * @return
     */
    protected ConstrainingType getConstrainingType(XMLStreamReader reader) {
        QName constrainingTypeName = getQName(reader, "constrainingType");
        if (constrainingTypeName != null) {
            ConstrainingType constrainingType = assemblyFactory.createConstrainingType();
            constrainingType.setName(constrainingTypeName);
            constrainingType.setUnresolved(true);
            return constrainingType;
        } else {
            return null;
        }
    }

    /**
     * Reads an abstract property element.
     * @param prop
     * @param reader
     * @throws XMLStreamException
     * @throws ContributionReadException
     */
    protected void readAbstractProperty(AbstractProperty prop, XMLStreamReader reader) throws XMLStreamException,
        ContributionReadException {
        prop.setName(getString(reader, "name"));
        prop.setMany(getBoolean(reader, "many"));
        prop.setMustSupply(getBoolean(reader, "mustSupply"));
        prop.setXSDElement(getQName(reader, "element"));
        prop.setXSDType(getQName(reader, "type"));
        try {
            Document value = readPropertyValue(reader, prop.getXSDType());
            prop.setValue(value);
        } catch (ParserConfigurationException e) {
            throw new ContributionReadException(e);
        }
    }

    /**
     * Reads a property element.
     * @param prop
     * @param reader
     * @throws XMLStreamException
     * @throws ContributionReadException
     */
    protected void readProperty(Property prop, XMLStreamReader reader) throws XMLStreamException,
        ContributionReadException {
        readAbstractProperty(prop, reader);
    }

    /**
     * Parse the next child element.
     * @param reader
     * @return
     * @throws XMLStreamException
     */
    protected boolean nextChildElement(XMLStreamReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == END_ELEMENT) {
                return false;
            }
            if (event == START_ELEMENT) {
                return true;
            }
        }
        return false;
    }

    /**
     * Advance the stream to the next END_ELEMENT event skipping any nested
     * content.
     * @param reader the reader to advance
     * @throws XMLStreamException if there was a problem reading the stream
     */
    protected void skipToEndElement(XMLStreamReader reader) throws XMLStreamException {
        int depth = 0;
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                depth++;
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (depth == 0) {
                    return;
                }
                depth--;
            }
        }
    }

    /**
     * Resolve an implementation.
     * @param implementation
     * @param resolver
     * @return
     * @throws ContributionResolveException
     */
    protected Implementation resolveImplementation(Implementation implementation, ModelResolver resolver)
        throws ContributionResolveException {
        if (implementation != null) {
            if (implementation.isUnresolved()) {
                implementation = resolver.resolveModel(Implementation.class, implementation);

                // Lazily resolve implementations
                if (implementation.isUnresolved()) {
                    extensionProcessor.resolve(implementation, resolver);
                    if (!implementation.isUnresolved()) {
                        resolver.addModel(implementation);
                    }
                }

                if (implementation instanceof PolicySetAttachPoint) {
                    resolveIntents(((PolicySetAttachPoint)implementation).getRequiredIntents(), resolver);
                    resolvePolicySets(((PolicySetAttachPoint)implementation).getPolicySets(), resolver);
                }
            }
        }
        return implementation;
    }

    /**
     * Resolve interface, callback interface and bindings on a list of contracts.
     * @param contracts the list of contracts
     * @param resolver the resolver to use to resolve models
     */
    protected <C extends Contract> void resolveContracts(List<C> contracts, ModelResolver resolver)
        throws ContributionResolveException {
        for (Contract contract : contracts) {
            // Resolve the interface contract
            InterfaceContract interfaceContract = contract.getInterfaceContract();
            if (interfaceContract != null) {
                extensionProcessor.resolve(interfaceContract, resolver);
            }

            // Resolve bindings
            for (int i = 0, n = contract.getBindings().size(); i < n; i++) {
                Binding binding = contract.getBindings().get(i);
                extensionProcessor.resolve(binding, resolver);
                if (binding instanceof IntentAttachPoint) {
                    IntentAttachPoint policiedBinding = (IntentAttachPoint)binding;
                    resolveIntents(policiedBinding.getRequiredIntents(), resolver);
                }
                if (binding instanceof PolicySetAttachPoint) {
                    PolicySetAttachPoint policiedBinding = (PolicySetAttachPoint)binding;
                    resolvePolicySets(policiedBinding.getPolicySets(), resolver);
                }
            }

            // Resolve callback bindings
            if (contract.getCallback() != null) {
                resolveIntents(contract.getCallback().getRequiredIntents(), resolver);
                resolvePolicySets(contract.getCallback().getPolicySets(), resolver);
                for (int i = 0, n = contract.getCallback().getBindings().size(); i < n; i++) {
                    Binding binding = contract.getCallback().getBindings().get(i);
                    extensionProcessor.resolve(binding, resolver);

                    if (binding instanceof IntentAttachPoint) {
                        IntentAttachPoint policiedBinding = (IntentAttachPoint)binding;
                        resolveIntents(policiedBinding.getRequiredIntents(), resolver);
                    }
                    if (binding instanceof PolicySetAttachPoint) {
                        PolicySetAttachPoint policiedBinding = (PolicySetAttachPoint)binding;
                        resolvePolicySets(policiedBinding.getPolicySets(), resolver);
                    }
                }
            }

            resolveIntents(contract.getRequiredIntents(), resolver);
            resolvePolicySets(contract.getPolicySets(), resolver);
        }
    }

    /**
     * Resolve interface and callback interface on a list of abstract contracts.
     * @param contracts the list of contracts
     * @param resolver the resolver to use to resolve models
     */
    protected <C extends AbstractContract> void resolveAbstractContracts(List<C> contracts, ModelResolver resolver)
        throws ContributionResolveException {
        for (AbstractContract contract : contracts) {

            // Resolve the interface contract
            InterfaceContract interfaceContract = contract.getInterfaceContract();
            if (interfaceContract != null) {
                extensionProcessor.resolve(interfaceContract, resolver);
            }
        }
    }

    /**
     * Start an element.
     * @param uri
     * @param name
     * @param attrs
     * @throws XMLStreamException
     */
    protected void writeStart(XMLStreamWriter writer, String uri, String name, XAttr... attrs)
        throws XMLStreamException {
        writeAttributePrefixes(writer, attrs);
        writer.writeStartElement(uri, name);
        writeAttributes(writer, attrs);
    }

    /**
     * Start an element.
     * @param writer
     * @param name
     * @param attrs
     * @throws XMLStreamException
     */
    protected void writeStart(XMLStreamWriter writer, String name, XAttr... attrs) throws XMLStreamException {
        writeAttributePrefixes(writer, attrs);
        writer.writeStartElement(SCA10_NS, name);
        writeAttributes(writer, attrs);
    }

    /**
     * End an element. 
     * @param writer
     * @throws XMLStreamException
     */
    protected void writeEnd(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
    }

    /**
     * Start a document.
     * @param writer
     * @throws XMLStreamException
     */
    protected void writeStartDocument(XMLStreamWriter writer, String name, XAttr... attrs) throws XMLStreamException {
        writer.writeStartDocument();
        writer.setDefaultNamespace(SCA10_NS);
        writeStart(writer, name, attrs);
        writer.writeDefaultNamespace(SCA10_NS);
    }

    /**
     * End a document.
     * @param writer
     * @throws XMLStreamException
     */
    protected void writeEndDocument(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndDocument();
    }

    /**
     * Write attributes to the current element.
     * @param writer
     * @param attrs
     * @throws XMLStreamException
     */
    protected void writeAttributes(XMLStreamWriter writer, XAttr... attrs) throws XMLStreamException {
        for (XAttr attr : attrs) {
            if (attr != null)
                attr.write(writer);
        }
    }

    /**
     * Write attribute prefixes to the current element.
     * @param writer
     * @param attrs
     * @throws XMLStreamException
     */
    protected void writeAttributePrefixes(XMLStreamWriter writer, XAttr... attrs) throws XMLStreamException {
        for (XAttr attr : attrs) {
            if (attr != null)
                attr.writePrefix(writer);
        }
    }

    /**
     * Write an SCA abstract property declaration.
     * @param writer
     * @param prop
     */
    protected void writeAbstractProperty(XMLStreamWriter writer, AbstractProperty prop) throws XMLStreamException {
    }

    /**
     * Write an SCA property declaration.
     * @param writer
     * @param prop
     */
    protected void writeProperty(XMLStreamWriter writer, Property prop) throws XMLStreamException {
        writeAbstractProperty(writer, prop);
    }

    /**
     * Returns a constrainingType attribute.
     * @param componentType
     * @return
     */
    protected QName getConstrainingTypeAttr(ComponentType componentType) {
        ConstrainingType constrainingType = componentType.getConstrainingType();
        if (constrainingType != null)
            return constrainingType.getName();
        else
            return null;
    }

    /**
     * Read a property value into a DOM document.
     * @param reader
     * @param type
     * @return
     * @throws XMLStreamException
     * @throws ContributionReadException
     * @throws ParserConfigurationException 
     */
    protected Document readPropertyValue(XMLStreamReader reader, QName type) throws XMLStreamException,
        ParserConfigurationException {

        Document doc = createDocument();

        // root element has no namespace and local name "value"
        Element root = doc.createElementNS(null, "value");
        if (type != null) {
            org.w3c.dom.Attr xsi = doc.createAttributeNS(XMLNS_ATTRIBUTE_NS_URI, "xmlns:xsi");
            xsi.setValue(W3C_XML_SCHEMA_INSTANCE_NS_URI);
            root.setAttributeNodeNS(xsi);

            String prefix = type.getPrefix();
            if (prefix == null || prefix.length() == 0) {
                prefix = "ns";
            }

            declareNamespace(root, prefix, type.getNamespaceURI());

            org.w3c.dom.Attr xsiType = doc.createAttributeNS(W3C_XML_SCHEMA_INSTANCE_NS_URI, "xsi:type");
            xsiType.setValue(prefix + ":" + type.getLocalPart());
            root.setAttributeNodeNS(xsiType);
        }
        doc.appendChild(root);

        loadElement(reader, root);
        return doc;
    }

    /**
     * Create a new DOM document.
     * @return
     * @throws ContributionReadException
     */
    private Document createDocument() throws ParserConfigurationException {
        return domFactory.newDocumentBuilder().newDocument();
    }

    /**
     * Create a DOM element
     * @param document
     * @param name
     * @return
     */
    private Element createElement(Document document, QName name) {
        String prefix = name.getPrefix();
        String qname =
            (prefix != null && prefix.length() > 0) ? prefix + ":" + name.getLocalPart() : name.getLocalPart();
        return document.createElementNS(name.getNamespaceURI(), qname);
    }

    /**
     * Declare a namespace.
     * @param element
     * @param prefix
     * @param ns
     */
    private void declareNamespace(Element element, String prefix, String ns) {
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
            NamedNodeMap attrs = node.getAttributes();
            if (attrs == null) {
                break;
            }
            Node attr = attrs.getNamedItem(qname);
            if (attr != null) {
                declared = ns.equals(attr.getNodeValue());
                break;
            }
            node = node.getParentNode();
        }
        if (!declared) {
            org.w3c.dom.Attr attr = element.getOwnerDocument().createAttributeNS(XMLNS_ATTRIBUTE_NS_URI, qname);
            attr.setValue(ns);
            element.setAttributeNodeNS(attr);
        }
    }

    /**
     * Load a property value specification from an StAX stream into a DOM
     * Document. Only elements, text and attributes are processed; all comments
     * and other whitespace are ignored.
     * 
     * @param reader the stream to read from
     * @param root the DOM node to load
     * @throws javax.xml.stream.XMLStreamException
     */
    private void loadElement(XMLStreamReader reader, Element root) throws XMLStreamException {
        Document document = root.getOwnerDocument();
        Node current = root;
        while (true) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    QName name = reader.getName();
                    Element child = createElement(document, name);

                    // push the new element and make it the current one
                    current.appendChild(child);
                    current = child;

                    int count = reader.getNamespaceCount();
                    for (int i = 0; i < count; i++) {
                        String prefix = reader.getNamespacePrefix(i);
                        String ns = reader.getNamespaceURI(i);
                        declareNamespace(child, prefix, ns);
                    }

                    if(!"".equals(name.getNamespaceURI()))
                    declareNamespace(child, name.getPrefix(), name.getNamespaceURI());

                    // add the attributes for this element
                    count = reader.getAttributeCount();
                    for (int i = 0; i < count; i++) {
                        String ns = reader.getAttributeNamespace(i);
                        String prefix = reader.getAttributePrefix(i);
                        String qname = reader.getAttributeLocalName(i);
                        String value = reader.getAttributeValue(i);
                        if (prefix != null && prefix.length() != 0) {
                            qname = prefix + ":" + qname;
                        }
                        child.setAttributeNS(ns, qname, value);
                        if (ns != null) {
                            declareNamespace(child, prefix, ns);
                        }
                    }

                    break;
                case XMLStreamConstants.CDATA:
                    current.appendChild(document.createCDATASection(reader.getText()));
                    break;
                case XMLStreamConstants.CHARACTERS:
                    current.appendChild(document.createTextNode(reader.getText()));
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    // if we are back at the root then we are done
                    if (current == root) {
                        return;
                    }

                    // pop the element off the stack
                    current = current.getParentNode();
            }
        }
    }

    /**
     * Resolve policy intents attached to a specific SCA Artifact
     * @param policyIntents list of policy intents
     * @param resolver
     */
    protected void resolveIntents(List<Intent> policyIntents, ModelResolver resolver) {
        List<Intent> requiredIntents = new ArrayList<Intent>();
        Intent resolvedIntent = null;
        for (Intent intent : policyIntents) {
            resolvedIntent = resolver.resolveModel(Intent.class, intent);
            requiredIntents.add(resolvedIntent);
        }
        policyIntents.clear();
        policyIntents.addAll(requiredIntents);
    }

    /**
     * Resolve policy sets attached to a specific SCA Construct
     * @param policySets list of attached policy sets
     * @param resolver
     */
    protected void resolvePolicySets(List<PolicySet> policySets, ModelResolver resolver) {
        List<PolicySet> resolvedPolicySets = new ArrayList<PolicySet>();
        PolicySet resolvedPolicySet = null;
        for (PolicySet policySet : policySets) {
            resolvedPolicySet = resolver.resolveModel(PolicySet.class, policySet);
            resolvedPolicySets.add(resolvedPolicySet);
        }
        policySets.clear();
        policySets.addAll(resolvedPolicySets);
    }

}
