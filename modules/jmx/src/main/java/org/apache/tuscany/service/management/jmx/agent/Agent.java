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

import javax.management.MBeanServer;

/**
 * Interface to a JMX agent.
 * @version $Revision$ $Date$
 *
 */
public interface Agent {

    /**
     * Registers a managed bean.
     * @param instance Instance to be registered.
     * @param name Object name of the instance.
     * @throws ManagementException If unable to register the object.
     */
    void register(Object instance, String name) throws ManagementException;

    /**
     * Starts the JMX server.
     * @throws ManagementException If unable to start the server.
     */
    void start() throws ManagementException;

    /**
     * Shuts down the JMX server.
     * @throws ManagementException If unable to shutdown the server.
     */
    void shutdown() throws ManagementException;
    
    /**
     * Gets the MBean server used by the agent.
     * @return MBean server used by the agent.
     */
    MBeanServer getMBeanServer();

}
