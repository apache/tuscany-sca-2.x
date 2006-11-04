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
package org.apache.tuscany.persistence.datasource;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.ObjectFactory;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class InjectorTestCase extends TestCase {

    @SuppressWarnings("unchecked")
    public void testInject() throws Exception {
        Method m = Foo.class.getMethod("setVal", String.class);
        ObjectFactory factory = EasyMock.createMock(ObjectFactory.class);
        EasyMock.expect(factory.getInstance()).andReturn("foo");
        EasyMock.replay(factory);
        Injector injector = new Injector(m, factory);
        Foo foo = new Foo();
        injector.inject(foo);
        assertEquals("foo", foo.val);
        EasyMock.verify(factory);
    }

    private class Foo {
        private String val;

        public void setVal(String val) {
            this.val = val;
        }
    }
}
