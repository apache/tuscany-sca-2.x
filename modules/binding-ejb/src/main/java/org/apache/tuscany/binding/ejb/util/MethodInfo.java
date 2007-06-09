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
package org.apache.tuscany.binding.ejb.util;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * MetaData for a java method
 */
public class MethodInfo implements Serializable {

    /** Automatically generated javadoc for: serialVersionUID */
    private static final long serialVersionUID = -5557260979514687514L;
    private String name;
    private String returnType;
    private String[] parameterTypes;
    private String[] exceptionTypes;

    private String IDLName;

    /**
     * Type Signature Java Type -------------- --------- Z boolean B byte C char
     * S short I int J long F float D double L fully-qualified-class ;
     * fully-qualified-class [ type type[] ( arg-types ) ret-type method type
     */
    private final static Map signatures = new HashMap();
    static {
        signatures.put("Z", boolean.class);
        signatures.put("B", byte.class);
        signatures.put("C", char.class);
        signatures.put("S", short.class);
        signatures.put("I", int.class);
        signatures.put("J", long.class);
        signatures.put("F", float.class);
        signatures.put("D", double.class);
        signatures.put("V", void.class);
    }

    public MethodInfo(Method method) {
        this.name = method.getName();
        // this.declaringClass = method.getDeclaringClass().getName();
        this.returnType = method.getReturnType().getName();
        Class[] types = method.getParameterTypes();
        this.parameterTypes = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            this.parameterTypes[i] = types[i].getName();
        }
        types = method.getExceptionTypes();
        this.exceptionTypes = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            this.exceptionTypes[i] = types[i].getName();
        }
        IDLName = this.name;
    }

    protected MethodInfo(String name, String returnType, String[] parameterTypes, String[] exceptionTypes) {
        this.name = name;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.exceptionTypes = exceptionTypes;
        this.IDLName = name;
    }

    /**
     * Parse the class name from the internal signature Sample signatures: int
     * ---> I; int[] ---> [I Object ---> java/lang/Object Object[] --->
     * [Ljava/lang/Object;
     * 
     * @param value
     * @return
     */
    private static String getName(String signature) {
        String name = signature;
        // Remove leading ARRAY ([) signatures
        int index = name.lastIndexOf('[');
        if (index != -1)
            name = name.substring(index + 1);

        // Remove L<...>;
        if (name.charAt(0) == 'L' && name.charAt(name.length() - 1) == ';')
            name = name.substring(1, name.length() - 1);

        // Primitive types
        Class primitiveClass = (Class)signatures.get(name);
        if (primitiveClass != null) {
            name = primitiveClass.getName();
        }

        for (int i = 0; i < index + 1; i++) {
            name = name + "[]";
        }
        return name;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @return
     */
    public String[] getParameterTypes() {
        return parameterTypes;
    }

    /**
     * @return
     */
    public String getReturnType() {
        return returnType;
    }

    /**
     * @return
     */
    public String[] getExceptionTypes() {
        return exceptionTypes;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getName(returnType)).append(" ").append(name).append("(");
        for (int j = 0; j < parameterTypes.length; j++) {
            sb.append(getName(parameterTypes[j])).append(" ").append("arg" + j);
            if (j < (parameterTypes.length - 1))
                sb.append(", ");
        }
        sb.append(")");
        if (exceptionTypes.length > 0) {
            sb.append(" throws ");
            for (int k = 0; k < exceptionTypes.length; k++) {
                sb.append(exceptionTypes[k]);
                if (k < (exceptionTypes.length - 1))
                    sb.append(", ");
            }
        }
        sb.append(";");
        return sb.toString();
    }

    /**
     * @return Returns the iDLName.
     */
    public String getIDLName() {
        return IDLName;
    }

    /**
     * @param name The iDLName to set.
     */
    public void setIDLName(String name) {
        IDLName = name;
    }
}
