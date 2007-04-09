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

package org.apache.tuscany.core.databinding.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.tuscany.assembly.Contract;
import org.apache.tuscany.databinding.DataType;
import org.apache.tuscany.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.java.introspection.JavaInterfaceProcessor;
import org.apache.tuscany.spi.databinding.DataBindingRegistry;
import org.osoa.sca.annotations.Reference;

/**
 * The databinding annotation processor for java interfaces
 * 
 * @version $Rev$ $Date$
 */
public class DataBindingJavaInterfaceProcessor implements JavaInterfaceProcessor {
    private static final Class[] SIMPLE_JAVA_TYPES = {Byte.class,
                                                      Character.class,
                                                      Short.class,
                                                      Integer.class,
                                                      Long.class,
                                                      Float.class,
                                                      Double.class,
                                                      Date.class,
                                                      Calendar.class,
                                                      GregorianCalendar.class,
                                                      Duration.class,
                                                      XMLGregorianCalendar.class,
                                                      BigInteger.class,
                                                      BigDecimal.class};

    private static final Set<Class> SIMPLE_TYPE_SET = new HashSet<Class>(Arrays.asList(SIMPLE_JAVA_TYPES));

    private DataBindingRegistry dataBindingRegistry;

    public DataBindingJavaInterfaceProcessor(@Reference
    DataBindingRegistry dataBindingRegistry) {
        super();
        this.dataBindingRegistry = dataBindingRegistry;
    }

    public void visitInterface(Class<?> clazz, Class<?> callbackClass, Contract contract)
        throws InvalidInterfaceException {
        if (!contract.getInterface().isRemotable()) {
            return;
        }
        List<Operation> operations = contract.getInterface().getOperations();
        processInterface(clazz, contract, operations);
        if (callbackClass != null) {
            List<Operation> callbackOperations = contract.getCallbackInterface().getOperations();
            processInterface(callbackClass, contract, callbackOperations);
        }
    }

    private void processInterface(Class<?> clazz, Contract contract, List<Operation> operations) {
        // IDLMapping interfaceMapping = clazz.getAnnotation(IDLMapping.class);
        DataType interfaceDataType = clazz.getAnnotation(DataType.class);
        if (interfaceDataType != null) {
            contract.setDataBinding(interfaceDataType.name());
        }
        for (Method method : clazz.getMethods()) {
            Operation operation = operations.get(method.getName());
            DataType operationDataType = method.getAnnotation(DataType.class);
            if (operationDataType == null) {
                operationDataType = interfaceDataType;
            }

            if (operationDataType != null) {
                operation.setDataBinding(operationDataType.name());
                // FIXME: [rfeng] Keep data context as metadata?
            }
            // IDLMapping operationMapping =
            // clazz.getAnnotation(IDLMapping.class);
            // if (operationMapping == null) {
            // operationMapping = interfaceMapping;
            // }
            //            
            // if (operationMapping != null) {
            // operation.setDataBinding(operationMapping.dataBinding());
            // operation.setWrapperStyle(operationMapping.wrapperStyle());
            // }
            // String dataBinding = operation.getDataBinding();

            Annotation[] annotations = null;
            if (operationDataType != null) {
                annotations = new Annotation[] {operationDataType};
            }
            // FIXME: We need a better way to identify simple java types
            for (org.apache.tuscany.interfacedef.DataType<?> d : operation.getInputType().getLogical()) {
                dataBindingRegistry.introspectType(d, annotations);
            }
            if (operation.getOutputType() != null) {
                dataBindingRegistry.introspectType(operation.getOutputType(), annotations);
            }
            for (org.apache.tuscany.interfacedef.DataType<?> d : operation.getFaultTypes()) {
                dataBindingRegistry.introspectType(d, annotations);
            }
        }
    }
}
