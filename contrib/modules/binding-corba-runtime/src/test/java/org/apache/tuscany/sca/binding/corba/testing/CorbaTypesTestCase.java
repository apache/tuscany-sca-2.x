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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;

import junit.framework.Assert;

import org.apache.tuscany.sca.binding.corba.impl.exceptions.CorbaException;
import org.apache.tuscany.sca.binding.corba.impl.exceptions.RequestConfigurationException;
import org.apache.tuscany.sca.binding.corba.impl.reference.DynaCorbaRequest;
import org.apache.tuscany.sca.binding.corba.impl.reference.DynaCorbaResponse;
import org.apache.tuscany.sca.binding.corba.testing.enums.Color;
import org.apache.tuscany.sca.binding.corba.testing.exceptions.CalcPackage.DivByZero;
import org.apache.tuscany.sca.binding.corba.testing.exceptions.CalcPackage.NotSupported;
import org.apache.tuscany.sca.binding.corba.testing.generated.SimpleStruct;
import org.apache.tuscany.sca.binding.corba.testing.generated.SomeStruct;
import org.apache.tuscany.sca.binding.corba.testing.hierarchy.ArraysTestStruct;
import org.apache.tuscany.sca.binding.corba.testing.hierarchy.DummyObject;
import org.apache.tuscany.sca.binding.corba.testing.hierarchy.InnerUnion;
import org.apache.tuscany.sca.binding.corba.testing.hierarchy.InvalidCorbaArray;
import org.apache.tuscany.sca.binding.corba.testing.hierarchy.InvalidEnum1;
import org.apache.tuscany.sca.binding.corba.testing.hierarchy.InvalidEnum2;
import org.apache.tuscany.sca.binding.corba.testing.hierarchy.InvalidEnum3;
import org.apache.tuscany.sca.binding.corba.testing.hierarchy.InvalidStruct1;
import org.apache.tuscany.sca.binding.corba.testing.hierarchy.InvalidStruct2;
import org.apache.tuscany.sca.binding.corba.testing.hierarchy.InvalidStruct3;
import org.apache.tuscany.sca.binding.corba.testing.hierarchy.InvalidUnion1;
import org.apache.tuscany.sca.binding.corba.testing.hierarchy.InvalidUnion2;
import org.apache.tuscany.sca.binding.corba.testing.hierarchy.InvalidUnion3;
import org.apache.tuscany.sca.binding.corba.testing.hierarchy.InvalidUnion4;
import org.apache.tuscany.sca.binding.corba.testing.hierarchy.InvalidUnion5;
import org.apache.tuscany.sca.binding.corba.testing.hierarchy.RichUnion;
import org.apache.tuscany.sca.binding.corba.testing.servants.ArraysSetterServant;
import org.apache.tuscany.sca.binding.corba.testing.servants.ArraysUnionsServant;
import org.apache.tuscany.sca.binding.corba.testing.servants.ArraysUnionsTuscanyServant;
import org.apache.tuscany.sca.binding.corba.testing.servants.CalcServant;
import org.apache.tuscany.sca.binding.corba.testing.servants.EnumManagerServant;
import org.apache.tuscany.sca.binding.corba.testing.servants.ObjectManagerServant;
import org.apache.tuscany.sca.binding.corba.testing.servants.PrimitivesSetterServant;
import org.apache.tuscany.sca.binding.corba.testing.servants.TestObjectServant;
import org.apache.tuscany.sca.host.corba.naming.TransientNameServer;
import org.apache.tuscany.sca.host.corba.naming.TransientNameService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextHelper;

/**
 * @version $Rev$ $Date$
 *          Tests API for dynamic CORBA requests. Tests handling various Java
 *          types.
 */
public class CorbaTypesTestCase {
    private static TransientNameServer server;
    private static ORB orb;

    private static Object refPrimitivesSetter;
    private static Object refArraysSetter;
    private static Object refTestObject;
    private static Object refCalcObject;
    private static Object refObjectManager;
    private static Object refEnumManager;
    private static Object refArraysUnions;

    /**
     * Spawns tnameserv process (must be in PATH). Initializes test servants and
     * stores it's references so tests can use it.
     */
    @BeforeClass
    public static void setUp() {
        try {
            try {
                server =
                    new TransientNameServer(TestConstants.TEST1_HOST, TestConstants.TEST1_PORT,
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

            Object nameService = orb.resolve_initial_references("NameService");
            NamingContext namingContext = NamingContextHelper.narrow(nameService);

            PrimitivesSetterServant singleSetter = new PrimitivesSetterServant();
            ArraysSetterServant arraysSetter = new ArraysSetterServant();
            TestObjectServant complexObject = new TestObjectServant();
            CalcServant calcObject = new CalcServant();
            ObjectManagerServant objectManager = new ObjectManagerServant();
            EnumManagerServant enumManager = new EnumManagerServant();
            ArraysUnionsServant arraysUnions = new ArraysUnionsServant();

            orb.connect(singleSetter);
            orb.connect(arraysSetter);

            NameComponent nc;
            NameComponent[] path;

            nc = new NameComponent("PrimitivesSetter", "");
            path = new NameComponent[] {nc};
            namingContext.rebind(path, singleSetter);

            nc = new NameComponent("ArraysSetter", "");
            path = new NameComponent[] {nc};
            namingContext.rebind(path, arraysSetter);

            nc = new NameComponent("TestObject", "");
            path = new NameComponent[] {nc};
            namingContext.rebind(path, complexObject);

            nc = new NameComponent("CalcObject", "");
            path = new NameComponent[] {nc};
            namingContext.rebind(path, calcObject);

            nc = new NameComponent("ObjectManager", "");
            path = new NameComponent[] {nc};
            namingContext.rebind(path, objectManager);

            nc = new NameComponent("EnumManager", "");
            path = new NameComponent[] {nc};
            namingContext.rebind(path, enumManager);

            nc = new NameComponent("ArraysUnions", "");
            path = new NameComponent[] {nc};
            namingContext.rebind(path, arraysUnions);

            NamingContextExt nce = NamingContextExtHelper.narrow(orb.resolve_initial_references("NameService"));

            refArraysSetter = nce.resolve(nce.to_name("ArraysSetter"));
            refPrimitivesSetter = nce.resolve(nce.to_name("PrimitivesSetter"));
            refTestObject = nce.resolve(nce.to_name("TestObject"));
            refCalcObject = nce.resolve(nce.to_name("CalcObject"));
            refObjectManager = nce.resolve(nce.to_name("ObjectManager"));
            refEnumManager = nce.resolve(nce.to_name("EnumManager"));
            refArraysUnions = nce.resolve(nce.to_name("ArraysUnions"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void stop() {
        server.stop();
    }

    /**
     * Tests remote operation, basing on given reference, operation name,
     * arguments, expected return type and content
     * 
     * @param ref remote object
     * @param operationName operation to invoke
     * @param clazz expected return type
     * @param arguments array of operation arguments
     * @param equalTo expected return content
     */
    private void dynaTestInvoker(Object ref,
                                 String operationName,
                                 Class<?> clazz,
                                 java.lang.Object[] arguments,
                                 java.lang.Object equalTo) {

        try {
            DynaCorbaRequest request = new DynaCorbaRequest(ref, operationName);
            request.setOutputType(clazz);
            for (int i = 0; arguments != null && i < arguments.length; i++) {
                request.addArgument(arguments[i]);
            }

            DynaCorbaResponse response = request.invoke();
            java.lang.Object content = (java.lang.Object)response.getContent();
            assertTrue(content.getClass().equals(clazz));
            if (equalTo != null && equalTo.getClass().isArray()) {
                for (int i = 0; i < Array.getLength(equalTo); i++) {
                    assertTrue(Array.get(content, i).equals(Array.get(equalTo, i)));
                }
            } else {
                assertTrue(content.equals(equalTo));
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error while invoking " + operationName);
        }
    }

    /**
     * Tests passing (and getting as result) varied primitives
     */
    @Test
    public void test_setPrimitives() {

        dynaTestInvoker(refPrimitivesSetter, "setBoolean", Boolean.class, new Boolean[] {true}, true);
        dynaTestInvoker(refPrimitivesSetter, "setOctet", Byte.class, new Byte[] {1}, (byte)1);
        dynaTestInvoker(refPrimitivesSetter, "setChar", Character.class, new Character[] {'A'}, 'A');
        dynaTestInvoker(refPrimitivesSetter, "setShort", Short.class, new Short[] {1}, (short)1);
        dynaTestInvoker(refPrimitivesSetter, "setLong", Integer.class, new Integer[] {1}, (int)1);
        dynaTestInvoker(refPrimitivesSetter, "setLongLong", Long.class, new Long[] {(long)1}, (long)1);
        dynaTestInvoker(refPrimitivesSetter, "setFloat", Float.class, new Float[] {(float)1}, (float)1);
        dynaTestInvoker(refPrimitivesSetter, "setDouble", Double.class, new Double[] {(double)1}, (double)1);
        dynaTestInvoker(refPrimitivesSetter, "setString", String.class, new String[] {"1"}, "1");

    }

    /**
     * Tests passing (and getting as result) varied types sequences
     */
    @Test
    public void test_setArrays() {

        dynaTestInvoker(refArraysSetter,
                        "setBoolean",
                        Boolean[].class,
                        new Boolean[][] {new Boolean[] {false, true}},
                        new Boolean[] {false, true});

        dynaTestInvoker(refArraysSetter,
                        "setChar",
                        Character[].class,
                        new Character[][] {new Character[] {'A', 'B'}},
                        new Character[] {'A', 'B'});

        dynaTestInvoker(refArraysSetter, "setOctet", Byte[].class, new Byte[][] {new Byte[] {1, 2}}, new Byte[] {1, 2});

        dynaTestInvoker(refArraysSetter, "setShort", Short[].class, new Short[][] {new Short[] {1, 2}}, new Short[] {1,
                                                                                                                     2});

        dynaTestInvoker(refArraysSetter,
                        "setLong",
                        Integer[].class,
                        new Integer[][] {new Integer[] {1, 2}},
                        new Integer[] {1, 2});

        dynaTestInvoker(refArraysSetter,
                        "setLongLong",
                        Long[].class,
                        new Long[][] {new Long[] {new Long(1), new Long(2)}},
                        new Long[] {new Long(1), new Long(2)});

        dynaTestInvoker(refArraysSetter,
                        "setFloat",
                        Float[].class,
                        new Float[][] {new Float[] {new Float(1.0), new Float(2.0)}},
                        new Float[] {new Float(1.0), new Float(2.0)});

        dynaTestInvoker(refArraysSetter,
                        "setDouble",
                        Double[].class,
                        new Double[][] {new Double[] {new Double(1.0), new Double(2.0)}},
                        new Double[] {new Double(1.0), new Double(2.0)});

        dynaTestInvoker(refArraysSetter,
                        "setString",
                        String[].class,
                        new String[][] {new String[] {"A", "B"}},
                        new String[] {"A", "B"});

    }

    /**
     * Tests passing (and getting as result) complex structure
     */
    @Test
    public void test_TestObject_setStruct() {
        DynaCorbaRequest request = new DynaCorbaRequest(refTestObject, "setStruct");

        SomeStruct struct = new SomeStruct();
        SimpleStruct inner = new SimpleStruct();
        inner.field1 = TestConstants.STR_1;
        inner.field2 = TestConstants.INT_1;
        struct.innerStruct = inner;
        struct.str_list = TestConstants.STR_ARR_2;
        struct.twoDimSeq = TestConstants.INT_ARRAY_2_DIM;
        struct.threeDimSeq = TestConstants.INT_ARRAY_3_DIM;
        struct.str = TestConstants.STR_1;

        try {
            request.addArgument(struct);
            request.setOutputType(SomeStruct.class);
            DynaCorbaResponse response = request.invoke();
            SomeStruct result = (SomeStruct)response.getContent();
            assertTrue(TestConstants.are2DimArraysEqual(result.twoDimSeq, TestConstants.INT_ARRAY_2_DIM));
            assertTrue(TestConstants.are3DimArraysEqual(result.threeDimSeq, TestConstants.INT_ARRAY_3_DIM));
            assertEquals(TestConstants.STR_1, result.str);
            assertEquals(TestConstants.STR_ARR_2[0], result.str_list[0]);
            assertEquals(TestConstants.STR_ARR_2[1], result.str_list[1]);
            assertEquals(TestConstants.STR_1, result.innerStruct.field1);
            assertEquals(TestConstants.INT_1, result.innerStruct.field2);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception occured during tests: " + e);
        }
    }

    /**
     * Test passing (and getting as result) simple two-field structure
     */
    @Test
    public void test_TestObject_setSimpleStruct() {
        SimpleStruct struct = new SimpleStruct();
        struct.field1 = TestConstants.STR_1;
        struct.field2 = TestConstants.INT_1;
        DynaCorbaRequest request = new DynaCorbaRequest(refTestObject, "setSimpleStruct");
        try {
            request.setOutputType(SimpleStruct.class);
            request.addArgument(struct);
            DynaCorbaResponse response = request.invoke();
            SimpleStruct retStruct = (SimpleStruct)response.getContent();
            assertTrue(retStruct.field1.equals(struct.field1) && retStruct.field2 == struct.field2);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception occured during tests: " + e);
        }
    }

    /**
     * Tests passing (and getting as result) two dim. sequence of long.
     */
    @Test
    public void test_TestObject_setLongSeq2() {
        int[][] arr1 = new int[2][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                arr1[i][j] = (int)(Math.random() * 1000);
            }
        }
        DynaCorbaRequest request = new DynaCorbaRequest(refTestObject, "setLongSeq2");
        try {
            request.setOutputType(arr1.getClass());
            request.addArgument(arr1);
            DynaCorbaResponse response = request.invoke();
            int[][] arr2 = (int[][])response.getContent();
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    assertEquals(arr1[i][j], arr2[i][j]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception occured during tests: " + e);
        }
    }

    /**
     * Tests passing multiple complex attributes.
     */
    @Test
    public void test_TestObject_pickStructFromArgs() {
        SomeStruct arg1 = new SomeStruct();
        SomeStruct arg2 = new SomeStruct();
        SomeStruct arg3 = new SomeStruct();

        SimpleStruct inner = new SimpleStruct();
        inner.field1 = TestConstants.STR_1;
        inner.field2 = TestConstants.INT_1;

        arg1.innerStruct = inner;
        arg2.innerStruct = inner;
        arg3.innerStruct = inner;

        arg1.str = TestConstants.STR_1;
        arg2.str = TestConstants.STR_2;
        arg3.str = TestConstants.STR_3;

        arg1.str_list = TestConstants.STR_ARR_1;
        arg2.str_list = TestConstants.STR_ARR_2;
        arg3.str_list = TestConstants.STR_ARR_2;

        arg1.threeDimSeq = TestConstants.INT_ARRAY_3_DIM;
        arg2.threeDimSeq = TestConstants.INT_ARRAY_3_DIM;
        arg3.threeDimSeq = TestConstants.INT_ARRAY_3_DIM;

        arg1.twoDimSeq = TestConstants.INT_ARRAY_2_DIM;
        arg2.twoDimSeq = TestConstants.INT_ARRAY_2_DIM;
        arg3.twoDimSeq = TestConstants.INT_ARRAY_2_DIM;

        DynaCorbaRequest request = new DynaCorbaRequest(refTestObject, "pickStructFromArgs");
        try {

            request.setOutputType(SomeStruct.class);
            request.addArgument(arg1);
            request.addArgument(arg2);
            request.addArgument(arg3);
            request.addArgument(1);
            DynaCorbaResponse response = request.invoke();
            SomeStruct result = (SomeStruct)response.getContent();

            // just make sure that servant returned right structure
            assertTrue(result.str.equals(TestConstants.STR_1));
        } catch (Exception e) {
            fail("Exception occured during tests " + e);
            e.printStackTrace();
        }

    }

    /**
     * Tests handling user defined remote exception (single declared)
     */
    @Test
    public void test_singleException() {
        DynaCorbaRequest request1 = new DynaCorbaRequest(refCalcObject, "div");
        try {
            request1.addArgument(2d);
            request1.addArgument(2d);
            request1.setOutputType(Double.class);
            request1.addExceptionType(DivByZero.class);
            request1.invoke();
        } catch (Exception e) {
            fail();
        }

        DynaCorbaRequest request2 = new DynaCorbaRequest(refCalcObject, "div");
        try {
            request2.addArgument(2d);
            request2.addArgument(0d);
            request2.setOutputType(Double.class);
            request2.addExceptionType(DivByZero.class);
            request2.invoke();
        } catch (DivByZero e) {
            assertTrue(e.info != null && e.arguments != null && e.arguments.arg1 == 2 && e.arguments.arg2 == 0);
        } catch (Exception exc) {
            exc.printStackTrace();
            fail();
        }
    }

    /**
     * Tests handling user defined multiple exceptions
     */
    @Test
    public void test_multipleExceptions() {
        DynaCorbaRequest request = new DynaCorbaRequest(refCalcObject, "divForSmallArgs");
        try {
            request.addArgument(101d);
            request.addArgument(101d);
            request.setOutputType(Double.class);
            request.addExceptionType(DivByZero.class);
            request.addExceptionType(NotSupported.class);
            request.invoke();
        } catch (Exception e) {
            assertTrue(e instanceof NotSupported);
        }
    }

    /**
     * Tests handling exceptions while user defined no exceptions
     */
    @Test
    public void test_noExceptionsDeclared() {
        DynaCorbaRequest request = new DynaCorbaRequest(refCalcObject, "div");
        try {
            request.addArgument(1d);
            request.addArgument(0d);
            request.setOutputType(Double.class);
            request.invoke();
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof RequestConfigurationException);
        }
    }

    /**
     * Tests handling exceptions while user defined no such exception
     */
    @Test
    public void test_noSuchExceptionDeclared() {
        DynaCorbaRequest request = new DynaCorbaRequest(refCalcObject, "div");
        try {
            request.addArgument(1d);
            request.addArgument(0d);
            request.addExceptionType(NotSupported.class);
            request.setOutputType(Double.class);
            request.invoke();
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof RequestConfigurationException);
        }
    }

    /**
     * Tests handling non existing operation situation
     */
    @Test
    public void test_systemException_BAD_OPERATION() {
        DynaCorbaRequest request = new DynaCorbaRequest(refCalcObject, "thisOperationSurelyDoesNotExist");
        try {
            request.invoke();
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof CorbaException);
        }
    }

    /**
     * Tests obtaining references to other objects and using them with specified
     * user interface
     */
    @Test
    @Ignore("Cause of tnameservice hang on stop")
    public void test_enchancedReferences() {
        DynaCorbaRequest request = null;
        try {
            request = new DynaCorbaRequest(refObjectManager, "getDummyObject");
            request.setOutputType(DummyObject.class);
            DynaCorbaResponse response = request.invoke();
            DummyObject dummy = (DummyObject)response.getContent();
            DummyObject dummy2 = dummy.cloneObject();
            dummy2.cloneObject();
            assertNotSame(dummy.getLong(), dummy2.getLong());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Test passing enums as arguments and retrieving them as a result
     */
    @Test
    public void test_enums() {
        try {
            DynaCorbaRequest request = new DynaCorbaRequest(refEnumManager, "getColor");
            Color color = Color.green;
            request.addArgument(color);
            request.setOutputType(Color.class);
            DynaCorbaResponse response = request.invoke();
            Color result = (Color)response.getContent();
            assertEquals(color.value(), result.value());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests recognizing structures
     */
    @Test
    public void test_structValidation() {
        DynaCorbaRequest request = null;
        try {
            request = new DynaCorbaRequest(refArraysSetter, "whatever");
            request.setOutputType(InvalidStruct1.class);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof RequestConfigurationException);
        }

        try {
            request = new DynaCorbaRequest(refArraysSetter, "whatever");
            request.setOutputType(InvalidStruct2.class);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof RequestConfigurationException);
        }

        try {
            request = new DynaCorbaRequest(refArraysSetter, "whatever");
            request.setOutputType(InvalidStruct3.class);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof RequestConfigurationException);
        }

        try {
            request = new DynaCorbaRequest(refArraysSetter, "whatever");
            request.setOutputType(SomeStruct.class);
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Tests recognizing enums
     */
    @Test
    public void test_enumValidation() {
        DynaCorbaRequest request = null;
        try {
            request = new DynaCorbaRequest(refArraysSetter, "whatever");
            request.setOutputType(InvalidEnum1.class);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof RequestConfigurationException);
        }

        try {
            request = new DynaCorbaRequest(refArraysSetter, "whatever");
            request.setOutputType(InvalidEnum2.class);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof RequestConfigurationException);
        }

        try {
            request = new DynaCorbaRequest(refArraysSetter, "whatever");
            request.setOutputType(InvalidEnum3.class);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof RequestConfigurationException);
        }

        try {
            request = new DynaCorbaRequest(refArraysSetter, "whatever");
            request.setOutputType(Color.class);
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Tests handling passing wrong params
     */
    @Test
    public void test_systemException_BAD_PARAM() {
        try {
            DynaCorbaRequest request = new DynaCorbaRequest(refCalcObject, "div");
            request.setOutputType(Double.class);
            request.addArgument(3d);
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
     * Tests passing CORBA arrays
     */
    @Test
    public void test_arraysPassing() {
        try {
            DynaCorbaRequest request = new DynaCorbaRequest(refArraysUnions, "passStringArray");
            Annotation[] notes =
                ArraysUnionsTuscanyServant.class.getMethod("passStringArray", new Class<?>[] {String[][].class})
                    .getAnnotations();
            request.setOutputType(String[][].class, notes);
            String[][] argument = { {"Hello", "World"}, {"Hi", "again"}};
            request.addArgument(argument, notes);
            DynaCorbaResponse response = request.invoke();
            String[][] result = (String[][])response.getContent();
            for (int i = 0; i < argument.length; i++) {
                for (int j = 0; j < argument[i].length; j++) {
                    assertEquals(argument[i][j], result[i][j]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        try {
            DynaCorbaRequest request = new DynaCorbaRequest(refArraysUnions, "passTestStruct");
            ArraysTestStruct arg = new ArraysTestStruct();
            String[] field1 = {"Hello", "World"};
            arg.field1 = field1;
            int[][] field2 = { {4, 2, 2, 5}, {6, 12, 5, 8}};
            arg.field2 = field2;
            float[][][] field3 = { { {2, 6}, {2, 7}, {9, 3}, {4, 6}}, { {3, 7}, {6, 6}, {3, 5}, {6, 2}}};
            arg.field3 = field3;
            request.addArgument(arg);
            request.setOutputType(ArraysTestStruct.class);
            DynaCorbaResponse response = request.invoke();
            ArraysTestStruct result = (ArraysTestStruct)response.getContent();
            for (int i = 0; i < arg.field1.length; i++) {
                assertEquals(arg.field1[i], result.field1[i]);
            }
            for (int i = 0; i < arg.field2.length; i++) {
                for (int j = 0; j < arg.field2[i].length; j++) {
                    assertEquals(arg.field2[i][j], result.field2[i][j]);
                }
            }
            for (int i = 0; i < arg.field2.length; i++) {
                for (int j = 0; j < arg.field2[i].length; j++) {
                    for (int k = 0; k < arg.field3[i][j].length; k++) {
                        assertEquals(arg.field3[i][j][k], result.field3[i][j][k], 0.0);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests situation when CORBA array dimension size doesn't match
     * CORBA array annotation arguments (which sets dimension lengths)
     */
    @Test
    public void test_invalidArrayAnnotationSize() {
        try {
            DynaCorbaRequest request = new DynaCorbaRequest(refArraysUnions, "passStringArray");
            Annotation[] notes =
                ArraysUnionsTuscanyServant.class.getMethod("passStringArray", new Class<?>[] {String[][].class})
                    .getAnnotations();
            request.setOutputType(String[][][].class, notes);
            fail();
        } catch (RequestConfigurationException e) {
            // expected
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        try {
            DynaCorbaRequest request = new DynaCorbaRequest(refArraysUnions, "passStringArray");
            Annotation[] notes =
                ArraysUnionsTuscanyServant.class.getMethod("passStringArray", new Class<?>[] {String[][].class})
                    .getAnnotations();
            request.addArgument(new String[0][0][0], notes);
            fail();
        } catch (RequestConfigurationException e) {
            // expected
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        try {
            DynaCorbaRequest request = new DynaCorbaRequest(refArraysUnions, "passStringArray");
            request.addArgument(new InvalidCorbaArray(), null);
            fail();
        } catch (RequestConfigurationException e) {
            // expected
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
    /**
     * Tests passing CORBA unions
     */
    @Test
    public void test_passingUnions() {
        try {
            DynaCorbaRequest request = new DynaCorbaRequest(refArraysUnions, "passRichUnion");
            request.setOutputType(RichUnion.class);
            RichUnion arg = new RichUnion();
            InnerUnion argIu = new InnerUnion();
            argIu.setX(10);
            arg.setIu(argIu);
            request.addArgument(arg);
            DynaCorbaResponse response = request.invoke();
            RichUnion result = (RichUnion)response.getContent();
            assertEquals(arg.getIu().getX(), result.getIu().getX());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        try {
            DynaCorbaRequest request = new DynaCorbaRequest(refArraysUnions, "passRichUnion");
            request.setOutputType(RichUnion.class);
            RichUnion arg = new RichUnion();
            arg.setDef(true);
            request.addArgument(arg);
            DynaCorbaResponse response = request.invoke();
            RichUnion result = (RichUnion)response.getContent();
            assertEquals(arg.isDef(), result.isDef());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
    /**
     * Tests handling invalid union declarations
     */
    @Test
    public void test_testInvalidUnionClasses() {
        try {
            DynaCorbaRequest request = new DynaCorbaRequest(refArraysUnions, "whatever");
            request.setOutputType(InvalidUnion1.class);
        } catch (Exception e) {
            assertEquals(RequestConfigurationException.class, e.getClass());
        }
        try {
            DynaCorbaRequest request = new DynaCorbaRequest(refArraysUnions, "whatever");
            request.setOutputType(InvalidUnion2.class);
        } catch (Exception e) {
            assertEquals(RequestConfigurationException.class, e.getClass());
        }
        try {
            DynaCorbaRequest request = new DynaCorbaRequest(refArraysUnions, "whatever");
            request.setOutputType(InvalidUnion3.class);
        } catch (Exception e) {
            assertEquals(RequestConfigurationException.class, e.getClass());
        }
        try {
            DynaCorbaRequest request = new DynaCorbaRequest(refArraysUnions, "whatever");
            request.setOutputType(InvalidUnion4.class);
        } catch (Exception e) {
            assertEquals(RequestConfigurationException.class, e.getClass());
        }
        try {
            DynaCorbaRequest request = new DynaCorbaRequest(refArraysUnions, "whatever");
            request.setOutputType(InvalidUnion5.class);
        } catch (Exception e) {
            assertEquals(RequestConfigurationException.class, e.getClass());
        }
    }
}
