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

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.tuscany.spi.wire.WireInvocationHandler;

public class RemoteMethodHandlerTestCase extends TestCase {
    
    public void testIntercept() throws SecurityException, NoSuchMethodException, Throwable {
        WireInvocationHandler h2 = createMock(WireInvocationHandler.class);
        Method method = Runnable.class.getDeclaredMethod("run", new Class[]{});
        Object[] noArgs = new Object[]{};
        expect(h2.invoke(method, noArgs)).andReturn("foo");
        replay(h2);
        RemoteMethodHandler handler = new RemoteMethodHandler(h2, Runnable.class);
        Object o = handler.intercept(null, method, noArgs, null);
        assertEquals("foo", o);
    }

}
