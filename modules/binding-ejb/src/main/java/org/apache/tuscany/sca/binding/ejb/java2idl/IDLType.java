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
 * Base class for all IDL types.
 */
public abstract class IDLType {

    /**
     * Unqualified IDL name.
     */
    protected String idlName;
    /**
     * Unqualified java name.
     */
    protected final String javaName;

    public IDLType(String idlName, String javaName) {
        this.idlName = idlName;
        this.javaName = javaName;
    }

    public IDLType(String javaName) {
        this(IDLUtil.javaToIDLName(javaName), javaName);
    }

    /**
     * Return my unqualified IDL name.
     */
    public String getIDLName() {
        return idlName;
    }

    /**
     * Return unqualified java name.
     */
    public String getJavaName() {
        return javaName;
    }

    /**
     * Set unqualified IDL name.
     */
    void setIDLName(String idlName) {
        this.idlName = idlName;
    }

}
