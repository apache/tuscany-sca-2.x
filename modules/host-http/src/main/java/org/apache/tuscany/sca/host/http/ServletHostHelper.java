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

import java.util.List;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;

public class ServletHostHelper {
    
    private static boolean webappHost;
    
    public static ServletHost getServletHost(ExtensionPointRegistry extensionPoints) {
        ServletHostExtensionPoint servletHosts = extensionPoints.getExtensionPoint(ServletHostExtensionPoint.class);
        List<ServletHost> hosts = servletHosts.getServletHosts();
        for (ServletHost servletHost : hosts) {
            if (webappHost && !"webapp".equals(servletHost.getName())) {
                continue;
            }
            if (!webappHost && "webapp".equals(servletHost.getName())) {
                continue;
            }
            if(servletHost instanceof DefaultServletHostExtensionPoint.LazyServletHost) {
                return ((DefaultServletHostExtensionPoint.LazyServletHost) servletHost).getServletHost();
            } else {
                return servletHost;
            }
        }
        throw new IllegalStateException("No ServletHost found");
    }
    
    public static void setWebappHost(boolean b) {
        ServletHostHelper.webappHost = b;
    }
}
