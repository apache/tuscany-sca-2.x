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

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.osoa.sca.SCA;

import junit.framework.TestCase;
import static org.apache.tuscany.runtime.webapp.Constants.RUNTIME_ATTRIBUTE;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import org.easymock.classextension.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class TuscanyFilterTestCase extends TestCase {

    public void testStartStopFilter() throws Exception {
        SCA sca = EasyMock.createNiceMock(SCA.class);
        sca.start();
        sca.stop();
        EasyMock.replay(sca);
        WebappRuntime runtime = createMock(WebappRuntime.class);
        expect(runtime.getContext()).andReturn(sca);
        runtime.startRequest();
        runtime.stopRequest();
        replay(runtime);
        ServletContext context = createNiceMock(ServletContext.class);
        EasyMock.expect(context.getAttribute(RUNTIME_ATTRIBUTE)).andReturn(runtime);
        replay(context);
        TuscanyFilter filter = new TuscanyFilter();
        FilterConfig config = createMock(FilterConfig.class);
        expect(config.getServletContext()).andReturn(context);
        replay(config);
        filter.init(config);
        ServletRequest req = createNiceMock(ServletRequest.class);
        ServletResponse resp = createNiceMock(ServletResponse.class);
        FilterChain chain = createNiceMock(FilterChain.class);
        filter.doFilter(req, resp, chain);
        verify(runtime);
        EasyMock.verify(sca);
    }

    public void testExceptionCleanupFilter() throws Exception {
        SCA sca = EasyMock.createNiceMock(SCA.class);
        sca.start();
        sca.stop();
        EasyMock.replay(sca);
        WebappRuntime runtime = createMock(WebappRuntime.class);
        runtime.startRequest();
        runtime.stopRequest();
        expect(runtime.getContext()).andReturn(sca);
        replay(runtime);
        ServletContext context = createNiceMock(ServletContext.class);
        EasyMock.expect(context.getAttribute(RUNTIME_ATTRIBUTE)).andReturn(runtime);
        replay(context);
        TuscanyFilter filter = new TuscanyFilter();
        FilterConfig config = createMock(FilterConfig.class);
        expect(config.getServletContext()).andReturn(context);
        replay(config);
        filter.init(config);
        ServletRequest req = createNiceMock(ServletRequest.class);
        ServletResponse resp = createNiceMock(ServletResponse.class);
        FilterChain chain = createNiceMock(FilterChain.class);
        chain.doFilter(isA(ServletRequest.class), isA(ServletResponse.class));
        EasyMock.expectLastCall().andThrow(new TestException());
        filter.doFilter(req, resp, chain);
        verify(runtime);
        EasyMock.verify(sca);
    }

    public void testRuntimeNotConfigured() throws Exception {
        ServletContext context = createNiceMock(ServletContext.class);
        TuscanyFilter filter = new TuscanyFilter();
        FilterConfig config = createMock(FilterConfig.class);
        expect(config.getServletContext()).andReturn(context);
        replay(config);
        try {
            filter.init(config);
            fail();
        } catch (ServletException e) {
            //expected
        }
    }

    private class TestException extends RuntimeException {

    }


}
