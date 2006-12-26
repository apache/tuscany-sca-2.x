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

import java.io.File;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

/**
 * This class provides the commandline interface for starting the 
 * tuscany standalone server. 
 * 
 * <p>
 * The class boots the tuscany runtime and delegates the deployment 
 * of application composites to the runtime. This also starts a JMX 
 * server and listens for shutdown command.
 * </p>
 * 
 * <p>
 * The install directory can be specified using the system property 
 * <code>tuscany.installDir</code>. If not specified it is asumed to 
 * be the directory from where the JAR file containing the main class 
 * is loaded. All the boot libraries are expected to be in the 
 * <code>boot</code> directory under the install directory.
 * </p>
 * 
 * <p>
 * The administration port can be specified using the system property
 * <code>tuscany.adminPort</tuscany>.If not specified the default port 
 * that is used is <code>1066</code>
 * 
 * 
 * 
 * @version $Rev$ $Date$
 *
 */
public class TuscanyServer implements TuscanyServerMBean {
    
    /** Administration port system property. */
    private static final String ADMIN_PORT_PROPERTY = "tuscany.adminPort";
    
    /** Default admin port. */
    private static final int DEFAULT_ADMIN_PORT = 1066;
    
    /**
     * Constructor initializes all the required classloaders.
     *
     */
    private TuscanyServer() { 
        
        File installDirectory = DirectoryHelper.getInstallDirectory();
        File bootDirectory = DirectoryHelper.getBootDirectory(installDirectory);
        
    }
    
    /**
     * 
     * @param args Commandline arguments.
     */
    public static void main(String[] args) throws Exception { 
        
        TuscanyServer tuscanyServer = TuscanyServer.newInstance();
        tuscanyServer.start();
        
        MBeanServer mBeanServer = MBeanServerFactory.createMBeanServer("tuscany");
        mBeanServer.registerMBean(tuscanyServer, new ObjectName("tuscany:name=TuscanyServer"));

    }
    
    /**
     * Creates an instance of the server.
     * @return An instance of the tuscany server.
     */
    protected static TuscanyServer newInstance() {
        return new TuscanyServer();
    }
    
    /**
     * Starts the server.
     *
     */
    public void start() {
        System.err.println("Started");
    }
    
    /**
     * Starts the server.
     *
     */
    public void shutdown() {
        System.err.println("shutdown");
    }

}
