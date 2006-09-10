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
package org.apache.tuscany.osgi;

import java.io.File;
import java.net.URL;

import org.apache.tuscany.spi.component.CompositeComponent;

import org.apache.tuscany.api.TuscanyException;
import org.apache.tuscany.core.launcher.LauncherImpl;
import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.apache.tuscany.osgi.util.BundleContextUtil;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Responsible for launching the Tuscany Runtime in as part of an OSGi bundle
 */
public class LauncherActivator implements BundleActivator {

    private LauncherImpl launcher;

    public void start(BundleContext context) throws Exception {
        BundleContextUtil.setContext(context);
        startRuntime(context);

    }

    public void stop(BundleContext context) throws Exception {
        if (launcher != null) {
            launcher.shutdownRuntime();
        }
    }


    private void startRuntime(BundleContext context) throws OSGILauncherInitException {
        launcher = new LauncherImpl();
        // Current thread context classloader should be the webapp classloader
        ClassLoader webappClassLoader = Thread.currentThread().getContextClassLoader();
        launcher.setApplicationLoader(webappClassLoader);

        try {
            System.out.println(":::" + new File(".").toURL().toString());
            // URL systemScdl = getSystemSCDL(systemScdlPath);
            CompositeComponent rt =
                launcher.bootRuntime(new File("./sca/system.scdl").toURI().toURL(), new NullMonitorFactory());
        } catch (Exception e) {
            throw new OSGILauncherInitException(e);
        }
    }

    private void bootApplication(String name, URL scdl) throws TuscanyException {
        CompositeComponent root = launcher.bootApplication(name, scdl);
        root.start();
    }

    private void loadExtension(String name, URL scdl) throws TuscanyException {
        CompositeComponent root = launcher.bootApplication(name, scdl);
        root.start();
    }

}
