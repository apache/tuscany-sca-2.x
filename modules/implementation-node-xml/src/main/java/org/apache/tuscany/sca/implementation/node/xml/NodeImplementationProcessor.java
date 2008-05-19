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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.implementation.node.NodeImplementation;
import org.apache.tuscany.sca.implementation.node.NodeImplementationFactory;
import org.apache.tuscany.sca.monitor.Monitor;


/**
 * Implements a StAX artifact processor for node implementations.
 *
 * @version $Rev$ $Date$
 */
public class NodeImplementationProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<NodeImplementation> {
    private static final QName IMPLEMENTATION_NODE = new QName(Constants.SCA10_TUSCANY_NS, "implementation.node");
    
    private AssemblyFactory assemblyFactory;
    private NodeImplementationFactory implementationFactory;
    
    public NodeImplementationProcessor(ModelFactoryExtensionPoint modelFactories, Monitor monitor) {
        this.assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        this.implementationFactory = modelFactories.getFactory(NodeImplementationFactory.class);
    }

    public QName getArtifactType() {
        // Returns the QName of the XML element processed by this processor
        return IMPLEMENTATION_NODE;
    }

    public Class<NodeImplementation> getModelType() {
        // Returns the type of model processed by this processor
        return NodeImplementation.class;
    }

    public NodeImplementation read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        
        // Read an <implementation.node> element
        NodeImplementation implementation = implementationFactory.createNodeImplementation();
        implementation.setUnresolved(false);

        // Read the composite attribute
        QName qname = getQName(reader, "composite");
        if (qname != null) {
            Composite composite = assemblyFactory.createComposite();
            composite.setName(qname);
            String uri = getString(reader, "uri");
            composite.setURI(uri);
            composite.setUnresolved(true);
            implementation.setComposite(composite);
        }

        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && IMPLEMENTATION_NODE.equals(reader.getName())) {
                break;
            }
        }
        
        return implementation;
    }

    public void resolve(NodeImplementation implementation, ModelResolver resolver) throws ContributionResolveException {
        // We do not need to resolve the referenced composites here
        // Nodes and application composites are not in the same composition tree
    }

    public void write(NodeImplementation implementation, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {
        
        // Write <implementation.node>
        Composite composite = implementation.getComposite();
        QName qname;
        String uri;
        if (composite != null) {
            qname = composite.getName();
            uri = composite.getURI();
        } else {
            qname = null;
            uri = null;
        }
        writeStart(writer, IMPLEMENTATION_NODE.getNamespaceURI(), IMPLEMENTATION_NODE.getLocalPart(),
                                 new XAttr("composite", qname), new XAttr("uri", uri));
        
        writeEnd(writer);
    }
}
