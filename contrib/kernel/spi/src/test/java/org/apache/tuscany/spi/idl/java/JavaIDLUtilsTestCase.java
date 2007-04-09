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
package org.apache.tuscany.spi.idl.java;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.apache.tuscany.spi.idl.java.JavaIDLUtils.findOperation;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.Operation;
import static org.apache.tuscany.spi.model.Operation.NO_CONVERSATION;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class JavaIDLUtilsTestCase extends TestCase {
    private List<Operation<?>> operations;

    public void testNoParamsFindOperation() throws Exception {
        Method method = Foo.class.getMethod("foo");
        Operation ret = findOperation(method, operations);
        assertEquals("foo", ret.getName());
        assertEquals(0, method.getParameterTypes().length);
    }

    public void testParamsFindOperation() throws Exception {
        Method method = Foo.class.getMethod("foo", String.class);
        Operation ret = findOperation(method, operations);
        assertEquals("foo", ret.getName());
        assertEquals(String.class, method.getParameterTypes()[0]);
    }

    public void testPrimitiveParamFindOperation() throws NoSuchMethodException {
        Method method = Foo.class.getMethod("foo", Integer.TYPE);
        Operation<?> operation = findOperation(method, operations);
        assertEquals(Integer.TYPE, operation.getInputType().getLogical().get(0).getPhysical());
    }

    protected void setUp() throws Exception {
        super.setUp();

        Operation<Type> operation = new Operation<Type>("foo", null, null, null, false, null, NO_CONVERSATION);
        operations = new ArrayList<Operation<?>>();
        operations.add(operation);

        List<DataType<Type>> types = new ArrayList<DataType<Type>>();
        DataType<List<DataType<Type>>> inputType = new DataType<List<DataType<Type>>>(Object[].class, types);
        DataType<Type> type = new DataType<Type>(String.class, Object.class);
        types.add(type);
        operation = new Operation<Type>("foo", inputType, null, null, false, null, NO_CONVERSATION);
        operations.add(operation);

        types = new ArrayList<DataType<Type>>();
        type = new DataType<Type>(String.class, Object.class);
        DataType<Type> type2 = new DataType<Type>(String.class, Object.class);
        types.add(type);
        types.add(type2);
        inputType = new DataType<List<DataType<Type>>>(Object[].class, types);
        operation = new Operation<Type>("foo", inputType, null, null, false, null, NO_CONVERSATION);
        operations.add(operation);

        types = new ArrayList<DataType<Type>>();
        type = new DataType<Type>(Integer.class, Object.class);
        types.add(type);
        inputType = new DataType<List<DataType<Type>>>(Object[].class, types);
        operation = new Operation<Type>("foo", inputType, null, null, false, null, NO_CONVERSATION);
        operations.add(operation);

        types = new ArrayList<DataType<Type>>();
        type = new DataType<Type>(Integer.TYPE, Object.class);
        types.add(type);
        inputType = new DataType<List<DataType<Type>>>(Object[].class, types);
        operation = new Operation<Type>("foo", inputType, null, null, false, null, NO_CONVERSATION);
        operations.add(operation);

    }

    private interface Foo {
        void foo();

        void foo(String foo);

        void foo(int b);
    }
}
