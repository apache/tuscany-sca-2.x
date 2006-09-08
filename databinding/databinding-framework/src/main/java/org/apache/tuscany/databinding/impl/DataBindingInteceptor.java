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

import org.apache.tuscany.databinding.Mediator;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.RuntimeWire;

/**
 * An interceptor to transform data accross databindings on the wire
 */
public class DataBindingInteceptor implements Interceptor {
    private Interceptor next;

    private RuntimeWire<?> sourceWire; // NOPMD by rfeng on 8/29/06 10:15 AM

    private RuntimeWire<?> targetWire; // NOPMD by rfeng on 8/29/06 10:15 AM

    private Operation<?> sourceOperation;

    private Operation<?> targetOperation;

    private Mediator mediator;

    public DataBindingInteceptor(RuntimeWire<?> sourceWire, Operation<?> sourceOperation, RuntimeWire<?> targetWire,
            Operation<?> targetOperation) {
        super();
        this.sourceWire = sourceWire;
        this.sourceOperation = sourceOperation;
        this.targetWire = targetWire;
        this.targetOperation = targetOperation;
    }

    /**
     * @see org.apache.tuscany.spi.wire.Interceptor#getNext()
     */
    public Interceptor getNext() {
        return next;
    }

    /**
     * @see org.apache.tuscany.spi.wire.Interceptor#invoke(org.apache.tuscany.spi.wire.Message)
     */
    @SuppressWarnings("unchecked")
    public Message invoke(Message msg) {
        Object input = transform(msg.getBody(), sourceOperation.getInputType(), targetOperation.getInputType());
        msg.setBody(input);
        Message resultMsg = next.invoke(msg);
        Object result = resultMsg.getBody();
        if (result != null) {
            result = transform(result, targetOperation.getOutputType(), sourceOperation.getOutputType());
            resultMsg.setBody(result);
        }
        return resultMsg;
    }

    private Object transform(Object source, DataType sourceType, DataType targetType) {
        if (source == null) {
            // Shortcut for null value
            return null;
        }
        if (sourceType == targetType || (sourceType != null && sourceType.equals(targetType))) {
            return source;
        }
        return mediator.mediate(source, sourceType, targetType);
    }

    /**
     * @see org.apache.tuscany.spi.wire.Interceptor#isOptimizable()
     */
    public boolean isOptimizable() {
        return false;
    }

    /**
     * @see org.apache.tuscany.spi.wire.Interceptor#setNext(org.apache.tuscany.spi.wire.Interceptor)
     */
    public void setNext(Interceptor next) {
        this.next = next;
    }

    /**
     * @param mediator the mediator to set
     */
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

}
