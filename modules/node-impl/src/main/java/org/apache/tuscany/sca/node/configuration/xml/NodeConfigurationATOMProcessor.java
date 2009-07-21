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
package org.apache.tuscany.sca.node.configuration.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.node.configuration.ContributionConfiguration;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.apache.tuscany.sca.node.configuration.NodeConfigurationFactory;

/**
 * Implements a StAX artifact processor for configured node implementations.
 *
 * @version $Rev$ $Date$
 */
public class NodeConfigurationATOMProcessor extends BaseStAXArtifactProcessor implements
    StAXArtifactProcessor<NodeConfiguration> {

    private static final String ATOM_NS = "http://www.w3.org/2005/Atom";
    private static final QName FEED_QNAME = new QName(ATOM_NS, "feed");
    private static final QName ENTRY_QNAME = new QName(ATOM_NS, "entry");
    private static final QName ID_QNAME = new QName(ATOM_NS, "id");
    private static final QName LINK_QNAME = new QName(ATOM_NS, "link");
    private static final QName CONTENT_QNAME = new QName(ATOM_NS, "content");
    private static final String HREF = "href";

    private NodeConfigurationFactory factory;

    public NodeConfigurationATOMProcessor(FactoryExtensionPoint modelFactories, Monitor monitor) {
        this.factory = modelFactories.getFactory(NodeConfigurationFactory.class);
    }

    public QName getArtifactType() {
        return null;
    }

    public Class<NodeConfiguration> getModelType() {
        // Returns the type of model processed by this processor
        return NodeConfiguration.class;
    }

    public NodeConfiguration read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {

        NodeConfiguration config = factory.createNodeConfiguration();

        // Read a feed containing links to the composite and the contributions assigned to
        // the node
        ContributionConfiguration contribution = null;
        boolean id = false;
        QName name = null;

        while (reader.hasNext()) {
            int event = reader.getEventType();
            switch (event) {

                case START_ELEMENT:
                    name = reader.getName();

                    if (ENTRY_QNAME.equals(name)) {

                        // Read an <entry>
                        contribution = factory.createContributionConfiguration();
                    } else if (ID_QNAME.equals(name)) {

                        // Read an <id>
                        id = true;

                    } else if (LINK_QNAME.equals(name)) {

                        // Read a <link>
                        String href = getString(reader, HREF);

                        if (contribution != null) {
                            contribution.setLocation(href);
                        }
                    } else if (CONTENT_QNAME.equals(name)) {
                        // Read a <content>
                    } else if (FEED_QNAME.equals(name)) {
                        // Read a <feed>
                    }
                    break;

                case XMLStreamConstants.CHARACTERS:

                    // Read characters inside an <id> element
                    if (id) {
                        if (contribution != null) {
                            contribution.setURI(reader.getText());
                        }
                    }
                    break;

                case END_ELEMENT:
                    name = reader.getName();

                    // Clear current state when reading reaching end element
                    if (ENTRY_QNAME.equals(name)) {
                        if (contribution != null) {
                            config.getContributions().add(contribution);
                        }

                        contribution = null;

                    } else if (ID_QNAME.equals(name)) {
                        id = false;

                    } else if (FEED_QNAME.equals(name)) {

                        // We've reached the end of the feed
                        return config;
                    }
                    break;
            }

            // Read the next element
            if (reader.hasNext()) {
                reader.next();
            }
        }
        return config;
    }

    public void resolve(NodeConfiguration implementation, ModelResolver resolver) throws ContributionResolveException {
    }

    public void write(NodeConfiguration implementation, XMLStreamWriter writer) throws ContributionWriteException,
        XMLStreamException {
    }
}
