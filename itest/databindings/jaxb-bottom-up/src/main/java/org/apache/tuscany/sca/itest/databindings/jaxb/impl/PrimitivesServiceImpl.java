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
import org.osoa.sca.annotations.Service;

/**
 * An implementation of PrimitivesService.
 */
@Service(PrimitivesService.class)
public class PrimitivesServiceImpl implements PrimitivesService {

    public boolean negateBoolean(boolean flag) {
        return !flag;
    }

    public boolean[] negateBooleanArray(boolean[] flags) {
        for(int i = 0; i < flags.length; ++i) {
            flags[i] = !flags[i];
        }
        return flags;
    }
    
    public byte negateByte(byte b) {
        return (byte)-b;
    }

    public byte[] negateByteArray(byte[] ba) {
        for(int i = 0; i < ba.length; ++i) {
            ba[i] = (byte)-ba[i];
        }
        return ba;
    }
    
    public short negateShort(short s) {
        return (short)-s;
    }

    public short[] negateShortArray(short[] s) {
        for(int i = 0; i < s.length; ++i) {
            s[i] = (short)-s[i];
        }
        return s;
    }
    
    public int negateInt(int i) {
        return -i;
    }

    public int[] negateIntArray(int[] ia) {
        for(int i = 0; i < ia.length; ++i) {
            ia[i] = -ia[i];
        }
        return ia;
    }
    
    public long negateLong(long l) {
        return -l;
    }

    public long[] negateLongArray(long[] la) {
        for(int i = 0; i < la.length; ++i) {
            la[i] = -la[i];
        }
        return la;
    }
    
    public float negateFloat(float f) {
        return -f;
    }

    public float[] negateFloatArray(float[] fa) {
        for(int i = 0; i < fa.length; ++i) {
            fa[i] = -fa[i];
        }
        return fa;
    }
    
    public double negateDouble(double d) {
        return -d;
    }

    public double[] negateDoubleArray(double[] da) {
        for(int i = 0; i < da.length; ++i) {
            da[i] = -da[i];
        }
        return da;
    }
}
