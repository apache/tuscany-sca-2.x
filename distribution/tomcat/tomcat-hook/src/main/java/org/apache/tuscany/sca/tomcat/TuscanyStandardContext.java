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
import java.util.logging.Logger;

import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.apache.catalina.Loader;
import org.apache.catalina.core.StandardContext;

/**
 * A Tuscany StandardContext to initilize SCA applications.
 * There is a StandardContext instance for each webapp and its
 * called to handle all start/stop/etc requests. This intercepts
 * the start and inserts any required Tuscany configuration.
 */
public class TuscanyStandardContext extends StandardContext {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(TuscanyStandardContext.class.getName());

    private boolean isSCAApp;

    // TODO: this gives an instance per webapp, work out how to have only one per server
    // ?? is that comment still true?
    private static URLClassLoader tuscanyClassLoader;

    /**
     * Overrides the getLoader method in the Tomcat StandardContext as its a convenient
     * point to insert the Tuscany initilization. This gets called the first time during
     * StandardContext.start after the webapp resources have been created so this can
     * use getResources() to look for the SCA web.composite or sca-contribution.xml files,
     * but its still early enough in start to insert the required Tuscany config.
     */
    @Override
    public Loader getLoader() {
        if (loader != null) {
            return loader;
        }

        ClassLoader parent = getParentClassLoader();
        if (isSCAApp = isSCAApplication()) {
            setParentClassLoader(getTuscanyClassloader(parent));
            setDefaultWebXml("conf/tuscany-web.xml");
        }

        return super.getLoader();
    }

    @Override
    public boolean listenerStart() {
        if (isSCAApp) {
            enableTuscany();
        }
        return super.listenerStart();
    }

    private void enableTuscany() {

        if (isUseNaming() && getNamingContextListener() != null) {
            setAnnotationProcessor(new TuscanyAnnotationsProcessor(this, getNamingContextListener().getEnvContext()));
        } else {
            setAnnotationProcessor(new TuscanyAnnotationsProcessor(this, null));
        }

        log.info("Tuscany SCA is enabled for: " + this.getName());
    }

    private boolean isSCAApplication() {
        Object o = null;
        try {
            o = getResources().lookup("WEB-INF/web.composite");
        } catch (NamingException e) {
        }
        if (o == null) {
            try {
                o = getResources().lookup("META-INF/sca-contribution.xml");
            } catch (NamingException e) {
            }
        }
        if (o == null) {
            return false;
        }

        // Try to see if the Tuscany jars are packaged in the webapp
        NamingEnumeration<NameClassPair> enumeration;
        try {
            enumeration = getResources().list("WEB-INF/lib");
            while (enumeration.hasMoreElements()) {
                String jar = enumeration.nextElement().getName();
                if (jar.startsWith("tuscany-")) {
                    // Do not alter is
                    log.info("Tuscany SCA ignoring webapp with embedded Tuscany runtime: " + this.getName());
                    return false;
                }
            }
        } catch (NamingException e) {
        }
        return true;
    }

    private synchronized URLClassLoader getTuscanyClassloader(ClassLoader parent) {
        if (tuscanyClassLoader == null) {
            File tuscanyWar = new File(System.getProperty(TuscanyLifecycleListener.TUSCANY_WAR_PROP));
            File[] runtimeJars = new File(tuscanyWar, "tuscany-lib").listFiles();
            try {
                URL[] jarURLs = new URL[runtimeJars.length];
                for (int i = 0; i < jarURLs.length; i++) {
                    jarURLs[i] = runtimeJars[i].toURI().toURL();
                }
                tuscanyClassLoader = new URLClassLoader(jarURLs, parent);
                return tuscanyClassLoader;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return tuscanyClassLoader;
    }
    
}
