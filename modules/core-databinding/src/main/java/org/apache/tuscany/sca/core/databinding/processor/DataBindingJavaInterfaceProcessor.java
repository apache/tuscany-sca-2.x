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

package org.apache.tuscany.sca.core.databinding.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.annotation.DataBinding;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceVisitor;

/**
 * The databinding annotation processor for java interfaces
 * 
 * @version $Rev$ $Date$
 */
public class DataBindingJavaInterfaceProcessor implements JavaInterfaceVisitor {
    private DataBindingExtensionPoint dataBindingRegistry;

    public DataBindingJavaInterfaceProcessor(ExtensionPointRegistry registry) {
        super();
        this.dataBindingRegistry = registry.getExtensionPoint(DataBindingExtensionPoint.class);
    }

    public void visitInterface(JavaInterface javaInterface) throws InvalidInterfaceException {
        if (!javaInterface.isRemotable()) {
            return;
        }
        List<Operation> operations = javaInterface.getOperations();
        processInterface(javaInterface, operations);
    }

    private void processInterface(JavaInterface javaInterface, List<Operation> operations) {
        Class<?> clazz = javaInterface.getJavaClass();
        DataBinding dataBinding = clazz.getAnnotation(DataBinding.class);
        String dataBindingId = null;
        boolean wrapperStyle = false;
        if (dataBinding != null) {
            dataBindingId = dataBinding.value();
            wrapperStyle = dataBinding.wrapped();
        }

        for (Operation op : javaInterface.getOperations()) {
            JavaOperation operation = (JavaOperation) op;
            // In the case of @WebMethod, the method name can be different from the operation name

            if (dataBindingId != null) {
                op.setDataBinding(dataBindingId);
                op.setWrapperStyle(wrapperStyle);
            }

            Method method = operation.getJavaMethod();

            DataBinding methodDataBinding = clazz.getAnnotation(DataBinding.class);
            if (methodDataBinding == null) {
                methodDataBinding = dataBinding;
            }
            dataBindingId = null;
            wrapperStyle = false;
            if (dataBinding != null) {
                dataBindingId = dataBinding.value();
                wrapperStyle = dataBinding.wrapped();
                operation.setDataBinding(dataBindingId);
                operation.setWrapperStyle(wrapperStyle);
            }

            // FIXME: We need a better way to identify simple java types
            int i = 0;
            for (org.apache.tuscany.sca.interfacedef.DataType<?> d : operation.getInputType().getLogical()) {
                if (d.getDataBinding() == null) {
                    d.setDataBinding(dataBindingId);
                }
                for (Annotation a : method.getParameterAnnotations()[i]) {
                    if (a.annotationType() == org.apache.tuscany.sca.databinding.annotation.DataType.class) {
                        String value = ((org.apache.tuscany.sca.databinding.annotation.DataType)a).value();
                        d.setDataBinding(value);
                    }
                }
                dataBindingRegistry.introspectType(d, operation);
                i++;
            }
            if (operation.getOutputType() != null) {
                DataType<?> d = operation.getOutputType();
                if (d.getDataBinding() == null) {
                    d.setDataBinding(dataBindingId);
                }
                org.apache.tuscany.sca.databinding.annotation.DataType dt =
                    method.getAnnotation(org.apache.tuscany.sca.databinding.annotation.DataType.class);
                if (dt != null) {
                    d.setDataBinding(dt.value());
                }
                dataBindingRegistry.introspectType(d, operation);
            }
        }
    }
}
