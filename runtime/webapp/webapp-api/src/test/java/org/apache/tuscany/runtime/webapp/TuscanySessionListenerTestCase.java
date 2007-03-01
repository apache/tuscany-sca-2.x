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
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import junit.framework.TestCase;
import static org.apache.tuscany.runtime.webapp.Constants.RUNTIME_ATTRIBUTE;
import org.easymock.EasyMock;

/**
 * Verifies {@link org.apache.tuscany.runtime.webapp.TuscanySessionListener} notifies the runtime of session events
 *
 * @version $Rev$ $Date$
 */
public class TuscanySessionListenerTestCase extends TestCase {

    public void testSessionPropagated() throws Exception {
        WebappRuntime runtime = EasyMock.createNiceMock(WebappRuntime.class);
        runtime.sessionCreated(EasyMock.isA(HttpSessionEvent.class));
        runtime.sessionDestroyed(EasyMock.isA(HttpSessionEvent.class));
        EasyMock.replay(runtime);
        ServletContext context = EasyMock.createNiceMock(ServletContext.class);
        EasyMock.expect(context.getAttribute(RUNTIME_ATTRIBUTE)).andReturn(runtime);
        EasyMock.replay(context);
        HttpSession session = EasyMock.createNiceMock(HttpSession.class);
        EasyMock.expect(session.getServletContext()).andReturn(context);
        EasyMock.replay(session);
        HttpSessionEvent event = new HttpSessionEvent(session);
        TuscanySessionListener listener = new TuscanySessionListener();
        listener.sessionCreated(event);
        listener.sessionDestroyed(event);
        EasyMock.verify(context);
        EasyMock.verify(runtime);
    }

    /**
     * Verifies an error is logged when no runtime is configured
     *
     * @throws Exception
     */
    public void testRuntimeNotConfigured() throws Exception {
        ServletContext context = EasyMock.createNiceMock(ServletContext.class);
        context.log(EasyMock.isA(String.class), EasyMock.isA(ServletException.class));
        EasyMock.replay(context);
        TuscanySessionListener listener = new TuscanySessionListener();
        HttpSession session = EasyMock.createNiceMock(HttpSession.class);
        EasyMock.expect(session.getServletContext()).andReturn(context);
        EasyMock.replay(session);
        HttpSessionEvent event = new HttpSessionEvent(session);
        listener.sessionCreated(event);
        EasyMock.verify(context);
    }

    public void testSessionDestroyedBeforeCreated() throws Exception {
        TuscanySessionListener listener = new TuscanySessionListener();
        listener.sessionDestroyed(null);
    }
}
