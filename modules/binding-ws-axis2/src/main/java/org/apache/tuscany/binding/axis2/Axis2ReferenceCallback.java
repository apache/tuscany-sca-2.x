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
package org.apache.tuscany.binding.axis2;

import java.lang.reflect.InvocationTargetException;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.client.async.AsyncResult;
import org.apache.axis2.client.async.Callback;
import org.apache.axis2.context.MessageContext;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.TargetInvoker;

public class Axis2ReferenceCallback extends Callback {

    private Axis2ReferenceCallbackTargetInvoker targetInvoker;

    public Axis2ReferenceCallback(Axis2ReferenceCallbackTargetInvoker targetInvoker) {
        this.targetInvoker = targetInvoker;
    }

    public void onComplete(AsyncResult result) {
        MessageContext responseMC = result.getResponseMessageContext();
        OMElement responseOM = responseMC.getEnvelope().getBody().getFirstElement();
        try {
            targetInvoker.invokeTarget(new Object[] {responseOM}, TargetInvoker.NONE, null);
        } catch (InvocationTargetException e) {
            // FIXME what is the appropriate exception here?
            throw new InvocationRuntimeException(e);
        }
    }

    public void setComplete(boolean complete) {
        super.setComplete(complete);
    }

    public void onError(Exception e) {
        throw new InvocationRuntimeException(e);
    }
}
