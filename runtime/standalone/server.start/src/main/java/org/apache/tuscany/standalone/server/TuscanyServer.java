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

import java.beans.Beans;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import org.apache.tuscany.host.runtime.InitializationException;
import org.apache.tuscany.host.runtime.ShutdownException;
import org.apache.tuscany.host.runtime.TuscanyRuntime;
import org.apache.tuscany.host.util.LaunchHelper;
import org.apache.tuscany.runtime.standalone.StandaloneRuntimeInfo;
import org.apache.tuscany.runtime.standalone.StandaloneRuntimeInfoImpl;
import org.apache.tuscany.standalone.server.management.jmx.Agent;
import org.apache.tuscany.standalone.server.management.jmx.RmiAgent;
import org.osoa.sca.SCA;

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
 * that is used is <code>1099</code>
 * 
 * 
 * 
 * @version $Rev$ $Date$
 *
 */
public class TuscanyServer implements TuscanyServerMBean {
    
    /** Agent */
    private Agent agent = RmiAgent.getInstance();
    
    /** Context */
    private SCA context;
    
    /** Runtime */
    private TuscanyRuntime runtime;
    
    /**
     * 
     * @param args Commandline arguments.
     */
    public static void main(String[] args) throws Exception {         
        TuscanyServer tuscanyServer = new TuscanyServer();
        tuscanyServer.start();
    }
    
    /**
     * Constructor initializes all the required classloaders.
     *
     */
    private TuscanyServer() {
        
        agent.start();
        agent.register(this, "tuscanyServer");
        
    }
    
    /**
     * Starts the server.
     *
     */
    public void start() {
        
        try {
            
            File installDirectory = DirectoryHelper.getInstallDirectory();
            URL baseUrl = installDirectory.toURI().toURL();
            File bootDirectory = DirectoryHelper.getBootDirectory(installDirectory);boolean online = !Boolean.parseBoolean(System.getProperty("offline", Boolean.FALSE.toString()));
            StandaloneRuntimeInfo runtimeInfo = new StandaloneRuntimeInfoImpl(baseUrl, installDirectory, installDirectory, online);

            ClassLoader hostClassLoader = ClassLoader.getSystemClassLoader();
            ClassLoader bootClassLoader = getTuscanyClassLoader(bootDirectory);
            
            URL systemScdl = getSystemScdl(bootClassLoader);

            String className = System.getProperty("tuscany.launcherClass",
                "org.apache.tuscany.runtime.standalone.host.StandaloneRuntimeImpl");
            runtime = (TuscanyRuntime) Beans.instantiate(bootClassLoader, className);
            runtime.setMonitorFactory(runtime.createDefaultMonitorFactory());
            runtime.setSystemScdl(systemScdl);
            runtime.setHostClassLoader(hostClassLoader);
            
            runtime.setRuntimeInfo(runtimeInfo);
            runtime.initialize();
            context = runtime.getContext();

            context.start();
            
        } catch (IOException ex) {
            throw new TuscanyServerException(ex);
        } catch (ClassNotFoundException ex) {
            throw new TuscanyServerException(ex);
        } catch (InitializationException ex) {
            throw new TuscanyServerException(ex);
        }
        
        System.err.println("Started");
        
    }
    
    /**
     * Starts the server.
     *
     */
    public void shutdown() {        
        
        try {
            context.stop();
            runtime.destroy();
            agent.shutdown();
        } catch (ShutdownException ex) {
            throw new TuscanyServerException(ex);
        }
        System.err.println("Shutdown");
    }

    /**
     * Gets the tuscany classloader.
     * @param bootDir Boot directory.
     * @return Tuscany classloader.
     */
    private ClassLoader getTuscanyClassLoader(File bootDir) {
        URL[] urls = LaunchHelper.scanDirectoryForJars(bootDir);
        System.err.println(Arrays.asList(urls));
        return new URLClassLoader(urls, getClass().getClassLoader());
    }

    /**
     * Gets the system SCDL.
     * @param bootClassLoader Boot classloader.
     * @return URL to the system SCDL.
     */
    private URL getSystemScdl(ClassLoader bootClassLoader) {
        String resource = System.getProperty("tuscany.systemScdlPath", "META-INF/tuscany/system.scdl");
        return bootClassLoader.getResource(resource);
    }

}
