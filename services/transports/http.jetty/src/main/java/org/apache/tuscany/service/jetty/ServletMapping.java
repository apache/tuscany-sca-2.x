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
package org.apache.tuscany.service.jetty;

import javax.servlet.Servlet;

/**
 * Class that maps a path spec to a servlet.
 * 
 * @version $Revision$ $Date$
 *
 */
public class ServletMapping {
    
    // Path specification
    private String pathSpec;
    
    // Servlet that handles the path spec
    private Servlet servlet;

    /**
     * Gets the path specification.
     * @return Path specification.
     */
    public String getPathSpec() {
        return pathSpec;
    }

    /**
     * Sets the path specification.
     * @param pathSpec Path specification.
     */
    public void setPathSpec(String pathSpec) {
        this.pathSpec = pathSpec;
    }

    /**
     * Gets the servlet that handles the path.
     * @return Servlet that handles the path.
     */
    public Servlet getServlet() {
        return servlet;
    }

    /**
     * Sets the servlet that handles the path.
     * @param servlet The servlet that handles the path.
     */
    public void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }

}
