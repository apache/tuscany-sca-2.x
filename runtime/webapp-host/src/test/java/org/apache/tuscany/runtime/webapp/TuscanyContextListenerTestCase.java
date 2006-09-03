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
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpSessionEvent;

import org.osoa.sca.SCA;

import junit.framework.TestCase;
import org.apache.tuscany.host.servlet.ServletRequestInjector;
import static org.easymock.EasyMock.expect;
import org.easymock.classextension.EasyMock;

/**
 * Verifies a context listener is properly instantiated and lifecycle events are sent to it. this must be tested in
 * webapp-host as the listener isntantiates {@link ServletLauncherListener} reflectively
 *
 * @version $Rev$ $Date$
 */
public class TuscanyContextListenerTestCase extends TestCase {

    public void testLifecycle() {
        ServletContext context = EasyMock.createMock(ServletContext.class);
        expect(context.getInitParameter(Constants.BOOTDIR_PARAM)).andReturn("foo");
        expect(context.getResourcePaths("foo")).andReturn(null);
        expect(context.getInitParameter(Constants.LAUNCHER_PARAM)).andReturn(TestRuntime.class.getName());
        EasyMock.replay(context);
        ServletContextEvent event = EasyMock.createMock(ServletContextEvent.class);
        expect(event.getServletContext()).andReturn(context);
        EasyMock.replay(event);
        TuscanyContextListener listener = new TuscanyContextListener();
        listener.contextInitialized(event);
        listener.contextDestroyed(event);
        EasyMock.verify(context);
    }

    public static class TestRuntime implements TuscanyWebappRuntime {

        private static int initialized;
        private static int destroyed;

        public TestRuntime() {
        }

        public static int getInitialized() {
            return initialized;
        }

        public static int getDestroyed() {
            return destroyed;
        }

        public void contextInitialized(ServletContextEvent servletContextEvent) {
            ++initialized;
        }

        public void contextDestroyed(ServletContextEvent servletContextEvent) {
            ++destroyed;
        }

        public void sessionCreated(HttpSessionEvent httpSessionEvent) {

        }

        public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {

        }

        public SCA getContext() {
            return null;
        }

        public ServletRequestInjector getRequestInjector() {
            return null;
        }

        public void startRequest() {

        }

        public void stopRequest() {

        }
    }
}
