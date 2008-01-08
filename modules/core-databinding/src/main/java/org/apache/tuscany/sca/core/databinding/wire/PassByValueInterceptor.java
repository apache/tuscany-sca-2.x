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

package org.apache.tuscany.sca.core.databinding.wire;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.databinding.DataBinding;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.javabeans.JavaBeansDataBinding;
import org.apache.tuscany.sca.databinding.jaxb.JAXBDataBinding;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;

/**
 * Implementation of an interceptor that enforces pass-by-value semantics
 * on operation invocations by copying the operation input and output data.
 *
 * @version $Rev$ $Date$
 */
public class PassByValueInterceptor implements Interceptor {

    private DataBindingExtensionPoint dataBindings;
    private DataBinding[] inputDataBindings;
    private DataBinding outputDataBinding;
    private DataBinding javaBeanDataBinding;
    private DataBinding jaxbDataBinding;
    private Operation operation;
    private Invoker nextInvoker;

    /**
     * Constructs a new PassByValueInterceptor.
     * @param dataBindings databinding extension point
     * @param operation the intercepted operation
     */
    public PassByValueInterceptor(DataBindingExtensionPoint dataBindings, Operation operation) {
        this.operation = operation;
        
        // Cache data bindings to use
        this.dataBindings = dataBindings;
        jaxbDataBinding = dataBindings.getDataBinding(JAXBDataBinding.NAME);
        javaBeanDataBinding = dataBindings.getDataBinding(JavaBeansDataBinding.NAME);
        
        // Determine the input databindings
        if (operation.getInputType() != null) {
            List<DataType> inputTypes = operation.getInputType().getLogical();
            inputDataBindings = new DataBinding[inputTypes.size()];
            int i = 0;
            for (DataType inputType: inputTypes) {
                String id = inputType.getDataBinding(); 
                inputDataBindings[i++] = dataBindings.getDataBinding(id);
            }
        }
        
        // Determine the output databinding
        if (operation.getOutputType() != null) {
            String id = operation.getOutputType().getDataBinding();
            outputDataBinding = dataBindings.getDataBinding(id);
        }
    }

    public Message invoke(Message msg) {
        msg.setBody(copy((Object[])msg.getBody(), inputDataBindings));

        Message resultMsg = nextInvoker.invoke(msg);

        if (!msg.isFault() && operation.getOutputType() != null) {
            resultMsg.setBody(copy(resultMsg.getBody(), outputDataBinding));
        }
        return resultMsg;
    }

    /**
     * Copy an array of data objects passed to an operation
     * @param data array of objects to copy
     * @return the copy
     */
    private Object[] copy(Object[] data, DataBinding[] dataBindings) {
        if (data == null) {
            return null;
        }
        Object[] copy = new Object[data.length];
        Map<Object, Object> map = new IdentityHashMap<Object, Object>();
        for (int i = 0; i < data.length; i++) {
            Object arg = data[i];
            if (arg == null) {
                copy[i] = null;
            } else {
                Object copiedArg = map.get(arg);
                if (copiedArg != null) {
                    copy[i] = copiedArg;
                } else {
                    copiedArg = copy(arg, dataBindings[i]);
                    map.put(arg, copiedArg);
                    copy[i] = copiedArg;
                }
            }
        }
        return copy;
    }

    /**
     * Copy data using the specified databinding.
     * @param data input data
     * @param dataBinding databinding to use
     * @return a copy of the data
     */
    private Object copy(Object data, DataBinding dataBinding) {
        if (data == null) {
            return null;
        }

        // If no databinding was specified, introspect the given arg to
        // determine its databinding
        if (dataBinding == null) {
            DataType<?> dataType = dataBindings.introspectType(data);
            if (dataType != null) {
                dataBinding = dataBindings.getDataBinding(dataType.getDataBinding());
            }
            if (dataBinding == null) {
                
                // Default to the JavaBean databinding
                dataBinding = javaBeanDataBinding;            
            }
        }
        
        // Use the JAXB databinding to copy non-Serializable data
        if (dataBinding == javaBeanDataBinding) {
            
            // If the input data is an array containing non serializable elements
            // use JAXB
            Class<?> clazz = data.getClass();
            if (clazz.isArray()) {
                if (Array.getLength(data) != 0) {
                    Object element = Array.get(data, 0);
                    if (element != null && !(element instanceof Serializable)) {
                        dataBinding = jaxbDataBinding;
                    }
                }
            } else {

                // If the input data is not serializable use JAXB
                if (!(data instanceof Serializable)) {
                    dataBinding = jaxbDataBinding;
                }
            }
        }
        
        Object copy = dataBinding.copy(data);
        return copy;
    }

    public Invoker getNext() {
        return nextInvoker;
    }

    public void setNext(Invoker next) {
        this.nextInvoker = next;
    }

}
