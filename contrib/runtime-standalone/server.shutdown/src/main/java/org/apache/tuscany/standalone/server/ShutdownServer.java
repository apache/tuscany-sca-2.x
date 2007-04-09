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
package org.apache.tuscany.standalone.server;

import java.io.IOException;

import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnector;

/**
 * 
 * @version $Rev$ $Date$
 *
 */
public class ShutdownServer {
    
    /** Tuscany admin host. */
    private static final String ADMIN_HOST_PROPERTY = "tuscany.adminHost";
    
    /** Tuscany admin port. */
    private static final String ADMIN_PORT_PROPERTY = "tuscany.adminPort";
    
    /** Default host. */
    private static final String DEFAULT_ADMIN_HOST = "localhost";
    
    /** Default port. */
    private static final int DEFAULT_ADMIN_PORT = 1099;
    
    /** Host. */
    private String host = DEFAULT_ADMIN_HOST;
    
    /** Port. */
    private int port = DEFAULT_ADMIN_PORT;
    
    /**
     * 
     * @param args Commandline arguments.
     */
    public static void main(String[] args) throws Exception {
        
        ShutdownServer shutdownServer = new ShutdownServer();
        shutdownServer.shutdown();
        
    }
    
    /**
     * Initializes the host and the port.
     *
     */
    private ShutdownServer() {
        
        if(System.getProperty(ADMIN_HOST_PROPERTY) != null) {
            host = System.getProperty(ADMIN_HOST_PROPERTY);
        }
        
        if(System.getProperty(ADMIN_PORT_PROPERTY) != null) {
            port = Integer.parseInt(System.getProperty(ADMIN_PORT_PROPERTY));
        }
        
    }
    
    /**
     * Shuts down the server.
     * @throws IOException 
     * @throws JMException
     *
     */
    private void shutdown() throws IOException, JMException {
        
        RMIConnector rmiConnector = null;
        
        try {
            
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/server");
            rmiConnector = new RMIConnector(url, null);
            rmiConnector.connect();
            
            MBeanServerConnection con = rmiConnector.getMBeanServerConnection();
            con.invoke(new ObjectName("tuscany:name=tuscanyServer"), "shutdown", null, null);
            
        } finally {
            if(rmiConnector != null) {
                rmiConnector.close();
            }
        }
        
    }

}
