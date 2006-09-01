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
package org.apache.tuscany.core.injection;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.tuscany.spi.ObjectCreationException;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $Rev$ $Date$
 */
public class JNDIObjectFactoryTestCase extends MockObjectTestCase {

    public void testGetInstance() throws Exception {
        Mock mock = mock(Context.class);
        mock.expects(once()).method("lookup").with(eq("foo")).will(returnValue(new Foo()));
        Context ctx = (Context) mock.proxy();
        JNDIObjectFactory<Foo> factory = new JNDIObjectFactory<Foo>(ctx, "foo");
        assertTrue(factory.getInstance() instanceof Foo); // must do an instanceof b/c of type erasure
    }

    public void testGetInstanceError() throws Exception {
        Mock mock = mock(Context.class);
        mock.expects(once()).method("lookup").with(eq("foo")).will(throwException(new NamingException()));
        Context ctx = (Context) mock.proxy();
        JNDIObjectFactory<Foo> factory = new JNDIObjectFactory<Foo>(ctx, "foo");
        try {
            factory.getInstance();
            fail();
        } catch (ObjectCreationException e) {
            //expected
        }
    }


    private class Foo {

    }
}
