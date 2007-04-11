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
package org.apache.tuscany.interfacedef.impl;

import java.util.List;

import org.apache.tuscany.interfacedef.DataType;
import org.apache.tuscany.interfacedef.Interface;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.util.WrapperInfo;

/**
 * Represents an operation on a service interface.
 * 
 * @version $Rev$ $Date$
 */
public class OperationImpl implements Operation {

    private String name;
    private boolean unresolved;
    private DataType outputType;
    private DataType<List<DataType>> inputType;
    private List<DataType> faultTypes;
    private Interface interfaze;
    private ConversationSequence conversationSequence = ConversationSequence.NO_CONVERSATION;
    private boolean nonBlocking;
    private boolean wrapperStyle;
    private WrapperInfo wrapper;
    

    /**
     * @param name
     */
    public OperationImpl() {
        super();
    }

    /**
     * @param name
     */
    public OperationImpl(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean undefined) {
        this.unresolved = undefined;
    }

    /**
     * @return the faultTypes
     */
    public List<DataType> getFaultTypes() {
        return faultTypes;
    }

    /**
     * @param faultTypes the faultTypes to set
     */
    public void setFaultTypes(List<DataType> faultTypes) {
        this.faultTypes = faultTypes;
    }

    /**
     * @return the inputType
     */
    public DataType<List<DataType>> getInputType() {
        return inputType;
    }

    /**
     * @param inputType the inputType to set
     */
    public void setInputType(DataType<List<DataType>> inputType) {
        this.inputType = inputType;
    }

    /**
     * @return the outputType
     */
    public DataType getOutputType() {
        return outputType;
    }

    /**
     * @param outputType the outputType to set
     */
    public void setOutputType(DataType outputType) {
        this.outputType = outputType;
    }

    /**
     * @return the interfaze
     */
    public Interface getInterface() {
        return interfaze;
    }

    /**
     * @param interfaze the interfaze to set
     */
    public void setInterface(Interface interfaze) {
        this.interfaze = interfaze;
    }

    /**
     * @return the conversationSequence
     */
    public ConversationSequence getConversationSequence() {
        return conversationSequence;
    }

    /**
     * @param conversationSequence the conversationSequence to set
     */
    public void setConversationSequence(ConversationSequence conversationSequence) {
        this.conversationSequence = conversationSequence;
    }

    /**
     * @return the nonBlocking
     */
    public boolean isNonBlocking() {
        return nonBlocking;
    }

    /**
     * @param nonBlocking the nonBlocking to set
     */
    public void setNonBlocking(boolean nonBlocking) {
        this.nonBlocking = nonBlocking;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((conversationSequence == null) ? 0 : conversationSequence.hashCode());
        // result = PRIME * result + ((faultTypes == null) ? 0 : faultTypes.hashCode());
        result = PRIME * result + ((inputType == null) ? 0 : inputType.hashCode());
        result = PRIME * result + ((name == null) ? 0 : name.hashCode());
        result = PRIME * result + (nonBlocking ? 1231 : 1237);
        result = PRIME * result + ((outputType == null) ? 0 : outputType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OperationImpl other = (OperationImpl)obj;
        if (conversationSequence == null) {
            if (other.conversationSequence != null) {
                return false;
            }
        } else if (!conversationSequence.equals(other.conversationSequence)) {
            return false;
        }
        /*
        if (faultTypes == null) {
            if (other.faultTypes != null) {
                return false;
            }
        } else if (!faultTypes.equals(other.faultTypes)) {
            return false;
        }
        */
        
        if (inputType == null) {
            if (other.inputType != null) {
                return false;
            }
        } else if (!inputType.equals(other.inputType)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (nonBlocking != other.nonBlocking) {
            return false;
        }
        if (outputType == null) {
            if (other.outputType != null) {
                return false;
            }
        } else if (!outputType.equals(other.outputType)) {
            return false;
        }
        return true;
    }

    /**
     * @return the wrapperInfo
     */
    public WrapperInfo getWrapper() {
        return wrapper;
    }

    /**
     * @param wrapperInfo the wrapperInfo to set
     */
    public void setWrapper(WrapperInfo wrapperInfo) {
        this.wrapper = wrapperInfo;
    }

    /**
     * @return the wrapperStyle
     */
    public boolean isWrapperStyle() {
        return wrapperStyle;
    }

    /**
     * @param wrapperStyle the wrapperStyle to set
     */
    public void setWrapperStyle(boolean wrapperStyle) {
        this.wrapperStyle = wrapperStyle;
    }

}
