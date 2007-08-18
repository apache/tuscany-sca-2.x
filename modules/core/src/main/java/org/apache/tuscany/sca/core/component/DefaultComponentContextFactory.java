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

package org.apache.tuscany.sca.core.component;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.context.ComponentContextFactory;
import org.apache.tuscany.sca.context.RequestContextFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.runtime.CompositeActivator;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.osoa.sca.ComponentContext;

/**
 * @version $Rev$ $Date$
 */
public class DefaultComponentContextFactory implements ComponentContextFactory {
    private final CompositeActivator compositeActivator;
    private final RequestContextFactory requestContextFactory;
    private final ProxyFactory proxyFactory;
    private final AssemblyFactory assemblyFactory;
    private final JavaInterfaceFactory javaInterfaceFactory;
    private final InterfaceContractMapper interfaceContractMapper;

    public DefaultComponentContextFactory(CompositeActivator compositeActivator,
                                          AssemblyFactory assemblyFactory,
                                          ProxyFactory proxyFactory,
                                          InterfaceContractMapper interfaceContractMapper,
                                          RequestContextFactory requestContextFactory,
                                          JavaInterfaceFactory javaInterfaceFactory) {
        super();
        this.compositeActivator = compositeActivator;
        this.assemblyFactory = assemblyFactory;
        this.proxyFactory = proxyFactory;
        this.requestContextFactory = requestContextFactory;
        this.javaInterfaceFactory = javaInterfaceFactory;
        this.interfaceContractMapper = interfaceContractMapper;
    }

    /**
     * @see org.apache.tuscany.sca.context.ComponentContextFactory#createComponentContext(org.apache.tuscany.sca.runtime.RuntimeComponent, org.apache.tuscany.sca.context.RequestContextFactory)
     */
    public ComponentContext createComponentContext(RuntimeComponent component,
                                                   RequestContextFactory requestContextFactory) {
        if (requestContextFactory == null) {
            requestContextFactory = this.requestContextFactory;
        }
        return new ComponentContextImpl(compositeActivator, assemblyFactory, proxyFactory, interfaceContractMapper,
                                        requestContextFactory, javaInterfaceFactory, component);
    }

}
