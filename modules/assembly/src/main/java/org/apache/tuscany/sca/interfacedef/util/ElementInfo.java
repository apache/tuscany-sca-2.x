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
 * An abstraction of XML schema elements.
 *
 * @version $Rev$ $Date$
 */
public class ElementInfo {
    private final QName name;
    private final TypeInfo type;
    private boolean many = false;
    private boolean nillable = false;

    /**
     * @param name
     * @param type
     */
    public ElementInfo(QName name, TypeInfo type) {
        super();
        this.name = name;
        this.type = type;
    }

    /**
     * @return the name
     */
    public QName getQName() {
        return name;
    }

    /**
     * @return the type
     */
    public TypeInfo getType() {
        return type;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Element: ").append(name).append(" ").append(type);
        return sb.toString();
    }

    public boolean isMany() {
        return many;
    }

    public void setMany(boolean many) {
        this.many = many;
    }

    public boolean isNillable() {
        return nillable;
    }

    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        final ElementInfo other = (ElementInfo)obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        /*
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        */    
        return true;
    }
}
