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

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.Operation;
import static org.apache.tuscany.spi.model.Operation.NO_CONVERSATION;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.ChainHolder;
import org.apache.tuscany.spi.wire.IncompatibleServiceContractException;
import org.apache.tuscany.spi.wire.ProxyCreationException;
import org.apache.tuscany.spi.wire.Wire;
import org.apache.tuscany.spi.wire.ProxyService;
import org.apache.tuscany.spi.wire.InvocationChain;

import junit.framework.TestCase;
import org.osoa.sca.CallableReference;

/**
 * TODO some tests commented out due to DataType.equals() needing to be strict
 *
 * @version $Rev$ $Date$
 */
public class ContractCompatibilityTestCase extends TestCase {

    private ProxyService proxyService = new MockProxyService();

    public void testNoOperation() throws Exception {
        ServiceContract source = new MockContract<Type>("FooContract");
        ServiceContract target = new MockContract<Type>("FooContract");
        proxyService.checkCompatibility(source, target, false, false);
    }

    public void testBasic() throws Exception {
        ServiceContract<Type> source = new MockContract<Type>("FooContract");
        Operation<Type> opSource1 = new Operation<Type>("op1", null, null, null, false, null, NO_CONVERSATION);
        Map<String, Operation<Type>> sourceOperations = new HashMap<String, Operation<Type>>();
        sourceOperations.put("op1", opSource1);
        source.setOperations(sourceOperations);
        ServiceContract<Type> target = new MockContract<Type>("FooContract");
        Operation<Type> opSource2 = new Operation<Type>("op1", null, null, null, false, null, NO_CONVERSATION);
        Map<String, Operation<Type>> targetOperations = new HashMap<String, Operation<Type>>();
        targetOperations.put("op1", opSource2);
        target.setOperations(targetOperations);
        proxyService.checkCompatibility(source, target, false, false);
    }

    public void testBasicIncompatibleOperationNames() throws Exception {
        ServiceContract<Type> source = new MockContract<Type>("FooContract");
        Operation<Type> opSource1 = new Operation<Type>("op1", null, null, null, false, null, NO_CONVERSATION);
        Map<String, Operation<Type>> sourceOperations = new HashMap<String, Operation<Type>>();
        sourceOperations.put("op1", opSource1);
        source.setOperations(sourceOperations);
        ServiceContract<Type> target = new MockContract<Type>("FooContract");
        Operation<Type> opSource2 = new Operation<Type>("op2", null, null, null, false, null, NO_CONVERSATION);
        Map<String, Operation<Type>> targetOperations = new HashMap<String, Operation<Type>>();
        targetOperations.put("op2", opSource2);
        target.setOperations(targetOperations);
        try {
            proxyService.checkCompatibility(source, target, false, false);
            fail();
        } catch (IncompatibleServiceContractException e) {
            //expected
        }
    }

    public void testInputTypes() throws Exception {
        ServiceContract<Type> source = new MockContract<Type>("FooContract");
        List<DataType<Type>> sourceInputTypes = new ArrayList<DataType<Type>>();
        sourceInputTypes.add(new DataType<Type>(Object.class, Object.class));
        DataType<List<DataType<Type>>> inputType = new DataType<List<DataType<Type>>>(String.class, sourceInputTypes);
        Operation<Type> opSource1 = new Operation<Type>("op1", inputType, null, null, false, null, NO_CONVERSATION);
        Map<String, Operation<Type>> sourceOperations = new HashMap<String, Operation<Type>>();
        sourceOperations.put("op1", opSource1);
        source.setOperations(sourceOperations);

        ServiceContract<Type> target = new MockContract<Type>("FooContract");
        List<DataType<Type>> targetInputTypes = new ArrayList<DataType<Type>>();
        targetInputTypes.add(new DataType<Type>(Object.class, Object.class));
        DataType<List<DataType<Type>>> targetInputType =
            new DataType<List<DataType<Type>>>(String.class, targetInputTypes);

        Operation<Type> opTarget =
            new Operation<Type>("op1", targetInputType, null, null, false, null, NO_CONVERSATION);
        Map<String, Operation<Type>> targetOperations = new HashMap<String, Operation<Type>>();
        targetOperations.put("op1", opTarget);
        target.setOperations(targetOperations);
        proxyService.checkCompatibility(source, target, false, false);
    }


    public void testIncompatibleInputTypes() throws Exception {
        ServiceContract<Type> source = new MockContract<Type>("FooContract");
        List<DataType<Type>> sourceInputTypes = new ArrayList<DataType<Type>>();
        sourceInputTypes.add(new DataType<Type>(Integer.class, Integer.class));
        DataType<List<DataType<Type>>> inputType = new DataType<List<DataType<Type>>>(String.class, sourceInputTypes);
        Operation<Type> opSource1 = new Operation<Type>("op1", inputType, null, null, false, null, NO_CONVERSATION);
        Map<String, Operation<Type>> sourceOperations = new HashMap<String, Operation<Type>>();
        sourceOperations.put("op1", opSource1);
        source.setOperations(sourceOperations);

        ServiceContract<Type> target = new MockContract<Type>("FooContract");
        List<DataType<Type>> targetInputTypes = new ArrayList<DataType<Type>>();
        targetInputTypes.add(new DataType<Type>(String.class, String.class));
        DataType<List<DataType<Type>>> targetInputType =
            new DataType<List<DataType<Type>>>(String.class, targetInputTypes);

        Operation<Type> opTarget =
            new Operation<Type>("op1", targetInputType, null, null, false, null, NO_CONVERSATION);
        Map<String, Operation<Type>> targetOperations = new HashMap<String, Operation<Type>>();
        targetOperations.put("op1", opTarget);
        target.setOperations(targetOperations);
        try {
            proxyService.checkCompatibility(source, target, false, false);
            fail();
        } catch (IncompatibleServiceContractException e) {
            //expected
        }
    }

    /**
     * Verfies source input types can be super types of the target
     */
    public void testSourceSuperTypeInputCompatibility() throws Exception {
//        ServiceContract<Type> source = new MockContract<Type>("FooContract");
//        List<DataType<Type>> sourceInputTypes = new ArrayList<DataType<Type>>();
//        sourceInputTypes.add(new DataType<Type>(Object.class, Object.class));
//        DataType<List<DataType<Type>>> inputType = new DataType<List<DataType<Type>>>(String.class, sourceInputTypes);
//        Operation<Type> opSource1 = new Operation<Type>("op1", inputType, null, null, false, null);
//        Map<String, Operation<Type>> sourceOperations = new HashMap<String, Operation<Type>>();
//        sourceOperations.put("op1", opSource1);
//        source.setOperations(sourceOperations);
//
//        ServiceContract<Type> target = new MockContract<Type>("FooContract");
//        List<DataType<Type>> targetInputTypes = new ArrayList<DataType<Type>>();
//        targetInputTypes.add(new DataType<Type>(String.class, String.class));
//        DataType<List<DataType<Type>>> targetInputType =
//            new DataType<List<DataType<Type>>>(String.class, targetInputTypes);
//
//        Operation<Type> opTarget = new Operation<Type>("op1", targetInputType, null, null, false, null);
//        Map<String, Operation<Type>> targetOperations = new HashMap<String, Operation<Type>>();
//        targetOperations.put("op1", opTarget);
//        target.setOperations(targetOperations);
//        wireService.checkCompatibility(source, target, false);
    }

    public void testOutputTypes() throws Exception {
        ServiceContract<Type> source = new MockContract<Type>("FooContract");
        DataType<Type> sourceOutputType = new DataType<Type>(String.class, String.class);
        Operation<Type> opSource1 =
            new Operation<Type>("op1", null, sourceOutputType, null, false, null, NO_CONVERSATION);
        Map<String, Operation<Type>> sourceOperations = new HashMap<String, Operation<Type>>();
        sourceOperations.put("op1", opSource1);
        source.setOperations(sourceOperations);

        ServiceContract<Type> target = new MockContract<Type>("FooContract");
        DataType<Type> targetOutputType = new DataType<Type>(String.class, String.class);
        Operation<Type> opTarget =
            new Operation<Type>("op1", null, targetOutputType, null, false, null, NO_CONVERSATION);
        Map<String, Operation<Type>> targetOperations = new HashMap<String, Operation<Type>>();
        targetOperations.put("op1", opTarget);
        target.setOperations(targetOperations);
        proxyService.checkCompatibility(source, target, false, false);
    }

    /**
     * Verfies a return type that is a supertype of of the target is compatible
     */
    public void testSupertypeOutputTypes() throws Exception {
//        ServiceContract<Type> source = new MockContract<Type>("FooContract");
//        DataType<Type> sourceOutputType = new DataType<Type>(Object.class, Object.class);
//        Operation<Type> opSource1 = new Operation<Type>("op1", null, sourceOutputType, null, false, null);
//        Map<String, Operation<Type>> sourceOperations = new HashMap<String, Operation<Type>>();
//        sourceOperations.put("op1", opSource1);
//        source.setOperations(sourceOperations);
//
//        ServiceContract<Type> target = new MockContract<Type>("FooContract");
//        DataType<Type> targetOutputType = new DataType<Type>(String.class, String.class);
//        Operation<Type> opTarget = new Operation<Type>("op1", null, targetOutputType, null, false, null);
//        Map<String, Operation<Type>> targetOperations = new HashMap<String, Operation<Type>>();
//        targetOperations.put("op1", opTarget);
//        target.setOperations(targetOperations);
//        wireService.checkCompatibility(source, target, false);
    }

    public void testIncompatibleOutputTypes() throws Exception {
        ServiceContract<Type> source = new MockContract<Type>("FooContract");
        DataType<Type> sourceOutputType = new DataType<Type>(String.class, String.class);
        Operation<Type> opSource1 =
            new Operation<Type>("op1", null, sourceOutputType, null, false, null, NO_CONVERSATION);
        Map<String, Operation<Type>> sourceOperations = new HashMap<String, Operation<Type>>();
        sourceOperations.put("op1", opSource1);
        source.setOperations(sourceOperations);

        ServiceContract<Type> target = new MockContract<Type>("FooContract");
        DataType<Type> targetOutputType = new DataType<Type>(Integer.class, Integer.class);
        Operation<Type> opTarget =
            new Operation<Type>("op1", null, targetOutputType, null, false, null, NO_CONVERSATION);
        Map<String, Operation<Type>> targetOperations = new HashMap<String, Operation<Type>>();
        targetOperations.put("op1", opTarget);
        target.setOperations(targetOperations);
        try {
            proxyService.checkCompatibility(source, target, false, false);
            fail();
        } catch (IncompatibleServiceContractException e) {
            //expected
        }
    }

    public void testFaultTypes() throws Exception {
        ServiceContract<Type> source = new MockContract<Type>("FooContract");
        DataType<Type> sourceFaultType = new DataType<Type>(String.class, String.class);
        List<DataType<Type>> sourceFaultTypes = new ArrayList<DataType<Type>>();
        sourceFaultTypes.add(0, sourceFaultType);
        Operation<Type> opSource1 =
            new Operation<Type>("op1", null, null, sourceFaultTypes, false, null, NO_CONVERSATION);
        Map<String, Operation<Type>> sourceOperations = new HashMap<String, Operation<Type>>();
        sourceOperations.put("op1", opSource1);
        source.setOperations(sourceOperations);

        ServiceContract<Type> target = new MockContract<Type>("FooContract");
        DataType<Type> targetFaultType = new DataType<Type>(String.class, String.class);
        List<DataType<Type>> targetFaultTypes = new ArrayList<DataType<Type>>();
        targetFaultTypes.add(0, targetFaultType);

        Operation<Type> opTarget =
            new Operation<Type>("op1", null, null, targetFaultTypes, false, null, NO_CONVERSATION);
        Map<String, Operation<Type>> targetOperations = new HashMap<String, Operation<Type>>();
        targetOperations.put("op1", opTarget);
        target.setOperations(targetOperations);
        proxyService.checkCompatibility(source, target, false, false);
    }

    public void testSourceFaultTargetNoFaultCompatibility() throws Exception {
        ServiceContract<Type> source = new MockContract<Type>("FooContract");
        DataType<Type> sourceFaultType = new DataType<Type>(String.class, String.class);
        List<DataType<Type>> sourceFaultTypes = new ArrayList<DataType<Type>>();
        sourceFaultTypes.add(0, sourceFaultType);
        Operation<Type> opSource1 =
            new Operation<Type>("op1", null, null, sourceFaultTypes, false, null, NO_CONVERSATION);
        Map<String, Operation<Type>> sourceOperations = new HashMap<String, Operation<Type>>();
        sourceOperations.put("op1", opSource1);
        source.setOperations(sourceOperations);

        ServiceContract<Type> target = new MockContract<Type>("FooContract");
        Operation<Type> opTarget = new Operation<Type>("op1", null, null, null, false, null, NO_CONVERSATION);
        Map<String, Operation<Type>> targetOperations = new HashMap<String, Operation<Type>>();
        targetOperations.put("op1", opTarget);
        target.setOperations(targetOperations);
        proxyService.checkCompatibility(source, target, false, false);
    }

    /**
     * Verifies a source's fault which is a supertype of the target's fault are compatibile
     *
     * @throws Exception
     */
    public void testFaultSuperTypes() throws Exception {
//        ServiceContract<Type> source = new MockContract<Type>("FooContract");
//        DataType<Type> sourceFaultType = new DataType<Type>(Exception.class, Exception.class);
//        List<DataType<Type>> sourceFaultTypes = new ArrayList<DataType<Type>>();
//        sourceFaultTypes.add(0, sourceFaultType);
//        Operation<Type> opSource1 = new Operation<Type>("op1", null, null, sourceFaultTypes, false, null);
//        Map<String, Operation<Type>> sourceOperations = new HashMap<String, Operation<Type>>();
//        sourceOperations.put("op1", opSource1);
//        source.setOperations(sourceOperations);
//
//        ServiceContract<Type> target = new MockContract<Type>("FooContract");
//        DataType<Type> targetFaultType = new DataType<Type>(TuscanyException.class, TuscanyException.class);
//        List<DataType<Type>> targetFaultTypes = new ArrayList<DataType<Type>>();
//        targetFaultTypes.add(0, targetFaultType);
//
//        Operation<Type> opTarget = new Operation<Type>("op1", null, null, targetFaultTypes, false, null);
//        Map<String, Operation<Type>> targetOperations = new HashMap<String, Operation<Type>>();
//        targetOperations.put("op1", opTarget);
//        target.setOperations(targetOperations);
//        wireService.checkCompatibility(source, target, false);
    }

    /**
     * Verifies a source's faults which are supertypes and a superset of the target's faults are compatibile
     */
    public void testFaultSuperTypesAndSuperset() throws Exception {
//        ServiceContract<Type> source = new MockContract<Type>("FooContract");
//        DataType<Type> sourceFaultType = new DataType<Type>(Exception.class, Exception.class);
//        DataType<Type> sourceFaultType2 = new DataType<Type>(RuntimeException.class, RuntimeException.class);
//        List<DataType<Type>> sourceFaultTypes = new ArrayList<DataType<Type>>();
//        sourceFaultTypes.add(0, sourceFaultType);
//        sourceFaultTypes.add(1, sourceFaultType2);
//        Operation<Type> opSource1 = new Operation<Type>("op1", null, null, sourceFaultTypes, false, null);
//        Map<String, Operation<Type>> sourceOperations = new HashMap<String, Operation<Type>>();
//        sourceOperations.put("op1", opSource1);
//        source.setOperations(sourceOperations);
//
//        ServiceContract<Type> target = new MockContract<Type>("FooContract");
//        DataType<Type> targetFaultType = new DataType<Type>(TuscanyException.class, TuscanyException.class);
//        List<DataType<Type>> targetFaultTypes = new ArrayList<DataType<Type>>();
//        targetFaultTypes.add(0, targetFaultType);
//
//        Operation<Type> opTarget = new Operation<Type>("op1", null, null, targetFaultTypes, false, null);
//        Map<String, Operation<Type>> targetOperations = new HashMap<String, Operation<Type>>();
//        targetOperations.put("op1", opTarget);
//        target.setOperations(targetOperations);
//        wireService.checkCompatibility(source, target, false);
    }

    private class MockContract<T> extends ServiceContract<T> {
        public MockContract() {
        }

        public MockContract(Class interfaceClass) {
            super(interfaceClass);
        }

        public MockContract(String interfaceName) {
            super(interfaceName);
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

        public void createWires(ReferenceBinding referenceBinding, ServiceContract<?> contract,
                                QualifiedName targetName) {
            throw new UnsupportedOperationException();
        }

        public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }
    }

}
