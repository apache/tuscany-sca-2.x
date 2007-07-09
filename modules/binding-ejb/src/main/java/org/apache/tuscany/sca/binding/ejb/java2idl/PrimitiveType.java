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
package org.apache.tuscany.sca.binding.ejb.java2idl;

import java.util.HashMap;
import java.util.Map;

/**
 * Type class for primitive types.
 */
public final class PrimitiveType extends ClassType {

    public static final PrimitiveType VOID_TYPE = new PrimitiveType(void.class, "void", "void");
    public static final PrimitiveType BOOLEAN_TYPE = new PrimitiveType(boolean.class, "boolean", "boolean");
    public static final PrimitiveType CHAR_TYPE = new PrimitiveType(char.class, "wchar", "char");
    public static final PrimitiveType BYTE_TYPE = new PrimitiveType(byte.class, "octet", "byte");
    public static final PrimitiveType SHORT_TYPE = new PrimitiveType(short.class, "short", "short");
    public static final PrimitiveType INT_TYPE = new PrimitiveType(int.class, "long", "int");
    public static final PrimitiveType LONG_TYPE = new PrimitiveType(long.class, "long_long", "long");
    public static final PrimitiveType FLOAT_TYPE = new PrimitiveType(float.class, "float", "float");
    public static final PrimitiveType DOUBLE_TYPE = new PrimitiveType(double.class, "double", "double");

    private static final Map<Class, PrimitiveType> TYPES = new HashMap<Class, PrimitiveType>();
    static {
        TYPES.put(void.class, VOID_TYPE);
        TYPES.put(boolean.class, BOOLEAN_TYPE);
        TYPES.put(byte.class, BYTE_TYPE);
        TYPES.put(char.class, CHAR_TYPE);
        TYPES.put(short.class, SHORT_TYPE);
        TYPES.put(int.class, INT_TYPE);
        TYPES.put(long.class, LONG_TYPE);
        TYPES.put(float.class, FLOAT_TYPE);
        TYPES.put(double.class, DOUBLE_TYPE);
    }

    private PrimitiveType(Class cls, String idlName, String javaName) {
        super(cls, idlName, javaName);
    }

    /**
     * Get a singleton instance representing one of the peimitive types.
     */
    public static PrimitiveType getPrimitiveType(final Class cls) {
        final PrimitiveType type = TYPES.get(cls);
        if (type == null) {
            throw new IllegalArgumentException(cls + " is not a primitive type");
        }    
        return type;
    }

}
