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
import java.net.MalformedURLException;
import java.lang.reflect.Method;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import junit.framework.TestCase;
import static org.easymock.classextension.EasyMock.*;

/**
 * @version $Rev$ $Date$
 */
public class TuscanyContextListenerTestCase extends TestCase {
    private ServletContext context;
    private TuscanyContextListener listener;
    private ClassLoader cl;
    private URL systemUrl;
    private URL applicationUrl;
    private Method getRuntimeMethod;

    public void testInitializationUsingDefaults() throws Exception {
        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        listener = createMock(TuscanyContextListener.class, new Method[]{getRuntimeMethod});
        try {
            Thread.currentThread().setContextClassLoader(cl);
            ServletContextEvent event = createMock(ServletContextEvent.class);
            WebappRuntime runtime = createMock(WebappRuntime.class);
            expect(event.getServletContext()).andReturn(context);
            replay(event);
            expect(context.getInitParameter("tuscany.bootDir")).andReturn(null);
            expect(context.getResourcePaths("/WEB-INF/tuscany/boot")).andReturn(null);
            expect(context.getInitParameter("tuscany.systemScdlPath")).andReturn(null);
            expect(context.getInitParameter("tuscany.applicationScdlPath")).andReturn(null);
            expect(context.getResource("/WEB-INF/default.scdl")).andReturn(applicationUrl);
            replay(context);
            expect(cl.getResource("META-INF/tuscany/webapp.scdl")).andReturn(systemUrl);
            replay(cl);
            expect(listener.getRuntime(context, cl)).andReturn(runtime);
            replay(listener);
            runtime.setServletContext(context);
            runtime.setWebappClassLoader(cl);
            runtime.setSystemScdl(systemUrl);
            runtime.setApplicationScdl(applicationUrl);
            runtime.initialize();
            replay(runtime);
            listener.contextInitialized(event);
            verify(event);
            verify(context);
            verify(listener);
            verify(cl);
            verify(runtime);
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }
    }

    public void testGetInitParameterWhenSpecified() {
        String name = "name";
        String value = "default";
        expect(context.getInitParameter(name)).andReturn(value);
        replay(context);

        assertEquals(value, listener.getInitParameter(context, name, "default"));
        verify(context);
    }

    public void testGetInitParameterUsingDefault() {
        String name = "name";
        String value = "default";
        expect(context.getInitParameter(name)).andReturn(null);
        replay(context);

        assertEquals(value, listener.getInitParameter(context, name, value));
        verify(context);
    }

    public void testGetInitParameterWithZeroLength() {
        String name = "name";
        String value = "default";
        expect(context.getInitParameter(name)).andReturn("");
        replay(context);

        assertEquals(value, listener.getInitParameter(context, name, value));
        verify(context);
    }

    public void testGetScdlFromWebapp() throws MalformedURLException {
        String path = "/WEB-INF/test";
        expect(context.getResource(path)).andReturn(systemUrl);
        replay(context);
        replay(cl);
        assertSame(systemUrl, listener.getScdlURL(path, context, cl));
        verify(context);
        verify(cl);
    }

    public void testGetScdlFromWebappMissing() throws MalformedURLException {
        String path = "/WEB-INF/test";
        expect(context.getResource(path)).andReturn(null);
        replay(context);
        replay(cl);
        assertNull(listener.getScdlURL(path, context, cl));
        verify(context);
        verify(cl);
    }

    public void testGetScdlFromWebappMalformed() throws MalformedURLException {
        String path = "/WEB-INF/test";
        expect(context.getResource(path)).andThrow(new MalformedURLException());
        replay(context);
        replay(cl);
        try {
            listener.getScdlURL(path, context, cl);
            fail();
        } catch (MalformedURLException e) {
            // OK
        }
        verify(context);
        verify(cl);
    }

    public void testGetScdlFromClasspath() throws MalformedURLException {
        String path = "META-INF/test";
        replay(context);
        expect(cl.getResource(path)).andReturn(systemUrl);
        replay(cl);
        assertSame(systemUrl, listener.getScdlURL(path, context, cl));
        verify(context);
        verify(cl);
    }

    public void testGetScdlFromClasspathMissing() throws MalformedURLException {
        String path = "META-INF/test";
        replay(context);
        expect(cl.getResource(path)).andReturn(null);
        replay(cl);
        assertNull(listener.getScdlURL(path, context, cl));
        verify(context);
        verify(cl);
    }

    protected void setUp() throws Exception {
        super.setUp();
        getRuntimeMethod = TuscanyContextListener.class.getDeclaredMethod("getRuntime",
                                                                  ServletContext.class,
                                                                  ClassLoader.class);
        listener = new TuscanyContextListener();
        context = createMock(ServletContext.class);
        cl = createMock(ClassLoader.class);
        systemUrl = new URL("file:/system.scdl");
        applicationUrl = new URL("file:/application.scdl");
    }
}
