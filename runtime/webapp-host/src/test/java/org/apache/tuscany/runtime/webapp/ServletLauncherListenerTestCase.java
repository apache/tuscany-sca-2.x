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
import java.util.Collections;
import javax.servlet.ServletContext;

import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import org.easymock.IAnswer;
import org.easymock.classextension.EasyMock;

import org.apache.tuscany.api.TuscanyException;
import static org.apache.tuscany.runtime.webapp.Constants.DEFAULT_EXTENSION_PATH_PARAM;
import static org.apache.tuscany.runtime.webapp.Constants.RUNTIME_ATTRIBUTE;
import static org.apache.tuscany.runtime.webapp.Constants.SYSTEM_SCDL_PATH_PARAM;

/**
 * @version $Rev$ $Date$
 */
public class ServletLauncherListenerTestCase extends TestCase {

    /**
     * Verifies the web app host is configured properly to perform a basic boot
     */
    public void testBoot() throws Exception {
        final TuscanyWebappRuntime[] runtime = new TuscanyWebappRuntime[1];
        ServletLauncherListener listener = new ServletLauncherListener();
        listener.setTestSystemScdl(getClass().getClassLoader().getResource("META-INF/sca/webapp.system.scdl"));
        ServletContext context = EasyMock.createNiceMock(ServletContext.class);
        expect(context.getServletContextName()).andReturn("foo").anyTimes();
        expect(context.getResourcePaths(DEFAULT_EXTENSION_PATH_PARAM)).andReturn(Collections.emptySet());
        context.setAttribute(EasyMock.eq(RUNTIME_ATTRIBUTE), EasyMock.isA(TuscanyWebappRuntime.class));
        EasyMock.expectLastCall().andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                Object o = EasyMock.getCurrentArguments()[1];
                runtime[0] = (TuscanyWebappRuntime) o;
                return null;
            }
        });
        URL resource = getClass().getClassLoader().getResource("testapp.scdl");
        expect(context.getResource("/WEB-INF/default.scdl")).andReturn(resource);
        context.setAttribute(EasyMock.eq(RUNTIME_ATTRIBUTE), EasyMock.isA(TuscanyWebappRuntime.class));
        EasyMock.replay(context);

        listener.initialize(context);
        listener.destroy();
        EasyMock.verify(context);
    }

    /**
     * Verifies a an is thrown when the application SCDL is not found
     */
    public void testApplicationSCDLNotFound() throws Exception {
        ServletLauncherListener listener = new ServletLauncherListener();
        listener.setTestSystemScdl(getClass().getClassLoader().getResource("META-INF/sca/webapp.system.scdl"));
        ServletContext context = EasyMock.createNiceMock(ServletContext.class);
        expect(context.getServletContextName()).andReturn("foo").anyTimes();
        expect(context.getResourcePaths(DEFAULT_EXTENSION_PATH_PARAM)).andReturn(Collections.emptySet());
        EasyMock.replay(context);

        try {
            listener.initialize(context);
            fail();
        } catch (ServletLauncherInitException e) {
            assertTrue(e.getCause() instanceof TuscanyException);
        }
        EasyMock.verify(context);
    }

    /**
     * Verifies an exception is thrown if the system SCDL is not found
     */
    public void testSystemSCDLNotFound() throws Exception {
        ServletLauncherListener listener = new ServletLauncherListener();
        listener.setTestSystemScdl(getClass().getClassLoader().getResource("META-INF/sca/webapp.system.scdl"));
        ServletContext context = EasyMock.createNiceMock(ServletContext.class);
        expect(context.getServletContextName()).andReturn("foo").anyTimes();
        expect(context.getInitParameter(SYSTEM_SCDL_PATH_PARAM)).andReturn("notthere");
        expect(context.getResourcePaths(DEFAULT_EXTENSION_PATH_PARAM)).andReturn(Collections.emptySet());
        EasyMock.replay(context);

        try {
            listener.initialize(context);
            fail();
        } catch (ServletLauncherInitException e) {
            assertTrue(e.getCause() instanceof TuscanyException);
        }
        EasyMock.verify(context);
    }


}
