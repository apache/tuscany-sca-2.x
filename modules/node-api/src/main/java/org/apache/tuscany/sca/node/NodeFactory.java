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

package org.apache.tuscany.sca.node;

import static org.apache.tuscany.sca.node.ContributionLocationHelper.getContributionLocations;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.tuscany.sca.node.configuration.DefaultNodeConfigurationFactory;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.apache.tuscany.sca.node.configuration.NodeConfigurationFactory;
import org.oasisopen.sca.ServiceReference;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * A factory for SCA processing nodes. An SCA processing node can be loaded
 * with an SCA composite and the SCA contributions required by the composite.
 *
 * @version $Rev$ $Date$
 */
public abstract class NodeFactory extends DefaultNodeConfigurationFactory {
    /**
     * Default location of contribution metadata in an SCA contribution.
     */
    private static final String SCA_CONTRIBUTION_META = "META-INF/sca-contribution.xml";

    /**
     * Default location of a generated contribution metadata in an SCA contribution.
     */
    private static final String SCA_CONTRIBUTION_GENERATED_META = "META-INF/sca-contribution-generated.xml";

    protected static NodeFactory instance;
    protected static Class<?> factoryImplClass;
    
    protected static List<NodeFactory> factories = new ArrayList<NodeFactory>();

    protected static void setNodeFactory(NodeFactory factory) {
        instance = factory;
    }

    public static class NodeProxy implements Node {
        private Object node;

        private NodeProxy(Object node) {
            super();
            this.node = node;
        }

        public static <T> T createProxy(Class<T> type, Object node) {
            try {
                return type.getDeclaredConstructor(Object.class).newInstance(node);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        public <B, R extends ServiceReference<B>> R cast(B target) throws IllegalArgumentException {
            try {
                return (R)node.getClass().getMethod("cast", Object.class).invoke(node, target);
            } catch (Throwable e) {
                handleException(e);
                return null;
            }
        }

        public <B> B getService(Class<B> businessInterface, String serviceName) {
            try {
                return (B)node.getClass().getMethod("getService", Class.class, String.class).invoke(node, businessInterface, serviceName);
            } catch (Throwable e) {
                handleException(e);
                return null;
            }
        }

        public <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String serviceName) {
            try {
                return (ServiceReference<B>)node.getClass().getMethod("getServiceReference", Class.class, String.class).invoke(node, businessInterface, serviceName);
            } catch (Throwable e) {
                handleException(e);
                return null;
            }
        }

        public Node start() {
            try {
                return new NodeProxy(node.getClass().getMethod("start").invoke(node));
            } catch (Throwable e) {
                handleException(e);
                return null;
            }
        }

        public void stop() {
            try {
                node.getClass().getMethod("stop").invoke(node);
            } catch (Throwable e) {
                handleException(e);
            }
        }

        public void destroy() {
            try {
                node.getClass().getMethod("destroy").invoke(node);
            } catch (Throwable e) {
                handleException(e);
            }
        }

        private static void handleException(Throwable ex) {
            if (ex instanceof InvocationTargetException) {
                ex = ((InvocationTargetException)ex).getTargetException();
            }
            if (ex instanceof RuntimeException) {
                throw (RuntimeException)ex;
            }
            if (ex instanceof Error) {
                throw (Error)ex;
            } else {
                throw new RuntimeException(ex);
            }
        }

    }

    /**
     * Returns the SCA node factory instance.
     *
     * @return the SCA node factory
     */
    public synchronized static NodeFactory getInstance() {
        if (instance == null) {
            instance = newInstance();
        }
        return instance;
    }

    /**
     * Returns a new SCA node factory instance.
     *
     * @return a new SCA node factory
     */
    public static NodeFactory newInstance() {
        NodeFactory nodeFactory = null;
        try {
            Class<?> factoryClass = getFactoryImplClass();
            nodeFactory = (NodeFactory)factoryClass.newInstance();

        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
        factories.add(nodeFactory);
        return nodeFactory;
    }

    public static NodeFactory newInstance(Map<String, Map<String, String>> attributes) {
        NodeFactory nodeFactory = null;
        try {
            Class<?> factoryClass = getFactoryImplClass();
            nodeFactory = (NodeFactory)factoryClass.newInstance();
            nodeFactory.configure(attributes);
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
        return nodeFactory;
    }
    
    protected Properties properties;

    protected NodeFactory() {
        this.properties = new Properties();
        properties.setProperty("defaultScheme", "vm");
        properties.setProperty("defaultDomainName", "default");
    }

    public static NodeFactory newInstance(Properties configProperties) {
        NodeFactory nodeFactory = null;
        try {
            Class<?> factoryClass = getFactoryImplClass();
            nodeFactory = (NodeFactory)factoryClass.newInstance();
            nodeFactory.properties = configProperties;
            nodeFactory.configure(new HashMap<String, Map<String,String>>());
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
        return nodeFactory;
    }

    public static NodeFactory newInstance(String configURI) {
        Properties properties;
        if (configURI == null || configURI.length() < 1) {
            return newInstance();
        } else if (configURI.startsWith("properties:")) {
            try {
                properties = loadProperties(configURI.substring("properties:".length()));
            } catch (IOException e) {
                throw new ServiceRuntimeException(e);
            }
        } else if (configURI.startsWith("uri:")) {
            properties = parseConfigURI(configURI.substring("uri:".length()));
        } else {
            properties = new Properties();
            properties.setProperty("defaultDomainName", configURI);
        }
        return newInstance(properties);
    }

    /**
     * Parse the config string into a Properties object.
     * The config URI has the following format:
     * uri:<domainName>?name=value&...
     */
    private static Properties parseConfigURI(String configURI) {
        Properties properties = new Properties();
        int qm = configURI.indexOf('?');
        if (qm < 0) {
            properties.setProperty("defaultDomainName", configURI);
        } else {
            if (qm == 0) {
                properties.setProperty("defaultDomainName", "default");
            } else {
                properties.setProperty("defaultDomainName", configURI.substring(0, qm));
            }
            if (configURI.length() > qm+1) {
                Map<String, String> params = new HashMap<String, String>();
                for (String param : configURI.substring(qm+1).split("&")) {
                    String[] px = param.split("=");
                    if (px.length == 2) {
                        params.put(px[0], px[1]);
                    } else {
                        params.put(px[0], "");
                    }
                }
                for (String name : params.keySet()) {
                    properties.setProperty(name, params.get(name));
                }
            }
        }
        return properties;
    }

    /**
     * load the properties from external URL or a relative file
     * properties:<url to properties file>
     */
    private static Properties loadProperties(String propsURL) throws IOException {

        Properties properties = new Properties();
        File file = new File(propsURL);

        InputStream inputStream = null;
        if (file.exists()) {
            inputStream = new FileInputStream(file);
        } else {
            URL url = null;
            try {
                url = new URL(propsURL);
            } catch (MalformedURLException e) {
                inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(propsURL);
                if (inputStream == null) {
                    throw new IOException("File does not exist: " + propsURL + ", could not be found on the classpath and is not a valid URL: " + e);
                }
            }
            if (inputStream == null && url != null) {
                inputStream = url.openStream();
            }
        }
        if (inputStream != null) {
            properties.load(inputStream);
            inputStream.close();
        }

        return properties;
    }

    /**
     * Configure the NodeFactory instance
     * @param attributes
     */
    public void configure(Map<String, Map<String, String>> attributes) {
    }
    
    private synchronized static Class<?> getFactoryImplClass() throws Exception {
        if (factoryImplClass == null) {
            // Use reflection APIs to call ServiceDiscovery to avoid hard dependency to tuscany-extensibility
            try {
                Class<?> discoveryClass = Class.forName("org.apache.tuscany.sca.extensibility.ServiceDiscovery");
                Object instance = discoveryClass.getMethod("getInstance").invoke(null);
                Object factoryDeclaration =
                    discoveryClass.getMethod("getServiceDeclaration", Class.class).invoke(instance, NodeFactory.class);
                if (factoryDeclaration != null) {
                    factoryImplClass =
                        (Class<?>)factoryDeclaration.getClass().getMethod("loadClass").invoke(factoryDeclaration);
                }
            } catch (ClassNotFoundException e) {
                // Ignore
            }

            if (factoryImplClass == null) {
                // Fail back to default impl
                String className = "org.apache.tuscany.sca.node.impl.NodeFactoryImpl";

                factoryImplClass = Class.forName(className);
            }
        }
        return factoryImplClass;
    }

    /**
     * Open a URL connection without cache
     * @param url
     * @return
     * @throws IOException
     */
    private static InputStream openStream(URL url) throws IOException {
        InputStream is = null;
        URLConnection connection = url.openConnection();
        connection.setUseCaches(false);
        is = connection.getInputStream();
        return is;
    }

    /**
     * Escape the space in URL string
     * @param uri
     * @return
     */
    private static URI createURI(String uri) {
        int index = uri.indexOf(':');
        String scheme = null;
        String ssp = uri;
        if (index != -1) {
            scheme = uri.substring(0, index);
            ssp = uri.substring(index + 1);
        }
        try {
            return new URI(scheme, ssp, null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Creates a new SCA node from the configuration URL
     *
     * @param configurationURL the URL of the node configuration which is the XML document
     * that contains the URI of the composite and a collection of URLs for the contributions
     *
     * @return a new SCA node.
     */
    public Node createNode(URL configurationURL) {
        try {
            InputStream is = openStream(configurationURL);
            NodeConfiguration configuration = loadConfiguration(is, configurationURL);
            return createNode(configuration);
        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    /**
     * Creates a new SCA node from the XML configuration of the node
     * @param is The input stream that the XML configuration can be read. The stream will be closed
     * after this call.
     * @return a new SCA node
     */
    public Node createNode(InputStream is) {
        NodeConfiguration configuration = loadConfiguration(is, null);
        return createNode(configuration);
    }

    /**
     * Creates a new SCA node.
     *
     * @param deploymentCompositeURI the URI of the deployment composite. If the URI is relative, it should
     * be resolved against the first contribution. Otherwise, the absolute URI is used to load the XML
     * description of the composite. The deployment composite will be attached to the first contribution.
     *
     * @param contributions the URI of the contributions that provides the composites and related
     * artifacts. If the list is empty, then we will use the thread context classloader to discover
     * the contribution on the classpath
     *
     * @return a new SCA node.
     */
    public Node createNode(String deploymentCompositeURI, Contribution... contributions) {
        if (contributions == null || contributions.length == 0) {
            if (deploymentCompositeURI == null || deploymentCompositeURI.indexOf(':') != -1) {
                throw new ServiceRuntimeException("No SCA contribution is provided or discovered");
            }
            // Try to find contributions on the classpath by the composite URI
            List<String> locations = getContributionLocations(null, deploymentCompositeURI);
            if (locations.isEmpty()) {
                throw new ServiceRuntimeException("No SCA contributions are found on the classpath");
            }
            contributions = getContributions(locations);
        }
        NodeConfiguration configuration = createConfiguration(contributions);
        if (deploymentCompositeURI != null && configuration.getContributions().size() > 0) {
            configuration.getContributions().get(0).addDeploymentComposite(createURI(deploymentCompositeURI));
        }
        return createNode(configuration);
    }

    public final Node createNode(URI domainRegistryURI, String... locations) {
        return createNode(domainRegistryURI, null, locations);
    }

    public final Node createNode(URI domainRegistryURI, String deploymentCompositeURI, String[] locations) {
        Contribution[] contributions = getContributions(Arrays.asList(locations));
        NodeConfiguration configuration = createConfiguration(contributions);
        if (deploymentCompositeURI != null && configuration.getContributions().size() > 0) {
            configuration.getContributions().get(0).addDeploymentComposite(createURI(deploymentCompositeURI));
        }
        configuration.setDomainRegistryURI(domainRegistryURI.toString());
        configuration.setDomainURI(getDomainURI(domainRegistryURI));
        return createNode(configuration);
    }

    /**
     * TODO: cleanup node use of registry uri, domain uri, and domain name
     *       so that its consistent across the code base
     */
    public static String getDomainURI(URI configURI) {
        String s = configURI.getHost();
        if (s == null) {
            s = configURI.getSchemeSpecificPart();
            if (s != null) {
                if (s.indexOf('?') > -1) {
                    s = s.substring(0, s.indexOf('?'));
                }
            }
        }
        return s;
    }

    /**
     * The following methods are used by the node launcher
     */
    public final Node createNode(String deploymentCompositeURI, String[] uris, String locations[]) {
        return createNode(deploymentCompositeURI, getContributions(Arrays.asList(uris), Arrays.asList(locations)));
    }

    public final Node createNode(String deploymentCompositeURI, String locations[]) {
        return createNode(deploymentCompositeURI, getContributions(Arrays.asList(locations)));
    }

    public final Node createNode(Reader deploymentCompositeContent, String[] uris, String locations[]) {
        return createNode(deploymentCompositeContent, getContributions(Arrays.asList(uris), Arrays.asList(locations)));
    }

    public final Node createNode(String compositeURI, ClassLoader classLoader) {
        List<String> locations = ContributionLocationHelper.getContributionLocations(classLoader, compositeURI);
        return createNode(compositeURI, locations.toArray(new String[locations.size()]));
    }
    /**
     * ------------------- end of methods -----------------
     */

    /**
     * Create a new SCA node using the list of SCA contributions
     * @param contributions
     * @return
     */
    public Node createNode(Contribution... contributions) {
        NodeConfiguration configuration = createConfiguration(contributions);
        return createNode(configuration);
    }


    /**
     * Creates a new SCA node.
     *
     * @param compositeContent the XML content of the deployment composite
     * @param contributions the URI of the contributions that provides the composites and related artifacts
     * @return a new SCA node.
     */
    public Node createNode(InputStream compositeContent, Contribution... contributions) {
        NodeConfiguration configuration = createConfiguration(contributions);
        if (compositeContent != null && configuration.getContributions().size() > 0) {
            configuration.getContributions().get(0).addDeploymentComposite(compositeContent);
        }
        return createNode(configuration);
    }

    /**
     * Creates a new SCA node.
     *
     * @param compositeContent the XML content of the deployment composite
     * @param contributions the URI of the contributions that provides the composites and related artifacts
     * @return a new SCA node.
     */
    public Node createNode(Reader compositeContent, Contribution... contributions) {
        NodeConfiguration configuration = createConfiguration(contributions);
        if (compositeContent != null && configuration.getContributions().size() > 0) {
            configuration.getContributions().get(0).addDeploymentComposite(compositeContent);
        }
        return createNode(configuration);
    }

    /**
     * Creates a new SCA node using defaults for the contribution location and deployable composites.
     * By default, it uses the Thread context classloader to find META-INF/sca-contribution.xml or
     * META-INF/sca-contribution-generated.xml on the classpath. The locations that contain such resources
     * are taken as the SCA contributions.
     *
     * @return a new SCA node.
     */
    public Node createNode() {
        List<String> locations = new ArrayList<String>();
        locations.addAll(getContributionLocations(null, SCA_CONTRIBUTION_META));
        locations.addAll(getContributionLocations(null, SCA_CONTRIBUTION_GENERATED_META));
        if (locations.isEmpty()) {
            throw new ServiceRuntimeException("No SCA contributions are found on the classpath");
        }
        Contribution[] contributions = getContributions(locations);
        return createNode(contributions);
    }

    private NodeConfiguration createConfiguration(Contribution... contributions) {
        NodeConfigurationFactory factory = this;
        NodeConfiguration configuration = factory.createNodeConfiguration();
        configuration.setDomainURI(properties.getProperty("defaultDomainName"));
        // Make sure a unique node URI is created for the same node factory
        configuration.setURI(generateNodeURI());
        if (contributions != null) {
            for (Contribution c : contributions) {
                configuration.addContribution(c.getURI(), c.getLocation());
            }
        }
        return configuration;
    }

    private static int count = 0;

    protected synchronized String generateNodeURI() {
        return Node.DEFAULT_NODE_URI + (count++);
    }

    private Contribution[] getContributions(List<String> locations) {
        Contribution[] contributions = new Contribution[locations.size()];
        int index = 0;
        for (String location : locations) {
            contributions[index++] = new Contribution(location, location);
        }
        return contributions;
    }

    private Contribution[] getContributions(List<String> uris, List<String> locations) {
        if (uris.size() != locations.size()) {
            throw new IllegalArgumentException("The number of URIs does not match the number of locations");
        }
        Contribution[] contributions = new Contribution[locations.size()];
        for (int i = 0, n = locations.size(); i < n; i++) {
            contributions[i] = new Contribution(uris.get(i), locations.get(i));
        }
        return contributions;
    }

    public void destroy() {
        count = 0;
        instance = null;
        factories.remove(this);
    }
    
    public static List<NodeFactory> getNodeFactories() {
        return factories;
    }

    /**
     * Create a new SCA node based on the configuration
     * @param configuration The configuration of a node
     * @return The SCA node
     */
    public abstract Node createNode(NodeConfiguration configuration);

    /**
     * Create the node configuration from the XML document
     * @param configuration The input stream of the XML document
     * @return The node configuration
     */
    public abstract NodeConfiguration loadConfiguration(InputStream xml, URL base);

    public abstract <T> T getExtensionPointRegistry();

    public abstract void init();
}
