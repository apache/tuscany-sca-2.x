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
package org.apache.tuscany.sca.idl.impl;

import java.util.List;

import org.apache.tuscany.sca.idl.DataType;
import org.apache.tuscany.sca.idl.Operation;

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

}
