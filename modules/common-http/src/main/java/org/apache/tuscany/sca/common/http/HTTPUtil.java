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

package org.apache.tuscany.sca.common.http;

import javax.servlet.http.HttpServletRequest;

public class HTTPUtil {

    /**
     * Calculate the relative request path taking in consideration if 
     * the application is running in a embedded webContatiner or from
     * within a web application server host environment
     * 
     * @param request The http request
     * @return the relative path
     */
    public static String getRequestPath(HttpServletRequest request) {
        // Get the request path
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        String requestURI = request.getRequestURI();
        
        int contextPathLength = contextPath.length();
        int servletPathLenght = servletPath.contains(contextPath) ? servletPath.length() - contextPath.length() : servletPath.length();
        
        String requestPath = requestURI.substring(contextPathLength + servletPathLenght);
        
        return requestPath;
    }
    
    /**
     * Calculate the context root for an application taking in consideration if 
     * the application is running in a embedded webContatiner or from
     * within a web application server host environment. 
     * 
     * In the case of webContainer the contextRoot will always be a empty string.
     * 
     * @param request The http request
     * @return the contextRoot
     */    
    public static String getContextRoot(HttpServletRequest request) {
        // Get the request path
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        
        int contextPathLength = contextPath.length();
        
        String contextRoot = requestURI.substring(0, contextPathLength);
        
        return contextRoot;
    }
}
