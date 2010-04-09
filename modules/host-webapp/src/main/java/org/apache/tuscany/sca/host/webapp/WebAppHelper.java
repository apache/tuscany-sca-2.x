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
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;

public class WebAppHelper {
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
        String nodeConfigURI = (String) servletContext.getAttribute("node.configuration");
        if (nodeConfigURI != null) {
            URL url = getResource(servletContext, nodeConfigURI);
            configuration = factory.loadConfiguration(url.openStream(), url);
        } else {
            configuration = factory.createNodeConfiguration();
            Enumeration<String> names = servletContext.getAttributeNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                if (name.startsWith("contribution.")) {
                    String contrib = (String) servletContext.getAttribute(name);
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
            String nodeURI = (String) servletContext.getAttribute("node.uri");
            if (nodeURI == null) {
                nodeURI = new File(servletContext.getRealPath("/")).getName();
            }
            configuration.setURI(nodeURI);
            String domainURI = (String) servletContext.getAttribute("domain.uri");
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
            } else{
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
                factory = NodeFactory.getInstance();
                ExtensionPointRegistry registry = factory.getExtensionPointRegistry();
                ServletHostExtensionPoint servletHosts = registry.getExtensionPoint(ServletHostExtensionPoint.class);
                servletHosts.setWebApp(true);
                for (Enumeration<String> e = servletContext.getInitParameterNames(); e.hasMoreElements();) {
                    String name = e.nextElement();
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
        WebAppServletHost host = (WebAppServletHost) getServletHost(factory);
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
        return (WebAppServletHost) org.apache.tuscany.sca.host.http.ServletHostHelper.getServletHost(registry);
    }

    private static Node createAndStartNode(final ServletContext servletContext) throws ServletException {
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
        Node node = (Node)servletContext.getAttribute(WebAppHelper.SCA_NODE_ATTRIBUTE);
        if (node != null) {
            node.stop();
            servletContext.setAttribute(WebAppHelper.SCA_NODE_ATTRIBUTE, null);
        }
    }
}
