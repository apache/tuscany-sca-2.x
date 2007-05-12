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
package org.apache.tuscany.sca.interfacedef.java.impl;

import static org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil.findOperation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.tuscany.interfacedef.DataType;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.interfacedef.impl.OperationImpl;

/**
 * @version $Rev$ $Date$
 */
public class JavaInterfaceUtilTestCase extends TestCase {
    private List<Operation> operations;

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
        Operation operation = findOperation(method, operations);
        assertEquals(Integer.TYPE, operation.getInputType().getLogical().get(0).getPhysical());
    }

    protected void setUp() throws Exception {
        super.setUp();

        Operation operation = new OperationImpl("foo");
        List<DataType> types = new ArrayList<DataType>();
        DataType<List<DataType>> inputType = new DataTypeImpl<List<DataType>>(Object[].class, types);
        operation.setInputType(inputType);

        operations = new ArrayList<Operation>();
        operations.add(operation);

        types = new ArrayList<DataType>();
        inputType = new DataTypeImpl<List<DataType>>(Object[].class, types);
        DataType type = new DataTypeImpl<Class>(String.class, Object.class);
        types.add(type);
        operation = new OperationImpl("foo");
        operation.setInputType(inputType);
        operations.add(operation);

        types = new ArrayList<DataType>();
        type = new DataTypeImpl<Class>(String.class, Object.class);
        DataType type2 = new DataTypeImpl<Class>(String.class, Object.class);
        types.add(type);
        types.add(type2);
        inputType = new DataTypeImpl<List<DataType>>(Object[].class, types);
        operation = new OperationImpl("foo");
        operation.setInputType(inputType);
        operations.add(operation);

        types = new ArrayList<DataType>();
        type = new DataTypeImpl<Class>(Integer.class, Object.class);
        types.add(type);
        inputType = new DataTypeImpl<List<DataType>>(Object[].class, types);
        operation = new OperationImpl("foo");
        operation.setInputType(inputType);
        operations.add(operation);

        types = new ArrayList<DataType>();
        type = new DataTypeImpl<Class>(Integer.TYPE, Object.class);
        types.add(type);
        inputType = new DataTypeImpl<List<DataType>>(Object[].class, types);
        operation = new OperationImpl("foo");
        operation.setInputType(inputType);
        operations.add(operation);

    }

    private interface Foo {
        void foo();

        void foo(String foo);

        void foo(int b);
    }
}
