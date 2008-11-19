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
            new DefaultRMIHost().findService(null, "9994", "$BAD$");
            fail();
        } catch (RMIHostRuntimeException e) {
            // expected
        }
    }

    public void testRegisterService1() throws RMIHostRuntimeException, RMIHostException {
        DefaultRMIHost host = new DefaultRMIHost();
        host.registerService("foo1", 9996, new MockRemote());
        host.unregisterService("foo1", 9996);
    }

    public void testExistingRegistry() throws RMIHostRuntimeException, RMIHostException {
        DefaultRMIHost host1 = new DefaultRMIHost();
        host1.registerService("foo1", 9995, new MockRemote());
        DefaultRMIHost host2 = new DefaultRMIHost();
        host2.registerService("foo2", 9995, new MockRemote());
        host2.unregisterService("foo1", 9995);
        host2.unregisterService("foo2", 9995);
    }

    public void testRegisterService2() throws RMIHostRuntimeException, RMIHostException {
        DefaultRMIHost host = new DefaultRMIHost();
        host.registerService("bar1", 9999, new MockRemote());
        host.unregisterService("bar1", 9999);
    }

    public void testRegisterServiceAlreadyBound() throws RMIHostRuntimeException, RMIHostException {
        DefaultRMIHost host = new DefaultRMIHost();
        host.registerService("bar2", 9997, new MockRemote());
        try {
            host.registerService("bar2", 9997, new MockRemote());
        } catch (RMIHostException e) {
            // expected
            host.unregisterService("bar2", 9997);
        }
    }

    public void testUnRegisterService() throws RMIHostRuntimeException, RMIHostException {
        DefaultRMIHost host = new DefaultRMIHost();
        try {
            host.unregisterService("bar3", 9998);
            fail();
        } catch (RMIHostRuntimeException e) {
            // expected
        }
    }

    private static class MockRemote implements Remote, Serializable {
    }
}
