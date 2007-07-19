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
package org.apache.tuscany.sca.interfacedef.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.WrapperInfo;

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
                if (op.isWrapperStyle()) {
                    WrapperInfo wrapper = op.getWrapper();
                    if (wrapper != null) {
                        DataType<List<DataType>> unwrappedInputType = wrapper.getUnwrappedInputType();
                        if (unwrappedInputType != null) {
                            for (DataType d : unwrappedInputType.getLogical()) {
                                if (d.getDataBinding() == null) {
                                    d.setDataBinding(dataBinding);
                                }
                            }
                        }
                        DataType unwrappedOutputType = wrapper.getUnwrappedOutputType();
                        if (unwrappedOutputType != null && unwrappedOutputType.getDataBinding() == null) {
                            unwrappedOutputType.setDataBinding(dataBinding);
                        }
                    }
                }
            }
        }
    }

    public boolean isDynamic() {
        return false;
    }

    @Override
    public InterfaceImpl clone() throws CloneNotSupportedException {
        InterfaceImpl copy = (InterfaceImpl) super.clone();
        copy.operations = new OperationList();
        for (Operation operation : this.operations) {
            Operation clonedOperation = (Operation) operation.clone();
            copy.operations.add(clonedOperation);
        }
        return copy;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (conversational ? 1231 : 1237);
        result = prime * result + ((operations == null) ? 0 : operations.hashCode());
        result = prime * result + (remotable ? 1231 : 1237);
        result = prime * result + (unresolved ? 1231 : 1237);
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final InterfaceImpl other = (InterfaceImpl)obj;
        if (conversational != other.conversational)
            return false;
        if (operations == null) {
            if (other.operations != null)
                return false;
        } else if (!operations.equals(other.operations))
            return false;
        if (remotable != other.remotable)
            return false;
        if (unresolved != other.unresolved)
            return false;
        return true;
    }

}
