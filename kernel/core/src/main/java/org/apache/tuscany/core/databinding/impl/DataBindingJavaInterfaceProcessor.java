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

package org.apache.tuscany.core.databinding.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

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

    private static final String SIMPLE_JAVA_OBJECTS = "java.lang.Object";

    private static final Class[] SIMPLE_JAVA_TYPES =
        {Byte.class, Character.class, Short.class, Integer.class, Long.class, Float.class, Double.class,
         Date.class, Calendar.class, GregorianCalendar.class, Duration.class, XMLGregorianCalendar.class};

    private static final Set<Class> SIMPLE_TYPE_SET = new HashSet<Class>(Arrays.asList(SIMPLE_JAVA_TYPES));

    public void visitInterface(Class<?> clazz, Class<?> callbackClass, JavaServiceContract contract)
        throws InvalidServiceContractException {
        Map<String, Operation<Type>> operations = contract.getOperations();
        processInterface(clazz, contract, operations);
        if (callbackClass != null) {
            Map<String, Operation<Type>> callbackOperations = contract.getCallbackOperations();
            processInterface(callbackClass, contract, callbackOperations);
        }
    }

    private void processInterface(Class<?> clazz,
                                  JavaServiceContract contract,
                                  Map<String, Operation<Type>> operations) {
        DataType interfaceDataType = clazz.getAnnotation(DataType.class);
        if (interfaceDataType != null) {
            contract.setDataBinding(interfaceDataType.name());
            // FIXME: [rfeng] Keep data context as metadata?
            for (DataContext c : interfaceDataType.context()) {
                contract.setMetaData(c.key(), c.value());
            }
        }
        for (Method method : clazz.getMethods()) {
            Operation<?> operation = operations.get(method.getName());
            DataType operationDataType = method.getAnnotation(DataType.class);

            if (operationDataType != null) {
                operation.setDataBinding(operationDataType.name());
                // FIXME: [rfeng] Keep data context as metadata?
                for (DataContext c : operationDataType.context()) {
                    operation.setMetaData(c.key(), c.value());
                }
            }

            String dataBinding = operation.getDataBinding();

            // FIXME: We need a better way to identify simple java types
            for (org.apache.tuscany.spi.model.DataType<?> d : operation.getInputType().getLogical()) {
                adjustSimpleType(d, dataBinding);
            }
            if (operation.getOutputType() != null) {
                adjustSimpleType(operation.getOutputType(), dataBinding);
            }
            for (org.apache.tuscany.spi.model.DataType<?> d : operation.getFaultTypes()) {
                adjustSimpleType(d, dataBinding);
            }
        }
    }

    private void adjustSimpleType(org.apache.tuscany.spi.model.DataType<?> dataType, String dataBinding) {
        Type type = dataType.getPhysical();
        if (!(type instanceof Class)) {
            return;
        }
        Class cls = (Class)dataType.getPhysical();
        if (cls.isPrimitive() || SIMPLE_TYPE_SET.contains(cls)) {
            dataType.setDataBinding(SIMPLE_JAVA_OBJECTS);
        } else if (cls == String.class && (dataBinding == null || !dataBinding.equals(String.class.getName()))) {
            // Identify the String as a simple type
            dataType.setDataBinding(SIMPLE_JAVA_OBJECTS);
        }
    }
}
