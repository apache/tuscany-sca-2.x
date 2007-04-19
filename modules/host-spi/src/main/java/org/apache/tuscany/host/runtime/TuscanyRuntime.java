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
package org.apache.tuscany.host.runtime;

import java.net.URI;

import org.apache.tuscany.host.MonitorFactory;
import org.apache.tuscany.host.RuntimeInfo;
import org.apache.tuscany.host.management.ManagementService;
import org.osoa.sca.ComponentContext;

/**
 * @version $Rev$ $Date$
 */
public interface TuscanyRuntime<I extends RuntimeInfo> {
    /**
     * Returns the host ClassLoader that is parent to all Tuscany classloaders.
     *
     * @return the host's ClassLoader
     */
    ClassLoader getHostClassLoader();

    /**
     * Sets the host ClassLoader; this will be a parent for all Tuscany classloaders.
     *
     * @param classLoader the host's ClassLoader
     */
    void setHostClassLoader(ClassLoader classLoader);

    /**
     * Returns the info this runtime will make available to service components.
     *
     * @return the info this runtime will make available to service components
     */
    I getRuntimeInfo();

    /**
     * Sets the info this runtime should make available to service components.
     *
     * @param runtimeInfo the information this runtime should make available to service components
     */
    void setRuntimeInfo(I runtimeInfo);

    /**
     * Returns the MonitorFactory that this runtime is using.
     *
     * @return the MonitorFactory that this runtime is using
     */
    MonitorFactory getMonitorFactory();

    /**
     * Sets the MonitorFactory that this runtime should use.
     *
     * @param monitorFactory the MonitorFactory that this runtime should use
     */
    void setMonitorFactory(MonitorFactory monitorFactory);

    /**
     * Sets the ManagementService that this runtime should use.
     *
     * @param managementService the ManagementService that this runtime should use
     */
    void setManagementService(ManagementService<?> managementService);

    /**
     * Returns the ManagementService that this runtime is using.
     *
     * @return the ManagementService that this runtime is using
     */
    ManagementService<?> getManagementService();

    /**
     * Initialize a runtime.
     *
     * @throws InitializationException if there is an error initializing the runtime
     */
    void initialize() throws InitializationException;

    /**
     * Destroy the runtime. Any further invocations should result in an error.
     *
     * @throws ShutdownException if there is an error destroying the runtime
     */
    void destroy() throws ShutdownException;

    /**
     * Returns the ComponentContext for the designated component.
     *
     * @param componentId the id of the component whose context should be returned
     * @return the ComponentContext for the designated component
     */
    ComponentContext getComponentContext(URI componentId);
}
