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
package org.apache.tuscany.standalone.server.management.jmx;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

/**
 * Utility for starting the JMX server.
 * 
 * @version $Revsion$ $Date$
 *
 */
public class Agent {
    
    /** Instance */
    private static Agent INSTANCE = null;
    
    /** Root domain */
    private static final String DOMAIN = "tuscany";
    
    /** MBean server to use. */
    private MBeanServer mBeanServer;
    
    /**
     * Initialies the server.
     * @param port Management port.
     * @throws ManagementException If unable to start the agent.
     */
    private Agent(int port) throws ManagementException {
        mBeanServer = MBeanServerFactory.createMBeanServer(DOMAIN);
    }
    
    /**
     * Returns the singleton agent instance.
     * @return Agent instance.
     * @throws ManagementException If unable to start the agent.
     */
    public synchronized static Agent getInstance(int port) throws ManagementException {
        if(INSTANCE == null) {
            INSTANCE = new Agent(port);
        }
        return INSTANCE;
    }
    
    /**
     * Registers a managed bean.
     * @param instance Instance to be registered.
     * @param name Object name of the instance.
     * @throws ManagementException If unable to register the object.
     */
    public synchronized void register(Object instance, String name) throws ManagementException {
        try {
            mBeanServer.registerMBean(instance, new ObjectName("tuscany:name=TuscanyServer"));
        } catch (Exception ex) {
            throw new ManagementException(ex);
        }
    }
    
    /**
     * Starts the JMX server.
     * @throws ManagementException If unable to start the server.
     *
     */
    public void start() throws ManagementException {
    }
    
    /**
     * Shuts down the JMX server.
     * @throws ManagementException If unable to shutdown the server.
     *
     */
    public void shutdown() throws ManagementException {
    }

}
