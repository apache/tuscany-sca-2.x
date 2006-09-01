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
package org.apache.tuscany.host;

import java.net.URL;

import org.osoa.sca.CompositeContext;

import org.apache.tuscany.api.TuscanyException;

/**
 * Interface that allows a host to launch a runtime.
 *
 * @version $Rev$ $Date$
 */
public interface Launcher {
    /**
     * Boot the Tuscany runtime.
     *
     * @param systemScdl the SCDL defining the runtime's system configuration
     * @param systemClassLoader the root classloader to use to deploy the system SCDL
     * @param monitorFactory the monitor factory initialize the runtime with
     * @throws TuscanyException if there was a problem booting the runtimr
     */
    void bootRuntime(URL systemScdl, ClassLoader systemClassLoader, MonitorFactory monitorFactory)
        throws TuscanyException;

    /**
     * Shutdown the Tuscany runtime.
     */
    void shutdownRuntime();

    /**
     * Boot a default application into the runtime.
     *
     * @param applicationScdl the application's SCDL
     * @param applicationClassLoader the classloader to use to deploy the application
     * @return the CompositeContext for the application
     */
    CompositeContext bootApplication(URL applicationScdl, ClassLoader applicationClassLoader);
}
