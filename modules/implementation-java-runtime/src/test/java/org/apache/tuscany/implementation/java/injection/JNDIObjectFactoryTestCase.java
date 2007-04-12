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
package org.apache.tuscany.implementation.java.injection;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.tuscany.implementation.java.injection.JNDIObjectFactory;
import org.apache.tuscany.spi.ObjectCreationException;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class JNDIObjectFactoryTestCase extends TestCase {

    public void testGetInstance() throws Exception {
        Context ctx = EasyMock.createMock(Context.class);
        EasyMock.expect(ctx.lookup(EasyMock.eq("foo"))).andReturn(new Foo());
        EasyMock.replay(ctx);
        JNDIObjectFactory<Foo> factory = new JNDIObjectFactory<Foo>(ctx, "foo");
        assertTrue(factory.getInstance() instanceof Foo); // must do an instanceof b/c of type erasure
        EasyMock.verify(ctx);
    }

    public void testGetInstanceError() throws Exception {
        Context ctx = EasyMock.createMock(Context.class);
        EasyMock.expect(ctx.lookup(EasyMock.eq("foo"))).andThrow(new NamingException());
        EasyMock.replay(ctx);
        JNDIObjectFactory<Foo> factory = new JNDIObjectFactory<Foo>(ctx, "foo");
        try {
            factory.getInstance();
            fail();
        } catch (ObjectCreationException e) {
            //expected
        }
        EasyMock.verify(ctx);
    }


    private class Foo {

    }
}
