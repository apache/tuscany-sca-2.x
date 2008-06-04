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

package org.apache.tuscany.sca.itest.databindings.jaxb;

import junit.framework.Assert;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 */
public class PrimitivesDatabindingTestCase {

    private SCADomain domain;

    /**
     * Runs before each test method
     */
    @Before
    public void setUp() throws Exception {
        try { 
            domain = SCADomain.newInstance("primitivesservice.composite");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs after each test method
     */
    @After
    public void tearDown() {
        domain.close();
    }

    /**
     * Invokes the PrimitivesService service using SCA binding.
     * Service method invoked is negateBoolean.
     */
    @Test
    public void testSCANegateBoolean() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientSCAComponent");
        performTestNegateBoolean(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using SCA binding.
     * Service method invoked is negateBooleanArray.
     */
    @Test
    public void testSCANegateBooleanArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientSCAComponent");
        performTestNegateBooleanArray(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using SCA binding.
     * Service method invoked is negateByte.
     */
    @Test
    public void testSCANegateByte() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientSCAComponent");
        performTestNegateByte(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using SCA binding.
     * Service method invoked is negateByteArray.
     */
    @Test
    @Ignore("TUSCANY-2351")
    public void testSCANegateByteArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientSCAComponent");
        performTestNegateByteArray(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using SCA binding.
     * Service method invoked is negateShort.
     */
    @Test
    public void testSCANegateShort() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientSCAComponent");
        performTestNegateShort(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using SCA binding.
     * Service method invoked is negateShortArray.
     */
    @Test
    public void testSCANegateShortArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientSCAComponent");
        performTestNegateShortArray(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using SCA binding.
     * Service method invoked is negateInt.
     */
    @Test
    public void testSCANegateInt() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientSCAComponent");
        performTestNegateInt(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using SCA binding.
     * Service method invoked is negateIntArray.
     */
    @Test
    public void testSCANegateIntArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientSCAComponent");
        performTestNegateIntArray(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using SCA binding.
     * Service method invoked is negateLong.
     */
    @Test
    public void testSCANegateLong() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientSCAComponent");
        performTestNegateLong(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using SCA binding.
     * Service method invoked is negateLongArray.
     */
    @Test
    public void testSCANegateLongArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientSCAComponent");
        performTestNegateLongArray(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using SCA binding.
     * Service method invoked is negateFloat.
     */
    @Test
    public void testSCANegateFloat() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientSCAComponent");
        performTestNegateFloat(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using SCA binding.
     * Service method invoked is negateFloatArray.
     */
    @Test
    public void testSCANegateFloatArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientSCAComponent");
        performTestNegateFloatArray(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using SCA binding.
     * Service method invoked is negateDouble.
     */
    @Test
    public void testSCANegateDouble() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientSCAComponent");
        performTestNegateDouble(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using SCA binding.
     * Service method invoked is negateDoubleArray.
     */
    @Test
    public void testSCANegateDoubleArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientSCAComponent");
        performTestNegateDoubleArray(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateBoolean.
     */
    @Test
    public void testWSNegateBoolean() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientWSComponent");
        performTestNegateBoolean(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateBooleanArray.
     */
    @Test
    public void testWSNegateBooleanArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientWSComponent");
        performTestNegateBooleanArray(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateByte.
     */
    @Test
    public void testWSNegateByte() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientWSComponent");
        performTestNegateByte(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateByteArray.
     */
    @Test
    // @Ignore("TUSCANY-2349")
    public void testWSNegateByteArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientWSComponent");
        performTestNegateByteArray(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateShort.
     */
    @Test
    public void testWSNegateShort() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientWSComponent");
        performTestNegateShort(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateShortArray.
     */
    @Test
    public void testWSNegateShortArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientWSComponent");
        performTestNegateShortArray(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateInt.
     */
    @Test
    public void testWSNegateInt() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientWSComponent");
        performTestNegateInt(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateIntArray.
     */
    @Test
    public void testWSNegateIntArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientWSComponent");
        performTestNegateIntArray(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateLong.
     */
    @Test
    public void testWSNegateLong() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientWSComponent");
        performTestNegateLong(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateLongArray.
     */
    @Test
    public void testWSNegateLongArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientWSComponent");
        performTestNegateLongArray(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateFloat.
     */
    @Test
    public void testWSNegateFloat() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientWSComponent");
        performTestNegateFloat(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateFloatArray.
     */
    @Test
    public void testWSNegateFloatArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientWSComponent");
        performTestNegateFloatArray(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateDouble.
     */
    @Test
    public void testWSNegateDouble() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientWSComponent");
        performTestNegateDouble(primitivesServiceClient);
    }

    /**
     * Invokes the PrimitivesService service using WS binding.
     * Service method invoked is negateDoubleArray.
     */
    @Test
    public void testWSNegateDoubleArray() throws Exception {
        PrimitivesServiceClient primitivesServiceClient = domain.getService(PrimitivesServiceClient.class, "PrimitivesServiceClientWSComponent");
        performTestNegateDoubleArray(primitivesServiceClient);
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
