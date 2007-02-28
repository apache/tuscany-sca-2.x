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

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.event.EventFilter;
import org.apache.tuscany.spi.event.EventPublisher;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.host.ServletHost;
import org.apache.tuscany.spi.model.Scope;

import org.apache.tuscany.core.component.event.HttpRequestEnded;
import org.apache.tuscany.core.component.event.HttpRequestStart;
import org.apache.tuscany.core.component.event.HttpSessionEnd;
import org.apache.tuscany.core.component.event.HttpSessionStart;
import org.apache.tuscany.host.servlet.ServletRequestInjector;

/**
 * A <code>ServletHost</code> implementation that forwards requests to registered servlets
 *
 * @version $Rev$ $Date$
 */
@Service(ServletHost.class)
@EagerInit
public class ServletHostImpl implements ServletHost, ServletRequestInjector, EventPublisher {
    protected Map<String, Servlet> servlets;
    protected ScopeRegistry registry;
    protected WorkContext workContext;

    public ServletHostImpl() {
        this.servlets = new HashMap<String, Servlet>();
    }

    @Reference(required = false)
    public void setSessionScopeContainer(ScopeRegistry registry) {
        this.registry = registry;
    }

    @Reference(required = false)
    public void setWorkContext(WorkContext workContext) {
        this.workContext = workContext;
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

    public boolean isMappingRegistered(String mapping) {
        return servlets.containsKey(mapping);

    }

    public Servlet unregisterMapping(String path) {
        return servlets.remove(path);
    }

    public void addListener(RuntimeEventListener listener) {
        throw new UnSupportedRuntimeException("Not Supported");

    }

    public void addListener(EventFilter filter, RuntimeEventListener listener) {
        throw new UnSupportedRuntimeException("Not Supported");

    }

    public void publish(Event event) {
        if (null != registry && (event instanceof HttpSessionStart || event instanceof HttpSessionEnd)) {
            ScopeContainer sc = registry.getScopeContainer(Scope.SESSION);
            if (null != sc) {
                sc.onEvent(event);

            }
        }
        if (null != workContext) {
            if (event instanceof HttpRequestStart) {
                Object key = ((HttpRequestStart) event).getId();
                if (null != key) {
                    workContext.setIdentifier(Scope.SESSION, ((HttpRequestStart) event).getId());
                } else { // new request with no session.
                    workContext.clearIdentifier(Scope.SESSION);
                }
            } else if (event instanceof HttpRequestEnded) {
                workContext.clearIdentifier(Scope.SESSION);

            }

        }

    }

    public void removeListener(RuntimeEventListener listener) {
        throw new UnSupportedRuntimeException("Not Supported");

    }

}