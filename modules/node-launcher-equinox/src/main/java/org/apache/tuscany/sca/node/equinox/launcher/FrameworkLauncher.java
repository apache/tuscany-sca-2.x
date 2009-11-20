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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
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
        String propertyFile = null;
        String factoryName = factory.getClass().getName();
        if (factoryName.startsWith("org.eclipse.osgi.")) {
            propertyFile = "equinox.properties";
        } else if (factoryName.startsWith("org.apache.felix.")) {
            propertyFile = "felix.properties";
        }
        Map propMap = new HashMap();
        if (propertyFile != null) {
            InputStream is = getClass().getResourceAsStream(propertyFile);
            if (is != null) {
                Properties props = new Properties();
                try {
                    props.load(is);
                } catch (IOException e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                }
                propMap.putAll(props);
            }
        }
        propMap.putAll(properties);
        return factory.newFramework(propMap);
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

    private static final String DELIM_START = "${";
    private static final String DELIM_STOP = "}";

    /**
     * <p>
     * This method performs property variable substitution on the
     * specified value. If the specified value contains the syntax
     * <tt>${&lt;prop-name&gt;}</tt>, where <tt>&lt;prop-name&gt;</tt>
     * refers to either a configuration property or a system property,
     * then the corresponding property value is substituted for the variable
     * placeholder. Multiple variable placeholders may exist in the
     * specified value as well as nested variable placeholders, which
     * are substituted from inner most to outer most. Configuration
     * properties override system properties.
     * </p>
     * @param val The string on which to perform property substitution.
     * @param currentKey The key of the property being evaluated used to
     *        detect cycles.
     * @param cycleMap Map of variable references used to detect nested cycles.
     * @param configProps Set of configuration properties.
     * @return The value of the specified string after system property substitution.
     * @throws IllegalArgumentException If there was a syntax error in the
     *         property placeholder syntax or a recursive variable reference.
    **/
    public static String substVars(String val, String currentKey, Map cycleMap, Properties configProps)
        throws IllegalArgumentException {
        // If there is currently no cycle map, then create
        // one for detecting cycles for this invocation.
        if (cycleMap == null) {
            cycleMap = new HashMap();
        }

        // Put the current key in the cycle map.
        cycleMap.put(currentKey, currentKey);

        // Assume we have a value that is something like:
        // "leading ${foo.${bar}} middle ${baz} trailing"

        // Find the first ending '}' variable delimiter, which
        // will correspond to the first deepest nested variable
        // placeholder.
        int stopDelim = val.indexOf(DELIM_STOP);

        // Find the matching starting "${" variable delimiter
        // by looping until we find a start delimiter that is
        // greater than the stop delimiter we have found.
        int startDelim = val.indexOf(DELIM_START);
        while (stopDelim >= 0) {
            int idx = val.indexOf(DELIM_START, startDelim + DELIM_START.length());
            if ((idx < 0) || (idx > stopDelim)) {
                break;
            } else if (idx < stopDelim) {
                startDelim = idx;
            }
        }

        // If we do not have a start or stop delimiter, then just
        // return the existing value.
        if ((startDelim < 0) && (stopDelim < 0)) {
            return val;
        }
        // At this point, we found a stop delimiter without a start,
        // so throw an exception.
        else if (((startDelim < 0) || (startDelim > stopDelim)) && (stopDelim >= 0)) {
            throw new IllegalArgumentException("stop delimiter with no start delimiter: " + val);
        }

        // At this point, we have found a variable placeholder so
        // we must perform a variable substitution on it.
        // Using the start and stop delimiter indices, extract
        // the first, deepest nested variable placeholder.
        String variable = val.substring(startDelim + DELIM_START.length(), stopDelim);

        // Verify that this is not a recursive variable reference.
        if (cycleMap.get(variable) != null) {
            throw new IllegalArgumentException("recursive variable reference: " + variable);
        }

        // Get the value of the deepest nested variable placeholder.
        // Try to configuration properties first.
        String substValue = (configProps != null) ? configProps.getProperty(variable, null) : null;
        if (substValue == null) {
            // Ignore unknown property values.
            substValue = System.getProperty(variable, "");
        }

        // Remove the found variable from the cycle map, since
        // it may appear more than once in the value and we don't
        // want such situations to appear as a recursive reference.
        cycleMap.remove(variable);

        // Append the leading characters, the substituted value of
        // the variable, and the trailing characters to get the new
        // value.
        val = val.substring(0, startDelim) + substValue + val.substring(stopDelim + DELIM_STOP.length(), val.length());

        // Now perform substitution again, since there could still
        // be substitutions to make.
        val = substVars(val, currentKey, cycleMap, configProps);

        // Return the value.
        return val;
    }

}
