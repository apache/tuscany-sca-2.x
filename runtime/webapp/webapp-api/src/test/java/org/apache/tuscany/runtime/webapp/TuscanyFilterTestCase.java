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
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import junit.framework.TestCase;
import org.easymock.EasyMock;

import static org.apache.tuscany.runtime.webapp.Constants.RUNTIME_ATTRIBUTE;

/**
 * @version $Rev$ $Date$
 */
public class TuscanyFilterTestCase extends TestCase {
    private TuscanyFilter filter;
    private FilterConfig config;
    private ServletContext servletContext;
    private WebappRuntime runtime;
    private ServletRequest request;
    private ServletResponse response;
    private FilterChain chain;

    public void testFilterInit() {
        EasyMock.expect(config.getServletContext()).andReturn(servletContext);
        EasyMock.expect(servletContext.getAttribute(RUNTIME_ATTRIBUTE)).andReturn(runtime);

        EasyMock.replay(servletContext);
        EasyMock.replay(config);
        EasyMock.replay(runtime);
        try {
            filter.init(config);
        } catch (ServletException e) {
            fail(e.getMessage());
        }
        EasyMock.verify(servletContext);
        EasyMock.verify(config);
        EasyMock.verify(runtime);
    }

    public void testFilterInitWithNoRuntimeConfigured() {
        EasyMock.expect(config.getServletContext()).andReturn(servletContext);
        EasyMock.expect(servletContext.getAttribute(RUNTIME_ATTRIBUTE)).andReturn(null);

        EasyMock.replay(servletContext);
        EasyMock.replay(config);
        EasyMock.replay(runtime);
        try {
            filter.init(config);
            fail("Expected a ServletException");
        } catch (ServletException e) {
            // OK
        }
        EasyMock.verify(servletContext);
        EasyMock.verify(config);
        EasyMock.verify(runtime);
    }

    public void testContextIsAssociatedWithThread() throws ServletException, IOException {
        EasyMock.expect(config.getServletContext()).andReturn(servletContext);
        EasyMock.expect(servletContext.getAttribute(RUNTIME_ATTRIBUTE)).andReturn(runtime);
        EasyMock.replay(servletContext);
        EasyMock.replay(config);
        filter.init(config);

        chain.doFilter(EasyMock.same(request), EasyMock.same(response));
        EasyMock.replay(chain);
        runtime.startRequest();
        runtime.stopRequest();
        EasyMock.replay(runtime);
        try {
            filter.doFilter(request, response, chain);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        EasyMock.verify(chain);
        EasyMock.verify(runtime);
    }

    protected void setUp() throws Exception {
        super.setUp();
        filter = new TuscanyFilter();
        config = EasyMock.createMock(FilterConfig.class);
        servletContext = EasyMock.createMock(ServletContext.class);
        runtime = EasyMock.createMock(WebappRuntime.class);
        request = EasyMock.createMock(ServletRequest.class);
        response = EasyMock.createMock(ServletResponse.class);
        chain = EasyMock.createMock(FilterChain.class);
    }
}
