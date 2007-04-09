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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import static org.apache.tuscany.runtime.webapp.Constants.RUNTIME_ATTRIBUTE;

/**
 * Notifies the Tuscany runtime of session creation and expiration events.
 *
 * @version $Rev$ $Date$
 */
public class TuscanySessionListener implements HttpSessionListener {
    private WebappRuntime runtime;

    public void sessionCreated(HttpSessionEvent event) {
        if (runtime == null) {
            ServletContext context = event.getSession().getServletContext();
            runtime = (WebappRuntime) context.getAttribute(RUNTIME_ATTRIBUTE);
            if (runtime == null) {
                context.log("Error on session creation", new ServletException("Tuscany runtime not configured"));
                return;
            }
        }
        runtime.sessionCreated(event);
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        if (runtime != null) {
            runtime.sessionDestroyed(event);
        }
    }
}
