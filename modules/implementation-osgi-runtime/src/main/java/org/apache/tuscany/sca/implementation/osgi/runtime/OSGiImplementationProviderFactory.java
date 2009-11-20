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
import org.apache.tuscany.sca.core.invocation.ProxyFactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.osgi.OSGiImplementation;
import org.apache.tuscany.sca.implementation.osgi.OSGiImplementationFactory;
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
    private OSGiImplementationFactory implementationFactory;
    private ProxyFactoryExtensionPoint proxyFactoryExtensionPoint;

    public OSGiImplementationProviderFactory(ExtensionPointRegistry registry) {
        proxyFactoryExtensionPoint = registry.getExtensionPoint(ProxyFactoryExtensionPoint.class);
        FactoryExtensionPoint factoryExtensionPoint = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.implementationFactory = factoryExtensionPoint.getFactory(OSGiImplementationFactory.class);
    }

    public ImplementationProvider createImplementationProvider(RuntimeComponent component,
                                                               OSGiImplementation implementation) {
        try {
            return new OSGiImplementationProvider(component, implementation, proxyFactoryExtensionPoint,
                                                  implementationFactory);
        } catch (BundleException e) {
            throw new RuntimeException(e);
        }

    }

    public Class<OSGiImplementation> getModelType() {
        return OSGiImplementation.class;
    }

}
