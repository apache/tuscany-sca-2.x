/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.model.types;

import java.util.List;

import commonj.sdo.Type;

import org.apache.tuscany.model.assembly.AssemblyModelObject;

/**
 * Represents a service operation type.
 */
public interface OperationType extends AssemblyModelObject {

    /**
     * Returns the operation name.
     *
     * @return
     */
    String getName();

    /**
     * Sets the operation name.
     *
     * @param name
     */
    void setName(String name);

    /**
     * Returns the operation input type.
     *
     * @return
     */
    Type getInputType();

    /**
     * Returns the operation output type.
     *
     * @return
     */
    Type getOutputType();

    /**
     * Returns the operation exception types.
     * @return
     */
    List<Type> getExceptionTypes();

}
