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

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.TuscanyModuleComponentContext;

/**
 * Notifies the {@link TuscanyModuleComponentContext} of web request start and end events as well as setting up the current
 * session context. The latter is done using lazy Servlet-based session retrieval. The filter fires a session start event, passing
 * a <tt>LazyServletSessionId</tt> as the session id. The <tt>LazyServletSessionId</tt> is a wrapper for the servlet request
 * which may be called by the <tt>ModuleContext</tt> to retrieve the session id lazily.
 * 
 * @version $Rev$ $Date$
 */
public class TuscanyRequestFilter implements Filter {
    private TuscanyWebAppRuntime tuscanyRuntime;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public TuscanyRequestFilter() {
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public void init(FilterConfig filterConfig) throws ServletException {

        // Get the Tuscany runtime from the servlet context
        tuscanyRuntime = (TuscanyWebAppRuntime) filterConfig.getServletContext().getAttribute(
                TuscanyWebAppRuntime.class.getName());
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws ServletException,
            IOException {
        // Get the module component context from the tuscany runtime
        TuscanyModuleComponentContext moduleComponentContext = tuscanyRuntime.getModuleComponentContext();
        try {

            // Start the SCA implementation
            tuscanyRuntime.start();

            // Handle a request
            if (request instanceof HttpServletRequest) {
                if (((HttpServletRequest) request).getSession(false) != null) {

                    // A session is already active
                    moduleComponentContext
                            .fireEvent(EventContext.SESSION_NOTIFY, ((HttpServletRequest) request).getSession(true));
                } else {
                    // Create a lazy wrapper since a session is not yet active
                    moduleComponentContext.fireEvent(EventContext.SESSION_NOTIFY, new LazyHTTPSessionId(
                            (HttpServletRequest) request));
                }
            } else {
                moduleComponentContext.fireEvent(EventContext.SESSION_NOTIFY, request);
            }

            // Start processing the request
            moduleComponentContext.fireEvent(EventContext.REQUEST_START, request);

            // Dispatch to the next filter
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            throw new ServletException(e);

        } finally {
            try {

                // End processing the request
                moduleComponentContext.fireEvent(EventContext.REQUEST_END, request);

                // Stop the SCA implementation
                tuscanyRuntime.stop();

            } catch (Exception e) {
                throw new ServletException(e);
            }
        }

    }

}
