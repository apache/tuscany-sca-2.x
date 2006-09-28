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

import java.net.URL;

import org.apache.tuscany.host.RuntimeInfo;

/**
 * @version $Rev$ $Date$
 */
public interface TuscanyRuntime {
    /**
     * Returns the location of the SCDL used to boot this runtime.
     *
     * @return the location of the SCDL used to boot this runtime
     */
    URL getSystemScdl();

    /**
     * Sets the location of the SCDL used to boot this runtime.
     *
     * @param systemScdl the location of the SCDL used to boot this runtime
     */
    void setSystemScdl(URL systemScdl);

    /**
     * Returns the name of the component associated with the application SCDL.
     * @return the name of the component associated with the application SCDL
     */
    String getApplicationName();

    /**
     * Sets the name of the component associated with the application SCDL.
     * @param applicationName the name of the component associated with the application SCDL
     */
    void setApplicationName(String applicationName);

    /**
     * Returns the location of the default application's SCDL.
     *
     * @return the location of the default application's SCDL
     */
    URL getApplicationScdl();

    /**
     * Sets the location of the default application's SCDL
     *
     * @param applicationScdl the location of the default application's SCDL
     */
    void setApplicationScdl(URL applicationScdl);

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
    RuntimeInfo getRuntimeInfo();

    /**
     * Sets the info this runtime should make available to service components.
     * The instance supplied here should be registered in the system composite with the name
     * {@link RuntimeInfo#COMPONENT_NAME "RuntimeInfo"}.
     *
     * @param runtimeInfo the information this runtime should make available to service components
     */
    void setRuntimeInfo(RuntimeInfo runtimeInfo);

    /**
     * Initialize a runtime.
     *
     */
    void initialize();

    /**
     * Destroy the runtime. Any further invocations should result in an error.
     */
    void destroy();
}
