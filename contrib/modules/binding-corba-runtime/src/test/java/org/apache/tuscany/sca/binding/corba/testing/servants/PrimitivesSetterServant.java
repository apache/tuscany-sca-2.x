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

package org.apache.tuscany.sca.binding.corba.testing.servants;

import org.apache.tuscany.sca.binding.corba.testing.generated.RemoteObject;
import org.apache.tuscany.sca.binding.corba.testing.generated._PrimitivesSetterImplBase;

public class PrimitivesSetterServant extends _PrimitivesSetterImplBase {

    private static final long serialVersionUID = 1L;

    public boolean setBoolean(boolean arg) {
        return arg;
    }

    public char setChar(char arg) {
        return arg;
    }

    public double setDouble(double arg) {
        return arg;
    }

    public float setFloat(float arg) {
        return arg;
    }

    public int setLong(int arg) {
        return arg;
    }

    public long setLongLong(long arg) {
        return arg;
    }

    public byte setOctet(byte arg) {
        return arg;
    }

    public RemoteObject setRemoteObject(RemoteObject obj) {
        return obj;
    }

    public short setShort(short arg) {
        return arg;
    }

    public String setString(String arg) {
        return arg;
    }

    public int setUnsignedLong(int arg) {
        return arg;
    }

    public long setUnsignedLongLong(long arg) {
        return arg;
    }

    public short setUnsignedShort(short arg) {
        return arg;
    }

    public char setWchar(char arg) {
        return arg;
    }

    public String setWstring(String arg) {
        return arg;
    }

}
