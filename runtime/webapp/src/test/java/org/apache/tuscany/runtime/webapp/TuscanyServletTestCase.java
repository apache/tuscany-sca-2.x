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
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import junit.framework.TestCase;
import org.apache.tuscany.host.servlet.ServletRequestInjector;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

/**
 * Verifies {@link TuscanyServlet} properly services a request
 *
 * @version $Rev$ $Date$
 */
public class TuscanyServletTestCase extends TestCase {

    public void testService() throws Exception {
        ServletRequestInjector requestInjector = EasyMock.createMock(ServletRequestInjector.class);
        requestInjector.service(isA(ServletRequest.class), isA(ServletResponse.class));
        EasyMock.expectLastCall();
        EasyMock.replay(requestInjector);
        ServletContext context = org.easymock.classextension.EasyMock.createMock(ServletContext.class);
        expect(context.getAttribute(TuscanyServlet.TUSCANY_SERVLET_REQUEST_INJECTOR)).andReturn(requestInjector);
        org.easymock.classextension.EasyMock.replay(context);
        ServletConfig config = EasyMock.createMock(ServletConfig.class);
        EasyMock.expect(config.getServletContext()).andReturn(context);
        EasyMock.replay(config);
        TuscanyServlet servlet = new TuscanyServlet();
        servlet.init(config);
        ServletRequest req = EasyMock.createNiceMock(ServletRequest.class);
        ServletResponse res = EasyMock.createNiceMock(ServletResponse.class);
        servlet.service(req, res);
        EasyMock.verify(requestInjector);
    }

}
