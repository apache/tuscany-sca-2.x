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

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import junit.framework.TestCase;
import org.apache.tuscany.host.servlet.ServletRequestInjector;
import static org.apache.tuscany.runtime.webapp.Constants.RUNTIME_ATTRIBUTE;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * Verifies {@link TuscanyServlet} properly services a request
 *
 * @version $Rev$ $Date$
 */
public class TuscanyServletTestCase extends TestCase {

    public void testRequestInjection() throws Exception {
        ServletRequest req = createNiceMock(ServletRequest.class);
        ServletResponse resp = createNiceMock(ServletResponse.class);
        ServletRequestInjector injector = createMock(ServletRequestInjector.class);
        injector.service(eq(req), eq(resp));
        EasyMock.replay(injector);
        WebappRuntime runtime = createMock(WebappRuntime.class);
        expect(runtime.getRequestInjector()).andReturn(injector);
        replay(runtime);
        ServletContext context = createNiceMock(ServletContext.class);
        EasyMock.expect(context.getAttribute(RUNTIME_ATTRIBUTE)).andReturn(runtime);
        EasyMock.replay(context);
        TuscanyServlet servlet = new TuscanyServlet();
        ServletConfig config = createMock(ServletConfig.class);
        expect(config.getServletContext()).andReturn(context);
        replay(config);
        servlet.init(config);
        servlet.service(req, resp);
        verify(context);
        verify(injector);
    }

    public void testRuntimeNotConfigured() throws Exception {
        ServletContext context = createNiceMock(ServletContext.class);
        TuscanyServlet servlet = new TuscanyServlet();
        ServletConfig config = createMock(ServletConfig.class);
        expect(config.getServletContext()).andReturn(context);
        replay(config);
        try {
            servlet.init(config);
            fail();
        } catch (ServletException e) {
            //expected
        }
    }
}
