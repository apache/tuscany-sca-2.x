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
package org.apache.tuscany.binding.rmi;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.Remote;

import org.apache.tuscany.spi.wire.TargetInvoker;

import junit.framework.TestCase;

import org.apache.tuscany.host.rmi.RMIHost;
import org.apache.tuscany.host.rmi.RMIHostException;
import org.apache.tuscany.host.rmi.RMIHostRuntimeException;

public class RMIInvokerTestCase extends TestCase {

    public void testInvokeTarget() throws InvocationTargetException, RMIHostRuntimeException, RMIHostException, IllegalArgumentException,
            IllegalAccessException, SecurityException, NoSuchMethodException {
        Method method = Object.class.getDeclaredMethod("toString", new Class[] {});
        RMIHost host = createMock(RMIHost.class);
        expect(host.findService(null, null, null)).andReturn(new Remote() {
        });
        replay(host);
        RMIInvoker invoker = new RMIInvoker(host, null, null, null, method);
        assertNotNull(invoker.invokeTarget(new Object[] {}, TargetInvoker.NONE));
    }

// TODO: these don't seem to work
//    public void testInvokeTargetRMIHostException() throws InvocationTargetException, RMIHostRuntimeException, RMIHostException,
//            IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchMethodException {
//        Method method = foo.class.getDeclaredMethod("bang", new Class[] {});
//        RMIHost host = createMock(RMIHost.class);
//        expect(host.findService(null, null, null)).andReturn(new foo());
//        replay(host);
//        try {
//            new RMIInvoker(host, null, null, null, method).invokeTarget(new Object[] {});
//            fail();
//        } catch (InvocationTargetException e) {
//            // expected
//        }
//    }
//
//    public void testInvokeTargetIllegalAccessException() throws InvocationTargetException, RMIHostRuntimeException, RMIHostException,
//            IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchMethodException {
//        Method method = foo.class.getDeclaredMethod("crash", new Class[] {});
//        RMIHost host = createMock(RMIHost.class);
//        expect(host.findService(null, null, null)).andReturn(new foo());
//        replay(host);
//        try {
//            new RMIInvoker(host, null, null, null, method).invokeTarget(new Object[] {});
//            fail();
//        } catch (InvocationTargetException e) {
//            // expected
//        }
//    }
//
//    class foo implements Remote {
//        void crash() throws IllegalAccessException {
//            throw new IllegalAccessException();
//        }
//
//        void bang() throws RMIHostException {
//            throw new RMIHostException();
//        }
//    }
}
