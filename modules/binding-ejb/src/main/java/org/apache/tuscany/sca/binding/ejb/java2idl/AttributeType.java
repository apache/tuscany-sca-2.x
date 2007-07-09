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

import java.lang.reflect.Method;
import java.rmi.Remote;

/**
 * IDL Attribute
 */
public class AttributeType extends IDLType {

    /**
     * Attribute mode.
     */
    private AttributeMode mode;

    /**
     * Read Method.
     */
    private Method readMethod;
    /**
     * Write Method. This is null for read-only attributes.
     */
    private Method writeMethod;
    /**
     * Read method type.
     */
    private OperationType readOperationType;
    /**
     * Write method type. This is null for read-only attributes.
     */
    private OperationType writeOperationType;

    /**
     * Create an attribute type.
     */
    private AttributeType(String javaName, AttributeMode mode, Method readMethod, Method writeMethod) {
        super(IDLUtil.javaToIDLName(javaName), javaName);
        this.mode = mode;
        // this.cls = readMethod.getReturnType();
        this.readMethod = readMethod;
        this.writeMethod = writeMethod;
        // Only do operation type if the attribute is in a remote interface.
        if (readMethod.getDeclaringClass().isInterface() && Remote.class.isAssignableFrom(readMethod
            .getDeclaringClass())) {
            readOperationType = new OperationType(readMethod);
            if (writeMethod != null) {
                writeOperationType = new OperationType(writeMethod);
            }    
            setIDLName(getIDLName()); // Fixup operation names
        }
    }

    /**
     * Create an attribute type for a read-only attribute.
     */
    AttributeType(String javaName, Method accessor) {
        this(javaName, AttributeMode.ATTR_READONLY, accessor, null); //NOPMD
    }

    /**
     * Create an attribute type for a read-write attribute.
     */
    AttributeType(String javaName, Method accessor, Method mutator) {
        this(javaName, AttributeMode.ATTR_NORMAL, accessor, mutator); //NOPMD
    }

    /**
     * Return the attribute mode.
     */
    public AttributeMode getMode() {
        return mode;
    }

    /**
     * Return the accessor method
     */
    public Method getReadMethod() {
        return readMethod;
    }

    /**
     * Return the mutator method
     */
    public Method getWriteMethod() {
        return writeMethod;
    }

    /**
     * Return the accessor operation type
     */
    public OperationType getReadOperationType() {
        return readOperationType;
    }

    /**
     * Return the mutator operation type
     */
    public OperationType getWriteOperationType() {
        return writeOperationType;
    }

    /**
     * Set the unqualified IDL name. This also sets the names of the associated
     * operations.
     */
    void setIDLName(String idlName) {
        super.setIDLName(idlName);
        String name = idlName;
        // If the first char is an uppercase letter and the second char is not
        // an uppercase letter, then convert the first char to lowercase.
        char ch0 = name.charAt(0);
        if (Character.isUpperCase(ch0) && (name.length() <= 1 || (!Character.isUpperCase(name.charAt(1))))) {
            name = Character.toLowerCase(ch0) + name.substring(1);
        }
        if (readOperationType != null) {
            readOperationType.setIDLName("_get_" + name);
        }    
        if (writeOperationType != null) {
            writeOperationType.setIDLName("_set_" + name);
        }    
    }
}
