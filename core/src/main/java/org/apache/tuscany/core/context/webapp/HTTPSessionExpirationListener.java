/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.context.webapp;

import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EventContext;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Cleans up resources used by expired sessions
 * 
 * @version $Rev$ $Date$
 */
public class HTTPSessionExpirationListener implements HttpSessionListener {
    // ----------------------------------
    // Constructors
    // ----------------------------------

    public HTTPSessionExpirationListener() {
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public void sessionCreated(HttpSessionEvent event) {
        // do nothing since sessions are lazily created in {@link
        // org.apache.tuscany.tomcat.webapp.listener.RequestFilter}
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        TuscanyWebAppRuntime tuscanyRuntime = null;
        try {
            tuscanyRuntime = (TuscanyWebAppRuntime) event.getSession().getServletContext().getAttribute(
                    TuscanyWebAppRuntime.class.getName());
            tuscanyRuntime.start();

            // End the session
            AggregateContext context = tuscanyRuntime.getModuleComponentContext();
            context.fireEvent(EventContext.SESSION_END, event.getSession());
        } finally {
            if (tuscanyRuntime != null)
                tuscanyRuntime.stop();
        }
    }
}
