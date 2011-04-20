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

package org.apache.tuscany.sca.web.javascript.dojo;

import java.net.URI;
import java.util.logging.Logger;

import javax.servlet.Servlet;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostHelper;
import org.apache.tuscany.sca.host.http.ServletMappingException;
import org.apache.tuscany.sca.implementation.widget.javascript.WidgetImplementationJavascriptProvider;

public class DojoWidgetJavascriptProvider implements WidgetImplementationJavascriptProvider {
    private final static Logger logger = Logger.getLogger(DojoWidgetJavascriptProvider.class.getName());

    private static final String dojoBaseUri = URI.create("/dojo").toString();
    private static final String dojoUri = URI.create("/dojo/*").toString();

    private static final String dojoxBaseUri = URI.create("/dojox").toString();
    private static final String dojoxUri = URI.create("/dojox/*").toString();

    private static final String dijitBaseUri = URI.create("/dijit").toString();
    private static final String dijitUri = URI.create("/dijit/*").toString();
    
    private static final String tuscanyBaseUri = URI.create("/tuscany").toString();
    private static final String tuscanyUri = URI.create("/tuscany/*").toString();

    private static int counter = 0;

    private ServletHost servletHost;

    public DojoWidgetJavascriptProvider(ExtensionPointRegistry registry) {
        this.servletHost = ServletHostHelper.getServletHost(registry);
    }

    public void start() {

        if (servletHost == null) {
            throw new IllegalStateException("Can't find ServletHost reference !");
        }

        Servlet servlet = null;

        servlet = servletHost.getServletMapping(dojoBaseUri);
        if(servlet == null) {
            try {
                DojoResourceServlet baseResourceServlet = new DojoResourceServlet();
                servletHost.addServletMapping(dojoBaseUri, baseResourceServlet);

                DojoResourceServlet resourceServlet = new DojoResourceServlet();
                servletHost.addServletMapping(dojoUri, resourceServlet);
            } catch (ServletMappingException me ) {
                logger.warning("Dojo already registered at :" + dojoBaseUri);
            }
        }

        servlet = servletHost.getServletMapping(dojoxBaseUri);
        if(servlet == null) {
            try {
                DojoResourceServlet baseResourceServlet = new DojoResourceServlet();
                servletHost.addServletMapping(dojoxBaseUri, baseResourceServlet);

                DojoResourceServlet resourceServlet = new DojoResourceServlet();
                servletHost.addServletMapping(dojoxUri, resourceServlet);
            } catch (ServletMappingException me ) {
                logger.warning("Dojox already registered at :" + dojoxBaseUri);
            }
        }

        servlet = servletHost.getServletMapping(dijitBaseUri);
        if(servlet == null) {
            try {
                DojoResourceServlet baseResourceServlet = new DojoResourceServlet();
                servletHost.addServletMapping(dijitBaseUri, baseResourceServlet);

                DojoResourceServlet resourceServlet = new DojoResourceServlet();
                servletHost.addServletMapping(dijitUri, resourceServlet);
            } catch (ServletMappingException me ) {
                logger.warning("Dijit already registered at :" + dijitBaseUri);
            }
        }
        
        servlet = servletHost.getServletMapping(tuscanyBaseUri);
        if(servlet == null) {
            try {
                DojoResourceServlet baseResourceServlet = new DojoResourceServlet();
                servletHost.addServletMapping(tuscanyBaseUri, baseResourceServlet);

                DojoResourceServlet resourceServlet = new DojoResourceServlet();
                servletHost.addServletMapping(tuscanyUri, resourceServlet);
            } catch (ServletMappingException me ) {
                logger.warning("Tuscany dojo extensions already registered at :" + tuscanyBaseUri);
            }
        }

        counter += 1;
        logger.info("Registered Dojo and Tuscany Dojo extensions (counter=" + counter + ")");
    }

    public void stop() {
        if(counter > 0) {
            counter = counter -= 1;
            logger.info("Unregistering Dojo and Tuscany Dojo extensions (counter=" + counter + ")");
        }

        Servlet servlet = servletHost.getServletMapping(dojoBaseUri);
        if(servlet != null && counter == 0) {
            servletHost.removeServletMapping(dojoBaseUri);
            servletHost.removeServletMapping(dojoUri);

            servletHost.removeServletMapping(dojoxBaseUri);
            servletHost.removeServletMapping(dojoxUri);

            servletHost.removeServletMapping(tuscanyBaseUri);
            servletHost.removeServletMapping(tuscanyUri);
        }

        servletHost = null;

    }
}
