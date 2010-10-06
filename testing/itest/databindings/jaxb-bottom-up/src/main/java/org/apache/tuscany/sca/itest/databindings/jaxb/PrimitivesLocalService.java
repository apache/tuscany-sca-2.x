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


/**
 * The interface for PrimitivesLocalService.
 */
public interface PrimitivesLocalService {
    boolean negateBoolean(boolean flag);
    boolean[] negateBooleanArray(boolean[] flags);
    boolean[] identityBooleanArray(boolean[] flags);
    byte negateByte(byte b);
    byte[] negateByteArray(byte[] ba);
    byte[] identityByteArray(byte[] ba);
    short negateShort(short s);
    short[] negateShortArray(short[] s);
    short[] identityShortArray(short[] sa);
    int negateInt(int s);
    int[] negateIntArray(int[] s);
    int[] identityIntArray(int[] ia);
    long negateLong(long l);
    long[] negateLongArray(long[] la);
    long[] identityLongArray(long[] la);
    float negateFloat(float f);
    float[] negateFloatArray(float[] fa);
    float[] identityFloatArray(float[] fa);
    double negateDouble(double d);
    double[] negateDoubleArray(double[] da);
    double[] identityDoubleArray(double[] da);
}
