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

package org.apache.tuscany.sca.itest.databindings.jaxb.impl;

import org.apache.tuscany.sca.itest.databindings.jaxb.PrimitivesService;
import org.apache.tuscany.sca.itest.databindings.jaxb.PrimitivesServiceClient;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * An implementation of PrimitivesServiceClient.
 * The client forwards the request to the service component and returns the response from the service component.
 */
@Service(PrimitivesServiceClient.class)
public class PrimitivesServiceClientImpl implements PrimitivesServiceClient {

    private PrimitivesService service;

    @Reference
    public void setPrimitivesService(PrimitivesService service) {
        this.service = service;
    }

    public boolean negateBooleanForward(boolean flag) {
        return service.negateBoolean(flag);
    }
    
    public boolean[] negateBooleanArrayForward(boolean[] flags) {
        return service.negateBooleanArray(flags);
    }

    public byte negateByteForward(byte b) {
        return service.negateByte(b);
    }
    
    public byte[] negateByteArrayForward(byte[] ba) {
        return service.negateByteArray(ba);
    }

    public short negateShortForward(short s) {
        return service.negateShort(s);
    }
    
    public short[] negateShortArrayForward(short[] s) {
        return service.negateShortArray(s);
    }

    public int negateIntForward(int i) {
        return service.negateInt(i);
    }
    
    public int[] negateIntArrayForward(int[] ia) {
        return service.negateIntArray(ia);
    }

    public long negateLongForward(long l) {
        return service.negateLong(l);
    }
    
    public long[] negateLongArrayForward(long[] la) {
        return service.negateLongArray(la);
    }

    public float negateFloatForward(float f) {
        return service.negateFloat(f);
    }
    
    public float[] negateFloatArrayForward(float[] fa) {
        return service.negateFloatArray(fa);
    }

    public double negateDoubleForward(double d) {
        return service.negateDouble(d);
    }
    
    public double[] negateDoubleArrayForward(double[] da) {
        return service.negateDoubleArray(da);
    }
}
