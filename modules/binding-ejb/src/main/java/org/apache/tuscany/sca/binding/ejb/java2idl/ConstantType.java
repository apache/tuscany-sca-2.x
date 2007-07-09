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

import org.omg.CORBA.Any;

/**
 * IDL Constant
 */
public class ConstantType extends IDLType {

    /**
     * Java type of constant.
     */
    private final Class type;
    /**
     * The value of the constant.
     */
    private final Object value;

    ConstantType(String javaName, Class type, Object value) {
        super(javaName);
        if (type == void.class || (!type.isPrimitive()) && type != java.lang.String.class) {
            throw new IllegalArgumentException("Illegal type for constant: " + type.getName());
        }
        this.type = type;
        this.value = value;
    }

    /**
     * Return my Java type.
     */
    public Class getType() {
        return type;
    }

    /**
     * Return my value.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Insert the constant value into the argument Any.
     */
    public void insertValue(Any any) {
        if (type == String.class) {
            any.insert_wstring((String)value); // 1.3.5.10 Map to wstring
        } else {
            IDLUtil.insertAnyPrimitive(any, value);
        }
    }
}
