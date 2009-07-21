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
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.apache.tuscany.sca.node.impl.NodeImpl;

public class ServletHostHelper {
    public static final String DOMAIN_NAME_ATTR = "org.apache.tuscany.sca.domain.name";
    public static final String SCA_NODE_ATTRIBUTE = Node.class.getName();
    private static NodeFactory factory;

    private static InputStream openStream(ServletContext servletContext, String location) throws IOException {
        URI uri = URI.create(location);
        if (uri.isAbsolute()) {
            return uri.toURL().openStream();
        } else {
            String path = location;
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            return servletContext.getResourceAsStream(path);
        }
    }

    private static URL getResource(ServletContext servletContext, String location) throws IOException {
        URI uri = URI.create(location);
        if (uri.isAbsolute()) {
            return uri.toURL();
        } else {
            String path = location;
            if (!path.startsWith("/")) {
                path = "/" + path;
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

    @SuppressWarnings("unchecked")
    private static NodeConfiguration getNodeConfiguration(ServletContext servletContext) throws IOException {
        NodeConfiguration configuration = null;
        String nodeConfigURI = servletContext.getInitParameter("node.configuration");
        if (nodeConfigURI != null) {
            configuration = factory.loadConfiguration(openStream(servletContext, nodeConfigURI));
        } else {
            configuration = factory.createNodeConfiguration();
            configuration.setDomainURI(factory.getDomainURI());
            Enumeration<String> names = servletContext.getInitParameterNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                if (name.startsWith("contribution.")) {
                    String contrib = servletContext.getInitParameter(name);
                    if (contrib != null) {
                        configuration.addContribution(getResource(servletContext, contrib));
                    }
                }
            }
            if (configuration.getContributions().isEmpty()) {
                // TODO: Which path should be the default root
                configuration.addContribution(getResource(servletContext, "/"));
            }
            URL composite = getResource(servletContext, "/WEB-INF/web.composite");
            if (composite != null) {
                configuration.getContributions().get(0).addDeploymentComposite(composite);
            }
            String nodeURI = servletContext.getInitParameter("node.uri");
            if (nodeURI == null) {
                nodeURI = new File(servletContext.getRealPath("/")).getName();
            }
            configuration.setURI(nodeURI);
            String domainURI = servletContext.getInitParameter("domain.uri");
            if (domainURI != null) {
                configuration.setDomainURI(domainURI);
            }
        }
        return configuration;
    }

    public static ServletHost init(final ServletContext servletContext) {
        Node node = (Node)servletContext.getAttribute(SCA_NODE_ATTRIBUTE);
        if (node == null) {
            try {
                String domainName = (String)servletContext.getAttribute(DOMAIN_NAME_ATTR);
                if (domainName != null) {
                    factory = NodeFactory.getInstance(domainName);
                } else {
                    factory = NodeFactory.newInstance();
                }
                node = createNode(servletContext);
                servletContext.setAttribute(SCA_NODE_ATTRIBUTE, node);
                getServletHost(node).init(new ServletConfig() {
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
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }
        return getServletHost(node);
    }

    private static WebAppServletHost getServletHost(Node node) {
        NodeImpl nodeImpl = (NodeImpl)node;
        ExtensionPointRegistry eps = nodeImpl.getExtensionPoints();
        ServletHostExtensionPoint servletHosts = eps.getExtensionPoint(ServletHostExtensionPoint.class);
        List<ServletHost> hosts = servletHosts.getServletHosts();
        if (hosts == null || hosts.size() < 1) {
            throw new IllegalStateException("No ServletHost found");
        }
        ServletHost servletHost = hosts.get(0);
        if (!(servletHost instanceof WebAppServletHost)) {
            throw new IllegalStateException("unexpected ServletHost type: " + servletHost);
        }
        return (WebAppServletHost)servletHost;
    }

    private static Node createNode(final ServletContext servletContext) throws ServletException {
        NodeConfiguration configuration;
        try {
            configuration = getNodeConfiguration(servletContext);
        } catch (IOException e) {
            throw new ServletException(e);
        }
        Node node = factory.createNode(configuration).start();
        return node;
    }

    public static void stop(ServletContext servletContext) {
        Node node = (Node)servletContext.getAttribute(ServletHostHelper.SCA_NODE_ATTRIBUTE);
        if (node != null) {
            node.stop();
            servletContext.setAttribute(ServletHostHelper.SCA_NODE_ATTRIBUTE, null);
        }
    }
}
