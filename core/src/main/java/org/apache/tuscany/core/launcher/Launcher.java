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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URI;

import javax.xml.stream.XMLInputFactory;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.bootstrap.ComponentNames;
import org.apache.tuscany.spi.bootstrap.RuntimeComponent;
import org.apache.tuscany.spi.services.info.RuntimeInfo;
import org.apache.tuscany.core.bootstrap.Bootstrapper;
import org.apache.tuscany.core.bootstrap.DefaultBootstrapper;
import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;
import org.apache.tuscany.core.implementation.system.component.SystemCompositeComponent;

/**
 * Support class for launcher implementations.
 *
 * @version $Rev: 417136 $ $Date: 2006-06-26 03:54:48 -0400 (Mon, 26 Jun 2006) $
 */
public class Launcher {
    private ClassLoader applicationLoader = ClassLoader.getSystemClassLoader();
    private String className;
    private RuntimeComponent runtime;
    private Deployer deployer;

    /**
     * Returns the classloader for application classes.
     *
     * @return the classloader for application classes
     */
    public ClassLoader getApplicationLoader() {
        return applicationLoader;
    }

    /**
     * Set the classloader to be used for application classes.
     *
     * @param applicationLoader the classloader to be used for application classes
     */
    public void setApplicationLoader(ClassLoader applicationLoader) {
        this.applicationLoader = applicationLoader;
    }

    /**
     * Create a classloader for the supplied classpath.
     *
     * @param path   a list of file/directory names separated by the platform path separator
     * @param parent the parent for the new classloader
     * @return a classloader that will load classes from the supplied path
     */
    protected static ClassLoader createClassLoader(ClassLoader parent, String path) {
        String[] files = path.split(File.pathSeparator);
        return createClassLoader(parent, files);
    }

    /**
     * Create a classloader for a classpath supplied as individual file names.
     *
     * @param files  a list of file/directory names
     * @param parent the parent for the new classloader
     * @return a classloader that will load classes from the supplied path
     */
    protected static ClassLoader createClassLoader(ClassLoader parent, String[] files) {
        URL[] urls = new URL[files.length];
        for (int i = 0; i < files.length; i++) {
            try {
                File file = new File(files[i]);
                urls[i] = file.toURI().toURL();
            } catch (MalformedURLException e) {
                // just ignore this value
                continue;
            }
        }

        return new URLClassLoader(urls, parent);
    }

    /**
     * Create a classloader for a classpath supplied as a list of files.
     *
     * @param files  a list of files
     * @param parent the parent for the new classloader
     * @return a classloader that will load classes from the supplied path
     */
    protected ClassLoader createClassLoader(ClassLoader parent, File[] files) {
        URL[] urls = new URL[files.length];
        for (int i = 0; i < files.length; i++) {
            try {
                File file = files[i];
                urls[i] = file.toURI().toURL();
            } catch (MalformedURLException e) {

                continue;
            }
        }
        return new URLClassLoader(urls, parent);
    }

    /**
     * Returns the name of the application class.
     *
     * @return the name of the application class
     */
    protected String getClassName() {
        return className;
    }

    /**
     * Sets the name of the application class.
     *
     * @param className the name of the application class
     */
    protected void setClassName(String className) {
        this.className = className;
    }

    public CompositeComponent<?> bootRuntime() throws LoaderException {
        ClassLoader systemClassLoader = getClass().getClassLoader();
        XMLInputFactory xmlFactory = XMLInputFactory.newInstance("javax.xml.stream.XMLInputFactory", systemClassLoader);
        Bootstrapper bootstrapper = new DefaultBootstrapper(new NullMonitorFactory(), xmlFactory);
        Deployer bootDeployer = bootstrapper.createDeployer();

        // create and start the core runtime
        runtime = bootstrapper.createRuntime();
        runtime.start();

        // initialize the runtime info
        SystemCompositeComponent parent = (SystemCompositeComponent) runtime.getSystemComponent();
        RuntimeInfo runtimeInfo = new LauncherRuntimeInfo(getInstallDirectory());
        parent.registerJavaObject("RuntimeInfo", RuntimeInfo.class, runtimeInfo);

        // create a ComponentDefinition to represent the component we are going to deploy
        SystemCompositeImplementation moduleImplementation = new SystemCompositeImplementation();
        URL scdl = getClass().getResource("/META-INF/tuscany/system.scdl");
        if (scdl == null) {
            throw new LoaderException("No system scdl found");
        }
        moduleImplementation.setScdlLocation(scdl);
        moduleImplementation.setClassLoader(systemClassLoader);
        ComponentDefinition<SystemCompositeImplementation> moduleDefinition =
                new ComponentDefinition<SystemCompositeImplementation>(ComponentNames.TUSCANY_SYSTEM,
                                                                       moduleImplementation);

        // deploy the component into the runtime under the system parent
        CompositeComponent<?> composite = (CompositeComponent<?>) bootDeployer.deploy(parent, moduleDefinition);

        // start the system
        composite.start();

        deployer = (Deployer) composite.getChild("deployer").getServiceInstance();
        return composite;
    }

    public CompositeComponent<?> bootApplication() throws LoaderException {
        ClassLoader applicationLoader = getApplicationLoader();

        // create a ComponentDefinition to represent the component we are going to deploy
        SystemCompositeImplementation impl = new SystemCompositeImplementation();
        URL scdl = applicationLoader.getResource("META-INF/sca/default.scdl");
        if (scdl == null) {
            throw new LoaderException("No application scdl found");
        }
        impl.setScdlLocation(scdl);
        impl.setClassLoader(applicationLoader);
        ComponentDefinition<SystemCompositeImplementation> moduleDefinition =
                new ComponentDefinition<SystemCompositeImplementation>(ComponentNames.TUSCANY_SYSTEM, impl);

        // deploy the component into the runtime under the system parent
        CompositeComponent parent = runtime.getRootComponent();
        return (CompositeComponent<?>) deployer.deploy(parent, moduleDefinition);
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

        File jarFile = new File(URI.create(jarLocation));
        return jarFile.getParentFile().getParentFile();
    }
}
