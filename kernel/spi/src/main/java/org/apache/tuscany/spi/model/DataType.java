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
package org.apache.tuscany.spi.model;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Representation of the type of data associated with an operation. Data is represented in two forms: the physical form
 * used by the runtime and a logical form used by the assembly. The physical form is a Java Type because the runtime is
 * written in Java. This may be the same form used by the application but it may not; for example, an application that
 * is performing stream processing may want a physical form such as an {@link java.io.InputStream InputStream} to
 * semantially operate on application data such as a purchase order. The logical description is that used by the
 * assembly model and is an identifier into some well-known type space; examples may be a Java type represented by its
 * Class or an XML type represented by its QName. Every data type may also contain metadata describing the expected
 * data; for example, it could specify a preferred data binding technology or the size of a typical instance.
 *
 * @version $Rev$ $Date$
 */
public class DataType<L> extends ModelObject implements Cloneable {
    private String dataBinding;

    private final Type physical;

    private L logical;

    private Map<String, Object> metadata = new HashMap<String, Object>();

    private Operation operation;

    /**
     * Construct a data type specifying the physical and logical types.
     *
     * @param physical the physical class used by the runtime
     * @param logical  the logical type
     * @see #getLogical()
     */
    public DataType(Type physical, L logical) {
        this.physical = physical;
        this.logical = logical;
        if (physical instanceof Class) {
            this.dataBinding = ((Class) physical).getName();
        }
    }

    public DataType(String dataBinding, Type physical, L logical) {
        this.dataBinding = dataBinding;
        this.physical = physical;
        this.logical = logical;
    }

    /**
     * Returns the physical type used by the runtime.
     *
     * @return the physical type used by the runtime
     */
    public Type getPhysical() {
        return physical;
    }

    /**
     * Returns the logical identifier used by the assembly. The type of this value identifies the logical type system in
     * use. Known values are: <ul> <li>a java.lang.reflect.Type identifies a Java type by name and ClassLoader; this
     * includes Java Classes as they are specializations of Type</li> <li>a javax.xml.namespace.QName identifies an XML
     * type by local name and namespace</li> </ul>
     *
     * @return the logical type name
     */
    public L getLogical() {
        return logical;
    }

    /**
     * Returns all metadata about this type.
     *
     * @return all metadata about this type
     */
    public Map<String, ?> getMetadata() {
        return metadata;
    }

    /**
     * Returns the specified metadata item or null if not present.
     *
     * @param name the name of the metadata item
     * @return the value, or null if not present
     */
    public Object getMetadata(String name) {
        return metadata.get(name);
    }

    /**
     * Sets the specified metadata value. A null value undefines it.
     *
     * @param name  the name of the metadata item
     * @param value the value, or null to undefine
     * @return the old value for the item, or null if not present
     */
    public Object setMetadata(String name, Object value) {
        if (value == null) {
            return metadata.remove(name);
        } else {
            return metadata.put(name, value);
        }
    }

    public String getDataBinding() {
        if (dataBinding == null) {
            // databinding is not set at the DataType level, check the operation
            Operation<?> operation = (Operation<?>) getOperation();
            if (operation != null) {
                return operation.getDataBinding();
            }
        }
        return dataBinding;
    }

    /**
     * @param dataBinding the dataBinding to set
     */
    public void setDataBinding(String dataBinding) {
        this.dataBinding = dataBinding;
    }

    public int hashCode() {
        int result;
        result = dataBinding != null ? dataBinding.hashCode() : 0;
        result = 29 * result + (physical != null ? physical.hashCode() : 0);
        result = 29 * result + (logical != null ? logical.hashCode() : 0);
        // Commented the following line out since it causes infinite loop from Operation.hashCode() 
        // if the metadata map contains the Operation
        // result = 29 * result + (metadata != null ? metadata.hashCode() : 0);
        return result;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final DataType dataType = (DataType) o;

        if (dataBinding != null ? !dataBinding.equals(dataType.dataBinding) : dataType.dataBinding != null) {
            return false;
        }
        if (logical != null ? !logical.equals(dataType.logical) : dataType.logical != null) {
            return false;
        }
        return !(physical != null ? !physical.equals(dataType.physical) : dataType.physical != null);

    }

//    @SuppressWarnings("unchecked")
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//
//        final DataType dataType = (DataType) o;
//
//        if (logical instanceof Class && dataType.logical instanceof Class) {
//            Class<?> logicalClass = (Class<?>) logical;
//            Class<?> targetLogicalClass = (Class<?>) dataType.logical;
//            if (!logicalClass.isAssignableFrom(targetLogicalClass)) {
//                return false;
//            }
//        } else {
//            if (logical != null ? !logical.equals(dataType.logical) : dataType.logical != null) {
//                return false;
//            }
//        }
//        if (physical instanceof Class && dataType.physical instanceof Class) {
//            Class<?> physicalClass = (Class<?>) physical;
//            Class<?> physicalTargetClass = (Class<?>) dataType.physical;
//            if (dataBinding != null
//                && dataType.dataBinding != null
//                && dataBinding.equals(physicalClass.getName())
//                && dataType.dataBinding.equals(physicalTargetClass.getName())) {
//                return physicalClass.isAssignableFrom(physicalTargetClass);
//            }
//            if (!physicalClass.isAssignableFrom(physicalTargetClass)) {
//                return false;
//            }
//            return !(dataBinding != null ? !dataBinding.equals(dataType.dataBinding) : dataType.dataBinding != null);
//
//
//        }
//
//        if (dataBinding != null ? !dataBinding.equals(dataType.dataBinding) : dataType.dataBinding != null) {
//            return false;
//        }
//
//        return !(physical != null ? !physical.equals(dataType.physical) : dataType.physical != null);
//    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(physical).append(" ").append(dataBinding).append(" ").append(logical);
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object clone() throws CloneNotSupportedException {
        DataType<L> copy = (DataType<L>) super.clone();
        assert this.metadata instanceof HashMap;
        copy.metadata = (HashMap<String, Object>) ((HashMap) this.metadata).clone();
        return copy;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    /**
     * @param logical the logical to set
     */
    public void setLogical(L logical) {
        this.logical = logical;
    }

}
