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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.tuscany.interfacedef.DataType;
import org.apache.tuscany.interfacedef.Interface;
import org.apache.tuscany.interfacedef.Operation;

/**
 * Represents a service interface.
 * 
 * @version $Rev$ $Date$
 */
public class InterfaceImpl implements Interface {

    private boolean remotable;
    private boolean conversational;
    private OperationList operations = new OperationList();
    private boolean unresolved;

    public boolean isRemotable() {
        return remotable;
    }

    public void setRemotable(boolean local) {
        this.remotable = local;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean undefined) {
        this.unresolved = undefined;
    }

    /**
     * @return the conversational
     */
    public boolean isConversational() {
        return conversational;
    }

    /**
     * @param conversational the conversational to set
     */
    public void setConversational(boolean conversational) {
        this.conversational = conversational;
    }

    private class OperationList extends ArrayList<Operation> {
        private static final long serialVersionUID = -903469106307606099L;

        @Override
        public Operation set(int index, Operation element) {
            element.setInterface(InterfaceImpl.this);
            return super.set(index, element);
        }

        @Override
        public void add(int index, Operation element) {
            element.setInterface(InterfaceImpl.this);
            super.add(index, element);
        }

        @Override
        public boolean add(Operation o) {
            o.setInterface(InterfaceImpl.this);
            return super.add(o);
        }

        @Override
        public boolean addAll(Collection<? extends Operation> c) {
            for (Operation op : c) {
                op.setInterface(InterfaceImpl.this);
            }
            return super.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends Operation> c) {
            for (Operation op : c) {
                op.setInterface(InterfaceImpl.this);
            }
            return super.addAll(index, c);
        }

    }

    public void setDefaultDataBinding(String dataBinding) {
        for (Operation op : getOperations()) {
            if (op.getDataBinding() == null) {
                op.setDataBinding(dataBinding);
                DataType<List<DataType>> inputType = op.getInputType();
                if (inputType != null) {
                    for (DataType d : inputType.getLogical()) {
                        if (d.getDataBinding() == null) {
                            d.setDataBinding(dataBinding);
                        }
                    }
                }
                DataType outputType = op.getOutputType();
                if (outputType != null && outputType.getDataBinding() == null) {
                    outputType.setDataBinding(dataBinding);
                }
                List<DataType> faultTypes = op.getFaultTypes();
                if (faultTypes != null) {
                    for (DataType d : faultTypes) {
                        if (d.getDataBinding() == null) {
                            d.setDataBinding(dataBinding);
                        }
                    }
                }
            }
        }
    }

}
