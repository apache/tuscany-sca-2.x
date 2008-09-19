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

package org.apache.tuscany.sca.binding.corba.testing;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Array;

import junit.framework.Assert;

import org.apache.tuscany.sca.binding.corba.impl.exceptions.CorbaException;
import org.apache.tuscany.sca.binding.corba.impl.exceptions.RequestConfigurationException;
import org.apache.tuscany.sca.binding.corba.impl.reference.DynaCorbaRequest;
import org.apache.tuscany.sca.binding.corba.impl.reference.DynaCorbaResponse;
import org.apache.tuscany.sca.binding.corba.impl.service.ComponentInvocationProxy;
import org.apache.tuscany.sca.binding.corba.impl.service.DynaCorbaServant;
import org.apache.tuscany.sca.binding.corba.impl.service.InvocationProxy;
import org.apache.tuscany.sca.binding.corba.impl.types.util.Utils;
import org.apache.tuscany.sca.binding.corba.testing.arrays_unions.ArraysUnionsTests;
import org.apache.tuscany.sca.binding.corba.testing.arrays_unions.ArraysUnionsTestsHelper;
import org.apache.tuscany.sca.binding.corba.testing.arrays_unions.TestStruct;
import org.apache.tuscany.sca.binding.corba.testing.enums.Color;
import org.apache.tuscany.sca.binding.corba.testing.enums.EnumManager;
import org.apache.tuscany.sca.binding.corba.testing.enums.EnumManagerHelper;
import org.apache.tuscany.sca.binding.corba.testing.exceptions.Calc;
import org.apache.tuscany.sca.binding.corba.testing.exceptions.CalcHelper;
import org.apache.tuscany.sca.binding.corba.testing.exceptions.CalcPackage.DivByZero;
import org.apache.tuscany.sca.binding.corba.testing.exceptions.CalcPackage.NotSupported;
import org.apache.tuscany.sca.binding.corba.testing.generated.ArraysSetter;
import org.apache.tuscany.sca.binding.corba.testing.generated.ArraysSetterHelper;
import org.apache.tuscany.sca.binding.corba.testing.generated.PrimitivesSetter;
import org.apache.tuscany.sca.binding.corba.testing.generated.PrimitivesSetterHelper;
import org.apache.tuscany.sca.binding.corba.testing.generated.SimpleStruct;
import org.apache.tuscany.sca.binding.corba.testing.generated.SomeStruct;
import org.apache.tuscany.sca.binding.corba.testing.generated.TestObject;
import org.apache.tuscany.sca.binding.corba.testing.generated.TestObjectHelper;
import org.apache.tuscany.sca.binding.corba.testing.hierarchy.NonCorbaException;
import org.apache.tuscany.sca.binding.corba.testing.servants.ArraysSetterServant;
import org.apache.tuscany.sca.binding.corba.testing.servants.ArraysUnionsTuscanyServant;
import org.apache.tuscany.sca.binding.corba.testing.servants.CalcServant;
import org.apache.tuscany.sca.binding.corba.testing.servants.EnumManagerServant;
import org.apache.tuscany.sca.binding.corba.testing.servants.InvalidTestObjectServant;
import org.apache.tuscany.sca.binding.corba.testing.servants.InvalidTypesServant;
import org.apache.tuscany.sca.binding.corba.testing.servants.NonCorbaServant;
import org.apache.tuscany.sca.binding.corba.testing.servants.PrimitivesSetterServant;
import org.apache.tuscany.sca.binding.corba.testing.servants.TestObjectServant;
import org.apache.tuscany.sca.binding.corba.testing.service.mocks.TestRuntimeComponentService;
import org.apache.tuscany.sca.host.corba.naming.TransientNameServer;
import org.apache.tuscany.sca.host.corba.naming.TransientNameService;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;

/**
 * @version $Rev$ $Date$
 * Tests API for dynamic CORBA servants
 */
public class CorbaServantTestCase {

    private static ORB orb;
    private static TransientNameServer server;

    @BeforeClass
    public static void start() {
        try {
            server =
                new TransientNameServer(TestConstants.TEST2_HOST, TestConstants.TEST2_PORT,
                                        TransientNameService.DEFAULT_SERVICE_NAME);
            Thread t = server.start();
            if (t == null) {
                Assert.fail("The naming server cannot be started");
            }
            orb = server.getORB();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @AfterClass
    public static void stop() {
        server.stop();
    }

    /**
     * Binds servant implementation to name
     */
    private void bindServant(DynaCorbaServant servant, String name) {
        try {
            Object nameService = orb.resolve_initial_references("NameService");
            NamingContext namingContext = NamingContextHelper.narrow(nameService);

            NameComponent nc = new NameComponent(name, "");
            NameComponent[] path = new NameComponent[] {nc};
            namingContext.rebind(path, servant);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns object reference which is binded to given name
     * 
     * @param name
     * @return
     */
    private org.omg.CORBA.Object bindReference(String name) {
        try {
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContext ncRef = NamingContextHelper.narrow(objRef);
            NameComponent nc = new NameComponent(name, "");
            NameComponent path[] = {nc};
            return ncRef.resolve(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Tests primitives (arguments, return types)
     */
    @Test
    public void test_primitivesSetter() {
        try {
            PrimitivesSetter primitivesSetter = new PrimitivesSetterServant();
            TestRuntimeComponentService service = new TestRuntimeComponentService(primitivesSetter);
            Class<?> javaClass = ((JavaInterface)service.getInterfaceContract().getInterface()).getJavaClass();
            InvocationProxy proxy = new ComponentInvocationProxy(service, service.getRuntimeWire(null), javaClass);
            DynaCorbaServant servant = new DynaCorbaServant(proxy, Utils.getTypeId(javaClass));
            String[] ids =
                new String[] {"IDL:org/apache/tuscany/sca/binding/corba/testing/generated/PrimitivesSetter:1.0"};
            servant.setIds(ids);
            bindServant(servant, "PrimitivesSetter");
            PrimitivesSetter psClient = PrimitivesSetterHelper.narrow(bindReference("PrimitivesSetter"));
            assertTrue(psClient.setBoolean(true) == true);
            assertTrue(psClient.setChar('A') == 'A');
            assertTrue(psClient.setString("SomeTest").equals("SomeTest"));
            assertTrue(psClient.setDouble(2d) == 2d);
            assertTrue(psClient.setFloat(3f) == 3f);
            assertTrue(psClient.setLong(1) == 1);
            assertTrue(psClient.setLongLong(0L) == 0L);
            assertTrue(psClient.setOctet((byte)8) == (byte)8);
            assertTrue(psClient.setShort((short)6) == (short)6);
            assertTrue(psClient.setUnsignedLong(9) == 9);
            assertTrue(psClient.setUnsignedLongLong(11L) == 11L);
            assertTrue(psClient.setUnsignedShort((short)15) == (short)15);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests if array values are equal
     */
    private boolean areArraysEqual(java.lang.Object arr1, java.lang.Object arr2, int arrLen) {
        try {
            for (int i = 0; i < arrLen; i++) {
                if (!Array.get(arr1, i).equals(Array.get(arr2, i))) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Tests arrays (arguments, return types)
     */
    @Test
    public void test_arraysSetter() {
        try {
            ArraysSetter arraysSetter = new ArraysSetterServant();
            TestRuntimeComponentService service = new TestRuntimeComponentService(arraysSetter);
            Class<?> javaClass = ((JavaInterface)service.getInterfaceContract().getInterface()).getJavaClass();
            InvocationProxy proxy = new ComponentInvocationProxy(service, service.getRuntimeWire(null), javaClass);
            DynaCorbaServant servant = new DynaCorbaServant(proxy, Utils.getTypeId(javaClass));
            String[] ids = new String[] {"IDL:org/apache/tuscany/sca/binding/corba/testing/generated/ArraysSetter:1.0"};
            servant.setIds(ids);
            java.lang.Object result = null;
            bindServant(servant, "ArraysSetter");
            ArraysSetter asClient = ArraysSetterHelper.narrow(bindReference("ArraysSetter"));

            boolean[] bArr = new boolean[] {true, false};
            result = (java.lang.Object)asClient.setBoolean(bArr);
            assertTrue(areArraysEqual(bArr, result, bArr.length));

            byte[] byArr = new byte[] {1, 2};
            result = (java.lang.Object)asClient.setOctet(byArr);
            assertTrue(areArraysEqual(byArr, result, byArr.length));

            short[] shArr = new short[] {1, 2};
            result = (java.lang.Object)asClient.setShort(shArr);
            assertTrue(areArraysEqual(shArr, result, shArr.length));

            int[] iArr = new int[] {1, 2};
            result = (java.lang.Object)asClient.setLong(iArr);
            assertTrue(areArraysEqual(iArr, result, iArr.length));

            long[] lArr = new long[] {1, 2};
            result = (java.lang.Object)asClient.setLongLong(lArr);
            assertTrue(areArraysEqual(lArr, result, lArr.length));

            String[] strArr = new String[] {"Some", "Test"};
            result = (java.lang.Object)asClient.setString(strArr);
            assertTrue(areArraysEqual(strArr, result, strArr.length));

            char[] chArr = new char[] {'A', 'B'};
            result = (java.lang.Object)asClient.setChar(chArr);
            assertTrue(areArraysEqual(chArr, result, chArr.length));

            float[] flArr = new float[] {1, 2};
            result = (java.lang.Object)asClient.setFloat(flArr);
            assertTrue(areArraysEqual(flArr, result, flArr.length));

            double[] dbArr = new double[] {1, 2};
            result = (java.lang.Object)asClient.setDouble(dbArr);
            assertTrue(areArraysEqual(dbArr, result, dbArr.length));

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests structures (arguments, return types)
     */
    @Test
    public void test_TestObject_setStruct() {
        try {
            TestObject to = new TestObjectServant();
            TestRuntimeComponentService service = new TestRuntimeComponentService(to);
            Class<?> javaClass = ((JavaInterface)service.getInterfaceContract().getInterface()).getJavaClass();
            InvocationProxy proxy = new ComponentInvocationProxy(service, service.getRuntimeWire(null), javaClass);
            DynaCorbaServant servant = new DynaCorbaServant(proxy, Utils.getTypeId(javaClass));
            String[] ids = new String[] {"IDL:org/apache/tuscany/sca/binding/corba/testing/generated/TestObject:1.0"};
            servant.setIds(ids);
            bindServant(servant, "TestObject");
            TestObject testObject = TestObjectHelper.narrow(bindReference("TestObject"));
            SomeStruct ss = new SomeStruct();
            SimpleStruct inner = new SimpleStruct();
            inner.field1 = TestConstants.STR_1;
            inner.field2 = TestConstants.INT_1;
            ss.innerStruct = inner;
            ss.str = TestConstants.STR_2;
            ss.str_list = TestConstants.STR_ARR_1;
            ss.threeDimSeq = TestConstants.INT_ARRAY_3_DIM;
            ss.twoDimSeq = TestConstants.INT_ARRAY_2_DIM;
            SomeStruct result = testObject.setStruct(ss);
            assertTrue(TestConstants.are2DimArraysEqual(result.twoDimSeq, TestConstants.INT_ARRAY_2_DIM));
            assertTrue(TestConstants.are3DimArraysEqual(result.threeDimSeq, TestConstants.INT_ARRAY_3_DIM));
            assertTrue(result.str.equals(ss.str));
            assertTrue(result.innerStruct.field1.equals(ss.innerStruct.field1));
            assertTrue(result.innerStruct.field2 == ss.innerStruct.field2);
            assertTrue(areArraysEqual(result.str_list, ss.str_list, ss.str_list.length));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests handling BAD_OPERATION system exception
     */
    @Test
    public void test_systemException_BAD_OPERATION() {
        try {
            TestObjectServant tos = new TestObjectServant();
            TestRuntimeComponentService service = new TestRuntimeComponentService(tos);
            Class<?> javaClass = ((JavaInterface)service.getInterfaceContract().getInterface()).getJavaClass();
            InvocationProxy proxy = new ComponentInvocationProxy(service, service.getRuntimeWire(null), javaClass);
            DynaCorbaServant servant = new DynaCorbaServant(proxy, Utils.getTypeId(javaClass));
            String[] ids = new String[] {"IDL:org/apache/tuscany/sca/binding/corba/testing/generated/TestObject:1.0"};
            servant.setIds(ids);
            bindServant(servant, "TestObject");
            DynaCorbaRequest request =
                new DynaCorbaRequest(bindReference("TestObject"), "methodThatSurelyDoesNotExist");
            request.invoke();
            fail();
        } catch (Exception e) {
            if (e instanceof CorbaException) {
                assertTrue(true);
            } else {
                e.printStackTrace();
            }
        }

        try {
            InvalidTestObjectServant tos = new InvalidTestObjectServant();
            TestRuntimeComponentService service = new TestRuntimeComponentService(tos);
            Class<?> javaClass = ((JavaInterface)service.getInterfaceContract().getInterface()).getJavaClass();
            InvocationProxy proxy = new ComponentInvocationProxy(service, service.getRuntimeWire(null), javaClass);
            DynaCorbaServant servant = new DynaCorbaServant(proxy, Utils.getTypeId(javaClass));
            String[] ids = new String[] {"IDL:org/apache/tuscany/sca/binding/corba/testing/generated/TestObject:1.0"};
            servant.setIds(ids);
            bindServant(servant, "InvalidTestObject");
            TestObject to = TestObjectHelper.narrow(bindReference("InvalidTestObject"));
            SomeStruct str = new SomeStruct();
            str.innerStruct = new SimpleStruct();
            str.innerStruct.field1 = "Whatever";
            str.str = "Whatever";
            str.str_list = new String[] {};
            str.threeDimSeq = new int[][][] {};
            str.twoDimSeq = new int[][] {};
            to.setStruct(str);
        } catch (Exception e) {
            if (e instanceof BAD_OPERATION) {
                assertTrue(true);
            } else {
                e.printStackTrace();
            }
        }
    }

    /**
     * Tests handling user exceptions
     */
    @Test
    public void test_userExceptions() {
        try {
            CalcServant calc = new CalcServant();
            TestRuntimeComponentService service = new TestRuntimeComponentService(calc);
            Class<?> javaClass = ((JavaInterface)service.getInterfaceContract().getInterface()).getJavaClass();
            InvocationProxy proxy = new ComponentInvocationProxy(service, service.getRuntimeWire(null), javaClass);
            DynaCorbaServant servant = new DynaCorbaServant(proxy, Utils.getTypeId(javaClass));
            String[] ids = {"IDL:org/apache/tuscany/sca/binding/corba/testing/exceptions/Calc:1.0"};
            servant.setIds(ids);
            bindServant(servant, "Calc");
            Calc calcClient = CalcHelper.narrow(bindReference("Calc"));
            calcClient.div(1, 0);
            fail();
        } catch (Exception e) {
            if (e instanceof DivByZero) {
                assertTrue(true);
            } else {
                e.printStackTrace();
                fail();
            }
        }

        try {
            CalcServant calc = new CalcServant();
            TestRuntimeComponentService service = new TestRuntimeComponentService(calc);
            Class<?> javaClass = ((JavaInterface)service.getInterfaceContract().getInterface()).getJavaClass();
            InvocationProxy proxy = new ComponentInvocationProxy(service, service.getRuntimeWire(null), javaClass);
            DynaCorbaServant servant = new DynaCorbaServant(proxy, Utils.getTypeId(javaClass));
            String[] ids = {"IDL:org/apache/tuscany/sca/binding/corba/testing/exceptions/Calc:1.0"};
            servant.setIds(ids);
            bindServant(servant, "Calc");
            Calc calcClient = CalcHelper.narrow(bindReference("Calc"));
            calcClient.divForSmallArgs(255, 255);
            fail();
        } catch (Exception e) {
            if (e instanceof NotSupported) {
                assertTrue(true);
            } else {
                e.printStackTrace();
                fail();
            }
        }
    }

    /**
     * Tests enums (arguments, return types)
     */
    @Test
    public void test_enums() {
        try {
            EnumManagerServant ems = new EnumManagerServant();
            TestRuntimeComponentService service = new TestRuntimeComponentService(ems);
            Class<?> javaClass = ((JavaInterface)service.getInterfaceContract().getInterface()).getJavaClass();
            InvocationProxy proxy = new ComponentInvocationProxy(service, service.getRuntimeWire(null), javaClass);
            DynaCorbaServant servant = new DynaCorbaServant(proxy, Utils.getTypeId(javaClass));
            String[] ids = {"IDL:org/apache/tuscany/sca/binding/corba/testing/enums/EnumManager:1.0"};
            servant.setIds(ids);
            bindServant(servant, "Enum");
            EnumManager em = EnumManagerHelper.narrow(bindReference("Enum"));
            Color color = Color.red;
            assertTrue(em.getColor(color).value() == color.value());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_nonCorbaServants() {
        try {
            NonCorbaServant ncs = new NonCorbaServant();
            TestRuntimeComponentService service = new TestRuntimeComponentService(ncs);
            Class<?> javaClass = ((JavaInterface)service.getInterfaceContract().getInterface()).getJavaClass();
            InvocationProxy proxy = new ComponentInvocationProxy(service, service.getRuntimeWire(null), javaClass);
            DynaCorbaServant servant = new DynaCorbaServant(proxy, Utils.getTypeId(javaClass));
            bindServant(servant, "NonCorbaServant");
            // it's non corba servant so we don't have stubs to test them
            DynaCorbaRequest request = new DynaCorbaRequest(bindReference("NonCorbaServant"), "setString");
            request.addArgument(TestConstants.STR_1);
            request.setOutputType(String.class);
            DynaCorbaResponse response = request.invoke();
            assertTrue(response.getContent().equals(TestConstants.STR_1));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        try {
            NonCorbaServant ncs = new NonCorbaServant();
            TestRuntimeComponentService service = new TestRuntimeComponentService(ncs);
            Class<?> javaClass = ((JavaInterface)service.getInterfaceContract().getInterface()).getJavaClass();
            InvocationProxy proxy = new ComponentInvocationProxy(service, service.getRuntimeWire(null), javaClass);
            DynaCorbaServant servant = new DynaCorbaServant(proxy, Utils.getTypeId(javaClass));
            bindServant(servant, "NonCorbaServant");
            // it's non corba servant so we don't have stubs to test them
            DynaCorbaRequest request = new DynaCorbaRequest(bindReference("NonCorbaServant"), "throwException");
            request.addExceptionType(NonCorbaException.class);
            request.invoke();
            fail();
        } catch (Exception e) {
            if (e instanceof NonCorbaException) {
                assertTrue(true);
            } else {
                e.printStackTrace();
                fail();
            }
        }
    }

    /**
     * Tests handling BAD_PARAM system exception
     */
    @Test
    public void test_systemException_BAD_PARAM() {
        try {
            CalcServant calc = new CalcServant();
            TestRuntimeComponentService service = new TestRuntimeComponentService(calc);
            Class<?> javaClass = ((JavaInterface)service.getInterfaceContract().getInterface()).getJavaClass();
            InvocationProxy proxy = new ComponentInvocationProxy(service, service.getRuntimeWire(null), javaClass);
            DynaCorbaServant servant = new DynaCorbaServant(proxy, Utils.getTypeId(javaClass));
            String[] ids = new String[] {"IDL:org/apache/tuscany/sca/binding/corba/testing/generated/TestObject:1.0"};
            servant.setIds(ids);
            bindServant(servant, "Calc");
            DynaCorbaRequest request = new DynaCorbaRequest(bindReference("Calc"), "div");
            request.addArgument(2d);
            request.setOutputType(double.class);
            request.invoke();
            fail();
        } catch (Exception e) {
            if (e instanceof CorbaException) {
                assertTrue(true);
            } else {
                e.printStackTrace();
                fail();
            }
        }
    }

    /**
     * Tests handling BAD_PARAM system exception
     */
    @Test
    public void test_invalidServantConfiguraion() {
        try {
            InvalidTypesServant its = new InvalidTypesServant();
            TestRuntimeComponentService service = new TestRuntimeComponentService(its);
            //expecting exception...
            Class<?> javaClass = ((JavaInterface)service.getInterfaceContract().getInterface()).getJavaClass();
            InvocationProxy proxy = new ComponentInvocationProxy(service, service.getRuntimeWire(null), javaClass);
            new DynaCorbaServant(proxy, Utils.getTypeId(javaClass));
            fail();
        } catch (Exception e) {
            if (e instanceof RequestConfigurationException) {
                assertTrue(true);
            } else {
                e.printStackTrace();
                fail();
            }
        }
    }
    
    /**
     * Tests serving CORBA arrays by Tuscany CORBA servants
     */
    @Test
    public void test_arraysPassing() {
        try {
            ArraysUnionsTuscanyServant arraysUnions = new ArraysUnionsTuscanyServant();
            TestRuntimeComponentService service = new TestRuntimeComponentService(arraysUnions);
            Class<?> javaClass = ((JavaInterface)service.getInterfaceContract().getInterface()).getJavaClass();
            InvocationProxy proxy = new ComponentInvocationProxy(service, service.getRuntimeWire(null), javaClass);
            DynaCorbaServant servant = new DynaCorbaServant(proxy, Utils.getTypeId(javaClass));
            String[] ids = new String[] {"IDL:org/apache/tuscany/sca/binding/corba/testing/arrays_unions/ArraysUnionsTests:1.0"};
            servant.setIds(ids);
            bindServant(servant, "ArraysUnions");
            Object reference = bindReference("ArraysUnions");
            ArraysUnionsTests objRef = ArraysUnionsTestsHelper.narrow(reference);
            String[][] stringArray = {{"Hello", "World"}, {"Hi", "Again"}};
            String[][] result = objRef.passStringArray(stringArray);
            for (int i = 0; i < stringArray.length; i++) {
                for (int j = 0; j < stringArray[i].length; j++) {
                    assertEquals(stringArray[i][j], result[i][j]);
                }
            }
            TestStruct struct = new TestStruct();
            String[] field1 = {"Hello", "World"};
            int[][] field2 = { {4, 2, 2, 5}, {6, 12, 5, 8}};
            float[][][] field3 = { { {2, 6}, {2, 7}, {9, 3}, {4, 6}}, { {3, 7}, {6, 6}, {3, 5}, {6, 2}}};
            struct.oneDimArray = field1;
            struct.twoDimArray = field2;
            struct.threeDimArray = field3;
            
            TestStruct structResult = objRef.passTestStruct(struct);
            for (int i = 0; i < struct.oneDimArray.length; i++) {
                assertEquals(struct.oneDimArray[i], structResult.oneDimArray[i]);
            }
            for (int i = 0; i < struct.twoDimArray.length; i++) {
                for (int j = 0; j < struct.twoDimArray[i].length; j++) {
                    assertEquals(struct.twoDimArray[i][j], structResult.twoDimArray[i][j]);
                }
            }
            for (int i = 0; i < struct.threeDimArray.length; i++) {
                for (int j = 0; j < struct.threeDimArray[i].length; j++) {
                    for (int k = 0; k < struct.threeDimArray[i][j].length; k++) {
                        assertEquals(struct.threeDimArray[i][j][k], structResult.threeDimArray[i][j][k], 0.0);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}
