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

import org.omg.CORBA.ParameterMode;

/**
 * IDL Parameter
 */
public class ParameterType extends IDLType {

    /**
     * Java type of parameter.
     */
    private Class javaClass;
    /**
     * IDL type name of parameter type.
     */
    private String typeIDLName;

    ParameterType(String javaName, Class cls) {
        super(javaName);
        this.javaClass = cls;
        this.typeIDLName = IDLUtil.getTypeIDLName(cls);
    }

    /**
     * Return the attribute mode.
     */
    public ParameterMode getMode() {
        return ParameterMode.PARAM_IN;
    }

    /**
     * Return the Java type.
     */
    public Class getJavaClass() {
        return javaClass;
    }

    /**
     * Return the IDL type name of the parameter type.
     */
    public String getTypeIDLName() {
        return typeIDLName;
    }
}
