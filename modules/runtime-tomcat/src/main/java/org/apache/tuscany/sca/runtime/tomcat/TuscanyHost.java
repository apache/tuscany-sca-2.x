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
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.catalina.Container;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
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
 *       className="org.apache.tuscany.sca.runtime.tomcat.TuscanyHost"
 *       unpackWARs="true" autoDeploy="true"
 *       xmlValidation="false" xmlNamespaceAware="false">
 *       
 */
public class TuscanyHost extends StandardHost {
    private static final long serialVersionUID = 1L;

    private static final String REPO = "../sca-contributions";

    protected Launcher launcher;

    private String contextPath = "/tuscany";
    
    public synchronized void start() throws LifecycleException {
        try {

            launcher = initTuscany();

            super.start();
            
            launcher.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void stop() throws LifecycleException {
        super.stop();
        stopRuntime();
    }

    private Launcher initTuscany() throws ServletException {
        StandardContext tc = new TuscanyContext();
        tc.setPath(contextPath);
        super.addChild(tc);
        
        TuscanyServlet s = new TuscanyServlet();
        s.init(new MockServletConfig(contextPath));
        Wrapper wrapper = new TuscanyWrapper(s);
        wrapper.setName("TuscanyServlet");
        tc.addChild(wrapper);
        tc.addServletMapping("/*", "TuscanyServlet", true);

        Launcher launcher = new Launcher(new File(REPO));

        return launcher;
    }

    private void stopRuntime() {
        System.out.println("XXXXXXXX TomcatHost.stopRuntime");
        if (launcher != null) {
            launcher.stop();
        }
    }

    public synchronized void addChild(Container child) {
        if (!(child instanceof StandardContext)) {
            throw new IllegalArgumentException(sm.getString("tuscanyHost.notContext"));
        }
        super.addChild(child);
    }

}

class MockServletConfig implements ServletConfig {

    Map<String, String> initParams;
    
    public MockServletConfig(String contextPath) {
        initParams = new HashMap<String, String>();
        initParams.put("contextPath", contextPath);
    }

    public String getInitParameter(String initParam) {
        return initParams.get(initParam);
    }

    @SuppressWarnings("unchecked")
    public Enumeration getInitParameterNames() {
        return Collections.enumeration(initParams.keySet());
    }

    public ServletContext getServletContext() {
        return null;
    }

    public String getServletName() {
        return null;
    }
    
}
