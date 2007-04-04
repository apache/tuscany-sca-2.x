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

import java.util.Map;

import org.apache.tuscany.idl.DataType;

/**
 * This interface will be used as a Tuscany system service to perform data mediations
 * 
 * Mediate the data from one type to the other one
 *
 */
public interface Mediator {

    /**
     * Mediate the data from the source type to the target type
     * @param source The data to be mediated
     * @param sourceDataType Data type for the source data
     * @param targetDataType Data type for the target data
     * @param context 
     * @return
     */
    Object mediate(Object source, DataType sourceDataType, DataType targetDataType, Map<Class<?>, Object> context);
    /**
     * Mediate the source data into the target which is a sink to receive the data
     * @param source The data to be mediated
     * @param target The sink to receive data
     * @param sourceDataType Data type for the source data
     * @param targetDataType Data type for the target data
     */
    void mediate(
            Object source,
            Object target,
            DataType sourceDataType,
            DataType targetDataType,
            Map<Class<?>, Object> context);
    
    /**
     * Get the DataBinding registry
     * @return
     */
    DataBindingRegistry getDataBindingRegistry();
    
    /**
     * Get the Transformer registry
     * @return
     */
    TransformerRegistry getTransformerRegistry();    
}
