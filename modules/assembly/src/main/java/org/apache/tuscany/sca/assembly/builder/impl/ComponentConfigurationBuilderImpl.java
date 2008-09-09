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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.definitions.SCADefinitions;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * A composite builder that handles the configuration of components.
 *
 * @version $Rev$ $Date$
 */
public class ComponentConfigurationBuilderImpl extends BaseConfigurationBuilderImpl implements CompositeBuilder {

    @Deprecated
    public ComponentConfigurationBuilderImpl(AssemblyFactory assemblyFactory,
                                             SCABindingFactory scaBindingFactory,
                                             InterfaceContractMapper interfaceContractMapper,
                                             SCADefinitions policyDefinitions,
                                             Monitor monitor) {
        super(assemblyFactory, scaBindingFactory,
              null, null,
              interfaceContractMapper, policyDefinitions, monitor);
    }

    public ComponentConfigurationBuilderImpl(AssemblyFactory assemblyFactory,
                                             SCABindingFactory scaBindingFactory,
                                             DocumentBuilderFactory documentBuilderFactory,
                                             TransformerFactory transformerFactory,
                                             InterfaceContractMapper interfaceContractMapper,
                                             SCADefinitions policyDefinitions,
                                             Monitor monitor) {
        super(assemblyFactory, scaBindingFactory,
              documentBuilderFactory, transformerFactory,
              interfaceContractMapper, policyDefinitions, monitor);
    }

    public void build(Composite composite) throws CompositeBuilderException {
        configureComponents(composite);
    }
    
}
