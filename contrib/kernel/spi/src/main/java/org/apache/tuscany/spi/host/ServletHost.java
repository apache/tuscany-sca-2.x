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
package org.apache.tuscany.spi.host;

import javax.servlet.Servlet;

/**
 * Interface implemented by host environments that allow Servlets to be registered.
 * <p/>
 * This interface allows an SCA system service to register a servlet to handle inbound requests.
 *
 * @version $Rev$ $Date$
 */
public interface ServletHost {
    /**
     * Register a mapping for an instance of a Servlet. This requests that the servlet container direct all requests to
     * the designated mapping to the supplied Servlet instance.
     *
     * @param mapping the uri-mapping for the Servlet
     * @param servlet the Servlet that should be invoked
     */
    void registerMapping(String mapping, Servlet servlet);

    /**
     * Unregister a servlet mapping. This directs the servlet contain not to direct any more requests to a previously
     * registered Servlet.
     *
     * @param mapping the uri-mapping for the Servlet
     * @return the servlet that was registered to the mapping, null if nothing was registered to the mapping
     */
    Servlet unregisterMapping(String mapping);
    
    /**
     * Check to see if a mapping exists.
     * 
     * @param mapping  the uri-mapping for the Servlet
     * @return true if mapping is registered, false otherwise
     */
    boolean isMappingRegistered(String mapping);
        

}
