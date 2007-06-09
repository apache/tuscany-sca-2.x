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

/**
 * IDL types.
 */
public class ClassType extends IDLType {

    /**
     * java class.
     */
    protected Class javaClass;

    private static String getJavaName(Class cls) {
        if (cls == null)
            throw new IllegalArgumentException("Class cannot be null.");
        String s = cls.getName();
        int index = s.lastIndexOf('.');
        if (index == -1)
            return s;
        else
            return s.substring(index + 1);
    }

    public ClassType(Class cls, String idlName, String javaName) {
        super(idlName, javaName);
        this.javaClass = cls;
    }

    public ClassType(Class cls, String javaName) {
        this(cls, IDLUtil.javaToIDLName(javaName), javaName);
    }

    public ClassType(Class cls) {
        this(cls, getJavaName(cls));
    }

    /**
     * Return java class.
     */
    public Class getJavaClass() {
        return javaClass;
    }
}
