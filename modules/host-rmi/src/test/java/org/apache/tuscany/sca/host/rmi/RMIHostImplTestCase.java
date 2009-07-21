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
package org.apache.tuscany.sca.host.rmi;

import java.io.Serializable;
import java.rmi.Remote;

import junit.framework.TestCase;

/**
 * Test cases for the RMI Host.
 *
 * @version $Rev$ $Date$
 */
public class RMIHostImplTestCase extends TestCase {

    public void testInit() {
        new DefaultRMIHost();
    }

    public void testFindServiceBadHost() throws RMIHostRuntimeException, RMIHostException {
        try {
            new DefaultRMIHost().findService("rmi://locahost:9994/$BAD$");
            fail();
        } catch (RMIHostRuntimeException e) {
            // expected
        }
    }

    public void testRegisterService1() throws RMIHostRuntimeException, RMIHostException {
        DefaultRMIHost host = new DefaultRMIHost();
        host.registerService("rmi://localhost:9996/foo1", new MockRemote());
        host.unregisterService("rmi://localhost:9996/foo1");
    }

    public void testExistingRegistry() throws RMIHostRuntimeException, RMIHostException {
        DefaultRMIHost host1 = new DefaultRMIHost();
        host1.registerService("rmi://localhost:9995/foo1", new MockRemote());
        DefaultRMIHost host2 = new DefaultRMIHost();
        host2.registerService("rmi://localhost:9995/foo2", new MockRemote());
        host2.unregisterService("rmi://localhost:9995/foo1");
        host2.unregisterService("rmi://localhost:9995/foo2");
    }

    public void testRegisterService2() throws RMIHostRuntimeException, RMIHostException {
        DefaultRMIHost host = new DefaultRMIHost();
        host.registerService("rmi://localhost:9999/bar1", new MockRemote());
        host.unregisterService("rmi://localhost:9999/bar1");
    }

    public void testRegisterServiceAlreadyBound() throws RMIHostRuntimeException, RMIHostException {
        DefaultRMIHost host = new DefaultRMIHost();
        host.registerService("rmi://localhost:9997/bar2", new MockRemote());
        try {
            host.registerService("rmi://localhost:9997/bar2", new MockRemote());
        } catch (RMIHostException e) {
            // expected
            host.unregisterService("rmi://localhost:9997/bar2");
        }
    }

    public void testUnRegisterService() throws RMIHostRuntimeException, RMIHostException {
        DefaultRMIHost host = new DefaultRMIHost();
        try {
            host.unregisterService("rmi://localhost:9998/bar3");
            fail();
        } catch (RMIHostRuntimeException e) {
            // expected
        }
    }

    private static class MockRemote implements Remote, Serializable {
    }
}
