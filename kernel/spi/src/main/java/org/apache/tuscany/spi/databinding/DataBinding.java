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

import org.apache.tuscany.spi.model.DataType;

/**
 * DataBinding represents a data representation, for example, SDO, JAXB and AXIOM
 */
public interface DataBinding {
    /**
     * The name of a databinding should be case-insensitive and unique
     * 
     * @return The name of the databinding
     */
    String getName();

    /**
     * Introspect a java class or interface to create a DataType model
     * 
     * @param javaType The java class or interface to be introspected
     * @return The DataType or null if the java type is not supported by this databinding
     */
    DataType introspect(Class<?> javaType);

    /**
     * Introspect the data to figure out the corresponding data type
     * 
     * @param value The object to be checked
     * @return The DataType or null if the java type is not supported by this databinding
     */
    DataType introspect(Object value);

    /**
     * Provide a WrapperHandler for this databinding
     * @return A wrapper handler which can handle wrapping/wrapping for this databinding
     */
    WrapperHandler getWrapperHandler();

    /**
     * make a copy of the input object
     * @param source object to copy 
     * @return copy of the object passed in as argument
     */
    Object copy(Object object);
}
