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

import java.io.File;
import java.net.URL;
import javax.servlet.ServletContext;

/**
 * @version $Rev$ $Date$
 */
public class WebappRuntimeInfoImpl implements WebappRuntimeInfo {
    private final ServletContext servletContext;
    private final URL baseURL;

    public WebappRuntimeInfoImpl(ServletContext servletContext, URL baseURL) {
        this.servletContext = servletContext;
        this.baseURL = baseURL;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public URL getBaseURL() {
        return baseURL;
    }

    public File getInstallDirectory() {
        throw new UnsupportedOperationException();
    }

    public File getApplicationRootDirectory() {
        throw new UnsupportedOperationException();
    }
}
