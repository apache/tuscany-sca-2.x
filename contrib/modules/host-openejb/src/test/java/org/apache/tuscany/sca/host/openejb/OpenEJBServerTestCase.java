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
package org.apache.tuscany.sca.host.openejb;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.ejb.EJBSessionBean;

/**
 * @version $Rev$ $Date$
 */
public class OpenEJBServerTestCase extends TestCase {
    
    public interface TestRemote {
        String test(String s);
    }
    
    public static class TestImpl implements TestRemote {
        
        public TestImpl() {
        }
        
        public String test(String s) {
            return s.toUpperCase();
        }
        
        public void init() {
        }
        
        public void destroy() {
        }
    }
    
    private OpenEJBServer server;
    
    @Override
    protected void setUp() throws Exception {
        server = new OpenEJBServer();
    }
    
    @Override
    protected void tearDown() throws Exception {
        server.stop();
    }

    /**
     * Verifies registration and invocation of a session bean
     */
    public void testStatelessSessionBean() throws Exception {
        server.addSessionBean("TestBean", new EJBSessionBean(TestImpl.class, TestRemote.class));

        Properties properties = new Properties(System.getProperties());
        //properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitContextFactory.class.getName());
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.RemoteInitialContextFactory");
        properties.put(Context.PROVIDER_URL, "ejbd://localhost:2888");
        InitialContext ctx = new InitialContext(properties);
        Object object = ctx.lookup("TestBeanRemote");
        assertTrue(object instanceof TestRemote);

        TestRemote testRemote = (TestRemote)object;
        String s = testRemote.test("test");
        assertEquals("TEST", s);
    }

}
