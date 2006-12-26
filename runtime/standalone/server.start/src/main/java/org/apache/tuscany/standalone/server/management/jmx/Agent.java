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

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

/**
 * Utility for starting the JMX server.
 * 
 * @version $Revsion$ $Date$
 *
 */
public class Agent {

    /** Administration port system property. */
    private static final String ADMIN_PORT_PROPERTY = "tuscany.adminPort";

    /** Default admin port. */
    private static final int DEFAULT_ADMIN_PORT = 1099;

    /** Instance */
    private static final Agent INSTANCE = new Agent();

    /** Root domain */
    private static final String DOMAIN = "tuscany";

    /** MBean server to use. */
    private MBeanServer mBeanServer;
    
    /** RMI connector adaptor. */
    private JMXConnectorServer connectorServer;
    
    /** RMI registry. */
    private Registry registry;
    
    /** Listen port */
    private int port = DEFAULT_ADMIN_PORT;

    /**
     * Initialies the server.
     * @throws ManagementException If unable to start the agent.
     */
    private Agent() throws ManagementException {
        
        try {
            
            String portValue = System.getProperty(ADMIN_PORT_PROPERTY);
            if(portValue != null) {
                port = Integer.parseInt(portValue);
            }
            
            mBeanServer = MBeanServerFactory.createMBeanServer(DOMAIN);
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:" + port + "/server");
            
            connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mBeanServer);
            
        } catch (MalformedURLException ex) {
            throw new ManagementException(ex);
        } catch (IOException ex) {
            throw new ManagementException(ex);
        }
        
    }

    /**
     * Returns the singleton agent instance.
     * @return Agent instance.
     * @throws ManagementException If unable to start the agent.
     */
    public static Agent getInstance() throws ManagementException {
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

        try {
            registry = LocateRegistry.createRegistry(port);
            connectorServer.start();
        } catch (MalformedURLException ex) {
            throw new ManagementException(ex);
        } catch (IOException ex) {
            throw new ManagementException(ex);
        }

    }

    /**
     * Shuts down the JMX server.
     * @throws ManagementException If unable to shutdown the server.
     *
     */
    public void shutdown() throws ManagementException {
        try {
            connectorServer.stop();
            UnicastRemoteObject.unexportObject(registry, true);
        } catch (IOException ex) {
            throw new ManagementException(ex);
        }
    }

}
