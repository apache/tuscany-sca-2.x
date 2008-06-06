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

import org.apache.tuscany.sca.itest.databindings.jaxb.PrimitivesLocalService;
import org.apache.tuscany.sca.itest.databindings.jaxb.PrimitivesService;
import org.osoa.sca.annotations.Service;

/**
 * An implementation of PrimitivesService.
 * This implementation provides both a local and a remotable service.
 */
@Service(interfaces={PrimitivesService.class, PrimitivesLocalService.class})
public class PrimitivesServiceImpl implements PrimitivesService, PrimitivesLocalService {

    public boolean negateBoolean(boolean flag) {
        return !flag;
    }

    public boolean[] negateBooleanArray(boolean[] flags) {
        boolean[] resp = new boolean[flags.length];
        
        for(int i = 0; i < flags.length; ++i) {
            resp[i] = !flags[i];
        }
        return resp;
    }
    
    public boolean[] identityBooleanArray(boolean[] flags) {
        return flags;
    }
    
    public byte negateByte(byte b) {
        return (byte)-b;
    }

    public byte[] negateByteArray(byte[] ba) {
        byte[] resp = new byte[ba.length];
        
        for(int i = 0; i < ba.length; ++i) {
            resp[i] = (byte)-ba[i];
        }
        return resp;
    }
    
    public byte[] identityByteArray(byte[] ba) {
        return ba;
    }
    
    public short negateShort(short s) {
        return (short)-s;
    }

    public short[] negateShortArray(short[] s) {
        short[] resp = new short[s.length];
        
        for(int i = 0; i < s.length; ++i) {
            resp[i] = (short)-s[i];
        }
        return resp;
    }
    
    public short[] identityShortArray(short[] sa) {
        return sa;
    }

    public int negateInt(int i) {
        return -i;
    }

    public int[] negateIntArray(int[] ia) {
        int[] resp = new int[ia.length];
        
        for(int i = 0; i < ia.length; ++i) {
            resp[i] = -ia[i];
        }
        return resp;
    }

    public int[] identityIntArray(int[] ia) {
        return ia;
    }
    
    public long negateLong(long l) {
        return -l;
    }

    public long[] negateLongArray(long[] la) {
        long[] resp = new long[la.length];
        
        for(int i = 0; i < la.length; ++i) {
            resp[i] = -la[i];
        }
        return resp;
    }
    
    public long[] identityLongArray(long[] la) {
        return la;
    }
    
    public float negateFloat(float f) {
        return -f;
    }

    public float[] negateFloatArray(float[] fa) {
        float[] resp = new float[fa.length];
        
        for(int i = 0; i < fa.length; ++i) {
            resp[i] = -fa[i];
        }
        return resp;
    }
    
    public float[] identityFloatArray(float[] fa) {
        return fa;
    }
    
    public double negateDouble(double d) {
        return -d;
    }

    public double[] negateDoubleArray(double[] da) {
        double[] resp = new double[da.length];
        
        for(int i = 0; i < da.length; ++i) {
            resp[i] = -da[i];
        }
        return resp;
    }
    
    public double[] identityDoubleArray(double[] da) {
        return da;
    }
}
