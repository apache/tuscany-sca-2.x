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
package org.apache.tuscany.core.wire;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.Contract;
import org.apache.tuscany.assembly.impl.ComponentServiceImpl;
import org.apache.tuscany.idl.DataType;
import org.apache.tuscany.idl.Operation;
import org.apache.tuscany.idl.impl.DataTypeImpl;
import org.apache.tuscany.idl.impl.OperationImpl;
import org.apache.tuscany.idl.java.JavaInterface;
import org.apache.tuscany.idl.java.impl.JavaInterfaceImpl;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.wire.ChainHolder;
import org.apache.tuscany.spi.wire.IncompatibleServiceContractException;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.ProxyCreationException;
import org.apache.tuscany.spi.wire.ProxyService;
import org.apache.tuscany.spi.wire.Wire;
import org.osoa.sca.CallableReference;

/**
 * TODO some tests commented out due to DataType.equals() needing to be strict
 * 
 * @version $Rev$ $Date$
 */
public class ContractCompatibilityTestCase extends TestCase {
    private static final Operation.ConversationSequence NO_CONVERSATION = Operation.ConversationSequence.NO_CONVERSATION;
    private ProxyService proxyService = new MockProxyService();

    public void testNoOperation() throws Exception {
        Contract source = new MockContract("FooContract");
        Contract target = new MockContract("FooContract");
        proxyService.checkCompatibility(source, target, false, false);
    }

    public void testBasic() throws Exception {
        Contract source = new MockContract("FooContract");
        Operation opSource1 = new OperationImpl("op1");
        Map<String, Operation> sourceOperations = new HashMap<String, Operation>();
        sourceOperations.put("op1", opSource1);
        source.getInterface().getOperations().addAll(sourceOperations.values());
        Contract target = new MockContract("FooContract");
        Operation opSource2 = new OperationImpl("op1");
        Map<String, Operation> targetOperations = new HashMap<String, Operation>();
        targetOperations.put("op1", opSource2);
        target.getInterface().getOperations().addAll(targetOperations.values());
        proxyService.checkCompatibility(source, target, false, false);
    }

    public void testBasicIncompatibleOperationNames() throws Exception {
        Contract source = new MockContract("FooContract");
        Operation opSource1 = new OperationImpl("op1");
        Map<String, Operation> sourceOperations = new HashMap<String, Operation>();
        sourceOperations.put("op1", opSource1);
        source.getInterface().getOperations().addAll(sourceOperations.values());
        Contract target = new MockContract("FooContract");
        Operation opSource2 = new OperationImpl("op2");
        Map<String, Operation> targetOperations = new HashMap<String, Operation>();
        targetOperations.put("op2", opSource2);
        target.getInterface().getOperations().addAll(targetOperations.values());
        try {
            proxyService.checkCompatibility(source, target, false, false);
            fail();
        } catch (IncompatibleServiceContractException e) {
            // expected
        }
    }

    public void testInputTypes() throws Exception {
        Contract source = new MockContract("FooContract");
        List<DataType> sourceInputTypes = new ArrayList<DataType>();
        sourceInputTypes.add(new DataTypeImpl<Type>(Object.class, Object.class));
        DataType<List<DataType>> inputType = new DataTypeImpl<List<DataType>>(String.class, sourceInputTypes);
        Operation opSource1 = new OperationImpl("op1");
        opSource1.setInputType(inputType);
        Map<String, Operation> sourceOperations = new HashMap<String, Operation>();
        sourceOperations.put("op1", opSource1);
        source.getInterface().getOperations().addAll(sourceOperations.values());

        Contract target = new MockContract("FooContract");
        List<DataType> targetInputTypes = new ArrayList<DataType>();
        targetInputTypes.add(new DataTypeImpl<Type>(Object.class, Object.class));
        DataType<List<DataType>> targetInputType = new DataTypeImpl<List<DataType>>(String.class, targetInputTypes);

        Operation opTarget = new OperationImpl("op1");
        opTarget.setInputType(targetInputType);
        Map<String, Operation> targetOperations = new HashMap<String, Operation>();
        targetOperations.put("op1", opTarget);
        target.getInterface().getOperations().addAll(targetOperations.values());
        proxyService.checkCompatibility(source, target, false, false);
    }

    public void testIncompatibleInputTypes() throws Exception {
        Contract source = new MockContract("FooContract");
        List<DataType> sourceInputTypes = new ArrayList<DataType>();
        sourceInputTypes.add(new DataTypeImpl<Type>(Integer.class, Integer.class));
        DataType<List<DataType>> inputType = new DataTypeImpl<List<DataType>>(String.class, sourceInputTypes);
        Operation opSource1 = new OperationImpl("op1");
        opSource1.setInputType(inputType);
        Map<String, Operation> sourceOperations = new HashMap<String, Operation>();
        sourceOperations.put("op1", opSource1);
        source.getInterface().getOperations().addAll(sourceOperations.values());

        Contract target = new MockContract("FooContract");
        List<DataType> targetInputTypes = new ArrayList<DataType>();
        targetInputTypes.add(new DataTypeImpl<Type>(String.class, String.class));
        DataType<List<DataType>> targetInputType = new DataTypeImpl<List<DataType>>(String.class, targetInputTypes);

        Operation opTarget = new OperationImpl("op1");
        opTarget.setInputType(targetInputType);
        Map<String, Operation> targetOperations = new HashMap<String, Operation>();
        targetOperations.put("op1", opTarget);
        target.getInterface().getOperations().addAll(targetOperations.values());
        try {
            proxyService.checkCompatibility(source, target, false, false);
            fail();
        } catch (IncompatibleServiceContractException e) {
            // expected
        }
    }

    /**
     * Verfies source input types can be super types of the target
     */
    public void testSourceSuperTypeInputCompatibility() throws Exception {
        // Contract source = new MockContract("FooContract");
        // List<DataType> sourceInputTypes = new ArrayList<DataType>();
        // sourceInputTypes.add(new DataTypeImpl<Type>(Object.class,
        // Object.class));
        // DataType<List<DataType>> inputType = new
        // DataTypeImpl<List<DataType>>(String.class, sourceInputTypes);
        // Operation opSource1 = new OperationImpl("op1", inputType, null, null,
        // false, null);
        // Map<String, Operation> sourceOperations = new HashMap<String,
        // Operation>();
        // sourceOperations.put("op1", opSource1);
        // source.getInterface().getOperations().addAll(sourceOperations.values());
        //
        // Contract target = new MockContract("FooContract");
        // List<DataType> targetInputTypes = new ArrayList<DataType>();
        // targetInputTypes.add(new DataTypeImpl<Type>(String.class,
        // String.class));
        // DataType<List<DataType>> targetInputType =
        // new DataTypeImpl<List<DataType>>(String.class, targetInputTypes);
        //
        // Operation opTarget = new OperationImpl("op1", targetInputType, null,
        // null, false, null);
        // Map<String, Operation> targetOperations = new HashMap<String,
        // Operation>();
        // targetOperations.put("op1", opTarget);
        // target.getInterface().getOperations().addAll(targetOperations.values());
        // wireService.checkCompatibility(source, target, false);
    }

    public void testOutputTypes() throws Exception {
        Contract source = new MockContract("FooContract");
        DataType sourceOutputType = new DataTypeImpl<Type>(String.class, String.class);
        Operation opSource1 = new OperationImpl("op1");
        opSource1.setOutputType(sourceOutputType);
        Map<String, Operation> sourceOperations = new HashMap<String, Operation>();
        sourceOperations.put("op1", opSource1);
        source.getInterface().getOperations().addAll(sourceOperations.values());

        Contract target = new MockContract("FooContract");
        DataType targetOutputType = new DataTypeImpl<Type>(String.class, String.class);
        Operation opTarget = new OperationImpl("op1");
        opTarget.setOutputType(targetOutputType);
        Map<String, Operation> targetOperations = new HashMap<String, Operation>();
        targetOperations.put("op1", opTarget);
        target.getInterface().getOperations().addAll(targetOperations.values());
        proxyService.checkCompatibility(source, target, false, false);
    }

    /**
     * Verfies a return type that is a supertype of of the target is compatible
     */
    public void testSupertypeOutputTypes() throws Exception {
        // Contract source = new MockContract("FooContract");
        // DataType sourceOutputType = new DataTypeImpl<Type>(Object.class,
        // Object.class);
        // Operation opSource1 = new OperationImpl("op1", null,
        // sourceOutputType, null, false, null);
        // Map<String, Operation> sourceOperations = new HashMap<String,
        // Operation>();
        // sourceOperations.put("op1", opSource1);
        // source.getInterface().getOperations().addAll(sourceOperations.values());
        //
        // Contract target = new MockContract("FooContract");
        // DataType targetOutputType = new DataTypeImpl<Type>(String.class,
        // String.class);
        // Operation opTarget = new OperationImpl("op1", null, targetOutputType,
        // null, false, null);
        // Map<String, Operation> targetOperations = new HashMap<String,
        // Operation>();
        // targetOperations.put("op1", opTarget);
        // target.getInterface().getOperations().addAll(targetOperations.values());
        // wireService.checkCompatibility(source, target, false);
    }

    public void testIncompatibleOutputTypes() throws Exception {
        Contract source = new MockContract("FooContract");
        DataType sourceOutputType = new DataTypeImpl<Type>(String.class, String.class);
        Operation opSource1 = new OperationImpl("op1");
        opSource1.setOutputType(sourceOutputType);
        Map<String, Operation> sourceOperations = new HashMap<String, Operation>();
        sourceOperations.put("op1", opSource1);
        source.getInterface().getOperations().addAll(sourceOperations.values());

        Contract target = new MockContract("FooContract");
        DataType targetOutputType = new DataTypeImpl<Type>(Integer.class, Integer.class);
        Operation opTarget = new OperationImpl("op1");
        opTarget.setOutputType(targetOutputType);
        Map<String, Operation> targetOperations = new HashMap<String, Operation>();
        targetOperations.put("op1", opTarget);
        target.getInterface().getOperations().addAll(targetOperations.values());
        try {
            proxyService.checkCompatibility(source, target, false, false);
            fail();
        } catch (IncompatibleServiceContractException e) {
            // expected
        }
    }

    public void testFaultTypes() throws Exception {
        Contract source = new MockContract("FooContract");
        DataType sourceFaultType = new DataTypeImpl<Type>(String.class, String.class);
        List<DataType> sourceFaultTypes = new ArrayList<DataType>();
        sourceFaultTypes.add(0, sourceFaultType);
        Operation opSource1 = new OperationImpl("op1");
        opSource1.setFaultTypes(sourceFaultTypes);
        Map<String, Operation> sourceOperations = new HashMap<String, Operation>();
        sourceOperations.put("op1", opSource1);
        source.getInterface().getOperations().addAll(sourceOperations.values());

        Contract target = new MockContract("FooContract");
        DataType targetFaultType = new DataTypeImpl<Type>(String.class, String.class);
        List<DataType> targetFaultTypes = new ArrayList<DataType>();
        targetFaultTypes.add(0, targetFaultType);

        Operation opTarget = new OperationImpl("op1");
        opTarget.setFaultTypes(targetFaultTypes);
        Map<String, Operation> targetOperations = new HashMap<String, Operation>();
        targetOperations.put("op1", opTarget);
        target.getInterface().getOperations().addAll(targetOperations.values());
        proxyService.checkCompatibility(source, target, false, false);
    }

    public void testSourceFaultTargetNoFaultCompatibility() throws Exception {
        Contract source = new MockContract("FooContract");
        DataType sourceFaultType = new DataTypeImpl<Type>(String.class, String.class);
        List<DataType> sourceFaultTypes = new ArrayList<DataType>();
        sourceFaultTypes.add(0, sourceFaultType);
        Operation opSource1 = new OperationImpl("op1");
        opSource1.setFaultTypes(sourceFaultTypes);
        Map<String, Operation> sourceOperations = new HashMap<String, Operation>();
        sourceOperations.put("op1", opSource1);
        source.getInterface().getOperations().addAll(sourceOperations.values());

        Contract target = new MockContract("FooContract");
        Operation opTarget = new OperationImpl("op1");
        Map<String, Operation> targetOperations = new HashMap<String, Operation>();
        targetOperations.put("op1", opTarget);
        target.getInterface().getOperations().addAll(targetOperations.values());
        proxyService.checkCompatibility(source, target, false, false);
    }

    /**
     * Verifies a source's fault which is a supertype of the target's fault are
     * compatibile
     * 
     * @throws Exception
     */
    public void testFaultSuperTypes() throws Exception {
        // Contract source = new MockContract("FooContract");
        // DataType sourceFaultType = new DataTypeImpl<Type>(Exception.class,
        // Exception.class);
        // List<DataType> sourceFaultTypes = new ArrayList<DataType>();
        // sourceFaultTypes.add(0, sourceFaultType);
        // Operation opSource1 = new OperationImpl("op1", null, null,
        // sourceFaultTypes, false, null);
        // Map<String, Operation> sourceOperations = new HashMap<String,
        // Operation>();
        // sourceOperations.put("op1", opSource1);
        // source.getInterface().getOperations().addAll(sourceOperations.values());
        //
        // Contract target = new MockContract("FooContract");
        // DataType targetFaultType = new
        // DataTypeImpl<Type>(TuscanyException.class, TuscanyException.class);
        // List<DataType> targetFaultTypes = new ArrayList<DataType>();
        // targetFaultTypes.add(0, targetFaultType);
        //
        // Operation opTarget = new OperationImpl("op1", null, null,
        // targetFaultTypes, false, null);
        // Map<String, Operation> targetOperations = new HashMap<String,
        // Operation>();
        // targetOperations.put("op1", opTarget);
        // target.getInterface().getOperations().addAll(targetOperations.values());
        // wireService.checkCompatibility(source, target, false);
    }

    /**
     * Verifies a source's faults which are supertypes and a superset of the
     * target's faults are compatibile
     */
    public void testFaultSuperTypesAndSuperset() throws Exception {
        // Contract source = new MockContract("FooContract");
        // DataType sourceFaultType = new DataTypeImpl<Type>(Exception.class,
        // Exception.class);
        // DataType sourceFaultType2 = new
        // DataTypeImpl<Type>(RuntimeException.class, RuntimeException.class);
        // List<DataType> sourceFaultTypes = new ArrayList<DataType>();
        // sourceFaultTypes.add(0, sourceFaultType);
        // sourceFaultTypes.add(1, sourceFaultType2);
        // Operation opSource1 = new OperationImpl("op1", null, null,
        // sourceFaultTypes, false, null);
        // Map<String, Operation> sourceOperations = new HashMap<String,
        // Operation>();
        // sourceOperations.put("op1", opSource1);
        // source.getInterface().getOperations().addAll(sourceOperations.values());
        //
        // Contract target = new MockContract("FooContract");
        // DataType targetFaultType = new
        // DataTypeImpl<Type>(TuscanyException.class, TuscanyException.class);
        // List<DataType> targetFaultTypes = new ArrayList<DataType>();
        // targetFaultTypes.add(0, targetFaultType);
        //
        // Operation opTarget = new OperationImpl("op1", null, null,
        // targetFaultTypes, false, null);
        // Map<String, Operation> targetOperations = new HashMap<String,
        // Operation>();
        // targetOperations.put("op1", opTarget);
        // target.getInterface().getOperations().addAll(targetOperations.values());
        // wireService.checkCompatibility(source, target, false);
    }

    private class MockContract<T> extends ComponentServiceImpl {
        public MockContract() {
        }

        public MockContract(Class interfaceClass) {
            JavaInterface jInterface = new JavaInterfaceImpl();
            jInterface.setJavaClass(interfaceClass);
            setInterface(jInterface);
        }

        public MockContract(String interfaceClass) {
            JavaInterface jInterface = new JavaInterfaceImpl();
            jInterface.setUnresolved(true);
            jInterface.setName(interfaceClass);
            setInterface(jInterface);
        }
    }

    private class MockProxyService extends ProxyServiceExtension {
        public MockProxyService() {
            super(null);
        }

        public <T> T createProxy(Class<T> interfaze, Wire wire) throws ProxyCreationException {
            throw new UnsupportedOperationException();
        }

        public <T> T createProxy2(Class<T> interfaze, boolean conversational, Wire wire) throws ProxyCreationException {
            throw new UnsupportedOperationException();
        }

        public <T> T createProxy2(Class<T> interfaze, Wire wire) throws ProxyCreationException {
            throw new UnsupportedOperationException();
        }

        public <T> T createProxy(Class<T> interfaze, Wire wire, Map<Method, ChainHolder> mapping)
            throws ProxyCreationException {
            throw new UnsupportedOperationException();
        }

        public <T> T createProxy2(Class<T> interfaze, Wire wire, Map<Method, InvocationChain> mapping)
            throws ProxyCreationException {
            throw new UnsupportedOperationException();
        }

        public Object createCallbackProxy(Class<?> interfaze, List<Wire> wires) throws ProxyCreationException {
            throw new UnsupportedOperationException();
        }

        public Object createCallbackProxy(Class<?> interfaze) throws ProxyCreationException {
            throw new UnsupportedOperationException();
        }

        public void createWires(ReferenceBinding referenceBinding, Contract contract, QualifiedName targetName) {
            throw new UnsupportedOperationException();
        }

        public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }
    }

}
