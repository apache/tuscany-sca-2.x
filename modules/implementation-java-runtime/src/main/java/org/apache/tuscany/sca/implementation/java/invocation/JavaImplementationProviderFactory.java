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

package org.apache.tuscany.sca.implementation.java.invocation;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.context.ComponentContextFactory;
import org.apache.tuscany.sca.context.ContextFactoryExtensionPoint;
import org.apache.tuscany.sca.context.PropertyValueFactory;
import org.apache.tuscany.sca.context.RequestContextFactory;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.invocation.CglibProxyFactory;
import org.apache.tuscany.sca.core.invocation.ExtensibleProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactoryExtensionPoint;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.provider.ImplementationProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

/**
 * @version $Rev$ $Date$
 */
public class JavaImplementationProviderFactory implements ImplementationProviderFactory<JavaImplementation> {
    private static final Logger logger = Logger.getLogger(JavaImplementationProviderFactory.class.getName()); 
    private PropertyValueFactory propertyValueFactory;
    private DataBindingExtensionPoint databindings;
    private ProxyFactory proxyFactory;
    private ComponentContextFactory componentContextFactory;
    private RequestContextFactory requestContextFactory;

    public JavaImplementationProviderFactory(ExtensionPointRegistry registry) {
        FactoryExtensionPoint factories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        MessageFactory messageFactory = factories.getFactory(MessageFactory.class);

        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        InterfaceContractMapper interfaceContractMapper = utilities.getUtility(InterfaceContractMapper.class);

        ProxyFactoryExtensionPoint proxyFactories = registry.getExtensionPoint(ProxyFactoryExtensionPoint.class);
        try {
            proxyFactories.setClassProxyFactory(new CglibProxyFactory(messageFactory, interfaceContractMapper));
        } catch (NoClassDefFoundError e) {
            logger.log(Level.WARNING, "Class-based proxy is not supported", e);
        }

        databindings = registry.getExtensionPoint(DataBindingExtensionPoint.class);
        propertyValueFactory = factories.getFactory(PropertyValueFactory.class);

        ContextFactoryExtensionPoint contextFactories = registry.getExtensionPoint(ContextFactoryExtensionPoint.class);
        componentContextFactory = contextFactories.getFactory(ComponentContextFactory.class);
        requestContextFactory = contextFactories.getFactory(RequestContextFactory.class);

        proxyFactory = ExtensibleProxyFactory.getInstance(registry);
    }
    
    /*
    public JavaImplementationProviderFactory(ProxyFactory proxyService,
                                             DataBindingExtensionPoint dataBindingRegistry,
                                             PropertyValueFactory propertyValueObjectFactory,
                                             ComponentContextFactory componentContextFactory,
                                             RequestContextFactory requestContextFactory) {
        super();
        this.proxyFactory = proxyService;
        this.databindings = dataBindingRegistry;
        this.propertyValueFactory = propertyValueObjectFactory;
        this.componentContextFactory = componentContextFactory;
        this.requestContextFactory = requestContextFactory;
    }
    */

    public ImplementationProvider createImplementationProvider(RuntimeComponent component,
                                                               JavaImplementation implementation) {
        return new JavaImplementationProvider(component,
                                              implementation,
                                              proxyFactory,
                                              databindings,
                                              propertyValueFactory,
                                              componentContextFactory,
                                              requestContextFactory);
    }

    public Class<JavaImplementation> getModelType() {
        return JavaImplementation.class;
    }
}
