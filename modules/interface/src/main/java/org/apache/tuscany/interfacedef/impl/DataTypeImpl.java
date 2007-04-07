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
package org.apache.tuscany.interfacedef.impl;

import org.apache.tuscany.interfacedef.DataType;

/**
 * Representation of the type of data associated with an operation. Data is
 * represented in two forms: the physical form used by the runtime and a logical
 * form used by the assembly. The physical form is a Java Type because the
 * runtime is written in Java. This may be the same form used by the application
 * but it may not; for example, an application that is performing stream
 * processing may want a physical form such as an
 * {@link java.io.InputStream InputStream} to semantially operate on application
 * data such as a purchase order. The logical description is that used by the
 * assembly model and is an identifier into some well-known type space; examples
 * may be a Java type represented by its Class or an XML type represented by its
 * QName. Every data type may also contain metadata describing the expected
 * data; for example, it could specify a preferred data binding technology or
 * the size of a typical instance.
 * 
 * @version $Rev$ $Date$
 */
public class DataTypeImpl<L> implements DataType<L> {
    private boolean unresolved = true;
    private String dataBinding;
    private Class physical;
    private L logical;

    /**
     * Construct a data type specifying the physical and logical types.
     * 
     * @param physical the physical class used by the runtime
     * @param logical the logical type
     * @see #getLogical()
     */
    public DataTypeImpl(Class physical, L logical) {
        this.physical = physical;
        this.logical = logical;
    }

    public DataTypeImpl(String dataBinding, Class physical, L logical) {
        this.dataBinding = dataBinding;
        this.physical = physical;
        this.logical = logical;
    }

    /**
     * Returns the physical type used by the runtime.
     * 
     * @return the physical type used by the runtime
     */
    public Class getPhysical() {
        return physical;
    }

    /**
     * Returns the logical identifier used by the assembly. The type of this
     * value identifies the logical type system in use. Known values are:
     * <ul>
     * <li>a java.lang.reflect.Type identifies a Java type by name and
     * ClassLoader; this includes Java Classes as they are specializations of
     * Type</li>
     * <li>a javax.xml.namespace.QName identifies an XML type by local name and
     * namespace</li>
     * </ul>
     * 
     * @return the logical type name
     */
    public L getLogical() {
        return logical;
    }

    public String getDataBinding() {
        return dataBinding;
    }

    /**
     * @param dataBinding the dataBinding to set
     */
    public void setDataBinding(String dataBinding) {
        this.dataBinding = dataBinding;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(physical).append(" ").append(dataBinding).append(" ").append(logical);
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object clone() throws CloneNotSupportedException {
        DataTypeImpl copy = (DataTypeImpl)super.clone();
        return copy;
    }

    /**
     * @param logical the logical to set
     */
    public void setLogical(L logical) {
        this.logical = logical;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((dataBinding == null) ? 0 : dataBinding.hashCode());
        result = PRIME * result + ((logical == null) ? 0 : logical.hashCode());
        result = PRIME * result + ((physical == null) ? 0 : physical.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataTypeImpl other = (DataTypeImpl)obj;
        if (dataBinding == null) {
            if (other.dataBinding != null) {
                return false;
            }
        } else if (!dataBinding.equals(other.dataBinding)) {
            return false;
        }
        if (logical == null) {
            if (other.logical != null) {
                return false;
            }
        } else if (!logical.equals(other.logical)) {
            return false;
        }
        if (physical == null) {
            if (other.physical != null) {
                return false;
            }
        } else if (!physical.equals(other.physical)) {
            return false;
        }
        return true;
    }

    /**
     * @return the unresolved
     */
    public boolean isUnresolved() {
        return unresolved;
    }

    /**
     * @param unresolved the unresolved to set
     */
    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }

    /**
     * @param physical the physical to set
     */
    public void setPhysical(Class physical) {
        this.physical = physical;
    }

}
