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
package org.apache.tuscany.interfacedef;

import java.util.List;

import org.apache.tuscany.sca.interfacedef.util.WrapperInfo;

/**
 * Represents an operation on a service interface.
 */
public interface Operation {
    enum ConversationSequence {
        NO_CONVERSATION, CONVERSATION_CONTINUE, CONVERSATION_END
    };

    /**
     * Returns the name of the operation.
     * 
     * @return the name of the operation
     */
    String getName();

    /**
     * Sets the name of the operation.
     * 
     * @param name the name of the operation
     */
    void setName(String name);

    /**
     * Returns true if the model element is unresolved.
     * 
     * @return true if the model element is unresolved.
     */
    boolean isUnresolved();

    /**
     * Sets whether the model element is unresolved.
     * 
     * @param unresolved whether the model element is unresolved
     */
    void setUnresolved(boolean unresolved);

    /**
     * Get the data type that represents the input of this operation. The logic
     * type is a list of data types and each element represents a parameter
     * 
     * @return the inputType
     */
    DataType<List<DataType>> getInputType();
    
    /**
     * @param inputType
     */
    void setInputType(DataType<List<DataType>> inputType);

    /**
     * Get the data type for the output
     * 
     * @return the outputType
     */
    DataType getOutputType();
    
    /**
     * @param outputType
     */
    void setOutputType(DataType outputType);

    /**
     * Get a list of data types to represent the faults/exceptions
     * 
     * @return the faultTypes
     */
    List<DataType> getFaultTypes();
    
    /**
     * @param faultTypes
     */
    void setFaultTypes(List<DataType> faultTypes);
    
    /**
     * Get the owning interface
     * @return
     */
    Interface getInterface();
    
    /**
     * Set the owning interface
     * @param interfaze
     */
    void setInterface(Interface interfaze);
    
    /**
     * Get the sequence of the conversation
     * @return
     */
    ConversationSequence getConversationSequence();
    
    /**
     * Set the sequence of conversation for the operation
     * @param sequence
     */
    void setConversationSequence(ConversationSequence sequence);
    
    /**
     * Indicate if the operation is non-blocking
     * @return
     */
    boolean isNonBlocking();
    
    /**
     * Set the operation to be non-blocking
     */
    void setNonBlocking(boolean nonBlocking);
    
    /**
     * @return the wrapperInfo
     */
    WrapperInfo getWrapper();

    /**
     * @param wrapperInfo the wrapperInfo to set
     */
    void setWrapper(WrapperInfo wrapperInfo);

    /**
     * @return the wrapperStyle
     */
    boolean isWrapperStyle();

    /**
     * @param wrapperStyle the wrapperStyle to set
     */
    void setWrapperStyle(boolean wrapperStyle);
    
    /**
     * Get the databinding for the operation
     * @return
     */
    String getDataBinding();
    
    /**
     * Set the databinding for the operation
     * @param dataBinding
     */
    void setDataBinding(String dataBinding);
}
