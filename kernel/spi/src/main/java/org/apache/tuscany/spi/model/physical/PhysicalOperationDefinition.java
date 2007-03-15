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
package org.apache.tuscany.spi.model.physical;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.spi.model.ModelObject;

/**
 * Represents an operation.
 *
 * @version $Revision$ $Date$
 *          <p/>
 *          TODO Discuss with Jeremy/Jim on how to model MEPs, INOUT parameters, faults etc
 */
public class PhysicalOperationDefinition extends ModelObject {

    // Parameters
    private List<String> parameterTypes = new LinkedList<String>();

    // Return
    private String returnType;

    // Name of the operation
    private String name;

    // Callback
    private boolean callback;

    private int conversationSequence;

    // Interceptors defined against the operation
    private Set<PhysicalInterceptorDefinition> interceptors = new HashSet<PhysicalInterceptorDefinition>();

    /**
     * Returns the fully qualified parameter types for this operation.
     *
     * @return Parameter types.
     */
    public List<String> getParameters() {
        return Collections.unmodifiableList(parameterTypes);
    }

    /**
     * Add the fully qualified parameter type to the operation.
     *
     * @param parameter Parameter type to be added.
     */
    public void addParameter(String parameter) {
        parameterTypes.add(parameter);
    }

    /**
     * Gets the fuly qualified return type for this operation.
     *
     * @return Return type for this operation.
     */
    public String getReturnType() {
        return returnType;
    }

    /**
     * Sets the fully qualified return type for this operation.
     *
     * @param returnType Return type for this operation.
     */
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    /**
     * Returns the interceptor definitions available for this operation.
     *
     * @return Inteceptor definitions for this operation.
     */
    public Set<PhysicalInterceptorDefinition> getInterceptors() {
        return Collections.unmodifiableSet(interceptors);
    }

    /**
     * Adds an interceptor definition to the operation.
     *
     * @param interceptor Interceptor definition to be added.
     */
    public void addInterceptor(PhysicalInterceptorDefinition interceptor) {
        interceptors.add(interceptor);
    }

    /**
     * Gets the name of the operation.
     *
     * @return Operation name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the operation.
     *
     * @param name Operation name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Checks whether the operation is a callback.
     *
     * @return True if this is a callback.
     */
    public boolean isCallback() {
        return callback;
    }

    /**
     * Sets whether this is a callback operation or not.
     *
     * @param callback True if this is a callback.
     */
    public void setCallback(boolean callback) {
        this.callback = callback;
    }


    public int getConversationSequence() {
        return conversationSequence;
    }

    public void setConversationSequence(int conversationSequence) {
        this.conversationSequence = conversationSequence;
    }
}
