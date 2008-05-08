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

package org.apache.tuscany.sca.runtime.tomcat;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.catalina.Container;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;

/**
 * To use this copy all the Tuscany jars to the Tomcat lib folder and update
 * the Tomcat conf/server.xml <Host> to include className="org.apache.tuscany.sca.runtime.tomcat.TomcatHost2"
 * 
 * For example: 
 * 
 * <Host name="localhost"  appBase="webapps"
 *       className="org.apache.tuscany.sca.runtime.tomcat.TuscanyHost"
 *       unpackWARs="true" autoDeploy="true"
 *       xmlValidation="false" xmlNamespaceAware="false">
 *       
 */
public class TuscanyHost extends StandardHost {
    private static final long serialVersionUID = 1L;
    private final static Logger logger = Logger.getLogger(TuscanyHost.class.getName());

    public synchronized void start() throws LifecycleException {
        try {
            logger.log(Level.INFO, "Starting Tuscany/SCA runtime");

            super.start();
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception starting Tuscany/SCA runtime");
        }
    }

    public synchronized void stop() throws LifecycleException {
        try {
            logger.log(Level.INFO, "Stopping Tuscany/SCA runtime");

            super.stop();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception Stopping Tuscany/SCA runtime");
        }
    }

    public synchronized void addChild(Container child) {
        try {
            if (isSCAApp(child)) {
                initSCAApplication((StandardContext)child);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception detecting SCA application " + child.getName(), e);
        }
        super.addChild(child);
    }

    /**
     * Tests if the child is an SCA application by checking for the presence
     * of either an sca-contribution.xml file or a sca-deployables folder
     */
    protected boolean isSCAApp(Container child) throws IOException {
        if (child instanceof StandardContext) {
            StandardContext sc = (StandardContext) child;
            
            if (sc.getDocBase().endsWith(".war")) {
                JarFile jar = null;
                try {
                    jar = new JarFile(((StandardEngine)this.getParent()).getBaseDir() + "/" + getAppBase() + "/" + sc.getDocBase());

                    if (jar.getEntry("META-INF/sca-deployables/") != null) {
                        return true;
                    }

                    if (jar.getEntry("META-INF/sca-contribution.xml") != null) {
                        return true;
                    }

                } finally {
                    if (jar != null) {
                        jar.close();
                    }
                }
            } else {
                File webappRoot = new File(sc.getConfigFile()).getParentFile().getParentFile();

                File scaDeployables = new File(webappRoot, "META-INF/sca-deployables");
                if (scaDeployables.exists()) {
                    return true;
                }

                File scaContribution = new File(webappRoot, "META-INF/sca-contribution.xml");
                if (scaContribution.exists()) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void initSCAApplication(StandardContext scaApp) {
        logger.log(Level.INFO, "Initilizing SCA application: " + scaApp.getName());
        
        // Add the Tuscany ContextListener
        scaApp.addApplicationListener(org.apache.tuscany.sca.host.webapp.TuscanyContextListener.class.getName());
        
        // Add the Tuscany Servlet Filter
        FilterDef filterDef = new FilterDef();
        filterDef.setFilterClass(org.apache.tuscany.sca.host.webapp.TuscanyServletFilter.class.getName());
        filterDef.setFilterName(org.apache.tuscany.sca.host.webapp.TuscanyServletFilter.class.getName());
        scaApp.addFilterDef(filterDef);
        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName(filterDef.getFilterName());
        filterMap.addURLPattern("/*");
        scaApp.addFilterMap(filterMap);
    }

}
