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

import java.util.List;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * A composite builder that performs any additional building steps that
 * composite service bindings may need.  Used for WSDL generation.
 * 
 * TODO - What is this actually used for? I can't find any references in the
 *        code base
 *
 * @version $Rev$ $Date$
 */
public class CompositeBindingConfigurationBuilderImpl extends CompositeBindingURIBuilderImpl implements
    CompositeBuilder {

    public CompositeBindingConfigurationBuilderImpl(FactoryExtensionPoint factories, InterfaceContractMapper mapper) {
        super(factories.getFactory(AssemblyFactory.class), factories.getFactory(SCABindingFactory.class), null, null,
              mapper);
    }

    public CompositeBindingConfigurationBuilderImpl(AssemblyFactory assemblyFactory,
                                                    SCABindingFactory scaBindingFactory,
                                                    InterfaceContractMapper interfaceContractMapper) {
        super(assemblyFactory, scaBindingFactory, null, null, interfaceContractMapper);
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.CompositeServiceBindingBuilder";
    }

    public void build(Composite composite, Definitions definitions, Monitor monitor) throws CompositeBuilderException {
        List<Binding> defaultBindings = null;
        for (Object x : composite.getExtensions()) {
            if (x instanceof List) {
                defaultBindings = (List<Binding>)x;
            }
        }
        // TODO - EPR - is this ever used?
        //configureBindingURIs(composite, definitions, defaultBindings, monitor);
    }

}
