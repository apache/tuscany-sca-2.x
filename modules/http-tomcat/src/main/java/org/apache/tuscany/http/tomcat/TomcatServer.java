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
package org.apache.tuscany.http.tomcat;

import javax.servlet.Servlet;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Embedded;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.mapper.MappingData;
import org.apache.tuscany.http.ServletHostExtension;
import org.apache.tuscany.http.ServletMappingException;

/**
 * A Tomcat based implementation of ServletHost.
 * 
 * @version $Rev$ $Date$
 */
public class TomcatServer implements ServletHostExtension {

    private Embedded tomcat;
    private Host host;
    Connector connector;
    private boolean started;

    public void init() throws ServletMappingException {
        tomcat = new Embedded();

        // Create an engine
        Engine engine = tomcat.createEngine();
        engine.setDefaultHost("localhost");

        // Create a host
        host = tomcat.createHost("localhost", "");
        engine.addChild(host);

        // Create the ROOT context
        Context context = tomcat.createContext("", "");
        host.addChild(context);

        // Install the engine
        tomcat.addEngine(engine);

    }

    public void destroy() throws ServletMappingException {

        // Stop the server
        if (started) {
            try {
                tomcat.stop();
            } catch (LifecycleException e) {
                throw new ServletMappingException(e);
            }
            started = false;
        }
    }

    public void addServletMapping(int port, String mapping, Servlet servlet) {

        // Install a default HTTP connector
        if (connector == null) {
            //TODO support multiple connectors on different ports
            try {
                connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
                connector.setPort(port);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            tomcat.addConnector(connector);
        }
        
        // Register the servlet mapping
        Context context = host.map(mapping);
        Wrapper wrapper = new ServletWrapper(servlet);
        wrapper.setName(mapping);
        wrapper.addMapping(mapping);
        context.addChild(wrapper);
        context.addServletMapping(mapping, mapping);

        // Start Tomcat
        try {
            if (!started) {
                tomcat.start();
                started = true;
            }

        } catch (LifecycleException e) {
            // TODO use a better runtime exception
            throw new RuntimeException(e);
        }
    }

    public Servlet removeServletMapping(int port, String mapping) {
        Context context = host.map(mapping);
        MappingData md = new MappingData();
        MessageBytes mb = MessageBytes.newInstance();
        mb.setString(mapping);
        try {
            context.getMapper().map(mb, md);
        } catch (Exception e) {
            return null;
        }
        if (md.wrapper instanceof ServletWrapper) {
            ServletWrapper servletWrapper = (ServletWrapper)md.wrapper;
            context.removeServletMapping(mapping);
            context.removeChild(servletWrapper);
            return servletWrapper.getServlet();
        } else {
            return null;
        }
    }

}
