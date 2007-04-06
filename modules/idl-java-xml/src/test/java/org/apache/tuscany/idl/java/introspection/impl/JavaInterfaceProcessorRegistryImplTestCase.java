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
package org.apache.tuscany.idl.java.introspection.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.lang.reflect.Type;
import java.util.List;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Contract;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.idl.DataType;
import org.apache.tuscany.idl.InvalidInterfaceException;
import org.apache.tuscany.idl.Operation;
import org.apache.tuscany.idl.java.JavaInterface;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class JavaInterfaceProcessorRegistryImplTestCase extends TestCase {
    private JavaInterfaceProcessorRegistryImpl impl;
    private AssemblyFactory factory = new DefaultAssemblyFactory();

    public void testSimpleInterface() throws InvalidInterfaceException {
        Contract contract = factory.createComponentService();
        impl.introspect(contract, Simple.class);

        JavaInterface intf = (JavaInterface)contract.getInterface();

        assertEquals(Simple.class, intf.getJavaClass());
        List<Operation> operations = intf.getOperations();
        assertEquals(1, operations.size());
        Operation baseInt = operations.get(0);
        assertEquals("baseInt", baseInt.getName());

        DataType<Type> returnType = baseInt.getOutputType();
        assertEquals(Integer.TYPE, returnType.getPhysical());
        assertEquals(Integer.TYPE, returnType.getLogical());

        List<DataType> parameterTypes = baseInt.getInputType().getLogical();
        assertEquals(1, parameterTypes.size());
        DataType<Type> arg0 = parameterTypes.get(0);
        assertEquals(Integer.TYPE, arg0.getPhysical());
        assertEquals(Integer.TYPE, arg0.getLogical());

        List<DataType> faultTypes = baseInt.getFaultTypes();
        assertEquals(1, faultTypes.size());
        DataType<Type> fault0 = faultTypes.get(0);
        assertEquals(IllegalArgumentException.class, fault0.getPhysical());
        assertEquals(IllegalArgumentException.class, fault0.getLogical());
    }

    public void testUnregister() throws Exception {
        org.apache.tuscany.idl.java.introspection.JavaInterfaceProcessor processor = createMock(org.apache.tuscany.idl.java.introspection.JavaInterfaceProcessor.class);
        processor.visitInterface(eq(Base.class), EasyMock.same((Class)null), isA(Contract.class));
        expectLastCall().once();
        replay(processor);
        Contract contract = factory.createComponentService();
        impl.registerProcessor(processor);
        impl.introspect(contract, Base.class);
        impl.unregisterProcessor(processor);
        impl.introspect(contract, Base.class);
        verify(processor);
    }

    protected void setUp() throws Exception {
        super.setUp();
        impl = new JavaInterfaceProcessorRegistryImpl();

    }

    private static interface Base {
        int baseInt(int param) throws IllegalArgumentException;
    }

    private static interface Simple extends Base {

    }
}
