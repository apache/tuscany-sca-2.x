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
package org.apache.tuscany.sca.assembly.builder.impl;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderMonitor;
import org.apache.tuscany.sca.assembly.builder.Problem;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;

/**
 * A builder that handles the configuration of the components inside a composite
 * and the wiring of component references to component services.
 * 
 * @version $Rev$ $Date$
 */
public class CompositeBuilderImpl implements CompositeBuilder {

    private CompositeIncludeBuilderImpl includeBuilder;
    private CompositeWireBuilderImpl wireBuilder;
    private CompositeCloneBuilderImpl cloneBuilder;
    private CompositeConfigurationBuilderImpl configurationBuilder;

    /**
     * Constructs a new composite util.
     * 
     * @param assemblyFactory
     * @param interfaceContractMapper
     */
    public CompositeBuilderImpl(AssemblyFactory assemblyFactory,
                                SCABindingFactory scaBindingFactory,
                                InterfaceContractMapper interfaceContractMapper,
                                CompositeBuilderMonitor monitor) {
        
        if (monitor == null) {
            // Create a default monitor that does nothing.
            monitor = new CompositeBuilderMonitor() {
                public void problem(Problem problem) {
                }
            };
        }

        includeBuilder = new CompositeIncludeBuilderImpl(monitor);
        wireBuilder = new CompositeWireBuilderImpl(assemblyFactory, scaBindingFactory, interfaceContractMapper, monitor);
        cloneBuilder = new CompositeCloneBuilderImpl(monitor);
        configurationBuilder = new CompositeConfigurationBuilderImpl(assemblyFactory, interfaceContractMapper, monitor);
    }

    public void build(Composite composite) throws CompositeBuilderException {

        // Collect and fuse includes
        includeBuilder.fuseIncludes(composite);

        // Expand nested composites
        cloneBuilder.expandCompositeImplementations(composite);

        // Configure all components
        configurationBuilder.configureComponents(composite);

        // Wire the composite
        wireBuilder.wireComposite(composite);

        // Activate composite services
        configurationBuilder.activateCompositeServices(composite);

        // Wire composite references
        wireBuilder.wireCompositeReferences(composite);
    }

}
