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
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Logger;

import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.apache.catalina.LifecycleException;
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
    private static Object node;
    private static Class<?> nodeClass;
    private static Method nodeStopMethod;

    public TuscanyStandardContext() {
    }
    
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
            if (tuscanyClassLoader == null) {
                initTuscany();
            }
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

    private URLClassLoader getTuscanyClassloader(ClassLoader parent) {
        return tuscanyClassLoader;
    }

    private void initTuscany() {
        initTuscanyClassloader(getParentClassLoader());
        initDomain();
    }
    
    private void initTuscanyClassloader(ClassLoader parent) {
        if (tuscanyClassLoader == null) {
            File tuscanyWar = new File(System.getProperty(TuscanyLifecycleListener.TUSCANY_WAR_PROP));
            File[] runtimeJars = new File(tuscanyWar, "tuscany-lib").listFiles();
            try {
                URL[] jarURLs = new URL[runtimeJars.length];
                for (int i = 0; i < jarURLs.length; i++) {
                    jarURLs[i] = runtimeJars[i].toURI().toURL();
                }
                tuscanyClassLoader = new URLClassLoader(jarURLs, parent);
                
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void initDomain() {
        ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(tuscanyClassLoader);
            Class<?> nodeFactoryClass = Class.forName("org.apache.tuscany.sca.node.NodeFactory", true, tuscanyClassLoader);
            Method getInstanceMethod = nodeFactoryClass.getMethod("getInstance", new Class[0]);
            Object instance = getInstanceMethod.invoke(null);
            Method createNodeMethod = nodeFactoryClass.getMethod("createNode", new Class[]{URI.class, new String[0].getClass()});
            URI domainURI = URI.create(TuscanyLifecycleListener.getDomainURI());
            this.node = createNodeMethod.invoke(instance, new Object[]{domainURI, new String[0]});
            this.nodeClass = Class.forName("org.apache.tuscany.sca.node.Node", true, tuscanyClassLoader);
            Method nodeStartMethod = nodeClass.getMethod("start", new Class[0]);
            this.nodeStopMethod = nodeClass.getMethod("stop", new Class[0]);
            nodeStartMethod.invoke(node);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldCL);
        }
    }

    @Override
    public synchronized void stop() throws LifecycleException {
        super.stop();
        
        if (node != null && nodeStopMethod != null) {
            try {
                nodeStopMethod.invoke(node);
                node = null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
