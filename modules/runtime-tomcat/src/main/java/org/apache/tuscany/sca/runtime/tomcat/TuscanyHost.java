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
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
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
 *       className="org.apache.tuscany.sca.runtime.tomcat.TomcatHost"
 *       unpackWARs="true" autoDeploy="true"
 *       xmlValidation="false" xmlNamespaceAware="false">
 *       
 */
public class TuscanyHost extends StandardHost {
    private static final long serialVersionUID = 1L;

    private static final String REPO = "../sca-contributions";

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
        
        TomcatServletHost.getInstance().setTuscanyHost(this);

        addTuscany();
        
        launcher = new Launcher(new File(REPO));
        try {
            launcher.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    private void addTuscany() {
        StandardContext tc = new TuscanyContext();
        tc.setPath("/tuscany");
        tc.setDocBase("tuscany");
        super.addChild(tc);
    }

    public void registerMapping(String mapping, Servlet servlet) {
        Context ctx = map(mapping);
        if (ctx == null) {
            throw new UnsupportedOperationException("Cannot find context for mapping " + mapping);
        }
        String contextPath = ctx.getPath();

        mapping = mapping.substring(contextPath.length());
        Wrapper wrapper = new TuscanyWrapper(servlet);
        wrapper.setName(mapping);
        ctx.addChild(wrapper);
        wrapper.addMapping(mapping);
        ctx.getMapper().addWrapper(mapping, wrapper, false);
    }

    public Servlet unregisterMapping(String mapping) {
        Context ctx = map(mapping);
        if (ctx == null) {
            throw new UnsupportedOperationException("Cannot find context for mapping " + mapping);
        }
        String contextPath = ctx.getPath();

        mapping = mapping.substring(contextPath.length());
        
        TuscanyWrapper wrapper = (TuscanyWrapper) ctx.findChild(mapping);
        ctx.getMapper().removeWrapper(mapping);
        ctx.removeChild(wrapper);

        return wrapper.getServlet();
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

class TestServlet extends HttpServlet {
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException, IOException {
      PrintWriter out = response.getWriter();
      out.println("hi!");
    }
    
}
