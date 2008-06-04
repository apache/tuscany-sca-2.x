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

package org.apache.tuscany.sca.interfacedef.util;

import javax.xml.namespace.QName;

/**
 * An abstraction of XML schema types
 *
 * @version $Rev$ $Date$
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

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Type: ").append(name);
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TypeInfo other = (TypeInfo)obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
