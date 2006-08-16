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
package org.apache.tuscany.runtime.webapp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.tuscany.spi.host.ServletHost;

/**
 * ServletHost impl that forwards requests to registered servlets
 */
public class ServletHostImpl implements ServletHost {

    protected Map<String, Servlet> servlets;

    public ServletHostImpl() {
        this.servlets = new HashMap<String, Servlet>();
    }

    public void handleService(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        String path = ((HttpServletRequest) req).getPathInfo();
        Servlet servlet = servlets.get(path);
        if (servlet == null) {
            throw new IllegalStateException("no servlet registered for path: " + path);
        }
        servlet.service(req, res);
    }

    public void registerMapping(String path, Servlet servlet) {
        if (servlets.containsKey(path)) {
            throw new IllegalStateException("servlet already registered at path: " + path);
        }
        servlets.put(path, servlet);
    }

    public void unregisterMapping(String path) {
        servlets.remove(path);
    }

}
