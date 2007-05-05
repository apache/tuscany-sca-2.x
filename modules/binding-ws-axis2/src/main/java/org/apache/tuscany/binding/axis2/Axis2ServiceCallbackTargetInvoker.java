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
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.engine.AxisEngine;
import org.apache.axis2.util.Utils;
import org.apache.tuscany.binding.axis2.Axis2ServiceBinding.InvocationContext;
import org.apache.tuscany.invocation.InvocationRuntimeException;
import org.apache.tuscany.invocation.Message;
import org.apache.tuscany.invocation.TargetInvoker;
import org.apache.tuscany.spi.component.WorkContext;

public class Axis2ServiceCallbackTargetInvoker implements TargetInvoker {

    private Axis2ServiceBinding service;
    
    protected static final OMElement RESPONSE = null;

    public Axis2ServiceCallbackTargetInvoker(Axis2ServiceBinding service) {
        this.service = service;
    }

    public Object invokeTarget(final Object payload, final short sequence, WorkContext workContext) throws InvocationTargetException {
        throw new InvocationTargetException(new InvocationRuntimeException("Operation not supported"));
    }

    private Object invokeTarget(final Object payload, Object correlationId) throws InvocationTargetException {
        try {
            // Use current correlation id as index to retrieve inv context
            InvocationContext invCtx = service.retrieveMapping(correlationId);

            MessageContext outMC = Utils.createOutMessageContext(invCtx.inMessageContext);
            outMC.getOperationContext().addMessageContext(outMC);

            OMElement responseOM;
            if (payload != null && !payload.getClass().isArray()) {
                responseOM = (OMElement) payload;
            } else {
                responseOM = (OMElement) ((Object[]) payload)[0];
            }
            SOAPEnvelope soapEnvelope = invCtx.soapFactory.getDefaultEnvelope();
            soapEnvelope.getBody().addChild(responseOM);
            outMC.setEnvelope(soapEnvelope);
            outMC.getOperationContext().setProperty(Constants.RESPONSE_WRITTEN, Constants.VALUE_TRUE);

            AxisEngine engine =
                new AxisEngine(
                    invCtx.inMessageContext.getOperationContext().getServiceContext().getConfigurationContext());
            engine.send(outMC);

            invCtx.doneSignal.countDown();

            service.removeMapping(correlationId);
        } catch (AxisFault e) {
            throw new InvocationTargetException(e);
        } catch (Throwable t) {
            throw new InvocationTargetException(t);
        }

        return RESPONSE;
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            Object correlationId = msg.getCorrelationId();
            if (correlationId == null) {
                throw new InvocationRuntimeException("Missing correlation id");
            }
            Object resp = invokeTarget(msg.getBody(), correlationId);
            msg.setBody(resp);
        } catch (Throwable e) {
            msg.setBodyWithFault(e);
        }
        return msg;
    }

    public Axis2ServiceCallbackTargetInvoker clone() throws CloneNotSupportedException {
        try {
            return (Axis2ServiceCallbackTargetInvoker) super.clone();
        } catch (CloneNotSupportedException e) {
            // will not happen
            return null;
        }
    }

    public boolean isCacheable() {
        return true;
    }

    public void setCacheable(boolean cacheable) {

    }

    public boolean isOptimizable() {
        return false;
    }

}
