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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.definitions.SCADefinitions;
import org.apache.tuscany.sca.interfacedef.IncompatibleInterfaceContractException;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.policy.IntentAttachPointTypeFactory;

/**
 * A builder that handles the configuration of the components inside a composite
 * and the wiring of component references to component services.
 * 
 * @version $Rev$ $Date$
 */
public class CompositeBuilderImpl implements CompositeBuilder {
    private final static Logger logger = Logger.getLogger(CompositeBuilderImpl.class.getName());
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
                                IntentAttachPointTypeFactory  intentAttachPointTypeFactory,
                                InterfaceContractMapper interfaceContractMapper,
                                Monitor monitor) {
        
        if (monitor == null){
            monitor = new Monitor () {
                public void problem(Problem problem) {
                    
                    Logger problemLogger = Logger.getLogger(problem.getSourceClassName(), problem.getBundleName());
                    
                    if (problemLogger == null){
                        logger.severe("Can't get logger " + problem.getSourceClassName()+ " with bundle " + problem.getBundleName());
                    }
                    
                    if (problem.getSeverity() == Severity.INFO) {
                        problemLogger.log(Level.INFO, problem.getMessageId(), problem.getMessageParams());
                    } else if (problem.getSeverity() == Severity.WARNING) {
                        problemLogger.log(Level.WARNING, problem.getMessageId(), problem.getMessageParams());
                    } else if (problem.getSeverity() == Severity.ERROR) {
                        if (problem.getCause() != null) {
                            problemLogger.log(Level.SEVERE, problem.getMessageId(), problem.getCause());
                        } else {
                            problemLogger.log(Level.SEVERE, problem.getMessageId(), problem.getMessageParams());
                        }
                    }
                }                
            };
        }
        
        includeBuilder = new CompositeIncludeBuilderImpl(monitor); 
        wireBuilder = new CompositeWireBuilderImpl(assemblyFactory, interfaceContractMapper, monitor);
        cloneBuilder = new CompositeCloneBuilderImpl(monitor);
        configurationBuilder = new CompositeConfigurationBuilderImpl(assemblyFactory, scaBindingFactory, intentAttachPointTypeFactory, interfaceContractMapper, monitor);
        
    }
    
    public CompositeBuilderImpl(AssemblyFactory assemblyFactory,
                                SCABindingFactory scaBindingFactory,
                                IntentAttachPointTypeFactory  intentAttachPointTypeFactory,
                                InterfaceContractMapper interfaceContractMapper,
                                Monitor monitor, 
                                SCADefinitions scaDefns) {
        this(assemblyFactory, scaBindingFactory, intentAttachPointTypeFactory, interfaceContractMapper, monitor);
        configurationBuilder.setScaDefinitions(scaDefns);
    }

    public void build(Composite composite) throws CompositeBuilderException {

        // Collect and fuse includes
        includeBuilder.fuseIncludes(composite);

        // Expand nested composites
        cloneBuilder.expandCompositeImplementations(composite);

        // Configure all components
        configurationBuilder.configureComponents(composite);

        // Wire the composite
        try {
			wireBuilder.wireComposite(composite);
		} catch (IncompatibleInterfaceContractException e) {
			throw new CompositeBuilderException(e);
		}

        // Activate composite services
        configurationBuilder.activateCompositeServices(composite);

        // Wire composite references
        wireBuilder.wireCompositeReferences(composite);
        
        // Fuse nested composites
        //cloneBuilder.fuseCompositeImplementations(composite);
    }

}
