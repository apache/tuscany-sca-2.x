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
import java.io.IOException;
import java.net.URL;

import org.apache.catalina.Host;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.ExpandWar;

public class TuscanyContextConfig extends ContextConfig{

    /**
     * Return the location of the default deployment descriptor
     * 
     * Override the super class method to use the Tuscany specific
     * default web.xml which has the Tuscany listener and filer config 
     */
    @Override
    public String getDefaultWebXml() {
        if( defaultWebXml == null ) {
            defaultWebXml="conf/tuscany-web.xml";
        }
        return (this.defaultWebXml);
    }
    
    /**
     * Adjust docBase.
     * 
     * This is cutNpaste of the Tomcat method but changed on the lines marked with // TUSCANY:
     * to override the default that only works for file names ending with .war
     * 
     */
    @Override
    protected void fixDocBase()
        throws IOException {
        
        Host host = (Host) context.getParent();
        String appBase = host.getAppBase();

        boolean unpackWARs = true;
        if (host instanceof StandardHost) {
            unpackWARs = ((StandardHost) host).isUnpackWARs() 
                && ((StandardContext) context).getUnpackWAR();
        }

        File canonicalAppBase = new File(appBase);
        if (canonicalAppBase.isAbsolute()) {
            canonicalAppBase = canonicalAppBase.getCanonicalFile();
        } else {
            canonicalAppBase = 
                new File(System.getProperty("catalina.base"), appBase)
                .getCanonicalFile();
        }

        String docBase = context.getDocBase();
        if (docBase == null) {
            // Trying to guess the docBase according to the path
            String path = context.getPath();
            if (path == null) {
                return;
            }
            if (path.equals("")) {
                docBase = "ROOT";
            } else {
                if (path.startsWith("/")) {
                    docBase = path.substring(1);
                } else {
                    docBase = path;
                }
            }
        }

        File file = new File(docBase);
        if (!file.isAbsolute()) {
            docBase = (new File(canonicalAppBase, docBase)).getPath();
        } else {
            docBase = file.getCanonicalPath();
        }
        file = new File(docBase);
        String origDocBase = docBase;
        
        String contextPath = context.getPath();
        if (contextPath.equals("")) {
            contextPath = "ROOT";
        } else {
            if (contextPath.lastIndexOf('/') > 0) {
                contextPath = "/" + contextPath.substring(1).replace('/','#');
            }
        }
        // TUSCANY: update from .war to also support .jar and .zip SCA contributions 
        if ((docBase.toLowerCase().endsWith(".war") || docBase.toLowerCase().endsWith(".jar")||docBase.toLowerCase().endsWith(".zip")) && !file.isDirectory() && unpackWARs) {
            URL war = new URL("jar:" + (new File(docBase)).toURI().toURL() + "!/");
            docBase = ExpandWar.expand(host, war, contextPath);
            file = new File(docBase);
            docBase = file.getCanonicalPath();
            if (context instanceof StandardContext) {
                ((StandardContext) context).setOriginalDocBase(origDocBase);
            }
        } else {
            File docDir = new File(docBase);
            if (!docDir.exists()) {
                // TUSCANY: update from .war to also support .jar and .zip SCA contributions 
                File warFile = new File(docBase + ".war");
                if (warFile.exists()) {
                    if (unpackWARs) {
                        URL war =
                            new URL("jar:" + warFile.toURI().toURL() + "!/");
                        docBase = ExpandWar.expand(host, war, contextPath);
                        file = new File(docBase);
                        docBase = file.getCanonicalPath();
                    } else {
                        docBase = warFile.getCanonicalPath();
                    }
                } else {
                    warFile = new File(docBase + ".jar");
                    if (warFile.exists()) {
                        if (unpackWARs) {
                            URL war =
                                new URL("jar:" + warFile.toURI().toURL() + "!/");
                            docBase = ExpandWar.expand(host, war, contextPath);
                            file = new File(docBase);
                            docBase = file.getCanonicalPath();
                        } else {
                            docBase = warFile.getCanonicalPath();
                        }
                    } else {
                        warFile = new File(docBase + ".zip");
                        if (warFile.exists()) {
                            if (unpackWARs) {
                                URL war =
                                    new URL("jar:" + warFile.toURI().toURL() + "!/");
                                docBase = ExpandWar.expand(host, war, contextPath);
                                file = new File(docBase);
                                docBase = file.getCanonicalPath();
                            } else {
                                docBase = warFile.getCanonicalPath();
                            }
                        }
                    }
                }
                if (context instanceof StandardContext) {
                    ((StandardContext) context).setOriginalDocBase(origDocBase);
                }
            }
        }

        if (docBase.startsWith(canonicalAppBase.getPath())) {
            docBase = docBase.substring(canonicalAppBase.getPath().length());
            docBase = docBase.replace(File.separatorChar, '/');
            if (docBase.startsWith("/")) {
                docBase = docBase.substring(1);
            }
        } else {
            docBase = docBase.replace(File.separatorChar, '/');
        }

        context.setDocBase(docBase);

    }
    
}
