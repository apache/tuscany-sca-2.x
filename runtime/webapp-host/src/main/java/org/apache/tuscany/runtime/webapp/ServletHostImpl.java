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
package org.apache.tuscany.runtime.webapp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.tuscany.host.servlet.ServletRequestInjector;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.event.EventFilter;
import org.apache.tuscany.spi.event.EventPublisher;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.host.ServletHost;
import org.apache.tuscany.spi.model.Scope;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Service;

/**
 * A <code>ServletHost</code> implementation that forwards requests to registered servlets 
 */
@Service(ServletHost.class)
public class ServletHostImpl implements ServletHost, ServletRequestInjector, EventPublisher {

    protected Map<String, Servlet> servlets;
    

    public ServletHostImpl() {
        this.servlets = new HashMap<String, Servlet>();
    }

    @Init(eager = true)
    public void init() {
    }
    
    protected ScopeRegistry registry= null;
    
    
    @Autowire(required=false)
    public void setSessionScopeContainer(ScopeRegistry registry) {
       this.registry = registry;     
    }


    public void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
        assert req instanceof HttpServletRequest : "implementation only supports HttpServletRequest";
        String path = ((HttpServletRequest) req).getPathInfo();
        Servlet servlet = servlets.get(path);
        if (servlet == null) {
            throw new IllegalStateException("No servlet registered for path: " + path);
        }
        servlet.service(req, resp);
    }

    public void registerMapping(String path, Servlet servlet) {
        if (servlets.containsKey(path)) {
            throw new IllegalStateException("Servlet already registered at path: " + path);
        }
        servlets.put(path, servlet);
    }

    public void unregisterMapping(String path) {
        servlets.remove(path);
    }

    public void addListener(RuntimeEventListener listener) {
       throw new UnSupportedRuntimeException("Not Supported");
        
    }

    public void addListener(EventFilter filter, RuntimeEventListener listener) {
        throw new UnSupportedRuntimeException("Not Supported");
        
    }

    public void publish(Event event) {
       if(registry != null){
          ScopeContainer sc = registry.getScopeContainer(Scope.SESSION);
          if(null != sc) {
              sc.onEvent(event);
          
          }
       }
        
    }

    public void removeListener(RuntimeEventListener listener) {
        throw new UnSupportedRuntimeException("Not Supported");
        
    }

}
