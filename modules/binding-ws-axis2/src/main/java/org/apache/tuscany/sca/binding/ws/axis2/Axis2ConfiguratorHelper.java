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

package org.apache.tuscany.sca.binding.ws.axis2;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.deployment.URLBasedAxisConfigurator;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * The Helper class that loades the Axis2 configuration from the axis2.xml
 */
public class Axis2ConfiguratorHelper {

    /**
     * We cannot hold this method directly in the {@link TuscanyAxisConfigurator} class as the super class
     * uses the TCCL to load classes
     * 
     * @param isRampartRequired
     * @return
     */
    public static ConfigurationContext getAxis2ConfigurationContext(final boolean isRampartRequired) {
        try {
            // TuscanyAxisConfigurator tuscanyAxisConfigurator = new TuscanyAxisConfigurator();
            // Allow privileged access to read properties. Requires PropertyPermission read in
            // security policy.
            ConfigurationContext configContext =
                AccessController.doPrivileged(new PrivilegedExceptionAction<ConfigurationContext>() {
                    public ConfigurationContext run() throws AxisFault {
                        // The tuscany-binding-ws-axis2 class loader. We contribute the message
                        // receivers in the axis2.xml
                        ClassLoader cl0 = getClass().getClassLoader();
                        
                        // The axis2 class loader
                        ClassLoader cl1 = URLBasedAxisConfigurator.class.getClassLoader();
                        
                        ClassLoader tccl = ServiceDiscovery.getInstance().setContextClassLoader(cl0, cl1);
                        
                        try {
                            return new TuscanyAxisConfigurator(isRampartRequired).getConfigurationContext();
                        } finally {
                            if (tccl != null) {
                                Thread.currentThread().setContextClassLoader(tccl);
                            }
                        }
                    }
                });
            return configContext;
            // deployRampartModule();
            // configureSecurity();
        } catch (PrivilegedActionException e) {
            throw new ServiceRuntimeException(e.getException());
        }
    }

}
