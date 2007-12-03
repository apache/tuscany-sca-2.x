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
import java.net.MalformedURLException;

import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.StandardContext;
import org.apache.tuscany.sca.node.NodeException;
import org.apache.tuscany.sca.node.SCANode;

/**
 */
public class TuscanyContextListener implements LifecycleListener {

    private SCANode node;

    public TuscanyContextListener(SCANode node) {
        this.node = node;
    }

    public void lifecycleEvent(LifecycleEvent event) {
        String type = event.getType();
        if (Lifecycle.AFTER_START_EVENT.equals(type)) {
            startContext((Context) event.getLifecycle());
        } else if (Lifecycle.STOP_EVENT.equals(type)) {
            stopContext((Context) event.getLifecycle());
        }
    }

    protected void startContext(Context context) {
        StandardContext sc = (StandardContext) context;
        String path = sc.getServletContext().getRealPath("/");
        System.out.println(path);
        try {
//            node.stop();
            File f = new File(path + "WEB-INF/classes");
            if (f.exists()) {
                node.addContribution(path, f.toURL());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        }
//        try {
//            node.start();
//        } catch (NodeException e) {
//            e.printStackTrace();
//        }
    }

    protected void stopContext(Context context) {
    }

}
