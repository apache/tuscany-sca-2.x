/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.bootstrap;

import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.StAXPropertyFactory;
import org.apache.tuscany.spi.monitor.MonitorFactory;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.core.implementation.Introspector;

/**
 * @version $Rev$ $Date$
 */
public interface Bootstrapper {
    MonitorFactory getMonitorFactory();

    Deployer createDeployer();

    LoaderRegistry createLoader(StAXPropertyFactory propertyFactory, Introspector introspector);

    Introspector createIntrospector();

    BuilderRegistry createBuilder(ScopeRegistry scopeRegistry);

    Connector createConnector();

    ScopeRegistry createScopeRegistry(WorkContext workContext);
}
