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
package org.apache.tuscany.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.bootstrap.ComponentNames;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.deployer.DeploymentMonitor;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.apache.tuscany.api.TuscanyException;
import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;
import org.apache.tuscany.core.launcher.CompositeContextImpl;
import org.apache.tuscany.core.launcher.LauncherImpl;
import org.apache.tuscany.core.monitor.JavaLoggingMonitorFactory;
import org.apache.tuscany.host.MonitorFactory;
import org.apache.tuscany.host.runtime.InitializationException;

import org.osoa.sca.CurrentCompositeContext;

/**
 * Base class for JUnit tests that want to run in an SCA client environment.
 *
 * @version $Rev$ $Date$
 */
public abstract class SCATestCase extends TestCase {
    protected CompositeComponent component;
    private CompositeContextImpl context;
    private Map<String, URL> extensions = new HashMap<String, URL>();
    private URL applicationSCDL;
    private LauncherImpl launcher;
    private MonitorFactory monitorFactory;

    protected void setUp() throws Exception {
        super.setUp();
        if (monitorFactory == null) {
            monitorFactory = new JavaLoggingMonitorFactory();
        }
        ClassLoader cl = getClass().getClassLoader();
        launcher = new LauncherImpl();
        launcher.setApplicationLoader(cl);
        URL scdl = cl.getResource(LauncherImpl.METAINF_SYSTEM_SCDL_PATH);

        try {
            CompositeComponent composite = launcher.bootRuntime(scdl, monitorFactory);
            for (String extensionName : extensions.keySet()) {
                deployExtension(composite, extensionName, extensions.get(extensionName));
            }

            SCAObject wireServiceComponent = composite.getSystemChild(ComponentNames.TUSCANY_WIRE_SERVICE);
            if (!(wireServiceComponent instanceof AtomicComponent)) {
                throw new InitializationException("WireService must be an atomic component");
            }

            WireService wireService = (WireService) ((AtomicComponent) wireServiceComponent).getTargetInstance();

            if (applicationSCDL == null) {
                throw new RuntimeException("application SCDL not found: " + applicationSCDL);
            }
            component = launcher.bootApplication("application", applicationSCDL);
            component.start();
            context = new CompositeContextImpl(component, wireService);
            CurrentCompositeContext.setContext(context);
        } catch (TuscanyException e) {
            DeploymentMonitor monitor = monitorFactory.getMonitor(DeploymentMonitor.class);
            monitor.deploymentError(e);
            throw e;
        }

    }

    /**
     * A TestCase can use this to overide the default SCDL location of "META-INF/sca/default.scdl"
     */
    protected void setApplicationSCDL(URL applicationSCDL) {
        this.applicationSCDL = applicationSCDL;
    }

    /**
     * Set the application scdl based on the classpath entry for a class. Normally this will be a class in the
     * production code associated with this test case.
     *
     * @param aClass a Class from which to determine the resource base url
     * @param path   location of the application SCDL relative to the base class
     * @throws MalformedURLException if the path is malformed
     */
    protected void setApplicationSCDL(Class<?> aClass, String path) throws MalformedURLException {
        URL root = getRoot(aClass);
        setApplicationSCDL(new URL(root, path));
    }

    /**
     * A TestCase can use this to add the SCDL location of an extention to be deployed to the runtime
     */
    protected void addExtension(String extensionName, URL extentionSCDL) {
        extensions.put(extensionName, extentionSCDL);
    }


    /**
     * Sets the monitor factory to use
     *
     * @param monitorFactory the monitor factory to use
     */
    protected void setMonitorFactory(MonitorFactory monitorFactory) {
        this.monitorFactory = monitorFactory;
    }

    protected void deployExtension(CompositeComponent composite, String extensionName, URL scdlURL)
        throws LoaderException, BuilderException, ComponentException, InitializationException {
        SystemCompositeImplementation implementation = new SystemCompositeImplementation();
        implementation.setScdlLocation(scdlURL);
        implementation.setClassLoader(new URLClassLoader(new URL[]{scdlURL}, getClass().getClassLoader()));

        ComponentDefinition<SystemCompositeImplementation> definition =
            new ComponentDefinition<SystemCompositeImplementation>(extensionName, implementation);


        SCAObject child = composite.getSystemChild(ComponentNames.TUSCANY_DEPLOYER);
        if (!(child instanceof AtomicComponent)) {
            throw new InitializationException("Deployer must be an atomic component");
        }
        Deployer deployer = (Deployer) ((AtomicComponent) child).getTargetInstance();
        Component component = deployer.deploy(composite, definition);
        component.start();
    }


    protected static URL getRoot(Class<?> aClass) {
        String name = aClass.getName();
        String classPath = "/" + name.replace('.', '/') + ".class";
        URL classURL = aClass.getResource(classPath);
        assert classURL != null;
        StringBuilder prefix = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) == '.') {
                prefix.append("../");
            }
        }
        try {
            return new URL(classURL, prefix.toString());
        } catch (MalformedURLException e) {
            throw new AssertionError();
        }
    }

    protected void tearDown() throws Exception {
        CurrentCompositeContext.setContext(null);
        component.stop();
        launcher.shutdownRuntime();
        super.tearDown();
    }
}
