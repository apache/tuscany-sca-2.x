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

package org.apache.tuscany.sca.binding.rest.provider;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.validation.SchemaFactory;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.extensibility.ClassLoaderContext;
import org.apache.wink.common.internal.registry.ProvidersRegistry;
import org.apache.wink.common.internal.registry.metadata.MethodMetadata;
import org.apache.wink.server.internal.DeploymentConfiguration;
import org.apache.wink.server.internal.RequestProcessor;
import org.apache.wink.server.internal.registry.ResourceRecord;
import org.apache.wink.server.internal.servlet.RestServlet;

/**
 *
 */
public class TuscanyRESTServlet extends RestServlet {
    private static final long serialVersionUID = 89997233133964915L;
    private ExtensionPointRegistry registry;
    private Class<?> resourceClass;
    private boolean fixed;

    public TuscanyRESTServlet(ExtensionPointRegistry registry, Class<?> resourceClass) {
        super();
        this.registry = registry;
        this.resourceClass = resourceClass;
    }
    
    public void init() throws ServletException {
        ClassLoader cl =
            ClassLoaderContext.setContextClassLoader(Thread.currentThread().getContextClassLoader(),
                                                     registry.getServiceDiscovery(),
                                                     "/META-INF/server/wink-providers");
        try {
            super.init();
        } finally {
            if (cl != null) {
                // return previous classLoader
                Thread.currentThread().setContextClassLoader(cl);
            }
        }
    }

    @Override
    public DeploymentConfiguration getDeploymentConfiguration() throws ClassNotFoundException, InstantiationException,
        IllegalAccessException, IOException {


        // setup proper classLoader to work on OSGi environment
        ClassLoader cl =
            ClassLoaderContext.setContextClassLoader(Thread.currentThread().getContextClassLoader(),
                                                     registry.getServiceDiscovery(),
                                                     "javax.ws.rs.ext.RuntimeDelegate",
                                                     "/META-INF/wink-alternate-shortcuts.properties",
                                                     "/META-INF/server/wink-providers");

        DeploymentConfiguration config = null;
        try {
            config = super.getDeploymentConfiguration();
        } finally {
            if (cl != null) {
                // return previous classLoader
                Thread.currentThread().setContextClassLoader(cl);
            }
        }

        // [rfeng] FIXME: This is a hack to fool Apache wink to not remove the servlet path
        config.setFilterConfig(new FilterConfig() {

            public ServletContext getServletContext() {
                return getServletContext();
            }

            public Enumeration getInitParameterNames() {
                return getInitParameterNames();
            }

            public String getInitParameter(String arg0) {
                return getInitParameter(arg0);
            }

            public String getFilterName() {
                return getServletName();
            }
        });

        ProvidersRegistry providers = config.getProvidersRegistry();
        providers.addProvider(new DataBindingJAXRSReader(registry), 0.001, true);
        providers.addProvider(new DataBindingJAXRSWriter(registry), 0.001, true);

        return config;
    }

    private synchronized void fixMediaTypes(DeploymentConfiguration config) {
        if (fixed) {
            return;
        }
        // FIXME: A hacky workaround for https://issues.apache.org/jira/browse/TUSCANY-3572
        ResourceRecord record = config.getResourceRegistry().getRecord(resourceClass);

        for (MethodMetadata methodMetadata : record.getMetadata().getResourceMethods()) {
            String method = methodMetadata.getHttpMethod();
            if (HttpMethod.GET.equals(method) || HttpMethod.HEAD.equals(method) || HttpMethod.DELETE.equals(method)) {
                methodMetadata.addConsumes(MediaType.APPLICATION_OCTET_STREAM_TYPE);
                methodMetadata.addConsumes(MediaType.WILDCARD_TYPE);
            }
            if (HttpMethod.HEAD.equals(method) || HttpMethod.DELETE.equals(method)) {
                methodMetadata.addProduces(MediaType.APPLICATION_OCTET_STREAM_TYPE);
                methodMetadata.addConsumes(MediaType.WILDCARD_TYPE);
            }
        }
        for (MethodMetadata methodMetadata : record.getMetadata().getSubResourceMethods()) {
            String method = methodMetadata.getHttpMethod();
            if (HttpMethod.GET.equals(method) || HttpMethod.HEAD.equals(method) || HttpMethod.DELETE.equals(method)) {
                methodMetadata.addConsumes(MediaType.APPLICATION_OCTET_STREAM_TYPE);
                methodMetadata.addConsumes(MediaType.WILDCARD_TYPE);
            }
            if (HttpMethod.HEAD.equals(method) || HttpMethod.DELETE.equals(method)) {
                methodMetadata.addProduces(MediaType.APPLICATION_OCTET_STREAM_TYPE);
                methodMetadata.addConsumes(MediaType.WILDCARD_TYPE);
            }
        }
        fixed = true;
    }

    @Override
    public RequestProcessor getRequestProcessor() {
        RequestProcessor processor = super.getRequestProcessor();
        // The 1st call returns null
        if (processor != null) {
            fixMediaTypes(processor.getConfiguration());
        }
        return processor;
    }

}
