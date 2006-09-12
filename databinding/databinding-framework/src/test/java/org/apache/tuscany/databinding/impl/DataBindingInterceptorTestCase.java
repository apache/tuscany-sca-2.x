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

package org.apache.tuscany.databinding.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.databinding.Mediator;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.easymock.EasyMock;

/**
 * 
 */
public class DataBindingInterceptorTestCase extends TestCase {
    private DataBindingInteceptor interceptor;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test method for
     * {@link org.apache.tuscany.databinding.impl.DataBindingInteceptor#invoke(org.apache.tuscany.spi.wire.Message)}.
     */
    public final void testInvoke() {
        DataType<Class> type1 = new DataType<Class>("xml:string", String.class, String.class);
        List<DataType<Class>> types1 = new ArrayList<DataType<Class>>();
        types1.add(type1);
        DataType<List<DataType<Class>>> inputType1 =
                new DataType<List<DataType<Class>>>("xml:string", Object[].class, types1);

        DataType<Class> type2 = new DataType<Class>("foo", Foo.class, Foo.class);
        List<DataType<Class>> types2 = new ArrayList<DataType<Class>>();
        types2.add(type2);
        DataType<List<DataType<Class>>> inputType2 
            = new DataType<List<DataType<Class>>>("foo", Object[].class, types2);

        Operation<Class> operation1 = new Operation<Class>("call", inputType1, type1, null, false, "xml:string");
        Operation<Class> operation2 = new Operation<Class>("call", inputType2, type2, null, false, "org.w3c.dom.Node");
        interceptor = new DataBindingInteceptor(null, operation1, null, operation2);
        Mediator mediator = createMock(Mediator.class);
        Object[] source = new Object[] { "<foo>bar</foo>" };
        Foo foo = new Foo();
        foo.bar = "bar";
        Object[] target = new Object[] { foo };
        expect(mediator.mediate(source, inputType1, inputType2)).andReturn(target);
        expect(mediator.mediate(target[0], type2, type1)).andReturn(source[0]);
        replay(mediator);
        interceptor.setMediator(mediator);
        Message msg = createMock(Message.class);
        msg.setBody(EasyMock.anyObject());
        expectLastCall().anyTimes();
        expect(msg.getBody()).andReturn(source).once().andReturn(target[0]).once().andReturn(source[0]);
        replay(msg);
        Interceptor next = createMock(Interceptor.class);
        expect(next.invoke(msg)).andReturn(msg);
        replay(next);
        interceptor.setNext(next);
        interceptor.invoke(msg);
        String result = (String) msg.getBody();
        Assert.assertEquals(source[0], result);
        EasyMock.verify(mediator, msg, next);
    }

    private static class Foo {
        private String bar;
    }
}
