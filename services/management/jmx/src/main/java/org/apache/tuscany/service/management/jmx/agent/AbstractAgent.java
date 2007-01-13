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
package org.apache.tuscany.service.management.jmx.agent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

/**
 * Abstract super class for all the agents.
 * @version $Revison$ $Date$
 *
 */
public abstract class AbstractAgent implements Agent {

    /** Root domain */
    private static final String DOMAIN = "tuscany";

    /** MBean server to use. */
    private MBeanServer mBeanServer;
    
    /** Start flag. */
    private AtomicBoolean started = new AtomicBoolean();
    
    /** RMI connector adaptor. */
    private JMXConnectorServer connectorServer;

    /**
     * Initialies the server.
     * @throws ManagementException If unable to start the agent.
     */
    protected AbstractAgent() throws ManagementException {        
        mBeanServer = MBeanServerFactory.createMBeanServer(DOMAIN);        
    }
    
    /**
     * @see org.apache.tuscany.service.management.jmx.agent.Agent#getMBeanServer()
     */
    public MBeanServer getMBeanServer() {
        return mBeanServer;
    }

    /**
     * @see org.apache.tuscany.service.management.jmx.agent.Agent#register(java.lang.Object, java.lang.String)
     */
    public final void register(Object instance, String name) throws ManagementException {
        
        try {
            mBeanServer.registerMBean(instance, new ObjectName(name));
        } catch (Exception ex) {
            throw new ManagementException(ex);
        }
        
    }

    /**
     * @see org.apache.tuscany.service.management.jmx.agent.Agent#start()
     */
    public final void start() throws ManagementException {

        try {
            
            if(started.get()) {
                throw new IllegalArgumentException("Agent already started");
            }
            
            preStart();
            
            JMXServiceURL url = getAdaptorUrl();
            connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mBeanServer);
            
            connectorServer.start();
            
            started.set(true);
            
        } catch (MalformedURLException ex) {
            throw new ManagementException(ex);
        } catch (IOException ex) {
            throw new ManagementException(ex);
        }

    }

    /**
     * @see org.apache.tuscany.service.management.jmx.agent.Agent#shutdown()
     */
    public final void shutdown() throws ManagementException {
        
        try {
            
            if(!started.get()) {
                throw new IllegalArgumentException("Agent not started");
            }
            
            connectorServer.stop();
            postStop();
            started.set(false);
            
        } catch (IOException ex) {
            throw new ManagementException(ex);
        }
        
    }
    
    /**
     * Gets the underlying MBean server.
     * @return A reference to the mbean server.
     */
    protected MBeanServer getMbeanServer() {
        return mBeanServer;
    }
    
    /**
     * Gets the adaptor URL.
     * @return Adaptor URL.
     */
    protected abstract JMXServiceURL getAdaptorUrl();
    
    /**
     * Any initialiation required for protocol specific agent.
     *
     */
    protected abstract void preStart();
    
    /**
     * Any initialiation required for protocol specific agent.
     *
     */
    protected abstract void postStop();

}
