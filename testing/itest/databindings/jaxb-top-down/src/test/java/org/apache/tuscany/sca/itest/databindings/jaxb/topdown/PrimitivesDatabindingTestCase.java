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

package org.apache.tuscany.sca.itest.databindings.jaxb.topdown;

import java.io.File;

import junit.framework.Assert;

import org.apache.tuscany.sca.itest.databindings.jaxb.PrimitivesServiceClient;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 */
public class PrimitivesDatabindingTestCase {

    private static Node node;

    /**
     * Runs before each test method
     */
    @BeforeClass
    public static void setUp() throws Exception {
        NodeFactory factory = NodeFactory.newInstance();
        node = factory.createNode(new File("src/main/resources/wsdl/wrapped/primitivesservice.composite").toURI().toURL().toString(),
                new Contribution("TestContribution", new File("src/main/resources/wsdl/wrapped/").toURI().toURL().toString()));
        node.start();
    }

    /**
     * Runs after each test method
     */
    @AfterClass
    public static void tearDown() {
        node.stop();
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateBoolean.
     */
    @Test
    public void testW2WNegateBoolean() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2WComponent");
        performTestNegateBoolean(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateBooleanArray.
     */
    @Test
    public void testW2WNegateBooleanArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2WComponent");
        performTestNegateBooleanArray(primitivesServiceClient);
    }

    /**
     * Test the pass-by-value semantics of a remotable service with WS binding.
     * Test for boolean array.
     */
    @Test
    public void testW2WPassByValueBooleanArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2WComponent");
        Assert.assertTrue(primitivesServiceClient.passByValueBooleanArray());
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateByte.
     */
    @Test
    public void testW2WNegateByte() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2WComponent");
        performTestNegateByte(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateByteArray.
     */
    @Test
    public void testW2WNegateByteArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2WComponent");
        performTestNegateByteArray(primitivesServiceClient);
    }

    /**
     * Test the pass-by-value semantics of a remotable service with WS binding.
     * Test for byte array.
     */
    @Test
    public void testW2WPassByValueByteArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2WComponent");
        Assert.assertTrue(primitivesServiceClient.passByValueByteArray());
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateShort.
     */
    @Test
    public void testW2WNegateShort() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2WComponent");
        performTestNegateShort(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateShortArray.
     */
    @Test
    public void testW2WNegateShortArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2WComponent");
        performTestNegateShortArray(primitivesServiceClient);
    }

    /**
     * Test the pass-by-value semantics of a remotable service with WS binding.
     * Test for short array.
     */
    @Test
    public void testW2WPassByValueShortArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2WComponent");
        Assert.assertTrue(primitivesServiceClient.passByValueShortArray());
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateInt.
     */
    @Test
    public void testW2WNegateInt() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2WComponent");
        performTestNegateInt(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateIntArray.
     */
    @Test
    public void testW2WNegateIntArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2WComponent");
        performTestNegateIntArray(primitivesServiceClient);
    }

    /**
     * Test the pass-by-value semantics of a remotable service with WS binding.
     * Test for int array.
     */
    @Test
    public void testW2WPassByValueIntArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2WComponent");
        Assert.assertTrue(primitivesServiceClient.passByValueIntArray());
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateLong.
     */
    @Test
    public void testW2WNegateLong() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2WComponent");
        performTestNegateLong(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateLongArray.
     */
    @Test
    public void testW2WNegateLongArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2WComponent");
        performTestNegateLongArray(primitivesServiceClient);
    }

    /**
     * Test the pass-by-value semantics of a remotable service with WS binding.
     * Test for long array.
     */
    @Test
    public void testW2WPassByValueLongArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2WComponent");
        Assert.assertTrue(primitivesServiceClient.passByValueLongArray());
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateFloat.
     */
    @Test
    public void testW2WNegateFloat() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2WComponent");
        performTestNegateFloat(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateFloatArray.
     */
    @Test
    public void testW2WNegateFloatArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2WComponent");
        performTestNegateFloatArray(primitivesServiceClient);
    }

    /**
     * Test the pass-by-value semantics of a remotable service with WS binding.
     * Test for float array.
     */
    @Test
    public void testW2WPassByValueFloatArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2WComponent");
        Assert.assertTrue(primitivesServiceClient.passByValueFloatArray());
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateDouble.
     */
    @Test
    public void testW2WNegateDouble() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2WComponent");
        performTestNegateDouble(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateDoubleArray.
     */
    @Test
    public void testW2WNegateDoubleArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2WComponent");
        performTestNegateDoubleArray(primitivesServiceClient);
    }

    /**
     * Test the pass-by-value semantics of a remotable service with WS binding.
     * Test for double array.
     */
    @Test
    public void testW2WPassByValueDoubleArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2WComponent");
        Assert.assertTrue(primitivesServiceClient.passByValueDoubleArray());
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateBoolean.
     */
    @Test
    public void testJ2WNegateBoolean() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientJ2WComponent");
        performTestNegateBoolean(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateBooleanArray.
     */
    @Test
    public void testJ2WNegateBooleanArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientJ2WComponent");
        performTestNegateBooleanArray(primitivesServiceClient);
    }

    /**
     * Test the pass-by-value semantics of a remotable service with WS binding.
     * Test for boolean array.
     */
    @Test
    public void testJ2WPassByValueBooleanArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientJ2WComponent");
        Assert.assertTrue(primitivesServiceClient.passByValueBooleanArray());
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateByte.
     */
    @Test
    public void testJ2WNegateByte() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientJ2WComponent");
        performTestNegateByte(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateByteArray.
     */
    @Test
    public void testJ2WNegateByteArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientJ2WComponent");
        performTestNegateByteArray(primitivesServiceClient);
    }

    /**
     * Test the pass-by-value semantics of a remotable service with WS binding.
     * Test for byte array.
     */
    @Test
    public void testJ2WPassByValueByteArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientJ2WComponent");
        Assert.assertTrue(primitivesServiceClient.passByValueByteArray());
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateShort.
     */
    @Test
    public void testJ2WNegateShort() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientJ2WComponent");
        performTestNegateShort(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateShortArray.
     */
    @Test
    public void testJ2WNegateShortArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientJ2WComponent");
        performTestNegateShortArray(primitivesServiceClient);
    }

    /**
     * Test the pass-by-value semantics of a remotable service with WS binding.
     * Test for short array.
     */
    @Test
    public void testJ2WPassByValueShortArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientJ2WComponent");
        Assert.assertTrue(primitivesServiceClient.passByValueShortArray());
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateInt.
     */
    @Test
    public void testJ2WNegateInt() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientJ2WComponent");
        performTestNegateInt(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateIntArray.
     */
    @Test
    public void testJ2WNegateIntArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientJ2WComponent");
        performTestNegateIntArray(primitivesServiceClient);
    }

    /**
     * Test the pass-by-value semantics of a remotable service with WS binding.
     * Test for int array.
     */
    @Test
    public void testJ2WPassByValueIntArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientJ2WComponent");
        Assert.assertTrue(primitivesServiceClient.passByValueIntArray());
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateLong.
     */
    @Test
    public void testJ2WNegateLong() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientJ2WComponent");
        performTestNegateLong(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateLongArray.
     */
    @Test
    public void testJ2WNegateLongArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientJ2WComponent");
        performTestNegateLongArray(primitivesServiceClient);
    }

    /**
     * Test the pass-by-value semantics of a remotable service with WS binding.
     * Test for long array.
     */
    @Test
    public void testJ2WPassByValueLongArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientJ2WComponent");
        Assert.assertTrue(primitivesServiceClient.passByValueLongArray());
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateFloat.
     */
    @Test
    public void testJ2WNegateFloat() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientJ2WComponent");
        performTestNegateFloat(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateFloatArray.
     */
    @Test
    public void testJ2WNegateFloatArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientJ2WComponent");
        performTestNegateFloatArray(primitivesServiceClient);
    }

    /**
     * Test the pass-by-value semantics of a remotable service with WS binding.
     * Test for float array.
     */
    @Test
    public void testJ2WPassByValueFloatArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientJ2WComponent");
        Assert.assertTrue(primitivesServiceClient.passByValueFloatArray());
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateDouble.
     */
    @Test
    public void testJ2WNegateDouble() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientJ2WComponent");
        performTestNegateDouble(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateDoubleArray.
     */
    @Test
    public void testJ2WNegateDoubleArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientJ2WComponent");
        performTestNegateDoubleArray(primitivesServiceClient);
    }

    /**
     * Test the pass-by-value semantics of a remotable service with WS binding.
     * Test for double array.
     */
    @Test
    public void testJ2WPassByValueDoubleArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientJ2WComponent");
        Assert.assertTrue(primitivesServiceClient.passByValueDoubleArray());
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateBoolean.
     */
    @Test
    public void testW2JNegateBoolean() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2JComponent");
        performTestNegateBoolean(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateBooleanArray.
     */
    @Test
    public void testW2JNegateBooleanArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2JComponent");
        performTestNegateBooleanArray(primitivesServiceClient);
    }

    /**
     * Test the pass-by-value semantics of a remotable service with WS binding.
     * Test for boolean array.
     */
    @Test
    public void testW2JPassByValueBooleanArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2JComponent");
        Assert.assertTrue(primitivesServiceClient.passByValueBooleanArray());
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateByte.
     */
    @Test
    public void testW2JNegateByte() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2JComponent");
        performTestNegateByte(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateByteArray.
     */
    @Test
    public void testW2JNegateByteArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2JComponent");
        performTestNegateByteArray(primitivesServiceClient);
    }

    /**
     * Test the pass-by-value semantics of a remotable service with WS binding.
     * Test for byte array.
     */
    @Test
    public void testW2JPassByValueByteArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2JComponent");
        Assert.assertTrue(primitivesServiceClient.passByValueByteArray());
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateShort.
     */
    @Test
    public void testW2JNegateShort() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2JComponent");
        performTestNegateShort(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateShortArray.
     */
    @Test
    public void testW2JNegateShortArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2JComponent");
        performTestNegateShortArray(primitivesServiceClient);
    }

    /**
     * Test the pass-by-value semantics of a remotable service with WS binding.
     * Test for short array.
     */
    @Test
    public void testW2JPassByValueShortArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2JComponent");
        Assert.assertTrue(primitivesServiceClient.passByValueShortArray());
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateInt.
     */
    @Test
    public void testW2JNegateInt() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2JComponent");
        performTestNegateInt(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateIntArray.
     */
    @Test
    public void testW2JNegateIntArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2JComponent");
        performTestNegateIntArray(primitivesServiceClient);
    }

    /**
     * Test the pass-by-value semantics of a remotable service with WS binding.
     * Test for int array.
     */
    @Test
    public void testW2JPassByValueIntArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2JComponent");
        Assert.assertTrue(primitivesServiceClient.passByValueIntArray());
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateLong.
     */
    @Test
    public void testW2JNegateLong() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2JComponent");
        performTestNegateLong(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateLongArray.
     */
    @Test
    public void testW2JNegateLongArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2JComponent");
        performTestNegateLongArray(primitivesServiceClient);
    }

    /**
     * Test the pass-by-value semantics of a remotable service with WS binding.
     * Test for long array.
     */
    @Test
    public void testW2JPassByValueLongArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2JComponent");
        Assert.assertTrue(primitivesServiceClient.passByValueLongArray());
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateFloat.
     */
    @Test
    public void testW2JNegateFloat() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2JComponent");
        performTestNegateFloat(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateFloatArray.
     */
    @Test
    public void testW2JNegateFloatArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2JComponent");
        performTestNegateFloatArray(primitivesServiceClient);
    }

    /**
     * Test the pass-by-value semantics of a remotable service with WS binding.
     * Test for float array.
     */
    @Test
    public void testW2JPassByValueFloatArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2JComponent");
        Assert.assertTrue(primitivesServiceClient.passByValueFloatArray());
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateDouble.
     */
    @Test
    public void testW2JNegateDouble() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2JComponent");
        performTestNegateDouble(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateDoubleArray.
     */
    @Test
    public void testW2JNegateDoubleArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2JComponent");
        performTestNegateDoubleArray(primitivesServiceClient);
    }

    /**
     * Test the pass-by-value semantics of a remotable service with WS binding.
     * Test for double array.
     */
    @Test
    public void testW2JPassByValueDoubleArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = node.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientW2JComponent");
        Assert.assertTrue(primitivesServiceClient.passByValueDoubleArray());
    }

    private void performTestNegateBoolean(PrimitivesServiceClient primitivesServiceClient) {
        Assert.assertTrue(primitivesServiceClient.negateBooleanForward(false));
        Assert.assertFalse(primitivesServiceClient.negateBooleanForward(true));
    }

    private void performTestNegateBooleanArray(PrimitivesServiceClient primitivesServiceClient) {
        boolean flags[] = new boolean[2];
        flags[0] = false;
        flags[1] = true;
        boolean[] respFlags = primitivesServiceClient.negateBooleanArrayForward(flags);
        Assert.assertEquals(flags.length, respFlags.length);
        for(int i = 0; i < flags.length; ++i) {
            Assert.assertEquals(!flags[i], respFlags[i]);
        }
    }

    private void performTestNegateByte(PrimitivesServiceClient primitivesServiceClient) {
        byte[] ba = new byte[3];
        ba[0] = -1;
        ba[1] = 0;
        ba[2] = 1;

        for(int i = 0; i < ba.length; ++i) {
            Assert.assertEquals((byte)-ba[i], primitivesServiceClient.negateByteForward(ba[i]));
        }
    }

    private void performTestNegateByteArray(PrimitivesServiceClient primitivesServiceClient) {
        byte[] ba = new byte[3];
        ba[0] = -1;
        ba[1] = 0;
        ba[2] = 1;

        byte[] r = primitivesServiceClient.negateByteArrayForward(ba);
        Assert.assertEquals(ba.length, r.length);
        for(int i = 0; i < ba.length; ++i) {
            Assert.assertEquals((byte)-ba[i], r[i]);
        }
    }
    
    private void performTestNegateShort(PrimitivesServiceClient primitivesServiceClient) {
        short[] s = new short[3];
        s[0] = -1;
        s[1] = 0;
        s[2] = 1;

        for(int i = 0; i < s.length; ++i) {
            Assert.assertEquals((short)-s[i], primitivesServiceClient.negateShortForward(s[i]));
        }
    }

    private void performTestNegateShortArray(PrimitivesServiceClient primitivesServiceClient) {
        short[] s = new short[3];
        s[0] = -1;
        s[1] = 0;
        s[2] = 1;

        short[] r = primitivesServiceClient.negateShortArrayForward(s);
        Assert.assertEquals(s.length, r.length);
        for(int i = 0; i < s.length; ++i) {
            Assert.assertEquals((short)-s[i], r[i]);
        }
    }

    private void performTestNegateInt(PrimitivesServiceClient primitivesServiceClient) {
        int[] ia = new int[3];
        ia[0] = -1;
        ia[1] = 0;
        ia[2] = 1;

        for(int i = 0; i < ia.length; ++i) {
            Assert.assertEquals(-ia[i], primitivesServiceClient.negateIntForward(ia[i]));
        }
    }

    private void performTestNegateIntArray(PrimitivesServiceClient primitivesServiceClient) {
        int[] ia = new int[3];
        ia[0] = -1;
        ia[1] = 0;
        ia[2] = 1;

        int[] r = primitivesServiceClient.negateIntArrayForward(ia);
        Assert.assertEquals(ia.length, r.length);
        for(int i = 0; i < ia.length; ++i) {
            Assert.assertEquals(-ia[i], r[i]);
        }
    }

    private void performTestNegateLong(PrimitivesServiceClient primitivesServiceClient) {
        long[] la = new long[3];
        la[0] = -1;
        la[1] = 0;
        la[2] = 1;

        for(int i = 0; i < la.length; ++i) {
            Assert.assertEquals(-la[i], primitivesServiceClient.negateLongForward(la[i]));
        }
    }

    private void performTestNegateLongArray(PrimitivesServiceClient primitivesServiceClient) {
        long[] la = new long[3];
        la[0] = -1;
        la[1] = 0;
        la[2] = 1;

        long[] r = primitivesServiceClient.negateLongArrayForward(la);
        Assert.assertEquals(la.length, r.length);
        for(int i = 0; i < la.length; ++i) {
            Assert.assertEquals(-la[i], r[i]);
        }
    }

    private void performTestNegateFloat(PrimitivesServiceClient primitivesServiceClient) {
        float[] fa = new float[3];
        fa[0] = -1;
        fa[1] = 0;
        fa[2] = 1;

        for(int i = 0; i < fa.length; ++i) {
            Assert.assertEquals(-fa[i], primitivesServiceClient.negateFloatForward(fa[i]));
        }
    }

    private void performTestNegateFloatArray(PrimitivesServiceClient primitivesServiceClient) {
        float[] ia = new float[3];
        ia[0] = -1;
        ia[1] = 0;
        ia[2] = 1;

        float[] r = primitivesServiceClient.negateFloatArrayForward(ia);
        Assert.assertEquals(ia.length, r.length);
        for(int i = 0; i < ia.length; ++i) {
            Assert.assertEquals(-ia[i], r[i]);
        }
    }

    private void performTestNegateDouble(PrimitivesServiceClient primitivesServiceClient) {
        double[] da = new double[3];
        da[0] = -1;
        da[1] = 0;
        da[2] = 1;

        for(int i = 0; i < da.length; ++i) {
            Assert.assertEquals(-da[i], primitivesServiceClient.negateDoubleForward(da[i]));
        }
    }

    private void performTestNegateDoubleArray(PrimitivesServiceClient primitivesServiceClient) {
        double[] da = new double[3];
        da[0] = -1;
        da[1] = 0;
        da[2] = 1;

        double[] r = primitivesServiceClient.negateDoubleArrayForward(da);
        Assert.assertEquals(da.length, r.length);
        for(int i = 0; i < da.length; ++i) {
            Assert.assertEquals(-da[i], r[i]);
        }
    }
}
