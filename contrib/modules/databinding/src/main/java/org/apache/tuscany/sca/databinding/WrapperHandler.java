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

import java.util.List;

import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;

/**
 * A contract for transformers to deal with wrapping/unwrapping for WSDL wrapper style operations
 *
 * @version $Rev$ $Date$
 */
public interface WrapperHandler<T> {
    /**
     * Create a wrapper element
     * @param operation The operation
     * @param input Is it for input or output
     * @return An object representing the wrapper element
     */
    T create(Operation operation, boolean input);

    /**
     * Get the data type for the wrapper
     * @param operation The operation
     * @param input Is it for input or output?
     * @return The data type of the wrapper, null if it's not a wrapper type
     */
    DataType getWrapperType(Operation operation, boolean input);

    /**
     * Check if the given data is an instance of the wrapper
     * @param wrapper
     * @param operation The operation
     * @param input Input or output
     * @return
     */
    boolean isInstance(Object wrapper, Operation operation, boolean input);

    /**
     * Populate the wrapper element with the child objects
     * @param wrapper The wrapper object
     * @param childObjects The child objects
     * @param operation The operation
     * @param input Is it for input or output
     */
    public void setChildren(T wrapper,
                            Object[] childObjects,
                            Operation operation,
                            boolean input);

    /**
     * Get a list of child elements from the wrapper
     * @param wrapper The wrapper object
     * @param operation The operation
     * @param input Is it for input or output
     * @return child elements under the wrapper
     */
    List getChildren(T wrapper, Operation operation, boolean input);
}
