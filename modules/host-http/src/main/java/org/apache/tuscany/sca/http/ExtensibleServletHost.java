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

package org.apache.tuscany.sca.http;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Servlet;

/**
 * Default implementation of an extensible servlet host.
 * 
 * @version $Rev$ $Date$
 */
public class ExtensibleServletHost implements ServletHost {
    
    private ServletHostExtensionPoint servletHosts;
    private List<String> uriList = new ArrayList<String>();
    
    public ExtensibleServletHost(ServletHostExtensionPoint servletHosts) {
        this.servletHosts = servletHosts;
    }

    public void addServletMapping(String uri, Servlet servlet) throws ServletMappingException {
        uriList.add(uri);
        if (servletHosts.getServletHosts().isEmpty()) {
            throw new ServletMappingException("No servlet host available");
        }

        // TODO implement selection of the correct servlet host based on the mapping
        // For now just select the first one
        servletHosts.getServletHosts().get(0).addServletMapping(uri, servlet);
    }

    public Servlet removeServletMapping(String uri) throws ServletMappingException {
        uriList.remove(uri);
        // TODO implement selection of the correct servlet host based on the mapping
        // For now just select the first one
        return servletHosts.getServletHosts().get(0).removeServletMapping(uri);
    }
    
    /**
     * For debugging purposes this returns the list of URI strings
     * that have been passed in 
     * 
     * @return the string uri list
     */
    public List<String> getURIList(){
        return uriList;
    }    

}
