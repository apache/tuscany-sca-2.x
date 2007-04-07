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

package org.apache.tuscany.idl.util;

import javax.xml.namespace.QName;

/**
 * An abstraction of XML schema types
 */
public class TypeInfo {
    private QName name;

    private boolean isSimpleType;

    private TypeInfo baseType;

    /**
     * @param name
     * @param isSimpleType
     */
    public TypeInfo(QName name, boolean isSimpleType, TypeInfo baseType) {
        super();
        this.name = name;
        this.isSimpleType = isSimpleType;
        this.baseType = baseType;
    }

    /**
     * @return the isSimpleType
     */
    public boolean isSimpleType() {
        return isSimpleType;
    }

    /**
     * @return the name
     */
    public QName getQName() {
        return name;
    }

    /**
     * @return the baseType
     */
    public TypeInfo getBaseType() {
        return baseType;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Type: ").append(name);
        return sb.toString();
    }

}
