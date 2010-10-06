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

package org.apache.tuscany.sca.databinding.protobuf;

import org.apache.tuscany.sca.databinding.DataBinding;
import org.apache.tuscany.sca.databinding.WrapperHandler;
import org.apache.tuscany.sca.databinding.XMLTypeHelper;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;

import com.google.protobuf.Message;

/**
 * Google ProtocolBuffers databinding
 */
public class ProtobufDatabinding implements DataBinding {

    public static final String NAME = "ProtocolBuffers";

    /**
     * 
     */
    public ProtobufDatabinding() {
    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.sca.databinding.DataBinding#getName()
     */
    @Override
    public String getName() {
        return NAME;
    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.sca.databinding.DataBinding#introspect(org.apache.tuscany.sca.interfacedef.DataType, org.apache.tuscany.sca.interfacedef.Operation)
     */
    @Override
    public boolean introspect(DataType dataType, Operation operation) {
        if (Message.class.isAssignableFrom(dataType.getPhysical())) {
            dataType.setDataBinding(NAME);
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.sca.databinding.DataBinding#introspect(java.lang.Object, org.apache.tuscany.sca.interfacedef.Operation)
     */
    @Override
    public DataType introspect(Object value, Operation operation) {
        if (value instanceof Message) {
            DataType dt = new DataTypeImpl(value.getClass(), null);
            dt.setDataBinding(NAME);
            return dt;
        } else {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.sca.databinding.DataBinding#getWrapperHandler()
     */
    @Override
    public WrapperHandler getWrapperHandler() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.sca.databinding.DataBinding#copy(java.lang.Object, org.apache.tuscany.sca.interfacedef.DataType, org.apache.tuscany.sca.interfacedef.DataType, org.apache.tuscany.sca.interfacedef.Operation, org.apache.tuscany.sca.interfacedef.Operation)
     */
    @Override
    public Object copy(Object object,
                       DataType sourceDataType,
                       DataType targetDataType,
                       Operation sourceOperation,
                       Operation targetOperation) {
        Message msg = (Message)object;
        return ((Message.Builder)msg.toBuilder().clone()).build();
    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.sca.databinding.DataBinding#getXMLTypeHelper()
     */
    @Override
    public XMLTypeHelper getXMLTypeHelper() {
        return null;
    }

}
