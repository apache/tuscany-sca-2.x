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

package org.apache.tuscany.sca.node.equinox.launcher;

import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

/**
 * Launcher for the OSGi framework using the framework launch APIs
 */
public class FrameworkLauncher implements BundleActivator {
    private static final Logger logger = Logger.getLogger(FrameworkLauncher.class.getName());

    private FrameworkFactory factory;
    private boolean isEquinox;

    @SuppressWarnings("unchecked")
    private synchronized FrameworkFactory loadFrameworkFactory() {
        if (factory == null) {
            // Use reflection APIs to call ServiceDiscovery to avoid hard dependency to tuscany-extensibility
            try {
                Class<?> discoveryClass = Class.forName("org.apache.tuscany.sca.extensibility.ServiceDiscovery");
                Object instance = discoveryClass.getMethod("getInstance").invoke(null);
                Object factoryDeclaration =
                    discoveryClass.getMethod("getServiceDeclaration", String.class).invoke(instance,
                                                                                           FrameworkFactory.class
                                                                                               .getName());
                if (factoryDeclaration != null) {
                    Class<? extends FrameworkFactory> factoryImplClass =
                        (Class<? extends FrameworkFactory>)factoryDeclaration.getClass().getMethod("loadClass")
                            .invoke(factoryDeclaration);
                    factory = factoryImplClass.newInstance();
                    if (factory != null && factory.getClass().getName().startsWith("org.eclipse.osgi.")) {
                        isEquinox = true;
                    }
                }
            } catch (Throwable e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return factory;
    }

    public Framework newFramework(Map properties) {
        FrameworkFactory factory = loadFrameworkFactory();
        return factory.newFramework(properties);
    }

    public boolean isEquinox() {
        return isEquinox;
    }

    public void start(BundleContext context) throws Exception {
        EquinoxHost.injectedBundleContext = context;
        if (context.getClass().getName().startsWith("org.eclipse.osgi.")) {
            isEquinox = true;
            try {
                context.registerService(CommandProvider.class.getName(), new NodeLauncherCommand(), new Hashtable());
            } catch (NoClassDefFoundError e) {
                // Ignore it
            }
        }
    }

    public void stop(BundleContext context) throws Exception {
        EquinoxHost.injectedBundleContext = null;
    }
}
