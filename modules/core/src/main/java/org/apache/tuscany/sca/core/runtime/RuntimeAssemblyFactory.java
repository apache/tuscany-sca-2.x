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

package org.apache.tuscany.sca.core.runtime;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;

/**
 * @version $Rev$ $Date$
 */
public class RuntimeAssemblyFactory extends DefaultAssemblyFactory implements AssemblyFactory {
    private final ProxyFactory proxyFactory;
    private final InterfaceContractMapper interfaceContractMapper;
    
    /**
     * @param proxyFactory
     */
    public RuntimeAssemblyFactory(InterfaceContractMapper interfaceContractMapper, ProxyFactory proxyFactory) {
        super();
        this.proxyFactory = proxyFactory;
        this.interfaceContractMapper = interfaceContractMapper;
    }

    @Override
    public Component createComponent() {
        return new RuntimeComponentImpl(proxyFactory);
    }

    @Override
    public ComponentReference createComponentReference() {
        return new RuntimeComponentReferenceImpl(interfaceContractMapper);
    }

    @Override
    public SCABinding createSCABinding() {
        return new RuntimeSCABindingImpl();
    }

    @Override
    public ComponentService createComponentService() {
        return new RuntimeComponentServiceImpl(interfaceContractMapper);
    }

}
