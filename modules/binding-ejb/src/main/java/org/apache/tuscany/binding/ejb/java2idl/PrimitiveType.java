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
package org.apache.tuscany.binding.ejb.java2idl;

import java.util.HashMap;
import java.util.Map;

/**
 * Type class for primitive types.
 */
public class PrimitiveType extends ClassType {

    public final static PrimitiveType voidType = new PrimitiveType(void.class, "void", "void");
    public final static PrimitiveType booleanType = new PrimitiveType(boolean.class, "boolean", "boolean");
    public final static PrimitiveType charType = new PrimitiveType(char.class, "wchar", "char");
    public final static PrimitiveType byteType = new PrimitiveType(byte.class, "octet", "byte");
    public final static PrimitiveType shortType = new PrimitiveType(short.class, "short", "short");
    public final static PrimitiveType intType = new PrimitiveType(int.class, "long", "int");
    public final static PrimitiveType longType = new PrimitiveType(long.class, "long_long", "long");
    public final static PrimitiveType floatType = new PrimitiveType(float.class, "float", "float");
    public final static PrimitiveType doubleType = new PrimitiveType(double.class, "double", "double");

    private final static Map types = new HashMap();
    static {
        types.put(void.class, voidType);
        types.put(boolean.class, booleanType);
        types.put(byte.class, byteType);
        types.put(char.class, charType);
        types.put(short.class, shortType);
        types.put(int.class, intType);
        types.put(long.class, longType);
        types.put(float.class, floatType);
        types.put(double.class, doubleType);
    }

    private PrimitiveType(Class cls, String idlName, String javaName) {
        super(cls, idlName, javaName);
    }

    /**
     * Get a singleton instance representing one of the peimitive types.
     */
    public final static PrimitiveType getPrimitiveType(Class cls) {
        PrimitiveType type = (PrimitiveType)types.get(cls);
        if (type == null)
            throw new IllegalArgumentException(cls + " is not a primitive type");
        return type;
    }

}
