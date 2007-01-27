/**
 *
 * Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.rmi.host;

import java.rmi.Remote;

import junit.framework.TestCase;

import org.apache.tuscany.host.rmi.RMIHostException;
import org.apache.tuscany.host.rmi.RMIHostRuntimeException;

public class RMIHostImplTestCase extends TestCase {

    public void testInit() {
        new RMIHostImpl();
    }

    public void testFindServiceBadHost() throws RMIHostRuntimeException, RMIHostException {
        try {
            new RMIHostImpl().findService(null, "0", null);
            fail();
        } catch (RMIHostRuntimeException e) {
            // expected
        }
    }

    public void testRegisterService1() throws RMIHostRuntimeException, RMIHostException {
        RMIHostImpl host = new RMIHostImpl();
        host.registerService("foo1", new MockRemote());
        host.unregisterService("foo1");
    }

    public void testRegisterService2() throws RMIHostRuntimeException, RMIHostException {
        RMIHostImpl host = new RMIHostImpl();
        host.registerService("bar1", 9999, new MockRemote());
        host.unregisterService("bar1", 9999);
    }

    public void testRegisterServiceAlreadyBound() throws RMIHostRuntimeException, RMIHostException {
        RMIHostImpl host = new RMIHostImpl();
        host.registerService("bar2", 9997, new MockRemote());
        try {
            host.registerService("bar2", 9997, new MockRemote());
        } catch (RMIHostException e) {
            // expected
            host.unregisterService("bar2", 9997);
        }
    }

    public void testUnRegisterService() throws RMIHostRuntimeException, RMIHostException {
        RMIHostImpl host = new RMIHostImpl();
        try {
            host.unregisterService("bar3", 9998);
            fail();
        } catch (RMIHostException e) {
            // expected
        }
    }

    private static class MockRemote implements Remote {
    }
}
