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
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.impl.NodeImpl;

public class ServletHostHelper {
    private static final Logger logger = Logger.getLogger(ServletHostHelper.class.getName());

    public static final String SCA_NODE_ATTRIBUTE = Node.class.getName();

    public static void init(ServletConfig servletConfig) {
        init(servletConfig.getServletContext());
    }

    public static ServletHost init(final ServletContext servletContext) {
        Node node = (Node)servletContext.getAttribute(SCA_NODE_ATTRIBUTE);
        if (node == null) {
            try {
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
                    }});
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }
        return getServletHost(node);
    }

    private static WebAppServletHost getServletHost(Node node) {
        NodeImpl nodeImpl = (NodeImpl) node;
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
        return (WebAppServletHost) servletHost;
    }

    private static Node createNode(final ServletContext servletContext) throws ServletException {
        // String contextPath = initContextPath(servletContext);
        String contributionRoot = getContributionRoot(servletContext);
        NodeFactory factory = NodeFactory.newInstance();
        InputStream webComposite = getWebComposite(servletContext);
        Node node = factory.createNode(webComposite, new Contribution(contributionRoot, contributionRoot));
        node.start();
        return node;
    }

    private static InputStream getWebComposite(ServletContext servletContext) {
        return servletContext.getResourceAsStream("/WEB-INF/web.composite");
    }

    private static String getContributionRoot(ServletContext servletContext) {
        String contributionRoot = null;
        try {

            InitialContext ic = new InitialContext();
            URL repoURL = (URL)ic.lookup("java:comp/env/url/contributions");

            contributionRoot = repoURL.toString();

        } catch (NamingException e) {

            // ignore exception and use default location

            try {

                String root = servletContext.getInitParameter("contributionRoot");
                if (root == null || root.length() < 1) {
                    root = "/";
                }
                URL rootURL = servletContext.getResource(root);
                if (rootURL.getProtocol().equals("jndi")) {
                    //this is Tomcat case, we should use getRealPath
                    File warRootFile = new File(servletContext.getRealPath(root));
                    contributionRoot = warRootFile.toURI().toString();
                } else {
                    //this is Jetty case
                    contributionRoot = rootURL.toString();
                }

            } catch (MalformedURLException mf) {
                //ignore, pass null
            }
        }

        logger.info("contributionRoot: " + contributionRoot);
        return contributionRoot;
    }

    /**
     * Initializes the contextPath
     * The 2.5 Servlet API has a getter for this, for pre 2.5 Servlet
     * containers use an init parameter.
     */
    @SuppressWarnings("unchecked")
    private static String initContextPath(ServletContext context) {
        String contextPath;
        if (Collections.list(context.getInitParameterNames()).contains("contextPath")) {
            contextPath = context.getInitParameter("contextPath");
        } else {
            try {
                // Try to get the method anyway since some ServletContext impl has this method even before 2.5
                Method m = context.getClass().getMethod("getContextPath", new Class[] {});
                contextPath = (String)m.invoke(context, new Object[] {});
            } catch (Exception e) {
                logger.warning("Servlet level is: " + context.getMajorVersion() + "." + context.getMinorVersion());
                throw new IllegalStateException("'contextPath' init parameter must be set for pre-2.5 servlet container");
            }
        }
        logger.info("ContextPath: " + contextPath);
        return contextPath;
    }

    public static void stop(ServletContext servletContext) {
        Node node = (Node) servletContext.getAttribute(ServletHostHelper.SCA_NODE_ATTRIBUTE);
        if (node != null) {
            node.stop();
            servletContext.setAttribute(ServletHostHelper.SCA_NODE_ATTRIBUTE, null);
        }
    }
}
