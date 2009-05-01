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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.tuscany.sca.invocation.DataExchangeSemantics;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * An interceptor to transform data across databindings on the wire
 * 
 * @version $Rev$ $Date$
 */
public class DataTransformationInterceptor implements Interceptor, DataExchangeSemantics {
    private Invoker next;

    private Operation sourceOperation;

    private Operation targetOperation;
    private RuntimeWire wire;
    private Mediator mediator;

    public DataTransformationInterceptor(RuntimeWire wire,
                                         Operation sourceOperation,
                                         Operation targetOperation,
                                         Mediator mediator) {
        super();
        this.sourceOperation = sourceOperation;
        this.targetOperation = targetOperation;
        this.mediator = mediator;
        this.wire = wire;
    }

    public Invoker getNext() {
        return next;
    }

    public Message invoke(Message msg) {
        Map<String, Object> metadata = new HashMap<String, Object>();
        metadata.put("wire", wire);
        Object input = mediator.mediateInput(msg.getBody(), sourceOperation, targetOperation, metadata);
        msg.setBody(input);
        Message resultMsg = next.invoke(msg);
        Object result = resultMsg.getBody();
        if (sourceOperation.isNonBlocking()) {
            // Not to reset the message body
            return resultMsg;
        }


        if (resultMsg.isFault()) {
            Object transformedFault = null;
            if ((result instanceof Exception) && !(result instanceof RuntimeException)) {
                transformedFault = mediator.mediateFault(result, sourceOperation, targetOperation, metadata);
                if (transformedFault != result) {
                    resultMsg.setFaultBody(transformedFault);
                }
            }
            //
            // Leave it to another layer to actually throw the Exception which constitutes
            // the message body.  We don't throw it here.
            //
        } else {
            assert !(result instanceof Throwable) : "Expected messages that are not throwable " + result;
            Object newResult = mediator.mediateOutput(result, sourceOperation, targetOperation, metadata);
            resultMsg.setBody(newResult);
        }

        return resultMsg;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }

    public boolean allowsPassByReference() {
        return true;
    }

}
