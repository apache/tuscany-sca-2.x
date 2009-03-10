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
package org.apache.tuscany.sca.implementation.osgi.runtime;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.invocation.ProxyFactoryExtensionPoint;
import org.apache.tuscany.sca.core.scope.ScopeRegistry;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.implementation.osgi.OSGiImplementation;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.provider.ImplementationProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.osgi.framework.BundleException;

/**
 * Builds a OSGi-based implementation provider from a component definition
 *
 * @version $Rev$ $Date$
 */
public class OSGiImplementationProviderFactory implements ImplementationProviderFactory<OSGiImplementation> {

    private ProxyFactoryExtensionPoint proxyFactoryExtensionPoint;
    private DataBindingExtensionPoint dataBindings;
    private ScopeRegistry scopeRegistry;
    private MessageFactory messageFactory;
    private InterfaceContractMapper mapper;

    public OSGiImplementationProviderFactory(ExtensionPointRegistry extensionPoints) {

        dataBindings = extensionPoints.getExtensionPoint(DataBindingExtensionPoint.class);

        // FIXME: Scope registry is not an extension point, and this usage is specific
        // to implementation.osgi since it needs to change scope after the component is
        // created. Do we need to find a better way?
        scopeRegistry = extensionPoints.getExtensionPoint(ScopeRegistry.class);
        
        proxyFactoryExtensionPoint = extensionPoints.getExtensionPoint(ProxyFactoryExtensionPoint.class);

        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        messageFactory = modelFactories.getFactory(MessageFactory.class);

        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        mapper = utilities.getUtility(InterfaceContractMapper.class);
    }

    public ImplementationProvider createImplementationProvider(RuntimeComponent component,
                                                               OSGiImplementation implementation) {

        try {

            return new OSGiImplementationProvider(component, implementation, dataBindings, scopeRegistry,
                                                  messageFactory, proxyFactoryExtensionPoint, mapper);

        } catch (BundleException e) {
            throw new RuntimeException(e);
        }

    }

    public Class<OSGiImplementation> getModelType() {
        return OSGiImplementation.class;
    }

}
