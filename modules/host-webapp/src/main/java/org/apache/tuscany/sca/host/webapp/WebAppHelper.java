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

package org.apache.tuscany.sca.host.webapp;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;

public class WebAppHelper {
    private static final String ROOT = "/";
    // The prefix for the parameters in web.xml which configure the folders that contain SCA contributions
    private static final String CONTRIBUTIONS = "contributions";
    private static final String DEFAULT_CONTRIBUTIONS = "/WEB-INF/sca-contributions";
    // The prefix for the parameters in web.xml which configure the individual SCA contributions
    private static final String CONTRIBUTION = "contribution";
    private static final String NODE_CONFIGURATION = "node.configuration";
    private static final String WEB_COMPOSITE = "/WEB-INF/web.composite";
    private static final String DOMAIN_URI = "domain.uri";
    private static final String NODE_URI = "node.uri";
    public static final String DOMAIN_NAME_ATTR = "org.apache.tuscany.sca.domain.name";
    public static final String SCA_NODE_ATTRIBUTE = Node.class.getName();
    private static NodeFactory factory;
    private static WebAppServletHost host;

    private static URL getResource(ServletContext servletContext, String location) throws IOException {
        URI uri = URI.create(location);
        if (uri.isAbsolute()) {
            return uri.toURL();
        } else {
            String path = location;
            if (!path.startsWith(ROOT)) {
                path = ROOT + path;
            }
            URL url = servletContext.getResource(path);
            if (url != null && url.getProtocol().equals("jndi")) {
                //this is Tomcat case, we should use getRealPath
                File warRootFile = new File(servletContext.getRealPath(path));
                return warRootFile.toURI().toURL();
            } else {
                //this is Jetty case
                return url;
            }
        }
    }
    
    private static String[] parse(String listOfValues) {
        if (listOfValues == null) {
            return null;
        }
        return listOfValues.split("(\\s|,)+");
    }

    @SuppressWarnings("unchecked")
    private static NodeConfiguration getNodeConfiguration(ServletContext servletContext) throws IOException,
        URISyntaxException {
        NodeConfiguration configuration = null;
        String nodeConfigURI = (String)servletContext.getAttribute(NODE_CONFIGURATION);
        if (nodeConfigURI != null) {
            URL url = getResource(servletContext, nodeConfigURI);
            configuration = factory.loadConfiguration(url.openStream(), url);
        } else {
            configuration = factory.createNodeConfiguration();
            
            
            boolean explicitContributions = false;
            Enumeration<String> names = servletContext.getAttributeNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                if (name.equals(CONTRIBUTION) || name.startsWith(CONTRIBUTION + ".")) {
                    explicitContributions = true;
                    // We need to have a way to select one or more folders within the webapp as the contributions
                    String listOfValues = (String)servletContext.getAttribute(name);
                    if (listOfValues != null) {
                        for (String path : parse(listOfValues)) {
                            if ("".equals(path)) {
                                continue;
                            }
                            File f = new File(getResource(servletContext, path).toURI());
                            configuration.addContribution(f.toURI().toURL());
                        }
                    }
                } else if (name.equals(CONTRIBUTIONS) || name.startsWith(CONTRIBUTIONS + ".")) {
                    explicitContributions = true;
                    String listOfValues = (String)servletContext.getAttribute(name);
                    if (listOfValues != null) {
                        for (String path : parse(listOfValues)) {
                            if ("".equals(path)) {
                                continue;
                            }
                            File f = new File(getResource(servletContext, path).toURI());
                            if (f.isDirectory()) {
                                for (File n : f.listFiles()) {
                                    configuration.addContribution(n.toURI().toURL());
                                }
                            } else {
                                configuration.addContribution(f.toURI().toURL());
                            }
                        }
                    }
                }
            }

            URL composite = getResource(servletContext, WEB_COMPOSITE);
            if (configuration.getContributions().isEmpty() || (!explicitContributions && composite != null)) {
                // TODO: Which path should be the default root
                configuration.addContribution(getResource(servletContext, ROOT));
            }
            if (composite != null) {
                configuration.getContributions().get(0).addDeploymentComposite(composite);
            }
            if (!explicitContributions) {
                URL url = getResource(servletContext, DEFAULT_CONTRIBUTIONS);
                if (url != null) {
                    File f = new File(url.toURI());
                    if (f.isDirectory()) {
                        for (File n : f.listFiles()) {
                            configuration.addContribution(n.toURI().toURL());
                        }
                    }
                }
            }
            String nodeURI = (String)servletContext.getAttribute(NODE_URI);
            if (nodeURI == null) {
                nodeURI = new File(servletContext.getRealPath(ROOT)).getName();
            }
            configuration.setURI(nodeURI);
            String domainURI = (String)servletContext.getAttribute(DOMAIN_URI);
            if (domainURI != null) {
                configuration.setDomainURI(domainURI);
            } else {
                domainURI = servletContext.getInitParameter("org.apache.tuscany.sca.defaultDomainURI");
                if (domainURI != null) {
                    configuration.setDomainURI(getDomainName(domainURI));
                    configuration.setDomainRegistryURI(domainURI);
                }
            }
        }
        return configuration;
    }

    // TODO: Temp for now to get the old samples working till i clean up all the domain uri/name after the ML discussion.
    private static String getDomainName(String configURI) {
        String domainName;
        if (configURI.startsWith("tuscany:vm:")) {
            domainName = configURI.substring("tuscany:vm:".length());
        } else if (configURI.startsWith("tuscany:")) {
            int i = configURI.indexOf('?');
            if (i == -1) {
                domainName = configURI.substring("tuscany:".length());
            } else {
                domainName = configURI.substring("tuscany:".length(), i);
            }
        } else {
            domainName = configURI;
        }
        return domainName;
    }

    public synchronized static ServletHost init(final ServletContext servletContext) {
        if (host == null) {
            try {

                String configValue = servletContext.getInitParameter("org.apache.tuscany.sca.config");
                if (configValue != null) {
                    factory = NodeFactory.newInstance(configValue);
                } else {
                    factory = NodeFactory.newInstance();
                }
                
                // Add ServletContext as a utility
                ExtensionPointRegistry registry = factory.getExtensionPointRegistry();
                UtilityExtensionPoint utilityExtensionPoint = registry.getExtensionPoint(UtilityExtensionPoint.class);
                utilityExtensionPoint.addUtility(ServletContext.class, servletContext);
                
                ServletHostExtensionPoint servletHosts = registry.getExtensionPoint(ServletHostExtensionPoint.class);
                servletHosts.setWebApp(true);

                // TODO: why are the init parameters copied to the attributes?
                for (Enumeration<?> e = servletContext.getInitParameterNames(); e.hasMoreElements();) {
                    String name = (String)e.nextElement();
                    String value = servletContext.getInitParameter(name);
                    servletContext.setAttribute(name, value);
                }

                host = getServletHost(servletContext);

            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }
        Node node = (Node)servletContext.getAttribute(SCA_NODE_ATTRIBUTE);
        if (node == null) {
            try {
                node = createAndStartNode(servletContext);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
            servletContext.setAttribute(SCA_NODE_ATTRIBUTE, node);
        }

        return host;
    }

    private static WebAppServletHost getServletHost(final ServletContext servletContext) throws ServletException {
        WebAppServletHost host = getServletHost(factory);
        host.init(new ServletConfig() {
            public String getInitParameter(String name) {
                return servletContext.getInitParameter(name);
            }

            public Enumeration<?> getInitParameterNames() {
                return servletContext.getInitParameterNames();
            }

            public ServletContext getServletContext() {
                return servletContext;
            }

            public String getServletName() {
                return servletContext.getServletContextName();
            }
        });
        return host;
    }

    private static WebAppServletHost getServletHost(NodeFactory factory) {
        ExtensionPointRegistry registry = factory.getExtensionPointRegistry();
        return (WebAppServletHost)org.apache.tuscany.sca.host.http.ServletHostHelper.getServletHost(registry);
    }

    private static Node createAndStartNode(final ServletContext servletContext) throws ServletException {
        NodeConfiguration configuration;
        try {
            configuration = getNodeConfiguration(servletContext);
        } catch (IOException e) {
            throw new ServletException(e);
        } catch (URISyntaxException e) {
            throw new ServletException(e);
        }
        Node node = factory.createNode(configuration).start();
        return node;
    }

    public static void stop(ServletContext servletContext) {
        Node node = (Node)servletContext.getAttribute(SCA_NODE_ATTRIBUTE);
        if (node != null) {
            node.stop();
            servletContext.setAttribute(SCA_NODE_ATTRIBUTE, null);
        }
    }

    public static NodeFactory getNodeFactory() {
        return factory;
    }
}
