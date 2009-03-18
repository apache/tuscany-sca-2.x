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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.WrapperHandler;
import org.apache.tuscany.sca.databinding.javabeans.JavaBeansDataBinding;
import org.apache.tuscany.sca.databinding.javabeans.SimpleJavaDataBinding;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceVisitor;
import org.apache.tuscany.sca.interfacedef.util.WrapperInfo;

/**
 * The databinding annotation processor for java interfaces
 * 
 * @version $Rev$ $Date$
 */
public class WrapperJavaInterfaceProcessor implements JavaInterfaceVisitor {
    private static final String JAXB_DATABINDING = "javax.xml.bind.JAXBElement";
    private DataBindingExtensionPoint dataBindingRegistry;

    public WrapperJavaInterfaceProcessor(DataBindingExtensionPoint dataBindingRegistry) {
        super();
        this.dataBindingRegistry = dataBindingRegistry;
    }

    public void visitInterface(JavaInterface javaInterface) throws InvalidInterfaceException {
        if (!javaInterface.isRemotable()) {
            return;
        }
        for (Operation operation : javaInterface.getOperations()) {
            WrapperInfo wrapper = operation.getWrapper();
            if (wrapper == null) {
                continue;
            }
            // JIRA: TUSCANY-842
            String db = wrapper.getDataBinding();
            if (db == null || JAXB_DATABINDING.equals(db)) {
                db = assignOperationDataBinding(operation);
            }

            // Introspect the wrapper data type
            org.apache.tuscany.sca.databinding.DataBinding dbObj = dataBindingRegistry.getDataBinding(db);
            WrapperHandler handler = dbObj == null ? null : dbObj.getWrapperHandler();
            if (handler != null) {
                wrapper.setInputWrapperType(handler.getWrapperType(operation, true));
                wrapper.setOutputWrapperType(handler.getWrapperType(operation, false));
            }
            if (dbObj != null && handler == null) {
                // To avoid JAXB wrapper bean generation
                wrapper.setInputWrapperType(null);
                wrapper.setOutputWrapperType(null);
            }
        }
    }

    /*
     *  Assigns an operation DB if one of the input types, output type, fault types has a non-default DB.
     *  However, if two of the input types, output type, fault types have two different non-default DBs 
     *  ( e.g. SDO and JAXB), then we do nothing to the operation DB.
     *  
     *  The method logic assumes the JavaBeans DataBinding is the default 
     */
    private String assignOperationDataBinding(Operation operation) {

        Set<String> dbs = new HashSet<String>();

        // Can't use DataType<?> since operation.getInputType() returns: DataType<List<DataType>> 
        List<DataType> opDataTypes = new LinkedList<DataType>();

        opDataTypes.addAll(operation.getInputType().getLogical());
        opDataTypes.add(operation.getOutputType());
        for (DataType<DataType> ft : operation.getFaultTypes()) {
            opDataTypes.add(ft.getLogical());
        }

        for (DataType<?> d : opDataTypes) {
            if (d != null) {
                String dataBinding = d.getDataBinding();
                if ("java:array".equals(dataBinding)) {
                    dataBinding = ((DataType)d.getLogical()).getDataBinding();
                }
                if (dataBinding != null) {
                    dbs.add(dataBinding);
                }
            }
        }

        dbs.remove(JavaBeansDataBinding.NAME);
        dbs.remove(SimpleJavaDataBinding.NAME);

        if (dbs.size() == 1) {
            String db = dbs.iterator().next();
            operation.getWrapper().setDataBinding(db);
            return db;
        } else {
            return operation.getWrapper().getDataBinding();
        }
    }
}
