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

package org.apache.tuscany.sca.node.launcher;

import static org.apache.tuscany.sca.node.launcher.NodeLauncherUtil.node;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

/**
 * A launcher for SCA nodes in JSE.
 * 
 * Agruments:
 * [-c <compositeURI>]: The composite URI
 * [-t <ttl>]: Time to live in milliseconds before the node is started
 * contribution1 ... contributionN: A list of contribution files or URLs 
 *
 * @version $Rev$ $Date$
 */
public class NodeLauncher {

    static final Logger logger = Logger.getLogger(NodeLauncher.class.getName());

    /**
     * Constructs a new node launcher.
     */
    private NodeLauncher() {
    }
    
    /**
     * Returns a new launcher instance.
     *  
     * @return a new launcher instance
     */
    public static NodeLauncher newInstance() {
        return new NodeLauncher();
    }

    /**
     * Creates a new SCA node from the configuration URL
     * 
     * @param configurationURL the URL of the node configuration which is the ATOM feed
     * that contains the URI of the composite and a collection of URLs for the contributions
     *  
     * @return a new SCA node.
     * @throws LauncherException
     */
    public <T> T createNodeFromURL(String configurationURL) throws LauncherException {
        return (T)node(configurationURL, null, null, null, null);
    }
    
    /**
     * Creates a new SCA Node.
     * 
     * @param compositeURI the URI of the composite to use 
     * @param contributions the URI of the contributions that provides the composites and related 
     * artifacts. If the list is empty, then we will use the thread context classloader to discover
     * the contribution on the classpath
     *   
     * @return a new SCA node.
     * @throws LauncherException
     */
    public <T> T createNode(String compositeURI, Contribution...contributions) throws LauncherException {
        return (T)node(null, compositeURI, null, contributions, null);
    }
    
    /**
     * Creates a new SCA Node.
     * 
     * @param compositeURI the URI of the composite to use 
     * @param compositeContent the XML content of the composite to use 
     * @param contributions the URI of the contributions that provides the composites and related artifacts 
     * @return a new SCA node.
     * @throws LauncherException
     */
    public <T> T createNode(String compositeURI, String compositeContent, Contribution...contributions) throws LauncherException {
        return (T)node(null, compositeURI, compositeContent, contributions, null);
    }
    
    /**
     * Create a SCA node based on the discovery of the contribution on the classpath for the 
     * given classloader. This method should be treated a convenient shortcut with the following
     * assumptions:
     * <ul>
     * <li>This is a standalone application and there is a deployable composite file on the classpath.
     * <li>There is only one contribution which contains the deployable composite file physically in its packaging hierarchy.
     * </ul> 
     * 
     * @param compositeURI The URI of the composite file relative to the root of the enclosing contribution
     * @param classLoader The ClassLoader used to load the composite file as a resource. If the value is null,
     * then thread context classloader will be used
     * @return A newly created SCA node
     */
    public <T> T createNodeFromClassLoader(String compositeURI, ClassLoader classLoader) throws LauncherException {
        return (T)node(null, compositeURI, null, null, classLoader);
    }
    
    static Options getCommandLineOptions() {
        Options options = new Options();
        Option opt1 = new Option("c", "composite", true, "URI for the composite");
        opt1.setArgName("compositeURI");
        options.addOption(opt1);
        Option opt2 = new Option("n", "node", true, "URI for the node configuration");
        opt2.setArgName("nodeConfigurationURI");
        options.addOption(opt2);
        Option opt3 = new Option("t", "ttl", true, "Time to live");
        opt3.setArgName("timeToLiveInMilliseconds");
        options.addOption(opt3);        
        Option opt4 = new Option("s", "service", true, "Service to invoke (componentName/serviceName#operation(arg0,...,argN)");
        options.addOption(opt4);
        return options;
    }

    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new PosixParser();
        Options options = getCommandLineOptions();
        CommandLine cli = parser.parse(options, args);
        
        Object node = null;
        ShutdownThread shutdown = null;
        try {
            while (true) {
                if (cli.hasOption("node")) {
                    
                    // Create a node from a configuration URI
                    String configurationURI = cli.getOptionValue("node");
                    logger.info("SCA Node configuration: " + configurationURI);
                    // Create a node launcher
                    NodeLauncher launcher = newInstance();
                    node = launcher.createNodeFromURL(configurationURI);
                } else {
                    
                    // Create a node from a composite URI and a contribution location
                    String compositeURI = cli.getOptionValue("composite");
                    List<String> contribs = cli.getArgList();
                    Contribution[] contributions = null;
                    if (!contribs.isEmpty()) {
                        contributions = new Contribution[contribs.size()];
                        int index = 0;
                        for (String contrib : contribs) {
                            logger.info("SCA contribution: " + contrib);
                            URL url = null;
                            try {
                                url = new URL(contrib);
                            } catch(MalformedURLException e) {
                                url = new File(contrib).toURI().toURL();
                            }
                            contributions[index] = new Contribution("contribution-" + index, url.toString());
                            index++;
                        }
                    } else {
                        HelpFormatter formatter = new HelpFormatter();
                        formatter.setSyntaxPrefix("Usage: ");
                        formatter.printHelp("java " + NodeLauncher.class.getName()
                                            + " [-c <compositeURI>]"
                                            + " [-s <service>]"
                                            + " [-t <ttl>]"
                                            + " contribution1 ... contributionN", options);                        return;
                    }
                    // Create a node launcher
                    logger.info("SCA composite: " + compositeURI);
                    NodeLauncher launcher = newInstance();
                    
                    node = launcher.createNode(compositeURI, contributions);
                }
                
                logger.info("Apache Tuscany SCA Node is starting...");

                // Start the node
                try {
                    node.getClass().getMethod("start").invoke(node);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "SCA Node could not be started", e);
                    throw e;
                }
                logger.info("SCA Node is now started.");
                
                String service = cli.getOptionValue("service");
                String regex = "(#|\\(|,|\\))";
                if (service != null) {
                    // componentName/serviceName/bindingName#methodName(arg0, ..., agrN)
                    String tokens[] = service.split(regex);
                    String serviceName = tokens[0];
                    String operationName = tokens[1];
                    String params[] = new String[tokens.length - 2];
                    System.arraycopy(tokens, 2, params, 0, params.length);
                    logger.info("Invoking service: " + service);
                    Method method = node.getClass().getMethod("getService", Class.class, String.class);
                    Object proxy = method.invoke(node, null, serviceName);

                    Object result = invoke(proxy, operationName, params);
                    if (result != null) {
                        logger.info("Result is: " + result);
                    }
                    break;
                }
                
                // Install a shutdown hook
                shutdown = new ShutdownThread(node);
                Runtime.getRuntime().addShutdownHook(shutdown);

                long ttl = Long.parseLong(cli.getOptionValue("ttl", "-1"));
                if (ttl >= 0) {
                    logger.info("Waiting for " + ttl + " milliseconds ...");
                    Thread.sleep(ttl);
                    // Stop the node
                    if (node != null) {
                        Object n = node;
                        node = null;
                        stopNode(n);
                    }
                    break; // Exit
                }
                
                logger.info("Press 'q' to quit, 'r' to restart.");
                int k = 0;
                try {
                    while ((k != 'q') && (k != 'r')) {
                        k = System.in.read();
                    }
                } catch (IOException e) {
                    
                    // Wait forever
                    Object lock = new Object();
                    synchronized(lock) {
                        lock.wait();
                    }
                }
                
                // Stop the node
                if (node != null) {
                    Object n = node;
                    node = null;
                    stopNode(n);
                }
                
                // Quit
                if (k == 'q' ) {
                    break;
                }
            }
        } catch (Exception e) {
            // Stop the node
            if (node != null) {
                try {
                    Object n = node;
                    node = null;
                    stopNode(n);
                } catch (Exception e2) {
                }
            }
            throw e;
            
        } finally {

            // Remove the shutdown hook
            if (shutdown != null) {
                Runtime.getRuntime().removeShutdownHook(shutdown);
            }
        }
    }
    
    static Object invoke(Object proxy, String operationName, String... params) throws IllegalAccessException,
        InvocationTargetException {
        for (Method m : proxy.getClass().getMethods()) {
            if (m.getName().equals(operationName) && m.getParameterTypes().length == params.length) {
                Object parameters[] = new Object[params.length];
                int i = 0;
                for (Class<?> type : m.getParameterTypes()) {
                    if (type == byte.class || type == Byte.class) {
                        parameters[i] = Byte.valueOf(params[i]);
                    } else if (type == char.class || type == Character.class) {
                        parameters[i] = params[i].charAt(0);
                    } else if (type == boolean.class || type == Boolean.class) {
                        parameters[i] = Boolean.valueOf(params[i]);
                    } else if (type == short.class || type == Short.class) {
                        parameters[i] = Short.valueOf(params[i]);
                    } else if (type == int.class || type == Integer.class) {
                        parameters[i] = Integer.valueOf(params[i]);
                    } else if (type == long.class || type == Long.class) {
                        parameters[i] = Long.valueOf(params[i]);
                    } else if (type == float.class || type == Float.class) {
                        parameters[i] = Float.valueOf(params[i]);
                    } else if (type == double.class || type == Double.class) {
                        parameters[i] = Double.valueOf(params[i]);
                    } else if (type == String.class) {
                        parameters[i] = params[i];
                    } else {
                        throw new IllegalArgumentException("Parameter type is not supported: " + type);
                    }
                    i++;
                }
                Object result = m.invoke(proxy, parameters);
                return result;
            }
        }
        throw new IllegalArgumentException("Invalid service operation: " + operationName);
    }

    /**
     * Stop the given node.
     * 
     * @param node
     * @throws Exception
     */
    private static void stopNode(Object node) throws Exception {
        try {
            node.getClass().getMethod("stop").invoke(node);
            logger.info("SCA Node is now stopped.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "SCA Node could not be stopped", e);
            throw e;
        }
    }
    
    private static class ShutdownThread extends Thread {
        private Object node;

        public ShutdownThread(Object node) {
            super();
            this.node = node;
        }

        @Override
        public void run() {
            try {
                stopNode(node);
            } catch (Exception e) {
                // Ignore
            }
        }
    }
}
