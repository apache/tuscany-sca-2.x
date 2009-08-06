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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private static final String FACTORY_RESOURCE = "META-INF/services/" + FrameworkFactory.class.getName();

    private static final Logger logger = Logger.getLogger(FrameworkLauncher.class.getName());

    private FrameworkFactory factory;
    private boolean isEquinox;

    @SuppressWarnings("unchecked")
    private synchronized FrameworkFactory loadFrameworkFactory() {
        if (factory == null) {
            try {
                ClassLoader classLoader = FrameworkFactory.class.getClassLoader();
                InputStream is = classLoader.getResourceAsStream(FACTORY_RESOURCE);
                if (is == null) {
                    classLoader = Thread.currentThread().getContextClassLoader();
                    is = classLoader.getResourceAsStream(FACTORY_RESOURCE);
                }
                if (is == null) {
                    return null;
                }
                BufferedReader reader = null;
                String line = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    while (true) {
                        line = reader.readLine();
                        if (line == null)
                            break;
                        line = line.trim();
                        if (!line.startsWith("#") && !"".equals(line)) {
                            break;
                        }
                    }
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            // Ignore
                        }
                    }
                }
                if (line != null) {
                    Class<? extends FrameworkFactory> factoryImplClass =
                        (Class<? extends FrameworkFactory>)Class.forName(line, false, classLoader);
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
        if (factory == null) {
            return null;
        }
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
