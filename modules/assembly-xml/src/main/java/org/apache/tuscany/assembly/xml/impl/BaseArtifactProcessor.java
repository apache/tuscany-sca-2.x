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

package org.apache.tuscany.assembly.xml.impl;

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
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.assembly.AbstractContract;
import org.apache.tuscany.assembly.AbstractProperty;
import org.apache.tuscany.assembly.AbstractReference;
import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Binding;
import org.apache.tuscany.assembly.ConstrainingType;
import org.apache.tuscany.assembly.Contract;
import org.apache.tuscany.assembly.Multiplicity;
import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.assembly.xml.Constants;
import org.apache.tuscany.idl.Interface;
import org.apache.tuscany.idl.Operation;
import org.apache.tuscany.policy.Intent;
import org.apache.tuscany.policy.IntentAttachPoint;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.policy.PolicySet;
import org.apache.tuscany.policy.PolicySetAttachPoint;
import org.apache.tuscany.services.spi.contribution.ArtifactResolver;
import org.apache.tuscany.services.spi.contribution.ContributionReadException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A base class with utility methods for the other artifact processors in this module. 
 * 
 * @version $Rev$ $Date$
 */
abstract class BaseArtifactProcessor implements Constants {

    private AssemblyFactory factory;
    private PolicyFactory policyFactory;

    /**
     * Constructs a new BaseArtifactProcessor.
     */
    BaseArtifactProcessor() {
    }

    /**
     * Construcst a new BaseArtifactProcessor.
     * @param factory
     * @param policyFactory
     */
    BaseArtifactProcessor(AssemblyFactory factory, PolicyFactory policyFactory) {
        this.factory = factory;
        this.policyFactory = policyFactory;
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
                    intent.getOperations().add(operation);
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
            ConstrainingType constrainingType = factory.createConstrainingType();
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
    protected void readAbstractProperty(AbstractProperty prop, XMLStreamReader reader)
        throws XMLStreamException, ContributionReadException {
        prop.setName(getString(reader, "name"));
        prop.setMany(getBoolean(reader, "many"));
        prop.setMustSupply(getBoolean(reader, "mustSupply"));
        prop.setXSDElement(getQName(reader, "element"));
        prop.setXSDType(getQName(reader, "type"));
        Node value = readPropertyValue(reader, prop.getXSDType());
        prop.setDefaultValue(value);
    }

    /**
     * Reads a property element.
     * @param prop
     * @param reader
     * @throws XMLStreamException
     * @throws ContributionReadException
     */
    protected void readProperty(Property prop, XMLStreamReader reader)
        throws XMLStreamException, ContributionReadException {
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
     * Resolve interface, callback interface and bindings on a list of contracts.
     * @param contracts the list of contracts
     * @param resolver the resolver to use to resolve models
     */
    protected <C extends Contract> void resolveContract(List<C> contracts, ArtifactResolver resolver) {
        for (Contract contract: contracts) {
            
            // Resolve interface
            Interface callInterface = contract.getInterface();
            callInterface = resolver.resolve(Interface.class, callInterface);
            contract.setInterface(callInterface);
    
            // Resolve callback interface 
            Interface callbackInterface = contract.getCallbackInterface();
            callbackInterface = resolver.resolve(Interface.class, callbackInterface);
            contract.setCallbackInterface(callbackInterface);
    
            // Resolve bindings
            for (int i = 0, n = contract.getBindings().size(); i < n; i++) {
                Binding binding = contract.getBindings().get(i);
                binding = resolver.resolve(Binding.class, binding);
                contract.getBindings().set(i, binding);
            }
        }
    }

    /**
     * Resolve interface and callback interface on a list of abstract contracts.
     * @param contracts the list of contracts
     * @param resolver the resolver to use to resolve models
     */
    protected <C extends AbstractContract> void resolveAbstractContract(List<C> contracts, ArtifactResolver resolver) {
        for (AbstractContract contract: contracts) {
            
            // Resolve interface
            Interface callInterface = contract.getInterface();
            callInterface = resolver.resolve(Interface.class, callInterface);
            contract.setInterface(callInterface);
    
            // Resolve callback interface 
            Interface callbackInterface = contract.getCallbackInterface();
            callbackInterface = resolver.resolve(Interface.class, callbackInterface);
            contract.setCallbackInterface(callbackInterface);
        }
    }

    /**
     * Read a property value.
     * @param reader
     * @param type
     * @return
     * @throws XMLStreamException
     * @throws ContributionReadException
     */
    public static Document readPropertyValue(XMLStreamReader reader, QName type)
        throws XMLStreamException, ContributionReadException {
        Document doc = DOMUtil.newDocument();

        // root element has no namespace and local name "value"
        Element root = doc.createElementNS(null, "value");
        if (type != null) {
            Attr xsi = doc.createAttributeNS(XMLNS_ATTRIBUTE_NS_URI, "xmlns:xsi");
            xsi.setValue(W3C_XML_SCHEMA_INSTANCE_NS_URI);
            root.setAttributeNodeNS(xsi);

            String prefix = type.getPrefix();
            if (prefix == null || prefix.length() == 0) {
                prefix = "ns";
            }

            DOMUtil.declareNamespace(root, prefix, type.getNamespaceURI());

            Attr xsiType = doc.createAttributeNS(W3C_XML_SCHEMA_INSTANCE_NS_URI, "xsi:type");
            xsiType.setValue(prefix + ":" + type.getLocalPart());
            root.setAttributeNodeNS(xsiType);
        }
        doc.appendChild(root);

        DOMUtil.loadDOM(reader, root);
        return doc;
    }


}
