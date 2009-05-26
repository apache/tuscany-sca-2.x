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
import java.util.logging.Logger;

import org.apache.catalina.Container;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.ServerFactory;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.startup.HostConfig;

/**
 * A Tomcat LifecycleListener that initilizes the Tuscany Tomcat integration.
 * It sets a System property with the location of the Tuscany runtime .war
 * and configures each Tomcat Connector to use the TuscanyStandardContext.
 * 
 * To configure Tomcat to use this add the following to the Tomcat conf/server.xml
 *   <Listener className="org.apache.tuscany.sca.tomcat.TuscanyLifecycleListener"/>
 */
public class TuscanyLifecycleListener implements LifecycleListener {
    private static final Logger log = Logger.getLogger(TuscanyLifecycleListener.class.getName());

    public static final String TUSCANY_WAR_PROP = "org.apache.tuscany.sca.tomcat.war";

    private static boolean running;
    public static boolean isRunning() {
        return running;
    }
    
    public TuscanyLifecycleListener() {
        running = true;
        log.info("Apache Tuscany initilizing");
    }
    
    public void lifecycleEvent(LifecycleEvent event) {
        if ("init".equals(event.getType()) && (event.getSource() instanceof StandardServer)) {
            File webappDir = findTuscanyWar();
            if (webappDir == null) {
                log.severe("Tuscany disabled as Tuscany webapp not found");
            } else {
                System.setProperty(TUSCANY_WAR_PROP, webappDir.getAbsolutePath());
                log.info("Using Tuscany webapp: " + webappDir.getAbsolutePath());
                StandardServer server = (StandardServer)event.getSource();
                StandardService catalina = (StandardService)server.findService("Catalina");
                for (Connector connector : catalina.findConnectors()) {
                    for (Container container: connector.getContainer().findChildren()) {
                        if (container instanceof StandardHost) {
                           for (LifecycleListener listener : ((StandardHost)container).findLifecycleListeners()) {
                               if (listener instanceof HostConfig) {
                                   ((HostConfig)listener).setContextClass("org.apache.tuscany.sca.tomcat.TuscanyStandardContext");
                                   log.info("Tuscany enabled on connector: " + container.getName() + ":" + connector.getPort());
                               }
                           }
                        }
                    }
                }
            }
        }
    }

    private static File findTuscanyWar() {

        // in Tomcat 5.5 the Tuscany war is in the server/webapps director
        String catalinaBase = System.getProperty("catalina.base");
        File serverWebapps = new File(catalinaBase, "server/webapps");
        File tuscanyWar = findTuscanyWar(serverWebapps);
        if (tuscanyWar != null) {
            return tuscanyWar;
        }

        // in Tomcat 6 the Tuscany war is normally in webapps, but we just scan all hosts directories
        for (Service service : ServerFactory.getServer().findServices()) {
            Container container = service.getContainer();
            if (container instanceof StandardEngine) {
                StandardEngine engine = (StandardEngine) container;
                for (Container child : engine.findChildren()) {
                    if (child instanceof StandardHost) {
                        StandardHost host = (StandardHost) child;
                        String appBase = host.getAppBase();

                        // determine the host dir (normally webapps)
                        File hostDir = new File(appBase);
                        if (!hostDir.isAbsolute()) {
                            hostDir = new File(catalinaBase, appBase);
                        }

                        tuscanyWar = findTuscanyWar(hostDir);
                        if (tuscanyWar != null) {
                            return tuscanyWar;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static File findTuscanyWar(File hostDir) {
        if (!hostDir.isDirectory()) {
            return null;
        }

        // iterate over the contexts
        for (File contextDir : hostDir.listFiles()) {
            // does this war have a web-inf lib dir
            File hookLib = new File(contextDir, "tomcat-lib");
            if (!hookLib.isDirectory()) {
                continue;
            }
            // iterate over the libs looking for the tuscany-tomcat-*.jar
            for (File file : hookLib.listFiles()) {
                if (file.getName().startsWith("tuscany-tomcat-hook-") && file.getName().endsWith(".jar")) {
                    // this should be in the Tuscany war...
                    // make sure it has a runtime directory
                    if (new File(contextDir, "tuscany-lib").isDirectory()) {
                        return contextDir;
                    }
                }
            }
        }
        return null;
    }
}
