/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.runtime;

import org.apache.tuscany.common.monitor.MonitorFactory;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.builder.WireBuilder;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.ConfigurationContext;
import org.apache.tuscany.core.context.SystemAggregateContext;
import org.apache.tuscany.model.scdl.loader.SCDLModelLoader;

/**
 * Represents a top-level component context in the runtime, that is the bootstrap context.
 * This context serves as the ultimate root of the context hierarchy. Under it are two
 * separate trees: the rootContext for user components and the systemContext for
 * system components (those that comprise the runtime itself).
 *
 * @version $Rev$ $Date$
 */
public interface RuntimeContext extends AutowireContext, ConfigurationContext {

    /* the symbolic name of the runtime bootstrap context */
    public static final String RUNTIME = "tuscany.runtime";

    /* the symbolic name of the aggregate context containing all system components in the runtime */
    public static final String SYSTEM = "tuscany.system";

    /* the symbolic name of the aggregate context containing all user components in the runtime */
    public static final String ROOT = "tuscany.root";

    /**
     * Returns the context that forms the root of the user component tree.
     * All user components will managed by contexts that are children of this root.
     * @return the root of the user component tree
     */
    public AggregateContext getRootContext();

    /**
     * Returns the context that forms the root of the system component tree.
     * All system components, components that provide system services needed by the
     * Tuscany runtime itself, will be managed by contexts that are children of this root.
     * @return the root of the system component tree
     */
    public SystemAggregateContext getSystemContext();

    /**
     * Adds a configuration builder to the runtime
     */
    @Deprecated
    public void addBuilder(RuntimeConfigurationBuilder builder);

    /**
     * Adds a wire builder to the runtime
     */
    @Deprecated
    public void addBuilder(WireBuilder builder);

    /**
     * Adds an SCDL model loader to the runtime
     */
    public void addLoader(SCDLModelLoader loader);

    /**
     * Returns the monitor factory in use by the runtime
     */
    public MonitorFactory getMonitorFactory();

}
