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

package org.apache.tuscany.sca.interfacedef;

/**
 * This interface represents the mapping between WSDL faults and Java exceptions
 * 
 * @version $Rev$ $Date$
 */
@SuppressWarnings("unchecked")
public interface FaultExceptionMapper {
    /**
     * Introspect an exception class to find out the fault data type following the WSDL2Java
     * mapping rules. The result will be populated into the logical type of the exception data
     * type
     * 
     * @param exceptionDataType The data type representing a java exception class
     * @param operation TODO
     * @param generatingFaultBean If JAXWS Section 3.7 Fault Bean will be generated
     * @return true if the introspection can recognize the exception data type 
     */
    boolean introspectFaultDataType(DataType<DataType> exceptionDataType, Operation operation, boolean generatingFaultBean);
    
    /**
     * Create a java exception to wrap the fault data
     * 
     * @param exceptionType The DataType for the exception
     * @param message message for the exception
     * @param faultInfo The fault data
     * @param cause of the exception
     * @param operation TODO
     * @return An instance of java exception to represent the fault
     */
    Throwable wrapFaultInfo(DataType<DataType> exceptionType, String message, Object faultInfo, Throwable cause, Operation operation);

    /**
     * Retrieve the fault info from a java exception
     * 
     * @param exception The java exception that represents the fault data
     * @param faultBeanClass
     * @param operation TODO
     * @return The fault data
     */
    Object getFaultInfo(Throwable exception, Class<?> faultBeanClass, Operation operation);
}
