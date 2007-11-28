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

import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;

/**
 */
public class TuscanyContextListener implements LifecycleListener {

    public void lifecycleEvent(LifecycleEvent event) {
        String type = event.getType();
        if (Lifecycle.AFTER_START_EVENT.equals(type)) {
            startContext((Context) event.getLifecycle());
        } else if (Lifecycle.STOP_EVENT.equals(type)) {
            stopContext((Context) event.getLifecycle());
        }
    }

    protected void startContext(Context context) {
        System.out.println("xxxxxxxx TuscanyContextListener.startContext" + context);
    }

    protected void stopContext(Context context) {
        System.out.println("xxxxxxxx TuscanyContextListener.stopContext" + context);
    }

}
