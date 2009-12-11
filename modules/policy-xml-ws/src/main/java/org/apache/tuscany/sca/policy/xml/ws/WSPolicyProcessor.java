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

package org.apache.tuscany.sca.policy.xml.ws;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.neethi.PolicyEngine;
import org.apache.tuscany.sca.common.xml.stax.StAXHelper;
import org.apache.tuscany.sca.common.xml.stax.reader.XMLDocumentStreamReader;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;

/**
 * Processor for handling xml models of PolicySet definitions
 *
 * @version $Rev$ $Date$
 */
public class WSPolicyProcessor extends BaseStAXArtifactProcessor implements
    StAXArtifactProcessor<org.apache.neethi.Policy> {
    public final static String WS_POLICY_NS = "http://schemas.xmlsoap.org/ws/2004/09/policy";
    public final static String WS_POLICY = "Policy";

    public final static QName WS_POLICY_QNAME = new QName(WS_POLICY_NS, WS_POLICY);
    private StAXHelper stAXHelper;

    public WSPolicyProcessor(ExtensionPointRegistry registry) {
        this.stAXHelper = StAXHelper.getInstance(registry);
    }

    public QName getArtifactType() {
        return WS_POLICY_QNAME;
    }

    public Class<org.apache.neethi.Policy> getModelType() {
        return org.apache.neethi.Policy.class;
    }

    public org.apache.neethi.Policy read(XMLStreamReader reader, ProcessorContext context)
        throws ContributionReadException, XMLStreamException {
        org.apache.neethi.Policy wsPolicy = null;
        XMLDocumentStreamReader doc = new XMLDocumentStreamReader(reader);
        StAXOMBuilder builder = new StAXOMBuilder(doc);
        OMElement element = builder.getDocumentElement();
        wsPolicy = PolicyEngine.getPolicy(element);
        return wsPolicy;
    }

    public void write(org.apache.neethi.Policy wsPolicy, XMLStreamWriter writer, ProcessorContext context)
        throws ContributionWriteException, XMLStreamException {

        // Write an <sca:policySet>
        writer.writeStartElement(WS_POLICY_NS, WS_POLICY);

        if (wsPolicy != null) {
            wsPolicy.serialize(writer);
        }

        writer.writeEndElement();
    }

    public void resolve(org.apache.neethi.Policy wsPolicy, ModelResolver resolver, ProcessorContext context)
        throws ContributionResolveException {

    }

    private OMElement loadElement(XMLStreamReader reader) throws XMLStreamException {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMElement head = fac.createOMElement(reader.getName());
        OMElement current = head;

        while (true) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    QName name = reader.getName();
                    OMElement child = fac.createOMElement(name, current);

                    int count = reader.getNamespaceCount();
                    for (int i = 0; i < count; i++) {
                        String prefix = reader.getNamespacePrefix(i);
                        String ns = reader.getNamespaceURI(i);
                        child.declareNamespace(ns, prefix);
                    }

                    if (!"".equals(name.getNamespaceURI())) {
                        child.declareNamespace(name.getNamespaceURI(), name.getPrefix());
                    }

                    // add the attributes for this element
                    count = reader.getAttributeCount();
                    for (int i = 0; i < count; i++) {
                        OMNamespace omNs = null;
                        String ns = reader.getAttributeNamespace(i);
                        String prefix = reader.getAttributePrefix(i);
                        String qname = reader.getAttributeLocalName(i);
                        String value = reader.getAttributeValue(i);

                        if (ns != null) {
                            omNs = fac.createOMNamespace(ns, prefix);
                        }

                        child.addAttribute(qname, value, omNs);
                        if (ns != null) {
                            child.declareNamespace(ns, prefix);
                        }
                    }
                    current = child;
                    break;
                case XMLStreamConstants.CDATA:
                    fac.createOMText(current, reader.getText());
                    break;
                case XMLStreamConstants.CHARACTERS:
                    fac.createOMText(current, reader.getText());
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if (current == head) {
                        return head;
                    } else {
                        current = (OMElement)current.getParent();
                    }
            }
        }
    }

}
