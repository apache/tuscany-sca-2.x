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

package org.apache.tuscany.sca.node.equinox.launcher;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.eclipse.core.runtime.adaptor.LocationManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * Wraps the Equinox runtime.
 */
public class EquinoxOSGiHost {
    private LauncherBundleActivator activator = new LauncherBundleActivator();
    private BundleContext context;
    private ClassLoader tccl;

    private final static String systemPackages =
        "org.osgi.framework; version=1.3.0," + "org.osgi.service.packageadmin; version=1.2.0, "
            + "org.osgi.service.startlevel; version=1.0.0, "
            + "org.osgi.service.url; version=1.0.0, "
            + "org.osgi.util.tracker; version=1.3.2, "
            + "javax.xml, "
            + "javax.xml.datatype, "
            + "javax.xml.namespace, "
            + "javax.xml.parsers, "
            + "javax.xml.transform, "
            + "javax.xml.transform.dom, "
            + "javax.xml.transform.sax, "
            + "javax.xml.transform.stream, "
            + "javax.xml.validation, "
            + "javax.xml.xpath, "
            // Force the classes to be imported from the system bundle
            // + "javax.xml.stream, "
            // + "javax.xml.stream.util, "
            + "javax.sql,"
            + "org.w3c.dom, "
            + "org.xml.sax, "
            + "org.xml.sax.ext, "
            + "org.xml.sax.helpers, "
            + "javax.security.auth, "
            + "javax.security.cert, "
            + "javax.security.auth.login, "
            + "javax.security.auth.callback, "
            + "javax.naming, "
            + "javax.naming.spi, "
            + "javax.naming.directory, "
            + "javax.management, "
            + "javax.imageio, "
            + "sun.misc, "
            + "javax.net, "
            + "javax.net.ssl, "
            + "javax.crypto, "
            + "javax.rmi, "
            + "javax.transaction, "
            + "javax.transaction.xa";

    public BundleContext start() {
        try {
            return startup();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    public Bundle findBundle(String symbolicName, String version) {
        if (context == null) {
            return null;
        }
        Bundle[] bundles = context.getBundles();
        if (version == null) {
            version = "0.0.0";
        }
        for (Bundle b : bundles) {
            String v = (String)b.getHeaders().get(Constants.BUNDLE_VERSION);
            if (v == null) {
                v = "0.0.0";
            }
            if (b.getSymbolicName().equals(symbolicName) && (version.equals("0.0.0") || v.equals(version))) {
                return b;
            }
        }
        return null;
    }

    public void stop() {
        try {
            shutdown();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public BundleContext getBundleContext() {
        return context;
    }

    private BundleContext startup() throws Exception {
        String args[] = {};
        Map<Object, Object> props = new HashMap<Object, Object>();
        props.put("org.osgi.framework.system.packages", systemPackages);
        // Set the extension bundle
        props.put("osgi.framework.extensions", "org.apache.tuscany.sca.extensibility.equinox");
        props.put(EclipseStarter.PROP_CLEAN, "true");
        props.put(LocationManager.PROP_INSTANCE_AREA, new File("target/workspace").toURI().toString());
        props.put(LocationManager.PROP_INSTALL_AREA, new File("target/eclipse/install").toURI().toString());
        props.put(LocationManager.PROP_CONFIG_AREA, new File("target/eclipse/config").toURI().toString());
        props.put(LocationManager.PROP_USER_AREA, new File("target/eclipse/user").toURI().toString());

        EclipseStarter.setInitialProperties(props);
        context = EclipseStarter.startup(args, null);
        activator.start(context);
        
// [rfeng] This is useful to report bundle resolving issues        
//        for (Bundle b : context.getBundles()) {
//            System.out.println("Starting: " + b);
//            try {
//                b.start();
//            } catch (BundleException e) {
//                e.printStackTrace();
//            }
//        }
        tccl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getContextClassLoader());
        return context;
    }

    private ClassLoader getContextClassLoader() {
        Bundle b = findBundle("org.apache.tuscany.sca.extensibility.equinox", null);
        if (b != null) {
            try {
                b.start();
                Class<?> discovererClass = b.loadClass("org.apache.tuscany.sca.extensibility.ServiceDiscovery");
                Method getInstance = discovererClass.getMethod("getInstance");
                Object instance = getInstance.invoke(null);
                Method getter = discovererClass.getMethod("getServiceDiscoverer");
                Object discoverer = getter.invoke(instance);

                Method getCL = discoverer.getClass().getMethod("getContextClassLoader");
                ClassLoader cl = (ClassLoader)getCL.invoke(discoverer);
                return cl;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        throw new IllegalStateException("Bundle org.apache.tuscany.sca.extensibility.equinox is not installed");
    }

    private void shutdown() throws Exception {
        activator.stop(context);
        EclipseStarter.shutdown();
        Thread.currentThread().setContextClassLoader(tccl);
    }

}
