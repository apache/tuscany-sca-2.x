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
package org.apache.tuscany.sca.databinding;

import java.util.Map;

import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;

/**
 * Context for data transformation
 *
 * @version $Rev$ $Date$
 */
public interface TransformationContext {

    /**
     * @return
     */
    Operation getSourceOperation();

    /**
     * @param sourceOperation
     */
    void setSourceOperation(Operation sourceOperation);

    /**
     * @return
     */
    Operation getTargetOperation();

    /**
     * @param targetOperation
     */
    void setTargetOperation(Operation targetOperation);

    /**
     * Get the source data type
     * 
     * @return
     */
    DataType getSourceDataType();

    /**
     * Get the target data type
     * 
     * @return
     */
    DataType getTargetDataType();

    /**
     * Set the source data type
     * 
     * @param sourceDataType
     */
    void setSourceDataType(DataType sourceDataType);

    /**
     * Set the target data type
     * 
     * @param targetDataType
     */
    void setTargetDataType(DataType targetDataType);

    /**
     * Get the ClassLoader
     * 
     * @return Returns the ClassLoader
     */
    ClassLoader getClassLoader();

    /**
     * Get a map of metadata
     * 
     * @return Returns a map of the metadata
     */
    Map<String, Object> getMetadata();

}
