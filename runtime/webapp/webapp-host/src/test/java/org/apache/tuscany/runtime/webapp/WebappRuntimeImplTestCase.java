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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.net.URL;

import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.apache.tuscany.core.monitor.NullMonitorFactory;

/**
 * @version $Rev$ $Date$
 */
public class WebappRuntimeImplTestCase extends TestCase {
    private URL applicationScdl;
    private URL systemScdl;
    private WebappRuntimeImpl runtime;
    private ServletContext context;

    /**
     * Verifies the web app host is configured properly to perform a basic boot
     */
    public void testBootWithDefaults() throws Exception {
        expect(context.getResourcePaths("/WEB-INF/tuscany/extensions/")).andReturn(null);
        replay(context);
        runtime.initialize();
        verify(context);
    }

/*
    public void testLazyHttpSessionId() throws Exception {
        expect(context.getResourcePaths("/WEB-INF/tuscany/extensions/")).andReturn(null);
        replay(context);
        runtime.initialize();
        verify(context);

        HttpServletRequest request = createMock(HttpServletRequest.class);
        expect(request.getSession(true)).andReturn(null);
        expect(request.getSession(false)).andReturn(null);
        replay(request);

        runtime.httpRequestStarted(request);
        
        ServletRequestInjector injector = runtime.getRequestInjector();
        class WorkContextAccessor extends ServletHostImpl {
            ServletHostImpl servletHostImpl;
            WorkContextAccessor(ServletHostImpl servletHostImpl){
                this.servletHostImpl = servletHostImpl;
            }
            WorkContext getWorkContext() {
                return servletHostImpl.workContext;
            }
        }
        WorkContext workContext = new WorkContextAccessor((ServletHostImpl)injector).getWorkContext();
        workContext.getIdentifier(Scope.SESSION);
        verify(request);
    }
*/

    protected void setUp() throws Exception {
        super.setUp();
        systemScdl = getClass().getResource("/META-INF/tuscany/webapp.scdl");
        applicationScdl = getClass().getResource("/testapp.scdl");
        context = createMock(ServletContext.class);

        runtime = new WebappRuntimeImpl();
        runtime.setRuntimeInfo(new WebappRuntimeInfoImpl(context, null, false));
        runtime.setMonitorFactory(new NullMonitorFactory());
        runtime.setHostClassLoader(getClass().getClassLoader());
        runtime.setServletContext(context);
        runtime.setSystemScdl(systemScdl);
        runtime.setApplicationName("foo");
        runtime.setApplicationScdl(applicationScdl);
    }
}
