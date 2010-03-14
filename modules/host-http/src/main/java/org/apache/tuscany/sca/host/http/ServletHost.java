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
package org.apache.tuscany.sca.host.http;

import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;

/**
 * Interface implemented by host environments that allow Servlets to be
 * registered. 
 * <p/> 
 * This interface allows a system service to register a Servlet
 * to handle inbound requests.
 * 
 * @version $Rev$ $Date$
 */
public interface ServletHost {
    
    /**
     * Sets the default port for the server.
     * 
     * @param port the default port
     */
    void setDefaultPort(int port);

    /**
     * Returns the default port for the server.
     * 
     * @return the default port
     */
    int getDefaultPort();
    
    /**
     * Add a mapping for an instance of a Servlet. This requests that the
     * Servlet container direct all requests to the designated mapping to the
     * supplied Servlet instance.
     * 
     * @param uri the URI-mapping for the Servlet
     * @param servlet the Servlet that should be invoked
     * @return The deployed URI
     * @throws ServletMappingException
     */
    String addServletMapping(String uri, Servlet servlet) throws ServletMappingException;
    
    /**
     * Add a mapping for an instance of a Servlet. This requests that the
     * Servlet container direct all requests to the designated mapping to the
     * supplied Servlet instance. SecurityContext can be passed to enable
     * QoS services such as Confidentiality (SSL) and Authentication/Authorization
     * 
     * @param uri the URI-mapping for the Servlet
     * @param servlet the Servlet that should be invoked
     * @param securityContext the SecurityContext to enable QoS services
     * @return The deployed URI
     * @throws ServletMappingException
     */    
    String addServletMapping(String uri, Servlet servlet, SecurityContext securityContext) throws ServletMappingException;    

    /**
     * Remove a Servlet mapping. This directs the Servlet container not to direct
     * any more requests to a previously registered Servlet.
     * 
     * @param uri the URI-mapping for the Servlet
     * @return the Servlet that was registered to the mapping, null if nothing
     *         was registered to the mapping
     * @throws ServletMappingException
     */
    Servlet removeServletMapping(String uri) throws ServletMappingException;

    /**
     * Returns the Servlet mapped to the given URI.
     * 
     * @param uri the URI-mapping for the Servlet
     * @return the Servlet registered with the mapping
     * @throws ServletMappingException
     */
    Servlet getServletMapping(String uri) throws ServletMappingException;

    /**
     * Returns a Servlet request dispatcher for the Servlet mapped to the specified URI.
     * 
     * @param uri the URI mapped to a Servlet
     * @return a RequestDispatcher that can be used to dispatch requests to
     * that Servlet
     * @throws ServletMappingException
     */
    RequestDispatcher getRequestDispatcher(String uri) throws ServletMappingException;

    /**
     * Returns the portion of the request URI that indicates the context of the request
     * 
     * @return a String specifying the portion of the request URI that indicates the context of the request
     */
    String getContextPath();

    /**
     * Sets the portion of the request URI that indicates the context of the request
     * 
     * @param path the context path
     */
    void setContextPath(String path);

    /**
     * Returns the complete URL mapped to the specified URI. 
     * @return the URL mapped to the specified URI
     */
    URL getURLMapping(String uri, SecurityContext securityContext);

    /**
     * Set an attribute in the application ServletContext 
     * @param name the name of the attribute
     * @param value the attribute value
     */
    void setAttribute(String name, Object value);
    
    /**
     * Returns the name that identify the server type (e.g jetty)
     * @return
     */
    String getName();
}
