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
package org.apache.tuscany.sca.rmi;

import java.rmi.Remote;

import org.apache.tuscany.sca.rmi.DefaultRMIHost;
import org.apache.tuscany.sca.rmi.RMIHostException;
import org.apache.tuscany.sca.rmi.RMIHostRuntimeException;

import junit.framework.TestCase;

public class RMIHostImplTestCase extends TestCase {

    public void testInit() {
        new DefaultRMIHost();
    }

    public void testFindServiceBadHost() throws RMIHostRuntimeException, RMIHostException {
        try {
            new DefaultRMIHost().findService(null, "0", null);
            fail();
        } catch (RMIHostRuntimeException e) {
            // expected
        }
    }

    public void testRegisterService1() throws RMIHostRuntimeException, RMIHostException {
        DefaultRMIHost host = new DefaultRMIHost();
        host.registerService("foo1", new MockRemote());
        host.unregisterService("foo1");
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
        } catch (RMIHostException e) {
            // expected
        }
    }

    private static class MockRemote implements Remote {
    }
}
