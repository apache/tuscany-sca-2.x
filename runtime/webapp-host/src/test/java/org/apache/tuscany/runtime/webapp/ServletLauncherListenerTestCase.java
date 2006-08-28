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

import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.MissingResourceException;

import junit.framework.TestCase;
import static org.apache.tuscany.runtime.webapp.ServletLauncherListener.APPLICATION_SCDL_PATH_PARAM;
import static org.apache.tuscany.runtime.webapp.ServletLauncherListener.SYSTEM_MONITORING_PARAM;
import static org.apache.tuscany.runtime.webapp.ServletLauncherListener.SYSTEM_SCDL_PATH_PARAM;
import static org.easymock.EasyMock.expect;
import org.easymock.classextension.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ServletLauncherListenerTestCase extends TestCase {

    /**
     * Verifies the web app host is configured properly to perform a basic boot
     * <p/>
     * FIXME Uncomment this test case when the webapp project is fixed by removing the dependency on web services
     */
    public void testBoot() throws Exception {
//        final Launcher[] launcher = new Launcher[1];
//        ServletLauncherListener listener = new ServletLauncherListener();
//        listener.setTestSystemScdl(getClass().getClassLoader().getResource("META-INF/sca/webapp.system.scdl"));
//        ServletContext context = EasyMock.createMock(ServletContext.class);
//        expect(context.getServletContextName()).andReturn("foo").anyTimes();
//        expect(context.getInitParameter(SYSTEM_SCDL_PATH_PARAM)).andReturn(null);
//        expect(context.getInitParameter(APPLICATION_SCDL_PATH_PARAM)).andReturn(null);
//        expect(context.getInitParameter(SYSTEM_MONITORING_PARAM)).andReturn(null);
//        context.setAttribute(EasyMock.eq("Tuscany.LauncherImpl"), EasyMock.isA(Launcher.class));
//        EasyMock.expectLastCall().andStubAnswer(new IAnswer() {
//            public Object answer() throws Throwable {
//                Object o = EasyMock.getCurrentArguments()[1];
//                launcher[0] = (Launcher) o;
//                return null;
//            }
//        });
//        context.setAttribute(EasyMock.eq("Tuscany.ServletRequestInjector"), EasyMock.isA(ServletHost.class));
//        expect(context.getResource("/WEB-INF/default.scdl"))
//            .andReturn(getClass().getClassLoader().getResource("testapp.scdl"));
//        expect(context.getAttribute(EasyMock.eq("Tuscany.LauncherImpl"))).andReturn(launcher[0]);
//        EasyMock.replay(context);
//        ServletContextEvent event = EasyMock.createMock(ServletContextEvent.class);
//        EasyMock.expect(event.getServletContext()).andReturn(context).anyTimes();
//        EasyMock.replay(event);
//        listener.contextInitialized(event);
//        listener.contextDestroyed(event);
//        EasyMock.verify(context);
    }

    /**
     * Verifies a {@link LoaderException} is thrown when the application SCDL is not found
     * <p/>
     * FIXME Uncomment this test case when the webapp project is fixed by removing the dependency on web services
     */
    public void testApplicationSCDLNotFound() throws Exception {
//        ServletLauncherListener listener = new ServletLauncherListener();
//        listener.setTestSystemScdl(getClass().getClassLoader().getResource("META-INF/sca/webapp.system.scdl"));
//        ServletContext context = EasyMock.createMock(ServletContext.class);
//        expect(context.getInitParameter(SYSTEM_SCDL_PATH_PARAM)).andReturn(null);
//        expect(context.getServletContextName()).andReturn("foo").anyTimes();
//        context.setAttribute(EasyMock.eq("Tuscany.LauncherImpl"), EasyMock.isA(Launcher.class));
//        context.setAttribute(EasyMock.eq("Tuscany.ServletRequestInjector"), EasyMock.isA(ServletHost.class));
//        expect(context.getInitParameter(APPLICATION_SCDL_PATH_PARAM)).andReturn(null);
//        expect(context.getInitParameter(SYSTEM_MONITORING_PARAM)).andReturn(null);
//        expect(context.getResource("/WEB-INF/default.scdl")).andReturn(null);
//        context.setAttribute(EasyMock.eq("Tuscany.LauncherImpl.Throwable"), EasyMock.isA(LoaderException.class));
//        context.log(EasyMock.isA(String.class), EasyMock.isA(Throwable.class));
//        EasyMock.replay(context);
//        ServletContextEvent event = EasyMock.createMock(ServletContextEvent.class);
//        expect(event.getServletContext()).andReturn(context);
//        EasyMock.replay(event);
//        listener.contextInitialized(event);
//        EasyMock.verify(context);
    }

    /**
     * Verifies a {@link MissingResourceException} is thrown if the system SCDL is not found
     *
     * @throws Exception
     */
    public void testSystemSCDLNotFound() throws Exception {
        ServletLauncherListener listener = new ServletLauncherListener();
        ServletContext context = EasyMock.createMock(ServletContext.class);
        expect(context.getInitParameter(SYSTEM_SCDL_PATH_PARAM)).andReturn("notthere");
        context
            .setAttribute(EasyMock.eq("Tuscany.LauncherImpl.Throwable"), EasyMock.isA(MissingResourceException.class));
        expect(context.getInitParameter(APPLICATION_SCDL_PATH_PARAM)).andReturn(null);
        expect(context.getInitParameter(SYSTEM_MONITORING_PARAM)).andReturn(null);
        context.log(EasyMock.isA(String.class), EasyMock.isA(Throwable.class));
        EasyMock.replay(context);
        ServletContextEvent event = EasyMock.createMock(ServletContextEvent.class);
        EasyMock.expect(event.getServletContext()).andReturn(context);
        EasyMock.replay(event);
        listener.contextInitialized(event);
        EasyMock.verify(context);
    }


}
