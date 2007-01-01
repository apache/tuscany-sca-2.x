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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.MBeanServer;

import org.apache.tuscany.host.RuntimeInfo;
import org.apache.tuscany.host.runtime.InitializationException;
import org.apache.tuscany.host.runtime.ShutdownException;
import org.apache.tuscany.host.runtime.TuscanyRuntime;
import org.apache.tuscany.host.util.LaunchHelper;
import org.apache.tuscany.standalone.server.management.jmx.Agent;
import org.apache.tuscany.standalone.server.management.jmx.RmiAgent;

import org.apache.tuscany.core.services.management.jmx.runtime.JmxRuntimeInfoImpl;

/**
 * This class provides the commandline interface for starting the 
 * tuscany standalone server. 
 * 
 * <p>
 * The class boots the tuscany server and also starts a JMX server 
 * and listens for shutdown command. The server itself is available 
 * by the object name <code>tuscany:type=server,name=tuscanyServer
 * </code>. It also allows a runtime to be booted given a bootpath. 
 * The JMX domain in which the runtime is registered si definied in 
 * the file <code>$bootPath/etc/runtime.properties</code>. The properties
 * defined are <code>jmx.domain</code> and <code>offline</code>.
 * </p>
 * 
 * <p>
 * The install directory can be specified using the system property 
 * <code>tuscany.installDir</code>. If not specified it is asumed to 
 * be the directory from where the JAR file containing the main class 
 * is loaded. 
 * </p>
 * 
 * <p>
 * The administration port can be specified using the system property
 * <code>tuscany.adminPort</tuscany>.If not specified the default port 
 * that is used is <code>1099</code>
 * 
 * @version $Rev$ $Date$
 *
 */
public class TuscanyServer implements TuscanyServerMBean {

    /** Agent */
    private final Agent agent;

    /** Install directory */
    private final File installDirectory;

    /** Base Url */
    private final URL baseUrl;

    /** Started runtimes. */
    private final Map<String, TuscanyRuntime> bootedRuntimes = new ConcurrentHashMap<String, TuscanyRuntime>();

    /**
     * 
     * @param args Commandline arguments.
     */
    public static void main(String[] args) throws Exception {
        new TuscanyServer().start();
    }

    /**
     * Constructor initializes all the required classloaders.
     * @throws MalformedURLException 
     *
     */
    private TuscanyServer() throws MalformedURLException {
        installDirectory = DirectoryHelper.getInstallDirectory();
        baseUrl = installDirectory.toURI().toURL();
        agent = RmiAgent.getInstance();
    }

    /**
     * @see org.apache.tuscany.standalone.server.TuscanyServerMBean#startRuntime(java.lang.String, boolean)
     */
    public final void startRuntime(final String bootPath, final boolean online, final String managementDomain) {

        try {

            final File bootDirectory = DirectoryHelper.getBootDirectory(installDirectory, bootPath);

            final MBeanServer mBeanServer = agent.getMBeanServer();
            final RuntimeInfo runtimeInfo = new JmxRuntimeInfoImpl(baseUrl, installDirectory, online, mBeanServer, managementDomain);

            final ClassLoader hostClassLoader = ClassLoader.getSystemClassLoader();
            final ClassLoader bootClassLoader = getTuscanyClassLoader(bootDirectory);

            final URL systemScdl = getSystemScdl(bootClassLoader);
            if (systemScdl == null) {
                throw new TuscanyServerException("Unable to find system scdl");
            }

            final String className =
                System.getProperty("tuscany.launcherClass",
                                   "org.apache.tuscany.runtime.standalone.jmx.JmxRuntimeImpl");
            final TuscanyRuntime runtime = (TuscanyRuntime)Beans.instantiate(bootClassLoader, className);
            runtime.setMonitorFactory(runtime.createDefaultMonitorFactory());
            runtime.setSystemScdl(systemScdl);
            runtime.setHostClassLoader(hostClassLoader);

            runtime.setRuntimeInfo(runtimeInfo);
            runtime.initialize();

            bootedRuntimes.put(bootPath, runtime);

        } catch (InitializationException ex) {
            ex.printStackTrace();
            throw new TuscanyServerException(ex);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new TuscanyServerException(ex);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            throw new TuscanyServerException(ex);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TuscanyServerException(ex);
        }

        System.err.println("Started");

    }

    /**
     * @see org.apache.tuscany.standalone.server.TuscanyServerMBean#shutdownRuntime(java.lang.String)
     */
    public final void shutdownRuntime(String bootPath) {

        try {
            TuscanyRuntime runtime = bootedRuntimes.get(bootPath);
            if (runtime != null) {
                runtime.destroy();
                bootedRuntimes.remove(runtime);
                runtime = null;
            }
        } catch (ShutdownException ex) {
            throw new TuscanyServerException(ex);
        }

    }

    /**
     * Starts the server.
     *
     */
    public final void shutdown() {

        for (String bootPath : bootedRuntimes.keySet()) {
            shutdownRuntime(bootPath);
        }
        agent.shutdown();
        System.err.println("Shutdown");
        System.exit(0);

    }

    /**
     * Gets the tuscany classloader.
     * @param bootDir Boot directory.
     * @return Tuscany classloader.
     */
    private ClassLoader getTuscanyClassLoader(File bootDir) {
        URL[] urls = LaunchHelper.scanDirectoryForJars(bootDir);
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

    /**
     * Starts the server and starts the JMX agent.
     *
     */
    private void start() {
        agent.start();
        agent.register(this, "tuscany:type=server,name=tuscanyServer");
    }

}
