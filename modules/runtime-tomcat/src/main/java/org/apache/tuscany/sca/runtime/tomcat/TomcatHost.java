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

import org.apache.catalina.Container;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.tuscany.sca.runtime.Launcher;

/**
 * To use this copy all the Tuscany jars to the Tomcat lib folder and update
 * the Tomcat conf/server.xml <Host> to include className="org.apache.tuscany.sca.runtime.tomcat.TomcatHost"
 * 
 * For example: 
 * 
 * <Host name="localhost"  appBase="webapps"
 *       className="org.apache.tuscany.sca.runtime.tomcat.TomcatHost"
 *       unpackWARs="true" autoDeploy="true"
 *       xmlValidation="false" xmlNamespaceAware="false">
 *       
 */
public class TomcatHost extends StandardHost {
    private static final long serialVersionUID = 1L;

    private static final String REPO = "/sca-contributions";

    protected Launcher launcher;
    
    public synchronized void start() throws LifecycleException {
        startRuntime();
        super.start();
    }

    public synchronized void stop() throws LifecycleException {
        super.stop();
        stopRuntime();
    }

    private void startRuntime() {
        System.out.println("XXXXXXXX TomcatHost.startRuntime");
        launcher = new Launcher(new File(REPO));
        try {
            launcher.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRuntime() {
        System.out.println("XXXXXXXX TomcatHost.stopRuntime");
        if (launcher != null) {
            launcher.stop();
        }
    }

    public synchronized void addChild(Container child) {
        System.out.println("XXXXXXXX TomcatHost.addChild" + child);
        if (!(child instanceof StandardContext)) {
            throw new IllegalArgumentException(sm.getString("tuscanyHost.notContext"));
        }
        StandardContext ctx = (StandardContext) child;
        ctx.addLifecycleListener(new TuscanyContextListener());
        super.addChild(child);
    }
}
