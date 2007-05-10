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

package org.apache.tuscany.core.runtime;

import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.assembly.SCABinding;
import org.apache.tuscany.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.invocation.ProxyFactory;

/**
 * @version $Rev$ $Date$
 */
public class RuntimeAssemblyFactory extends DefaultAssemblyFactory {
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
