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

package org.apache.tuscany.sca.topology.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.topology.Node;
import org.apache.tuscany.sca.topology.Runtime;
import org.apache.tuscany.sca.topology.Scheme;
import org.apache.tuscany.sca.topology.Component;
import org.apache.tuscany.sca.topology.TopologyFactory;

/**
 * A composite processor.
 * 
 * @version $Rev$ $Date$
 */
public class TopologyProcessor extends BaseTopologyArtifactProcessor implements StAXArtifactProcessor<Runtime> {
    
    /**
     * Construct a new composite processor
     * @param assemblyFactory
     * @param policyFactory
     * @param extensionProcessor 
     */
    public TopologyProcessor(TopologyFactory topologyFactory,
                             AssemblyFactory assemblyFactory,
                             StAXArtifactProcessor extensionProcessor) {
        super(topologyFactory, assemblyFactory, extensionProcessor);
    }
    
    public Runtime read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        QName name = null;
        Runtime runtime = null;
        Node node = null;
        String domainName = DEFAULT_DOMAIN;
      
        // Read the composite document
        while (reader.hasNext()) {
            int event = reader.getEventType();
            switch (event) {
                case START_ELEMENT:
                    name = reader.getName();
                    
                    if (RUNTIME_QNAME.equals(name)) {
                        // Read a <runtime>
                        runtime = topologyFactory.createRuntime();
                    } else if (NODE_QNAME.equals(name)) {
                        // Read a <node>
                        node = topologyFactory.createNode();
                        node.setName(getString(reader, NAME));
                        
                        // add node to runtime
                        runtime.getNodes().add(node);
                        
                        // reset domain name to the default
                        domainName = DEFAULT_DOMAIN;
                    } else if (DOMAIN_QNAME.equals(name)) {
                        // Read a <domain>
                        domainName = getString(reader, NAME);                            
                    } else if (SCHEME_QNAME.equals(name)) {
                        // Read a <scheme>
                        Scheme scheme = topologyFactory.createScheme();
                        scheme.setName(getString(reader, NAME));
                        scheme.setBaseURL(getString(reader, BASE_URL));
                        
                        scheme.setDomainName(domainName);
                        
                        // Add scheme to the node
                        node.getSchemes(domainName).add(scheme);
                    } else if (COMPONENT_QNAME.equals(name)) {
                        // Read a <component>
                        Component component = topologyFactory.createComponent();
                        component.setName(getString(reader, NAME));
                        
                        component.setDomainName(domainName);
                        
                        // Add scheme to the node
                        node.getComponents(domainName).add(component);                            

                    } else {
                        
                        // Read an extension element
                        Object extension = extensionProcessor.read(reader);
                        
                        if (extension != null) {
                            // no extensions are supported
                        }
                    }

                case END_ELEMENT:
                    name = reader.getName();
                    // Clear current state when reading reaching end element                
            }
            
            if (reader.hasNext()) {
                reader.next();
            }
        }
        return runtime;
    }

    
    public void write(Runtime runtime, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {

        writeStartDocument(writer, RUNTIME);
        
        // TODO - write out the scheme definitions

        for (Node node : runtime.getNodes()) {
            writeStart(writer, NODE, new XAttr(NAME, node.getName()));
           
            for (String domainName : node.getDomainNames()) {
                
                writeStart(writer, DOMAIN, new XAttr(NAME, domainName));
               
                for (Scheme scheme: node.getSchemes(domainName)) {
                    writeStart(writer, SCHEME, new XAttr(NAME, scheme.getName()), new XAttr(BASE_URL, scheme.getBaseURL()));
                    writeEnd(writer);
                }
                
                for (Component component: node.getComponents(domainName)) {
                    writeStart(writer, COMPONENT, new XAttr(NAME, component.getName()));
                    writeEnd(writer);
                }                    
                
                writeEnd(writer);
            }
  
            writeEnd(writer);
        }
   
        writeEndDocument(writer);
    }
    
    public void resolve(Runtime runtime, ModelResolver resolver) throws ContributionResolveException {
        // no resolution steps defined 
    }

    public QName getArtifactType() {
        return RUNTIME_QNAME;
    }
    
    public Class<Runtime> getModelType() {
        return Runtime.class;
    }
}
