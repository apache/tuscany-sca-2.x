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
package org.apache.tuscany.sca.interfacedef.impl;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.sca.interfacedef.DataType;

/**
 * Representation of the type of data associated with an operation. Data is
 * represented in two forms: the physical form used by the runtime and a logical
 * form used by the assembly. The physical form is a Java Type because the
 * runtime is written in Java. This may be the same form used by the application
 * but it may not; for example, an application that is performing stream
 * processing may want a physical form such as an
 * {@link java.io.InputStream InputStream} to semantically operate on application
 * data such as a purchase order. The logical description is that used by the
 * assembly model and is an identifier into some well-known type space; examples
 * may be a Java type represented by its Class or an XML type represented by its
 * QName. Every data type may also contain metadata describing the expected
 * data; for example, it could specify a preferred data binding technology or
 * the size of a typical instance.
 * 
 * @version $Rev$ $Date$
 * @tuscany.spi.extension.asclient
 */
public class DataTypeImpl<L> implements DataType<L> {
    private String dataBinding;
    private Class<?> physical;
    private Type genericType;
    private L logical;
    private Map<Class<?>, Object> metaDataMap;

    /**
     * Construct a data type specifying the physical and logical types.
     * 
     * @param physical the physical class used by the runtime
     * @param logical the logical type
     * @see #getLogical()
     */
    public DataTypeImpl(Class<?> physical, L logical) {
        this(null, physical, physical, logical);
    }

    /**
     * @param dataBinding
     * @param physical
     * @param logical
     */
    public DataTypeImpl(String dataBinding, Class<?> physical, L logical) {
        this(dataBinding, physical, physical, logical);
    }

    /**
     * @param dataBinding
     * @param physical
     * @param genericType
     * @param logical
     */
    public DataTypeImpl(String dataBinding, Class<?> physical, Type genericType, L logical) {
        super();
        this.dataBinding = dataBinding;
        this.physical = physical;
        this.genericType = genericType;
        this.logical = logical;
    }

    /**
     * Returns the physical type used by the runtime.
     * 
     * @return the physical type used by the runtime
     */
    public Class<?> getPhysical() {
        return physical;
    }

    /**
     * @param physical the physical to set
     */
    public void setPhysical(Class<?> physical) {
        this.physical = physical;
    }

    /**
     * Get the java generic type
     * @return The java generic type
     */
    public Type getGenericType() {
        return genericType;
    }

    /**
     * Set the java generic type
     * @param genericType
     */
    public void setGenericType(Type genericType) {
        this.genericType = genericType;
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

    /**
     * @param logical the logical to set
     */
    public void setLogical(L logical) {
        this.logical = logical;
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

    @Override
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dataBinding == null) ? 0 : dataBinding.hashCode());
        result = prime * result + ((genericType == null) ? 0 : genericType.hashCode());
        result = prime * result + ((logical == null) ? 0 : logical.hashCode());
        result = prime * result + ((physical == null) ? 0 : physical.hashCode());
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
        final DataTypeImpl other = (DataTypeImpl)obj;
        if (dataBinding == null) {
            if (other.dataBinding != null)
                return false;
        } else if (!dataBinding.equals(other.dataBinding))
            return false;
        if (genericType == null) {
            if (other.genericType != null)
                return false;
        } else if (!genericType.equals(other.genericType))
            return false;
        if (logical == null) {
            if (other.logical != null)
                return false;
        } else if (!logical.equals(other.logical))
            return false;
        if (physical == null) {
            if (other.physical != null)
                return false;
        } else if (!physical.equals(other.physical))
            return false;
        return true;
    }

    public <T> T getMetaData(Class<T> type) {
        return metaDataMap == null ? null : type.cast(metaDataMap.get(type));
    }

    public <T> void setMetaData(Class<T> type, T metaData) {
        if (metaDataMap == null) {
            metaDataMap = new ConcurrentHashMap<Class<?>, Object>();
        }
        metaDataMap.put(type, metaData);
    }
}
