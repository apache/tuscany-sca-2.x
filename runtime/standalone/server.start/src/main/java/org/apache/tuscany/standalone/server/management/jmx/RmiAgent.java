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
import java.util.concurrent.atomic.AtomicBoolean;

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
public class RmiAgent implements Agent {

    /** Administration port system property. */
    private static final String ADMIN_PORT_PROPERTY = "tuscany.adminPort";

    /** Default admin port. */
    private static final int DEFAULT_ADMIN_PORT = 1099;

    /** Instance */
    private static final Agent INSTANCE = new RmiAgent();

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
    
    /** Start flag. */
    private AtomicBoolean started = new AtomicBoolean();

    /**
     * Initialies the server.
     * @throws ManagementException If unable to start the agent.
     */
    private RmiAgent() throws ManagementException {
        
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

    /* (non-Javadoc)
     * @see org.apache.tuscany.standalone.server.management.jmx.Agent#register(java.lang.Object, java.lang.String)
     */
    public synchronized void register(Object instance, String name) throws ManagementException {
        try {
            mBeanServer.registerMBean(instance, new ObjectName("tuscany:name=TuscanyServer"));
        } catch (Exception ex) {
            throw new ManagementException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.standalone.server.management.jmx.Agent#start()
     */
    public void start() throws ManagementException {

        try {
            
            if(started.get()) {
                throw new IllegalArgumentException("Agent already started");
            }
            
            registry = LocateRegistry.createRegistry(port);
            connectorServer.start();
            
            started.set(true);
            
        } catch (MalformedURLException ex) {
            throw new ManagementException(ex);
        } catch (IOException ex) {
            throw new ManagementException(ex);
        }

    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.standalone.server.management.jmx.Agent#shutdown()
     */
    public void shutdown() throws ManagementException {
        
        try {
            
            if(!started.get()) {
                throw new IllegalArgumentException("Agent not started");
            }
            
            connectorServer.stop();
            UnicastRemoteObject.unexportObject(registry, true);
            
        } catch (IOException ex) {
            throw new ManagementException(ex);
        }
        
    }

}
