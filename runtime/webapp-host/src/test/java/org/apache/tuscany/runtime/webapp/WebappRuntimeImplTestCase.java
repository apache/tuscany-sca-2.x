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

import java.net.URL;
import javax.servlet.ServletContext;

import junit.framework.TestCase;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

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
        expect(context.getInitParameter(Constants.SYSTEM_MONITORING_PARAM)).andReturn(null);
        expect(context.getInitParameter(Constants.EXTENSION_SCDL_PATH_PARAM)).andReturn(null);
        expect(context.getResourcePaths("/META-INF/tuscany.extensions")).andReturn(null);
        expect(context.getServletContextName()).andReturn("foo");
        expect(context.getInitParameter(Constants.CURRENT_COMPOSITE_PATH_PARAM)).andReturn(null);
        context.setAttribute(eq(Constants.RUNTIME_ATTRIBUTE), isA(WebappRuntime.class));
        replay(context);
        runtime.initialize();
        verify(context);
    }

    protected void setUp() throws Exception {
        super.setUp();
        systemScdl = getClass().getResource("/META-INF/tuscany/webapp.scdl");
        applicationScdl = getClass().getResource("/testapp.scdl");
        context = createMock(ServletContext.class);

        runtime = new WebappRuntimeImpl();
        runtime.setHostClassLoader(getClass().getClassLoader());
        runtime.setServletContext(context);
        runtime.setSystemScdl(systemScdl);
        runtime.setApplicationScdl(applicationScdl);
    }
}
