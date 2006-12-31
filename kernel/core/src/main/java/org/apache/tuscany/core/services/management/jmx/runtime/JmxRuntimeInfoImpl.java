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
package org.apache.tuscany.core.services.management.jmx.runtime;

import java.io.File;
import java.net.URL;

import javax.management.MBeanServer;

import org.apache.tuscany.core.services.management.jmx.runtime.JmxRuntimeInfo;
import org.apache.tuscany.host.AbstractRuntimeInfo;

/**
 * Implementation for the <code>JmxRuntimeInfo</code> interface.
 * 
 * @version $Revision$ $Date: 2006-12-31 20:23:49 +0000 (Sun, 31 Dec
 *          2006) $
 */
public class JmxRuntimeInfoImpl extends AbstractRuntimeInfo implements JmxRuntimeInfo {

    /**
     * MBean server reference.
     */
    private final MBeanServer mBeanServer;

    /**
     * Default domain.
     */
    private final String defaultDomain;

    /**
     * Initializes the runtime info instance.
     * 
     * @param applicationRootDirectory Application root directory.
     * @param baseUrl Base Url.
     * @param installDirectory Install directory.
     * @param online Onlne indicator.
     * @param mBeanServer MBean server.
     */
    public JmxRuntimeInfoImpl(final File applicationRootDirectory,
                              final URL baseUrl,
                              final File installDirectory,
                              final boolean online,
                              final MBeanServer mBeanServer,
                              final String defaultDomain) {
        super(applicationRootDirectory, baseUrl, installDirectory, online);
        this.mBeanServer = mBeanServer;
        this.defaultDomain = defaultDomain;
    }

    /**
     * @see org.apache.tuscany.core.services.management.jmx.runtime.JmxRuntimeInfo#getMBeanServer()
     */
    public final MBeanServer getMBeanServer() {
        return mBeanServer;
    }

    /**
     * Returns the default domain used by the host.
     * 
     * @return Default domain used by the host.
     */
    public final String getDefaultDomain() {
        return defaultDomain;
    }

}
