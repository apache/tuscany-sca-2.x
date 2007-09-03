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

import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.topology.TopologyFactory;

/**
 * A base class with utility methods for the other artifact processors in this module. 
 * 
 * @version $Rev$ $Date$
 */
public abstract class BaseTopologyArtifactProcessor extends BaseStAXArtifactProcessor implements Constants {

    protected TopologyFactory               topologyFactory;
    protected AssemblyFactory               assemblyFactory;    
    protected StAXArtifactProcessor<Object> extensionProcessor;

    private static final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
    static {
        domFactory.setNamespaceAware(true);
    }

    /**
     * Construct a new BaseArtifactProcessor.
     * @param factory
     * @param policyFactory
     */
    @SuppressWarnings("unchecked")
    public BaseTopologyArtifactProcessor(TopologyFactory topologyFactory, AssemblyFactory assemblyFactory, StAXArtifactProcessor extensionProcessor) {
        this.topologyFactory = topologyFactory;
        this.assemblyFactory = assemblyFactory;
        this.extensionProcessor = extensionProcessor;
    }

    /**
     * Start an element.
     * @param writer
     * @param name
     * @param attrs
     * @throws XMLStreamException
     */
    protected void writeStart(XMLStreamWriter writer, String name, XAttr... attrs) throws XMLStreamException {
        writeStart(writer, TUSCANY_TOPOLOGY_10_NS, name, attrs);
    }

    /**
     * Start a document.
     * @param writer
     * @throws XMLStreamException
     */
    protected void writeStartDocument(XMLStreamWriter writer, String name, XAttr... attrs) throws XMLStreamException {
        writeStartDocument(writer, TUSCANY_TOPOLOGY_10_NS, name, attrs);
    }
    
    /**
     * Resolve an implementation.
     * @param implementation
     * @param resolver
     * @return
     * @throws ContributionResolveException
     */
    protected Implementation resolveImplementation(Implementation implementation, ModelResolver resolver) throws ContributionResolveException {
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
            }
        }
        return implementation;
    }

    /**
     * Resolve interface, callback interface and bindings on a list of contracts.
     * @param contracts the list of contracts
     * @param resolver the resolver to use to resolve models
     */
    protected <C extends Contract> void resolveContracts(List<C> contracts, ModelResolver resolver) throws ContributionResolveException {
        for (Contract contract: contracts) {

            // Resolve the interface contract
            InterfaceContract interfaceContract = contract.getInterfaceContract();
            if (interfaceContract != null) {
                extensionProcessor.resolve(interfaceContract, resolver);
            }

            // Resolve bindings
            for (int i = 0, n = contract.getBindings().size(); i < n; i++) {
                Binding binding = contract.getBindings().get(i);
                extensionProcessor.resolve(binding, resolver);
            }

            // Resolve callback bindings
            if (contract.getCallback() != null) {
                for (int i = 0, n = contract.getCallback().getBindings().size(); i < n; i++) {
                    Binding binding = contract.getCallback().getBindings().get(i);
                    extensionProcessor.resolve(binding, resolver);
                }
            }
        }
    }    

}
