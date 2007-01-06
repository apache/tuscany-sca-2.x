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
package org.apache.tuscany.runtime.standalone.jmx.info;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.management.MBeanServer;

import org.apache.tuscany.runtime.standalone.DirectoryHelper;
import org.apache.tuscany.runtime.standalone.StandaloneRuntimeInfoImpl;
import org.apache.tuscany.runtime.standalone.jmx.JmxHostException;
import org.apache.tuscany.runtime.standalone.jmx.management.JmxException;

/**
 * Implementation for the <code>JmxRuntimeInfo</code> interface.
 *
 * @version $Revision$ $Date$
 */
public class JmxRuntimeInfoImpl extends StandaloneRuntimeInfoImpl implements JmxRuntimeInfo {

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
     * @param profileName              the runtime's profile name
     * @param installDirectory         directory containing the standalone installation
     * @param profileDirectory         directory containing this runtime's profile
     * @param online                   true if this runtime should consider itself online
     * @param properties               properties for this runtime
     * @param mBeanServer              mbean server.
     * @param managementDomain         management domain for the runtime.
     */
    private JmxRuntimeInfoImpl(final String profileName,
                               final File installDirectory,
                               final File profileDirectory,
                               final boolean online,
                               final Properties properties,
                               final MBeanServer mBeanServer,
                               final String managementDomain) {
        super(profileName, installDirectory, profileDirectory, null, online, properties);
        this.mBeanServer = mBeanServer;
        this.managementDomain = managementDomain;
    }
    
    /**
     * Initializes the runtime info instance.
     *
     * @param profileName              the runtime's profile name
     * @param installDirectory         directory containing the standalone installation
     * @param online                   true if this runtime should consider itself online
     * @param mBeanServer              mbean server.
     * @param managementDomain         management domain for the runtime.
     */
    public static JmxRuntimeInfoImpl newInstance(String profileName,
                              File installDirectory,
                              boolean online,
                              final MBeanServer mBeanServer,
                              final String managementDomain)
    throws JmxException {
        
        // TODO This logic should move to super class's constructor
        File profileDirectory;
        try {
            profileDirectory = DirectoryHelper.getProfileDirectory(installDirectory, profileName);
            
            // Load properties for this runtime
            File propFile = new File(profileDirectory, "etc/runtime.properties");
            Properties props = DirectoryHelper.loadProperties(propFile, System.getProperties());
            
            return new JmxRuntimeInfoImpl(profileName, installDirectory, profileDirectory, online, props, mBeanServer, managementDomain);
        } catch (IOException ex) {
            throw new JmxHostException(ex);
        }
        
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
