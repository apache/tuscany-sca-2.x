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
package org.apache.tuscany.core.system.context;

import org.apache.tuscany.common.monitor.MonitorFactory;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.ConfigurationContext;

/**
 * Represents a top-level component context in the runtime, that is the bootstrap context
 * 
 * @version $Rev$ $Date$
 */
public interface RuntimeContext extends AutowireContext, ConfigurationContext {

    /* the symbolic name of the runtime bootstrap context */
    public static final String RUNTIME = "tuscany.runtime";

    /* the symbolic name of the root aggregate context containing all system components in the runtime */
    public static final String SYSTEM = "tuscany.system";

    /* the symbolic name of the root aggregate context containing all components in the runtime */
    public static final String ROOT = "tuscany.root";

    /**
     * Adds a configuration builder to the runtime
     */
    public void addBuilder(RuntimeConfigurationBuilder builder);

    /**
     * Returns the root component context. The root context contains all user components in the runtime
     */
    public AggregateContext getRootContext();

    /**
     * Returns the system component context.
     */
    public AggregateContext getSystemContext();

    /**
     * Returns the monitor factory in use by the runtime
     */
    public MonitorFactory getMonitorFactory();

}
