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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an operation that is part of a service contract. The type paramter of this operation identifies the
 * logical type system for all data types.
 *
 * @version $Rev$ $Date$
 */
public class Operation<T> {
    protected Map<String, Object> metaData;

    private final String name;

    private final DataType<T> outputType;

    private final DataType<List<DataType<T>>> inputType;

    private final List<DataType<T>> faultTypes;

    private boolean nonBlocking;

    private ServiceContract<T> contract;

    private boolean callback;

    private String dataBinding;

    /**
     * Construct a minimally-specified operation
     *
     * @param name       the name of the operation
     * @param inputType  the data types of parameters passed to the operation
     * @param outputType the data type returned by the operation
     * @param faultTypes the data type of faults raised by the operation
     */
    public Operation(String name,
                     DataType<List<DataType<T>>> inputType,
                     DataType<T> outputType,
                     List<DataType<T>> faultTypes) {
        this(name, inputType, outputType, faultTypes, true, null);
    }

    /**
     * Construct an operation
     *
     * @param name        the name of the operation
     * @param inputType   the data types of parameters passed to the operation
     * @param outputType  the data type returned by the operation
     * @param faultTypes  the data type of faults raised by the operation
     * @param nonBlocking if the operation is non-blocking
     * @param dataBinding the data-binding type required by the operation
     */
    public Operation(final String name,
                     final DataType<List<DataType<T>>> inputType,
                     final DataType<T> outputType,
                     final List<DataType<T>> faultTypes,
                     boolean nonBlocking,
                     String dataBinding) {
        super();
        this.name = name;
        List<DataType<T>> types = Collections.emptyList();
        this.inputType = (inputType != null) ? inputType : new DataType<List<DataType<T>>>(Object[].class, types);
        this.outputType = (outputType != null) ? outputType : new DataType<T>(void.class, null);
        this.faultTypes = (faultTypes == null) ? types : faultTypes;
        this.nonBlocking = nonBlocking;
        this.dataBinding = dataBinding;
        // Register the operation with the types
        for (DataType<?> d : this.inputType.getLogical()) {
            d.setMetadata(Operation.class.getName(), this);
        }
        this.outputType.setMetadata(Operation.class.getName(), this);
        for (DataType<?> d : this.faultTypes) {
            d.setMetadata(Operation.class.getName(), this);
        }
    }

    /**
     * Returns the service contract the operation is part of.
     *
     * @return the service contract the operation is part of.
     */
    public ServiceContract<T> getServiceContract() {
        return contract;
    }

    /**
     * Sets the service contract the operation is part of.
     *
     * @param contract the service contract the operation is part of.
     */
    public void setServiceContract(ServiceContract<T> contract) {
        this.contract = contract;
    }

    /**
     * Returns true if the operation is part of the callback contract.
     *
     * @return true if the operation is part of the callback contract.
     */
    public boolean isCallback() {
        return callback;
    }

    /**
     * Sets whether the operation is part of the callback contract.
     *
     * @param callback whether the operation is part of the callback contract.
     */
    public void setCallback(boolean callback) {
        this.callback = callback;
    }

    /**
     * Returns the name of the operation.
     *
     * @return the name of the operation
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the data type returned by the operation.
     *
     * @return the data type returned by the operation
     */
    public DataType<T> getOutputType() {
        return outputType;
    }

    /**
     * Returns the data types of the parameters passed to the operation.
     * <p/>
     * The inputType's logical type is a list of DataTypes which describes the parameter types
     *
     * @return the data types of the parameters passed to the operation
     */
    public DataType<List<DataType<T>>> getInputType() {
        return inputType;
    }

    /**
     * Returns the data types of the faults raised by the operation.
     *
     * @return the data types of the faults raised by the operation
     */
    public List<DataType<T>> getFaultTypes() {
        if (faultTypes == null) {
            return Collections.emptyList();
        }
        return faultTypes;
    }

    /**
     * Returns true if the operation is non-blocking. A non-blocking operation may not have completed execution at the
     * time an invocation of the operation returns.
     *
     * @return true if the operation is non-blocking
     */
    public boolean isNonBlocking() {
        return nonBlocking;
    }

    /**
     * Returns the data binding type specified for the operation or null.
     *
     * @return the data binding type specified for the operation or null.
     */
    public String getDataBinding() {
        return (dataBinding == null && contract != null) ? contract.getDataBinding() : dataBinding;
    }

    /**
     * Set the databinding for this operation
     *
     * @param dataBinding The databinding
     */
    public void setDataBinding(String dataBinding) {
        this.dataBinding = dataBinding;
    }

    /**
     * Returns a map of metadata key to value mappings for the operation.
     *
     * @return a map of metadata key to value mappings for the operation.
     */
    public Map<String, Object> getMetaData() {
        if (metaData == null) {
            return Collections.emptyMap();
        }
        return metaData;
    }

    /**
     * Adds metadata associated with the operation.
     *
     * @param key the metadata key
     * @param val the metadata value
     */
    public void setMetaData(String key, Object val) {
        if (metaData == null) {
            metaData = new HashMap<String, Object>();
        }
        metaData.put(key, val);
    }

    /**
     * Sets if the operation is non-blocking
     */
    public void setNonBlocking(boolean nonBlocking) {
        this.nonBlocking = nonBlocking;
    }

    public String toString() {
        return name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Operation operation = (Operation) o;

        if (name != null ? !name.equals(operation.name) : operation.name != null) {
            return false;
        }

        // HACK: If the operation is mappable, then the equality test is relaxed
        if (isMappable()) {
            return true;
        }

        if (faultTypes != null ? !faultTypes.equals(operation.faultTypes) : operation.faultTypes != null) {
            return false;
        }

        if (inputType != null ? !inputType.equals(operation.inputType) : operation.inputType != null) {
            return false;
        }
        return !(outputType != null ? !outputType.equals(operation.outputType) : operation.outputType != null);
    }

    public int hashCode() {
        int result;
        result = name != null ? name.hashCode() : 0;
        // HACK:
        if (isMappable()) {
            return result;
        }
        result = 29 * result + (outputType != null ? outputType.hashCode() : 0);
        result = 29 * result + (inputType != null ? inputType.hashCode() : 0);
        result = 29 * result + (faultTypes != null ? faultTypes.hashCode() : 0);
        return result;
    }

    /**
     * Returns true if the operation may be mapped to another target operation through an mediation
     */
    private boolean isMappable() {
        if (contract != null) {
            return contract.isRemotable();
        } else {
            return false;
        }
    }


}
