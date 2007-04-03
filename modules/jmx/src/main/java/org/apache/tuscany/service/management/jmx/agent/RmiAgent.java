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
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.management.remote.JMXServiceURL;

/**
 * Utility for starting the JMX server with an RMI agent.
 * 
 * @version $Revsion$ $Date$
 *
 */
public class RmiAgent extends AbstractAgent {

    /** Administration port system property. */
    private static final String ADMIN_PORT_PROPERTY = "tuscany.adminPort";

    /** Default admin port. */
    private static final int DEFAULT_ADMIN_PORT = 1099;

    /** Instance */
    private static final Agent INSTANCE = new RmiAgent();
    
    /** RMI registry. */
    private Registry registry;
    
    /** Listen port */
    private int port = DEFAULT_ADMIN_PORT;

    /**
     * Gets the adaptor URL.
     * @return Adaptor URL used by the agent.
     * @throws ManagementException If unable to start the agent.
     */
    protected JMXServiceURL getAdaptorUrl() throws ManagementException {
        
        try {
            
            String portValue = System.getProperty(ADMIN_PORT_PROPERTY);
            if(portValue != null) {
                port = Integer.parseInt(portValue);
            }
            
            // service:jmx:rmi:///jndi/rmi://localhost:1099/server
            return new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:" + port + "/server");
            
            
        } catch (MalformedURLException ex) {
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
     * @see org.apache.tuscany.service.management.jmx.agent.AbstractAgent#preStart()
     */
    @Override
    public void preStart() throws ManagementException {

        try {
            
            String portValue = System.getProperty(ADMIN_PORT_PROPERTY);
            if(portValue != null) {
                port = Integer.parseInt(portValue);
            }
            
            registry = LocateRegistry.createRegistry(port);
        } catch (RemoteException ex) {
            throw new ManagementException(ex);
        }

    }

    /**
     * @see org.apache.tuscany.service.management.jmx.agent.AbstractAgent#postStop()
     */
    @Override
    public void postStop() throws ManagementException {
        
        try {
            UnicastRemoteObject.unexportObject(registry, true);            
        } catch (IOException ex) {
            throw new ManagementException(ex);
        }
        
    }

}
