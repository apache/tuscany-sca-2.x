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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import javax.management.MBeanServer;

import org.apache.tuscany.host.management.ManagementService;
import org.apache.tuscany.host.runtime.InitializationException;
import org.apache.tuscany.host.runtime.ShutdownException;
import org.apache.tuscany.host.runtime.TuscanyRuntime;
import org.apache.tuscany.runtime.standalone.DirectoryHelper;
import org.apache.tuscany.runtime.standalone.StandaloneRuntime;
import org.apache.tuscany.runtime.standalone.StandaloneRuntimeInfo;
import org.apache.tuscany.runtime.standalone.StandaloneRuntimeInfoImpl;
import org.apache.tuscany.service.management.jmx.JmxManagementService;
import org.apache.tuscany.service.management.jmx.agent.Agent;
import org.apache.tuscany.service.management.jmx.agent.RmiAgent;

/**
 * This class provides the commandline interface for starting the
 * tuscany standalone server.
 * <p/>
 * <p/>
 * The class boots the tuscany server and also starts a JMX server
 * and listens for shutdown command. The server itself is available
 * by the object name <code>tuscany:type=server,name=tuscanyServer
 * </code>. It also allows a runtime to be booted given a bootpath.
 * The JMX domain in which the runtime is registered si definied in
 * the file <code>$bootPath/etc/runtime.properties</code>. The properties
 * defined are <code>jmx.domain</code> and <code>offline</code>.
 * </p>
 * <p/>
 * <p/>
 * The install directory can be specified using the system property
 * <code>tuscany.installDir</code>. If not specified it is asumed to
 * be the directory from where the JAR file containing the main class
 * is loaded.
 * </p>
 * <p/>
 * <p/>
 * The administration port can be specified using the system property
 * <code>tuscany.adminPort</tuscany>.If not specified the default port
 * that is used is <code>1099</code>
 *
 * @version $Rev$ $Date$
 */
public class TuscanyServer implements TuscanyServerMBean {

    /**
     * Agent
     */
    private final Agent agent;

    /**
     * Install directory
     */
    private final File installDirectory;

    /**
     * Started runtimes.
     */
    private final Map<String, TuscanyRuntime> bootedRuntimes = new ConcurrentHashMap<String, TuscanyRuntime>();

    /**
     * @param args Commandline arguments.
     */
    public static void main(String[] args) throws Exception {
        TuscanyServer server = new TuscanyServer();
        server.start();
        
        // Start any runtimes specified in the cli
        for(String profile : args) {
            server.startRuntime(profile);
        }
    }

    /**
     * Constructor initializes all the required classloaders.
     *
     * @throws MalformedURLException
     */
    private TuscanyServer() throws MalformedURLException {
        installDirectory = DirectoryHelper.getInstallDirectory(TuscanyServer.class);
        agent = RmiAgent.getInstance();
    }

    /**
     * Starts a runtime specified by the bootpath.
     * 
     * @param profileName Profile for the runtime.
     */
    public final void startRuntime(final String profileName) {

        try {

            final File profileDirectory = DirectoryHelper.getProfileDirectory(installDirectory, profileName);
            final File bootDirectory = DirectoryHelper.getBootDirectory(installDirectory, profileDirectory, null);

            final MBeanServer mBeanServer = agent.getMBeanServer();            
            final StandaloneRuntimeInfo runtimeInfo = createRuntimeInfo(profileName);
            final ManagementService<?> managementService = new JmxManagementService(mBeanServer, profileName);
            final TuscanyRuntime<?> runtime = createRuntime(bootDirectory, runtimeInfo);
            runtime.setManagementService(managementService);
            runtime.initialize();

            bootedRuntimes.put(profileName, runtime);

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
        } catch (Throwable ex) {
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
     * TODO Share this code with launcher.
     * 
     * Creates the runtime info.
     * 
     * @param profile profile for which runtime info is created.
     * @return Runtime info.
     * @throws IOException If unable to read the runtime properties.
     * @throws URISyntaxException 
     */
    private StandaloneRuntimeInfo createRuntimeInfo(String profile) throws IOException, URISyntaxException {
        
        File profileDir = DirectoryHelper.getProfileDirectory(installDirectory, profile);

        // load properties for this runtime
        File propFile = new File(profileDir, "etc/runtime.properties");
        Properties props = DirectoryHelper.loadProperties(propFile, System.getProperties());
        String domain = props.getProperty("domain");
        
        // online unless the offline property is set
        boolean online = !Boolean.parseBoolean(props.getProperty("offline", "false"));

        
        return new StandaloneRuntimeInfoImpl(new URI(domain), profile, installDirectory, profileDir, null, online, props);
    }

    /**
     * TODO Share this code with launcher.
     * 
     * Creates the runtime.
     * 
     * @param bootDirectory Boot directory for the runtime.
     * @param runtimeInfo Runtime info.
     * @return Runtime.
     */
    private TuscanyRuntime createRuntime(final File bootDirectory, final StandaloneRuntimeInfo runtimeInfo) throws IOException, ClassNotFoundException {
        
        final URL profileUrl = runtimeInfo.getProfileDirectory().toURL();
        final ClassLoader hostClassLoader = ClassLoader.getSystemClassLoader();
        final ClassLoader bootClassLoader = DirectoryHelper.createClassLoader(hostClassLoader, bootDirectory);

        final URL systemScdl = getSystemScdl(profileUrl, runtimeInfo);
        if (systemScdl == null) {
            throw new TuscanyServerException("Unable to find system scdl");
        }

        final String className =
            runtimeInfo.getProperty("tuscany.launcherClass",
                               "org.apache.tuscany.runtime.standalone.host.StandaloneRuntimeImpl");
        final StandaloneRuntime runtime = (StandaloneRuntime) Beans.instantiate(bootClassLoader, className);
        runtime.setSystemScdl(systemScdl);
        runtime.setHostClassLoader(hostClassLoader);

        runtime.setRuntimeInfo(runtimeInfo);
        return runtime;
        
    }

    /**
     * Gets the system SCDL.
     *
     * @param bootClassLoader Boot classloader.
     * @return URL to the system SCDL.
     * @throws MalformedURLException 
     */
    private URL getSystemScdl(URL profileUrl, StandaloneRuntimeInfo runtimeInfo) throws MalformedURLException {
        return new URL(profileUrl, runtimeInfo.getProperty("tuscany.systemSCDL", "system.scdl"));
    }

    /**
     * Starts the server and starts the JMX agent.
     */
    private void start() {
        agent.start();
        agent.register(this, "tuscany:type=server,name=tuscanyServer");
    }

}
