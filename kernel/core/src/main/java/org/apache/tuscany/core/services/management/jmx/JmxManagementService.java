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
package org.apache.tuscany.core.services.management.jmx;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.tuscany.core.services.management.jmx.instrument.InstrumentedComponent;
import org.apache.tuscany.core.services.management.jmx.runtime.JmxRuntimeInfo;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.services.management.ManagementService;

/**
 * JMX implementation of the management service.
 *
 * @version $Revision$ $Date$
 */
public abstract class JmxManagementService implements ManagementService {
    
    /**
     * MBean server used by the JMX management service.
     */
    private final MBeanServer mBeanServer;
    
    /**
     * Default domain used by the host.
     */
    private final String defaultDomain;

    /**
     * Initializes the MBean server.
     * @param runtimeInfo JMX runtime info.
     */
    public JmxManagementService(@Autowire final JmxRuntimeInfo runtimeInfo) {
        this.mBeanServer = runtimeInfo.getMBeanServer();
        this.defaultDomain = runtimeInfo.getManagementDomain();
    }

    /**
     * @see org.apache.tuscany.spi.services.management.ManagementService#registerComponent(java.lang.String,org.apache.tuscany.spi.component.Component)
     * @throws JmxException In case of an unexpected JMX exception.
     */
    public final void registerComponent(String name, Component component) throws JmxException {
        
        try {
            ObjectName on = new ObjectName(defaultDomain + ":" + "type=component,name=" + name);
            InstrumentedComponent mbean = new InstrumentedComponent(component);            
            mBeanServer.registerMBean(mbean, on);
        } catch (JMException ex) {
            throw new JmxException(ex);
        }
        
    }

}
