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

package org.apache.tuscany.spi.databinding;

import org.apache.tuscany.interfacedef.DataType;

/**
 * ExceptionHandler provides databinding-specific logic for exception handling
 * 
 * @version $Rev$ $Date$
 */
public interface ExceptionHandler {
    /**
     * Create an exception to wrap the fault data
     * 
     * @param exceptionType The DataType for the exception
     * @param message The error message
     * @param faultInfo The databinding-specific fault data
     * @param cause The protocol-specific error
     * @return An instance of java exception to represent the fault
     */
    Exception createException(DataType<DataType> exceptionType, String message, Object faultInfo, Throwable cause);

    /**
     * Retrieve the fault info from a java exception
     * 
     * @param exception The databinding-specific java exception that represents
     *            the fault data
     * @return The databinding-specific fault data
     */
    Object getFaultInfo(Exception exception);

    /**
     * Introspect an exception class to figure out the fault data type
     * 
     * @param exceptionDataType The exception class
     * @return The data type for the fault
     */
    DataType<?> getFaultType(DataType exceptionDataType);
}
