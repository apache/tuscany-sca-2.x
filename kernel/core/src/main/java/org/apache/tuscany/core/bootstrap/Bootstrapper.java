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
package org.apache.tuscany.core.bootstrap;

import org.apache.tuscany.core.resolver.AutowireResolver;
import org.apache.tuscany.host.MonitorFactory;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.implementation.java.Introspector;
import org.apache.tuscany.spi.loader.Loader;
import org.apache.tuscany.spi.loader.PropertyObjectFactory;

/**
 * Interface that abstracts the process used to create a running Tuscany system. Implementation of this may provide
 * different mechanisms for creating the primoridal system components used to boot the core to the level where it can
 * support end-user applications.
 *
 * @version $Rev$ $Date$
 */
public interface Bootstrapper {
    /**
     * Return the MonitorFactory being used by the implementation to provide monitor interfaces for the primordial
     * components.
     *
     * @return the MonitorFactory being used by the bootstrapper
     */
    MonitorFactory getMonitorFactory();

    /**
     * Create a Deployer that can be used to deploy the system definition. This will most likely only support a small
     * subset of the available programming model.
     *
     * @return a new primordial Deployer
     */
    Deployer createDeployer();

    /**
     * Create a Loader for parsing a system definition represented as a XML SCDL file.
     *
     * @param propertyFactory the StAXPropertyFactory to be used to parse property values
     * @param introspector    the introspector to be used to extract component type information from a Java class
     * @return a new prmordial Loader
     */
    Loader createLoader(PropertyObjectFactory propertyFactory, Introspector introspector);

    /**
     * Create a ScopeRegistry that supports the Scopes supported for primordial components
     *
     * @return a new primordial ScopeRegistry
     */
    ScopeRegistry createScopeRegistry();

    /**
     * Create a Connector that can wire together primordial components.
     *
     * @return a new primordial Connector
     */
    Connector getConnector();

    /**
     * Returns the AutowireResolver that resolves autowire targets
     *
     * @return the AutowireResolver that resolves autowire targets
     */
    AutowireResolver getAutowireResolver();

}
