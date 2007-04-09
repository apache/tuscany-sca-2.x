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

import static org.apache.tuscany.runtime.webapp.Constants.RUNTIME_ATTRIBUTE;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Notifies the Tuscany runtime of session creation and expiration events.
 * 
 * @version $Rev: 441961 $ $Date: 2006-09-10 11:48:29 -0400 (Sun, 10 Sep 2006) $
 */
public class TuscanyRequestListener implements ServletRequestListener {
    private WebappRuntime runtime;

    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {

        final ServletContext context = servletRequestEvent.getServletContext();
        getRuntime(context);
        ServletRequest servletRequest = servletRequestEvent.getServletRequest();
        if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            HttpSession session = httpServletRequest.getSession(false);
            runtime.httpRequestEnded(session == null ? null : session.getId());

        }

    }

    public void requestInitialized(ServletRequestEvent servletRequestEvent) {

        final ServletContext context = servletRequestEvent.getServletContext();
        getRuntime(context);
        ServletRequest servletRequest = servletRequestEvent.getServletRequest();
        if (servletRequest instanceof HttpServletRequest) {
            runtime.httpRequestStarted((HttpServletRequest) servletRequest);

        }

    }

    protected WebappRuntime getRuntime(final ServletContext context) {
        if (runtime == null) {

            runtime = (WebappRuntime) context.getAttribute(RUNTIME_ATTRIBUTE);
            if (runtime == null) {
                context.log("requestInitialized", new ServletException("Tuscany runtime not configured"));
                return null;
            }
        }
        return runtime;
    }
}