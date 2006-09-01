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

import static org.apache.tuscany.spi.idl.java.JavaIDLUtils.findMethod;
import static org.apache.tuscany.spi.idl.java.JavaIDLUtils.findOperation;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.Operation;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class JavaIDLUtilsTestCase extends TestCase {
    private Method[] methods;
    private List<Operation<?>> operations;

    public void testNoParamsFindMethod() {
        Operation<Type> operation = new Operation<Type>("foo", null, null, null, false, null);
        Method method = findMethod(operation, methods);
        assertEquals("foo", method.getName());
        assertEquals(0, method.getParameterTypes().length);
    }

    public void testNoParamsFindOperation() throws Exception {
        Method method = Foo.class.getMethod("foo");
        Operation ret = findOperation(method, operations);
        assertEquals("foo", ret.getName());
        assertEquals(0, method.getParameterTypes().length);
    }

    public void testParamsFindMethod() {
        List<DataType<Type>> types = new ArrayList<DataType<Type>>();
        DataType<Type> type = new DataType<Type>(String.class, Object.class);
        types.add(type);
        Operation<Type> operation = new Operation<Type>("foo", null, types, null, false, null);
        Method method = findMethod(operation, methods);
        assertEquals("foo", method.getName());
        assertEquals(String.class, method.getParameterTypes()[0]);
    }

    public void testParamsFindOperation() throws Exception {
        Method method = Foo.class.getMethod("foo", String.class);
        Operation ret = findOperation(method, operations);
        assertEquals("foo", ret.getName());
        assertEquals(String.class, method.getParameterTypes()[0]);
    }


    public void testTooManyParamsFindMethod() {
        List<DataType<Type>> types = new ArrayList<DataType<Type>>();
        DataType<Type> type = new DataType<Type>(String.class, Object.class);
        DataType<Type> type2 = new DataType<Type>(String.class, Object.class);
        types.add(type);
        types.add(type2);
        Operation<Type> operation = new Operation<Type>("foo", null, types, null, false, null);
        Method method = findMethod(operation, methods);
        assertNull(method);
    }

    public void testDifferentParamsFindMethod() {
        List<DataType<Type>> types = new ArrayList<DataType<Type>>();
        DataType<Type> type = new DataType<Type>(Integer.class, Object.class);
        types.add(type);
        Operation<Type> operation = new Operation<Type>("foo", null, types, null, false, null);
        Method method = findMethod(operation, methods);
        assertNull(method);
    }

    public void testPrimitiveParamNoFindMethod() {
        List<DataType<Type>> types = new ArrayList<DataType<Type>>();
        DataType<Type> type = new DataType<Type>(Integer.class, Object.class);
        types.add(type);
        Operation<Type> operation = new Operation<Type>("foo", null, types, null, false, null);
        Method method = findMethod(operation, methods);
        assertNull(method);
    }

    public void testPrimitiveParamFindMethod() {
        List<DataType<Type>> types = new ArrayList<DataType<Type>>();
        DataType<Type> type = new DataType<Type>(Integer.TYPE, Object.class);
        types.add(type);
        Operation<Type> operation = new Operation<Type>("foo", null, types, null, false, null);
        Method method = findMethod(operation, methods);
        assertEquals("foo", method.getName());
        assertEquals(Integer.TYPE, method.getParameterTypes()[0]);
    }

    public void testPrimitiveParamFindOperation() throws NoSuchMethodException {
        Method method = Foo.class.getMethod("foo", Integer.TYPE);
        Operation<?> operation = findOperation(method, operations);
        assertEquals(Integer.TYPE, operation.getParameterTypes().get(0).getPhysical());
    }


    public void testNotFoundMethod() {
        Operation<Type> operation = new Operation<Type>("not there", null, null, null, false, null);
        assertNull(findMethod(operation, methods));
    }

    protected void setUp() throws Exception {
        super.setUp();
        methods = Foo.class.getMethods();

        Operation<Type> operation = new Operation<Type>("foo", null, null, null, false, null);
        operations = new ArrayList<Operation<?>>();
        operations.add(operation);

        List<DataType<Type>> types = new ArrayList<DataType<Type>>();
        DataType<Type> type = new DataType<Type>(String.class, Object.class);
        types.add(type);
        operation = new Operation<Type>("foo", null, types, null, false, null);
        operations.add(operation);

        types = new ArrayList<DataType<Type>>();
        type = new DataType<Type>(String.class, Object.class);
        DataType<Type> type2 = new DataType<Type>(String.class, Object.class);
        types.add(type);
        types.add(type2);
        operation = new Operation<Type>("foo", null, types, null, false, null);
        operations.add(operation);

        types = new ArrayList<DataType<Type>>();
        type = new DataType<Type>(Integer.class, Object.class);
        types.add(type);
        operation = new Operation<Type>("foo", null, types, null, false, null);
        operations.add(operation);

        types = new ArrayList<DataType<Type>>();
        type = new DataType<Type>(Integer.TYPE, Object.class);
        types.add(type);
        operation = new Operation<Type>("foo", null, types, null, false, null);
        operations.add(operation);

    }

    private interface Foo {
        void foo();

        void foo(String foo);

        void foo(int b);
    }
}
