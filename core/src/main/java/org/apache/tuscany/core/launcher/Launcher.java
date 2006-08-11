/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.launcher;

import java.io.File;
import java.net.URL;
import javax.xml.stream.XMLInputFactory;

import org.apache.tuscany.core.bootstrap.Bootstrapper;
import org.apache.tuscany.core.bootstrap.DefaultBootstrapper;
import org.apache.tuscany.core.implementation.system.component.SystemCompositeComponent;
import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;
import org.apache.tuscany.spi.bootstrap.ComponentNames;
import org.apache.tuscany.spi.bootstrap.RuntimeComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.monitor.MonitorFactory;
import org.apache.tuscany.spi.services.info.RuntimeInfo;

/**
 * Basic launcher implementation.
 *
 * @version $Rev: 417136 $ $Date: 2006-06-26 03:54:48 -0400 (Mon, 26 Jun 2006) $
 */
public class Launcher {
    /**
     * A conventional META-INF based location for the system SCDL.
     *
     * @see #bootRuntime(URL, MonitorFactory)
     */
    public static final String METAINF_SYSTEM_SCDL_PATH = "META-INF/tuscany/system.scdl";

    /**
     * A conventional META-INF based location for the application SCDL.
     */
    public static final String METAINF_APPLICATION_SCDL_PATH = "META-INF/sca/default.scdl";

    private ClassLoader applicationLoader;

    private RuntimeComponent runtime;

    private Deployer deployer;

    private CompositeComponent<?> composite;

    /**
     * Returns the classloader for application classes.
     *
     * @return the classloader for application classes
     */
    public ClassLoader getApplicationLoader() {
        return applicationLoader;
    }

    /**
     * Set the classloader to be used for application classes. You should almost always supply your own application
     * classloader, based on the hosting environment that the runtime is embedded in.
     *
     * @param applicationLoader the classloader to be used for application classes
     */
    public void setApplicationLoader(ClassLoader applicationLoader) {
        this.applicationLoader = applicationLoader;
    }

    /**
     * Boots the runtime defined by the specified SCDL.
     *
     * @param systemScdl a resource path to the SCDL defining the system.
     * @return a CompositeComponent for the newly booted runtime system
     * @throws LoaderException
     */
    public CompositeComponent<?> bootRuntime(URL systemScdl, MonitorFactory monitor) throws LoaderException {
        if (systemScdl == null) {
            throw new LoaderException("Null system SCDL URL");
        }

        ClassLoader systemClassLoader = getClass().getClassLoader();
        XMLInputFactory xmlFactory = XMLInputFactory.newInstance("javax.xml.stream.XMLInputFactory", systemClassLoader);
        Bootstrapper bootstrapper = new DefaultBootstrapper(monitor, xmlFactory);
        Deployer bootDeployer = bootstrapper.createDeployer();

        // create and start the core runtime
        runtime = bootstrapper.createRuntime();
        runtime.start(); // REVIEW: is this redundant w/ the composite.start() call below?

        // initialize the runtime info
        SystemCompositeComponent parent = (SystemCompositeComponent) runtime.getSystemComponent();
        RuntimeInfo runtimeInfo = new LauncherRuntimeInfo(getInstallDirectory(), getApplicationRootDirectory());
        parent.registerJavaObject("RuntimeInfo", RuntimeInfo.class, runtimeInfo);

        // create a ComponentDefinition to represent the component we are going to deploy
        SystemCompositeImplementation moduleImplementation = new SystemCompositeImplementation();
        moduleImplementation.setScdlLocation(systemScdl);
        moduleImplementation.setClassLoader(systemClassLoader);
        ComponentDefinition<SystemCompositeImplementation> moduleDefinition =
            new ComponentDefinition<SystemCompositeImplementation>(
                ComponentNames.TUSCANY_SYSTEM, moduleImplementation);

        // deploy the component into the runtime under the system parent
        composite = (CompositeComponent<?>) bootDeployer.deploy(parent, moduleDefinition);

        // start the system
        composite.start();

        deployer = (Deployer) composite.getChild("deployer").getServiceInstance();
        return composite;
    }

    /**
     * Shuts down the active runtime being managed by this instance.
     */
    public void shutdownRuntime() {
        if (composite != null) {
            composite.stop();
            composite = null;
        }

        if (runtime != null) {
            runtime.stop();
            runtime = null;
        }
    }

    /**
     * Boots the application defined by the specified SCDL.
     *
     * @param name    the name of the application component
     * @param appScdl URL to the SCDL defining the application
     * @return a CompositeComponent for the newly booted application
     * @throws LoaderException
     * @see METAINF_APPLICATION_SCDL_PATH
     */
    public CompositeComponent<?> bootApplication(String name, URL appScdl) throws LoaderException {
        if (appScdl == null) {
            throw new LoaderException("No application scdl found");
        }
        ClassLoader applicationLoader = getApplicationLoader();

        // create a ComponentDefinition to represent the component we are going to deploy
        CompositeImplementation impl = new CompositeImplementation();
        impl.setScdlLocation(appScdl);
        impl.setClassLoader(applicationLoader);
        ComponentDefinition<CompositeImplementation> moduleDefinition =
            new ComponentDefinition<CompositeImplementation>(name, impl);

        // deploy the component into the runtime under the system parent
        CompositeComponent parent = runtime.getRootComponent();
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();

        try {

            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

            return (CompositeComponent<?>) deployer.deploy(parent, moduleDefinition);
        } finally {
            Thread.currentThread().setContextClassLoader(ccl);
        }
    }

    public File getInstallDirectory() {
        String property = System.getProperty("tuscany.installDir");
        if (property != null) {
            return new File(property);
        }

        URL url = getClass().getResource("Launcher.class");
        if (!"jar".equals(url.getProtocol())) {
            throw new IllegalStateException("Must be run from a jar: " + url);
        }
        String jarLocation = url.toString();
        jarLocation = jarLocation.substring(4, jarLocation.lastIndexOf("!/"));
        if (!jarLocation.startsWith("file:")) {
            throw new IllegalStateException("Must be run from a local filesystem: " + jarLocation);
        }

        File jarFile = new File(jarLocation.substring(5));
        return jarFile.getParentFile().getParentFile();
    }

    public File getApplicationRootDirectory() {
        String property = System.getProperty("tuscany.applicationRootDir");
        if (property != null) {
            return new File(property);
        }

        return new File(System.getProperty("user.dir"));
    }
}
