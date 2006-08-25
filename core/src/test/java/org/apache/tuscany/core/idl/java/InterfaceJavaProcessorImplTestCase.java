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
package org.apache.tuscany.core.idl.java;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.Operation;

import org.apache.tuscany.core.util.JavaIntrospectionHelper;

/**
 * @version $Rev$ $Date$
 */
public class InterfaceJavaProcessorImplTestCase extends TestCase {
    private InterfaceJavaIntrospectorImpl impl;

    public void testSimpleInterface() throws InvalidServiceContractException {
        JavaServiceContract contract = impl.introspect(Simple.class);
        // TODO spec to clairfy interface name
        assertEquals(JavaIntrospectionHelper.getBaseName(Simple.class), contract.getInterfaceName());
        assertEquals(Simple.class, contract.getInterfaceClass());
        Map<String, Operation<Type>> operations = contract.getOperations();
        assertEquals(1, operations.size());
        Operation<Type> baseInt = operations.get("baseInt");
        assertNotNull(baseInt);

        DataType<Type> returnType = baseInt.getReturnType();
        assertEquals(Integer.TYPE, returnType.getPhysical());
        assertEquals(Integer.TYPE, returnType.getLogical());

        List<DataType<Type>> parameterTypes = baseInt.getParameterTypes();
        assertEquals(1, parameterTypes.size());
        DataType<Type> arg0 = parameterTypes.get(0);
        assertEquals(Integer.TYPE, arg0.getPhysical());
        assertEquals(Integer.TYPE, arg0.getLogical());

        List<DataType<Type>> faultTypes = baseInt.getFaultTypes();
        assertEquals(1, faultTypes.size());
        DataType<Type> fault0 = faultTypes.get(0);
        assertEquals(RuntimeException.class, fault0.getPhysical());
        assertEquals(RuntimeException.class, fault0.getLogical());
    }

    protected void setUp() throws Exception {
        super.setUp();
        impl = new InterfaceJavaIntrospectorImpl();
    }

    private static interface Base {
        int baseInt(int param) throws RuntimeException;
    }

    private static interface Simple extends Base {

    }
}
