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

package org.apache.tuscany.sca.tomcat;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import javax.naming.NamingException;

import org.apache.catalina.Loader;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.deploy.FilterDef;

public class TuscanyStandardContext extends StandardContext {
    private static final long serialVersionUID = 1L;

    private ClassLoader tuscanyClassLoader;
    
    public Loader getLoader() {
        if (loader != null) {
            return loader;
        }
        
        initTuscany();
        
        return super.getLoader();
    }

    private void initTuscany() {
        String scaVersion = getSCAVersion();
        if ("1.1".equals(scaVersion)) {
            setParentClassLoader(getTuscanyClassloader(scaVersion));
            addApplicationListener("org.apache.tuscany.sca.host.webapp.TuscanyContextListener");
            FilterDef filterDef = new FilterDef();
            filterDef.setFilterName("TuscanyFilter");
            filterDef.setFilterClass("org.apache.tuscany.sca.host.webapp.TuscanyServletFilter");
            addFilterDef(filterDef);
        }
    }

    private String getSCAVersion() {
        Object o = null;
        try {
            o = getResources().lookup("WEB-INF/web.composite");
        } catch (NamingException e) {
        }
        return o != null ? "1.1" : null;
    }

    private ClassLoader getTuscanyClassloader(String version) {
        if (tuscanyClassLoader == null) {
            File tuscanyWar = new File(System.getProperty("org.apache.tuscany.sca.tomcat.war"));
            File[] runtimeJars = new File(tuscanyWar, "tuscany-lib").listFiles();
            try {
                URL[] jarURLs = new URL[runtimeJars.length];
                for (int i=0; i< jarURLs.length; i++) {
                    jarURLs[i] = runtimeJars[i].toURI().toURL();
                }
                return new URLClassLoader(jarURLs, getParentClassLoader());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return tuscanyClassLoader;
    }
}
