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

import org.apache.tuscany.host.AbstractRuntimeInfo;

/**
 * Implementation for the <code>JmxRuntimeInfo</code> interface.
 *
 * @version $Revision$ $Date$
 */
public class JmxRuntimeInfoImpl extends AbstractRuntimeInfo implements JmxRuntimeInfo {

    /**
     * MBean server reference.
     */
    private final MBeanServer mBeanServer;

    /**
     * Management domain.
     */
    private final String managementDomain;

    /**
     * Initializes the runtime info instance.
     *
     * @param baseUrl          Base Url.
     * @param installDirectory Install directory.
     * @param online           Onlne indicator.
     * @param mBeanServer      MBean server.
     * @param managementDomain Management doamin for the runtime.
     */
    public JmxRuntimeInfoImpl(final URL baseUrl,
                              final File installDirectory,
                              final boolean online,
                              final MBeanServer mBeanServer,
                              final String managementDomain) {
        super(null, baseUrl, installDirectory, online);
        this.mBeanServer = mBeanServer;
        this.managementDomain = managementDomain;
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
    public final String getManagementDomain() {
        return managementDomain;
    }

}
