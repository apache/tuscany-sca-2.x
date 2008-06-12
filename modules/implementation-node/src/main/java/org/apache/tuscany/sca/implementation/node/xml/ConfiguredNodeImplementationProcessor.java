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
package org.apache.tuscany.sca.implementation.node.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.implementation.node.ConfiguredNodeImplementation;
import org.apache.tuscany.sca.implementation.node.NodeImplementationFactory;
import org.apache.tuscany.sca.monitor.Monitor;


/**
 * Implements a StAX artifact processor for configured node implementations.
 *
 * @version $Rev$ $Date$
 */
public class ConfiguredNodeImplementationProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<ConfiguredNodeImplementation> {
    
    private static final String ATOM_NS = "http://www.w3.org/2005/Atom";
    private static final QName FEED_QNAME = new QName(ATOM_NS, "feed");
    private static final QName ENTRY_QNAME = new QName(ATOM_NS, "entry");
    private static final QName ID_QNAME = new QName(ATOM_NS, "id");
    private static final QName LINK_QNAME = new QName(ATOM_NS, "link");
    private static final String HREF = "href";
    
    private ContributionFactory contributionFactory;
    private AssemblyFactory assemblyFactory;
    private NodeImplementationFactory implementationFactory;
    
    public ConfiguredNodeImplementationProcessor(ModelFactoryExtensionPoint modelFactories, Monitor monitor) {
        this.contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        this.assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        this.implementationFactory = modelFactories.getFactory(NodeImplementationFactory.class);
    }

    public QName getArtifactType() {
        return null;
    }

    public Class<ConfiguredNodeImplementation> getModelType() {
        // Returns the type of model processed by this processor
        return ConfiguredNodeImplementation.class;
    }

    public ConfiguredNodeImplementation read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        
        ConfiguredNodeImplementation implementation = implementationFactory.createConfiguredNodeImplementation();
        implementation.setUnresolved(true);

        // Read a feed containing links to the composite and the contributions assigned to
        // the node
        Composite composite = null;
        Contribution contribution = null;
        boolean id = false;
        QName name = null;
        
        while (reader.hasNext()) {
            int event = reader.getEventType();
            switch (event) {

                case START_ELEMENT:
                    name = reader.getName();

                    if (ENTRY_QNAME.equals(name)) {

                        // Read an <entry>
                        if (implementation.getComposite() == null) {
                            composite = assemblyFactory.createComposite();
                        } else {
                            contribution = contributionFactory.createContribution();
                        }
                    } else if (ID_QNAME.equals(name)) {
                        
                        // Read an <id>
                        id = true;
                        
                    } else if (LINK_QNAME.equals(name)) {

                        // Read a <link>
                        String href = getString(reader, HREF);
                        
                        if (composite != null) {
                            composite.setURI(href);
                        } else if (contribution != null) {
                            contribution.setLocation(href);
                        }
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
                        if (composite != null) {
                            implementation.setComposite(composite);
                        } else if (contribution != null) {
                            implementation.getContributions().add(contribution);
                        }

                        composite = null;
                        contribution = null;
                        
                    } else if (ID_QNAME.equals(name)) {
                        id = false;
                        
                    } else if (FEED_QNAME.equals(name)) {
                        
                        // We've reached the end of the feed
                        return implementation;
                    }
                    break;
            }

            // Read the next element
            if (reader.hasNext()) {
                reader.next();
            }
        }
        return implementation;
    }

    public void resolve(ConfiguredNodeImplementation implementation, ModelResolver resolver) throws ContributionResolveException {
        
        // Resolve the referenced composite
        Composite composite = implementation.getComposite();
        if (composite != null) {
            composite = resolver.resolveModel(Composite.class, composite);
            if (!composite.isUnresolved()) {
                implementation.setComposite(composite);
            }
        }
        
        // Resolve the referenced contributions
        List<Contribution> contributions = implementation.getContributions();
        for (int i = 0, n = contributions.size(); i < n; i++) {
            Contribution contribution = contributions.get(i);
            contribution = resolver.resolveModel(Contribution.class, contribution);
            if (!contribution.isUnresolved()) {
                contributions.set(i, contribution);
            }
        }
        
        implementation.setUnresolved(false);
    }

    public void write(ConfiguredNodeImplementation implementation, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {
        //TODO Write the feed describing the node configuration
    }
}
