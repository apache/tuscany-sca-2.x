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

package org.apache.tuscany.sca.policy.builder.impl;

import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.common.xml.dom.DOMHelper;
import org.apache.tuscany.sca.common.xml.stax.StAXHelper;
import org.apache.tuscany.sca.common.xml.xpath.XPathHelper;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySubject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A composite builder that computes policy sets based on attached intents and policy sets.
 * Useful if you want to build the model without making any runtime decisions such as
 * reference/services matching
 *
 * @version $Rev$ $Date$
 */
public class PolicyAttachmentBuilderImpl implements CompositeBuilder {
    private XMLOutputFactory xmlOutputFactory;
    private StAXHelper staxHelper;
    private DOMHelper domHelper;
    private XPathHelper xpathHelper;
    private StAXArtifactProcessor processor;

    public PolicyAttachmentBuilderImpl(ExtensionPointRegistry registry) {
        xmlOutputFactory = registry.getExtensionPoint(FactoryExtensionPoint.class).getFactory(XMLOutputFactory.class);
        domHelper = DOMHelper.getInstance(registry);
        xpathHelper = XPathHelper.getInstance(registry);
        staxHelper = StAXHelper.getInstance(registry);
        StAXArtifactProcessorExtensionPoint processors =
            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        processor = processors.getProcessor(Composite.class);
    }

    public String getID() {
        return "org.apache.tuscany.sca.policy.builder.PolicyAttachmentBuilder";
    }

    public void build(Composite composite, Definitions definitions, Monitor monitor) throws CompositeBuilderException {
        try {
            applyXPath(composite, definitions, monitor);
        } catch (Exception e) {
            throw new CompositeBuilderException(e);
        }
    }

    private void applyXPath(Composite composite, Definitions definitions, Monitor monitor) throws Exception {
        // First write the composite into a DOM document so that we can apply the xpath
        StringWriter sw = new StringWriter();
        XMLStreamWriter writer = staxHelper.createXMLStreamWriter(sw);
        // Write the composite into a DOM document
        processor.write(composite, writer);
        writer.close();

        Document document = domHelper.load(sw.toString());

        for (PolicySet ps : definitions.getPolicySets()) {
            XPathExpression exp = ps.getAttachToXPathExpression();
            if (exp != null) {
                NodeList nodes = (NodeList)exp.evaluate(document, XPathConstants.NODESET);
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    // The node can be a component, service, reference or binding
                    node.getNamespaceURI();
                    // Use the node to find the corresponding element in the java model
                }
            }
        }
    }

    /**
     * Look up the corresponding Java model within the composite based on the DOM node
     * @param composite
     * @param node
     * @return
     */
    private PolicySubject lookup(Composite composite, Node node) {
        return null;
    }

}
