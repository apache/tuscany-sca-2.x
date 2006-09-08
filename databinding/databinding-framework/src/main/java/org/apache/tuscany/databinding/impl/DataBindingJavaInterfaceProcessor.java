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

package org.apache.tuscany.databinding.impl;

import java.lang.reflect.Method;

import org.apache.tuscany.api.annotation.DataContext;
import org.apache.tuscany.api.annotation.DataType;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorExtension;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Operation;

/**
 * The databinding annotation processor for java interfaces
 */
public class DataBindingJavaInterfaceProcessor extends JavaInterfaceProcessorExtension {

    /**
     * @see org.apache.tuscany.spi.idl.java.JavaInterfaceProcessor#visitInterface(java.lang.Class,
     *      org.apache.tuscany.spi.idl.java.JavaServiceContract)
     */
    public void visitInterface(Class<?> clazz, JavaServiceContract contract) throws InvalidServiceContractException {
        DataType interfaceDataType = clazz.getAnnotation(DataType.class);
        if (interfaceDataType != null) {
            contract.setDataBinding(interfaceDataType.name());
            // FIXME: [rfeng] Keep data context as metadata?
            for (DataContext c : interfaceDataType.context()) {
                contract.addMetaData(c.key(), c.value());
            }
        }
        for (Method method : clazz.getMethods()) {
            DataType operationDataType = method.getAnnotation(DataType.class);
            if (operationDataType == null) {
                operationDataType = interfaceDataType;
            }
            if (operationDataType != null) {
                Operation<?> operation = contract.getOperations().get(method.getName());
                String dataBinding = operationDataType.name();
                operation.setDataBinding(dataBinding);
                // FIXME: [rfeng] Keep data context as metadata?
                for (DataContext c : operationDataType.context()) {
                    operation.addMetaData(c.key(), c.value());
                }
                for (org.apache.tuscany.spi.model.DataType<?> d : operation.getInputType().getLogical()) {
                    d.setDataBinding(dataBinding);
                }
                if (operation.getOutputType() != null) {
                    operation.getOutputType().setDataBinding(dataBinding);
                }
                for (org.apache.tuscany.spi.model.DataType<?> d : operation.getFaultTypes()) {
                    d.setDataBinding(dataBinding);
                }
            }
        }
    }
}
