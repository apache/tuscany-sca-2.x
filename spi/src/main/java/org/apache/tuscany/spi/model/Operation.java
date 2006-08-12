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

import java.util.List;

/**
 * Represents an operation that is part of a service contract.
 *
 * @version $Rev$ $Date$
 */
public class Operation {
    private final String name;
    private final DataType<?,?> returnType;
    private final List<DataType<?,?>> parameterTypes;
    private final List<DataType<? extends Exception, ?>> faultTypes;
    private final boolean nonBlocking;

    /**
     * Construct an operation specifying all characteristics.
     *
     * @param name the name of the operation
     * @param returnType the data type returned by the operation
     * @param parameterTypes the data types of parameters passed to the operation
     * @param faultTypes the data type of faults raised by the operation
     * @param nonBlocking true if the operation is non-blocking
     */
    public Operation(String name,
                     DataType<?, ?> returnType,
                     List<DataType<?, ?>> parameterTypes,
                     List<DataType<? extends Exception, ?>> faultTypes,
                     boolean nonBlocking) {
        this.name = name;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.faultTypes = faultTypes;
        this.nonBlocking = nonBlocking;
    }

    /**
     * Returns the name of the operation.
     * @return the name of the operation
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the data type returned by the operation.
     * @return the data type returned by the operation
     */
    public DataType<?, ?> getReturnType() {
        return returnType;
    }

    /**
     * Returns the data types of the parameters passed to the operation.
     * @return the data types of the parameters passed to the operation
     */
    public List<DataType<?, ?>> getParameterTypes() {
        return parameterTypes;
    }

    /**
     * Returns the data types of the faults raised by the operation.
     * @return the data types of the faults raised by the operation
     */
    public List<DataType<? extends Exception, ?>> getFaultTypes() {
        return faultTypes;
    }

    /**
     * Returns true if the operation is non-blocking.
     * A non-blocking operation may not have completed execution at the time an invocation of the operation returns.
     *
     * @return true if the operation is non-blocking
     */
    public boolean isNonBlocking() {
        return nonBlocking;
    }
}
