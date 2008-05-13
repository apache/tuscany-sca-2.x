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
    private CompositeBuilder compositeIncludeBuilder;
    private CompositeBuilder componentWireBuilder;
    private CompositeBuilder compositeReferenceWireBuilder;
    private CompositeBuilder compositeCloneBuilder;
    private CompositeBuilder componentConfigurationBuilder;
    private CompositeBuilder compositeServiceConfigurationBuilder;
    
    /**
     * Constructs a new composite builder.
     * 
     * @param assemblyFactory
     * @param scaBindingFactory
     * @param intentAttachPointTypeFactory
     * @param interfaceContractMapper
     * @param monitor
     */
    public CompositeBuilderImpl(AssemblyFactory assemblyFactory,
                                SCABindingFactory scaBindingFactory,
                                IntentAttachPointTypeFactory  intentAttachPointTypeFactory,
                                InterfaceContractMapper interfaceContractMapper,
                                Monitor monitor) {
        this(assemblyFactory, scaBindingFactory, intentAttachPointTypeFactory, interfaceContractMapper, null, monitor);
    }
    
    /**
     * Constructs a new composite builder.
     * 
     * @param assemblyFactory
     * @param scaBindingFactory
     * @param intentAttachPointTypeFactory
     * @param interfaceContractMapper
     * @param policyDefinitions
     * @param monitor
     */
    public CompositeBuilderImpl(AssemblyFactory assemblyFactory,
                                SCABindingFactory scaBindingFactory,
                                IntentAttachPointTypeFactory  intentAttachPointTypeFactory,
                                InterfaceContractMapper interfaceContractMapper,
                                SCADefinitions policyDefinitions,
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
        
        compositeIncludeBuilder = new CompositeIncludeBuilderImpl(monitor); 
        componentWireBuilder = new ComponentReferenceWireBuilderImpl(assemblyFactory, interfaceContractMapper, monitor);
        compositeReferenceWireBuilder = new CompositeReferenceWireBuilderImpl(assemblyFactory, interfaceContractMapper, monitor);
        compositeCloneBuilder = new CompositeCloneBuilderImpl(monitor);
        componentConfigurationBuilder = new ComponentConfigurationBuilderImpl(assemblyFactory, scaBindingFactory, interfaceContractMapper, policyDefinitions, monitor);
        compositeServiceConfigurationBuilder = new CompositeServiceConfigurationBuilderImpl(assemblyFactory, scaBindingFactory, interfaceContractMapper, policyDefinitions, monitor);
    }

    public void build(Composite composite) throws CompositeBuilderException {

        // Collect and fuse includes
        compositeIncludeBuilder.build(composite);

        // Expand nested composites
        compositeCloneBuilder.build(composite);

        // Configure all components
        componentConfigurationBuilder.build(composite);

        // Wire the components
        componentWireBuilder.build(composite);

        // Configure composite services
        compositeServiceConfigurationBuilder.build(composite);

        // Wire the composite references
        compositeReferenceWireBuilder.build(composite);
        
        // Fuse nested composites
        //FIXME do this later
        //cloneBuilder.fuseCompositeImplementations(composite);
    }

}
