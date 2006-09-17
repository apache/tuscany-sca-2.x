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
     * Initialize a runtime.
     *
     */
    void initialize();

    /**
     * Destroy the runtime. Any further invocations should result in an error.
     */
    void destroy();
}
