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

import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.oasisopen.sca.ServiceRuntimeException;

public class WebAppHelper {
    private static final String ROOT = "/";
    // The prefix for the parameters in web.xml which configure the folders that contain SCA contributions
    private static final String CONTRIBUTIONS = "contributions";
    private static final String DEFAULT_CONTRIBUTIONS = "/WEB-INF/sca-contributions";
    // The prefix for the parameters in web.xml which configure the individual SCA contributions
    private static final String CONTRIBUTION = "contribution";
    private static final String NODE_CONFIGURATION = "node.configuration";
    private static final String DOMAIN_URI = "domain.uri";
    private static final String DOMAIN_REGISTRY_URI = "domain.registry.uri";
    private static final String NODE_URI = "node.uri";
    private static final String COMPOSITE_URI = "composite.uri";
    public static final String DOMAIN_NAME_ATTR = "org.apache.tuscany.sca.domain.name";
    public static final String DOMAIN_URI_PROP = "domainURI";
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

    // TODO: Temp for now to get the old samples working till i clean up all the domain uri/name after the ML discussion.
    private static String getDomainName(String configURI) {
        String domainName;
        if (configURI.startsWith("tuscany:vm:")) {
            domainName = configURI.substring("tuscany:vm:".length());
        } else if (configURI.startsWith("uri:")) {
            int i = configURI.indexOf('?');
            if (i == -1) {
                domainName = configURI.substring("uri:".length());
            } else {
                domainName = configURI.substring("uri:".length(), i);
            }
        } else {
            domainName = configURI;
        }
        return domainName;
    }

    public static WebAppServletHost getServletHost() {
        return host;
    }

    public static Node init(final WebContextConfigurator configurator) {
        synchronized (configurator) {

            bootstrapRuntime(configurator);
            Node node = (Node)configurator.getAttribute(SCA_NODE_ATTRIBUTE);
            if (node == null) {
                try {
                    node = createAndStartNode(configurator);
                } catch (ServletException e) {
                    throw new ServiceRuntimeException(e);
                }
                configurator.setAttribute(SCA_NODE_ATTRIBUTE, node);
            }
            return node;
        }
    }

    /**
     * Bootstrap the Tuscany runtime for the given scope
     * @param configurator
     */
    private synchronized static void bootstrapRuntime(final WebContextConfigurator configurator) {
        if (host == null) {
            try {

                String configValue = configurator.getInitParameter("org.apache.tuscany.sca.config");
                if (configValue != null) {
                    factory = NodeFactory.newInstance(configValue);
                } else {
                    factory = NodeFactory.newInstance();
                }

                // Add ServletContext as a utility
                ExtensionPointRegistry registry = factory.getExtensionPointRegistry();
                UtilityExtensionPoint utilityExtensionPoint = registry.getExtensionPoint(UtilityExtensionPoint.class);
                utilityExtensionPoint.addUtility(ServletContext.class, configurator.getServletContext());

                ServletHostExtensionPoint servletHosts = registry.getExtensionPoint(ServletHostExtensionPoint.class);
                servletHosts.setWebApp(true);

                host = getServletHost(configurator);

            } catch (ServletException e) {
                throw new ServiceRuntimeException(e);
            }
        }
    }

    private static WebAppServletHost getServletHost(final WebContextConfigurator configurator) throws ServletException {
        ExtensionPointRegistry registry = factory.getExtensionPointRegistry();
        WebAppServletHost host =
            (WebAppServletHost)org.apache.tuscany.sca.host.http.ServletHostHelper.getServletHost(registry);

        host.init(new ServletConfig() {
            public String getInitParameter(String name) {
                return configurator.getInitParameter(name);
            }

            public Enumeration<?> getInitParameterNames() {
                return configurator.getInitParameterNames();
            }

            public ServletContext getServletContext() {
                return configurator.getServletContext();
            }

            public String getServletName() {
                return configurator.getServletContext().getServletContextName();
            }
        });
        return host;
    }

    private static Node createAndStartNode(final WebContextConfigurator configurator) throws ServletException {
        NodeConfiguration configuration = null;
        try {
            configuration = getNodeConfiguration(configurator);
        } catch (IOException e) {
            throw new ServletException(e);
        } catch (URISyntaxException e) {
            throw new ServletException(e);
        }
        Node node = null;
        if (configuration != null) {
            node = factory.createNode(configuration).start();
        }
        return node;
    }

    public static void stop(WebContextConfigurator configurator) {
        Node node = null;
        if (configurator != null) {
            node = (Node)configurator.getAttribute(SCA_NODE_ATTRIBUTE);
        }
        if (node != null) {
            node.stop();
            configurator.setAttribute(SCA_NODE_ATTRIBUTE, null);
        }
    }

    public static void destroy() {
        if (factory != null) {
            factory.destroy();
        }
        factory = null;
        host = null;
    }

    public static NodeFactory getNodeFactory() {
        return factory;
    }

    private static String getDefaultComposite(WebContextConfigurator configurator) {
        String name = configurator.getName();
        if ("".equals(name)) {
            return "/WEB-INF/web.composite";
        } else {
            return "/WEB-INF/" + name + "/servlet.composite";
        }
    }

    private static NodeConfiguration getNodeConfiguration(WebContextConfigurator configurator) throws IOException,
        URISyntaxException {
        NodeConfiguration configuration = null;
        String nodeConfigURI = configurator.getInitParameter(NODE_CONFIGURATION);
        ServletContext servletContext = configurator.getServletContext();
        if (nodeConfigURI != null) {
            URL url = getResource(servletContext, nodeConfigURI);
            configuration = factory.loadConfiguration(url.openStream(), url);
        } else {
            configuration = factory.createNodeConfiguration();

            boolean explicitContributions = false;
            Enumeration<String> names = configurator.getInitParameterNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                if (name.equals(CONTRIBUTION) || name.startsWith(CONTRIBUTION + ".")) {
                    explicitContributions = true;
                    // We need to have a way to select one or more folders within the webapp as the contributions
                    String listOfValues = configurator.getInitParameter(name);
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
                    String listOfValues = (String)configurator.getInitParameter(name);
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

            String compositeURI = configurator.getInitParameter(COMPOSITE_URI);
            if (compositeURI == null) {
                compositeURI = getDefaultComposite(configurator);
            }
            URL composite = getResource(servletContext, compositeURI);
            if (configuration.getContributions().isEmpty() || (!explicitContributions && composite != null)) {
                if ("".equals(configurator.getName())) {
                    // Add the root of the web application
                    configuration.addContribution(getResource(servletContext, ROOT));
                } else {
                    // Add a dummy contribution
                    configuration.addContribution(URI.create("sca:contributions/" + configurator.getName()), null);
                }
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
            String nodeURI = configurator.getInitParameter(NODE_URI);
            if (nodeURI == null) {
                nodeURI = getResource(servletContext, ROOT).getPath() + configurator.getName();
            }
            configuration.setURI(nodeURI);
            String domainURI = configurator.getInitParameter(DOMAIN_URI);
            if (domainURI != null) {
                configuration.setDomainURI(domainURI);
            } else {
                domainURI = configurator.getInitParameter("org.apache.tuscany.sca.defaultDomainURI");
                if (domainURI == null) {
                    domainURI = System.getProperty(DOMAIN_URI_PROP);
                }
                if (domainURI != null) {
                    configuration.setDomainURI(getDomainName(domainURI));
                    configuration.setDomainRegistryURI(domainURI);
                }
            }
            String domainRegistryURI = configurator.getInitParameter(DOMAIN_REGISTRY_URI);
            if (domainRegistryURI != null) {
                configuration.setDomainRegistryURI(domainRegistryURI);
            }
        }
        configuration.setAttribute(ServletContext.class.getName(), servletContext);
        if(configurator instanceof ServletConfigurator) {
            configuration.setAttribute(Servlet.class.getName(), ((ServletConfigurator) configurator).servlet);
        }
        return configuration;
    }

    public static WebContextConfigurator getConfigurator(FilterConfig config) {
        return new FilterConfigurator(config);
    }

    public static WebContextConfigurator getConfigurator(ServletContext context) {
        return new ServletContextConfigurator(context);
    }

    public static WebContextConfigurator getConfigurator(Servlet context) {
        return new ServletConfigurator(context);
    }

    public static class FilterConfigurator implements WebContextConfigurator {
        private FilterConfig config;

        public FilterConfigurator(FilterConfig config) {
            super();
            this.config = config;
        }

        public String getInitParameter(String name) {
            String value = config.getInitParameter(name);
            if (value == null) {
                return config.getServletContext().getInitParameter(name);
            } else {
                return value;
            }
        }

        public Enumeration<String> getInitParameterNames() {
            Enumeration<String> names = config.getInitParameterNames();
            if (!names.hasMoreElements()) {
                return getServletContext().getInitParameterNames();
            } else {
                return names;
            }
        }

        public ServletContext getServletContext() {
            return config.getServletContext();
        }

        public void setAttribute(String name, Object value) {
            String prefix = "filter:" + config.getFilterName() + ":";
            getServletContext().setAttribute(prefix + name, value);
        }

        public <T> T getAttribute(String name) {
            String prefix = "filter:" + config.getFilterName() + ":";
            return (T)getServletContext().getAttribute(prefix + name);
        }

        public String getName() {
            return "";
        }

    }

    public static class ServletContextConfigurator implements WebContextConfigurator {
        private ServletContext context;

        public ServletContextConfigurator(ServletContext context) {
            super();
            this.context = context;
        }

        public String getInitParameter(String name) {
            return context.getInitParameter(name);
        }

        public Enumeration<String> getInitParameterNames() {
            return context.getInitParameterNames();
        }

        public ServletContext getServletContext() {
            return context;
        }

        public void setAttribute(String name, Object value) {
            context.setAttribute(name, value);
        }

        public <T> T getAttribute(String name) {
            return (T)context.getAttribute(name);
        }

        public String getName() {
            return "";
        }
    }

    public static class ServletConfigurator implements WebContextConfigurator {
        private ServletConfig config;
        private Servlet servlet;

        public ServletConfigurator(Servlet servlet) {
            super();
            this.servlet = servlet;
            this.config = servlet.getServletConfig();
        }

        public String getInitParameter(String name) {
            String value = config.getInitParameter(name);
            return value;
            /*
            if (value == null) {
                return config.getServletContext().getInitParameter(name);
            } else {
                return value;
            }
            */
        }

        public Enumeration<String> getInitParameterNames() {
            return config.getInitParameterNames();
        }

        public ServletContext getServletContext() {
            return config.getServletContext();
        }

        public void setAttribute(String name, Object value) {
            String prefix = "servlet:" + config.getServletName() + ":";
            getServletContext().setAttribute(prefix + name, value);
        }

        public <T> T getAttribute(String name) {
            String prefix = "servlet:" + config.getServletName() + ":";
            return (T)getServletContext().getAttribute(prefix + name);
        }

        public String getName() {
            return config.getServletName();
        }

    }
}
