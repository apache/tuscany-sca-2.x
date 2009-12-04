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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.startup.HostConfig;

/**
 * A Tuscany customized HostConfig that adds support for SCA contributions
 * to be deployed along with the usual .war files.
 */
public class TuscanyHost extends HostConfig {

    protected File scaBase = null;
    
    @Override
    protected void deployApps() {

        File appBase = appBase();
        File configBase = configBase();
        // Deploy XML descriptors from configBase
        deployDescriptors(configBase, configBase.list());
        // Deploy WARs, and loop if additional descriptors are found
        deployWARs(appBase, appBase.list());
        // TUSCANY: Deploy any SCA contibutions
        deploySCAContributions(appBase, appBase.list());
        // Deploy expanded folders
        deployDirectories(appBase, appBase.list());
    }

    protected void deploySCAContributions(File appBase, String[] files) {
        if (files == null)
            return;
        
        for (int i = 0; i < files.length; i++) {
            
            File scafile = new File(appBase, files[i]);
            
            if (scafile.isFile() && isSCAContribution(scafile)) {
                
                // Calculate the context path and make sure it is unique
                String contextPath = "/" + files[i].replace('#','/');
                int period = contextPath.lastIndexOf(".");
                if (period >= 0)
                    contextPath = contextPath.substring(0, period);

                if (isServiced(contextPath))
                    continue;
                
                String file = files[i];
                
                deploySCAContribution(contextPath, scafile, file);
                
            }
            
        }
        
    }

    protected String tuscanyContextClass = "org.apache.tuscany.sca.tomcat.TuscanyContextConfig";
    
    protected void deploySCAContribution(String contextPath, File dir, String file) {
        if (deploymentExists(contextPath))
            return;

        DeployedApplication deployedApp = new DeployedApplication(contextPath);
        
        // Deploy the application in this WAR file
        if(log.isInfoEnabled()) 
            log.info("Deploying SCA contibution: " + file);

        // Populate redeploy resources with the WAR file
        deployedApp.redeployResources.put(dir.getAbsolutePath(), new Long(dir.lastModified()));

        try {
            Context context = (Context) Class.forName(contextClass).newInstance();
            if (context instanceof Lifecycle) {
                
                // Tuscany: change to use the Tuscany ContextConfig class
                Class clazz = Class.forName(tuscanyContextClass);

                LifecycleListener listener =
                    (LifecycleListener) clazz.newInstance();
                ((Lifecycle) context).addLifecycleListener(listener);
            }
            context.setPath(contextPath);
            context.setDocBase(file);

            host.addChild(context);
            // If we're unpacking WARs, the docBase will be mutated after
            // starting the context
            if (unpackWARs && (context.getDocBase() != null)) {
                String name = null;
                String path = context.getPath();
                if (path.equals("")) {
                    name = "ROOT";
                } else {
                    if (path.startsWith("/")) {
                        name = path.substring(1);
                    } else {
                        name = path;
                    }
                }
                name = name.replace('/', '#');
                File docBase = new File(name);
                if (!docBase.isAbsolute()) {
                    docBase = new File(appBase(), name);
                }
                deployedApp.redeployResources.put(docBase.getAbsolutePath(),
                        new Long(docBase.lastModified()));
                addWatchedResources(deployedApp, docBase.getAbsolutePath(), context);
            } else {
                addWatchedResources(deployedApp, null, context);
            }
        } catch (Throwable t) {
            log.error(sm.getString("hostConfig.deployJar.error", file), t);
        }
        
        deployed.put(contextPath, deployedApp);
    }
    
    protected boolean isSCAContribution(File file) {
        ZipFile zip = null;
        ZipEntry entry = null;
        try {
            try {
                zip = new ZipFile(file);
                entry = zip.getEntry("META-INF/sca-contribution.xml");
            } catch (Exception e) {
            }

            return (entry != null);

        } finally {
            if (zip != null) {
                try {
                    zip.close();
                } catch (Throwable t) {
                }
            }
        }
    }
    
}
