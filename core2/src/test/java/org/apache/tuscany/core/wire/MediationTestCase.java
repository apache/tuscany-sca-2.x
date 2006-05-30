/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.wire;

import java.lang.reflect.Method;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.core.mock.component.SimpleTargetImpl;
import org.apache.tuscany.core.wire.StaticPojoTargetInvoker;

/**
 * Tests invoking on a different interface from the one actually implemented by the target
 *
 * @version $Rev$ $Date$
 */
public class MediationTestCase extends TestCase {

    private Method hello;

    public void setUp() throws Exception {
        hello = Hello.class.getMethod("hello", String.class);
    }

    public void testMediation() throws Exception {
        StaticPojoTargetInvoker invoker = new StaticPojoTargetInvoker(hello, new SimpleTargetImpl());
        Assert.assertEquals("foo", invoker.invokeTarget("foo"));
    }

    public interface Hello {

        public String hello(String message) throws Exception;

    }
}
