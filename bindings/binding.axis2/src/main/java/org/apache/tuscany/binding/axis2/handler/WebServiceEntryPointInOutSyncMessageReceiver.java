/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.axis2.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.receivers.AbstractInOutSyncMessageReceiver;
import org.apache.tuscany.binding.axis2.util.DataBinding;
import org.apache.tuscany.core.wire.InvocationRuntimeException;
import org.apache.ws.commons.om.OMElement;
import org.apache.ws.commons.soap.SOAPEnvelope;
import org.apache.ws.commons.soap.SOAPProcessingException;

public class WebServiceEntryPointInOutSyncMessageReceiver extends AbstractInOutSyncMessageReceiver {

    private Object entryPointProxy;

    protected Method operationMethod;

    protected DataBinding dataBinding;

    public WebServiceEntryPointInOutSyncMessageReceiver(Object entryPointProxy, Method operationMethod, DataBinding dataBinding) {
        this.entryPointProxy = entryPointProxy;
        this.operationMethod = operationMethod;
        this.dataBinding = dataBinding;
    }

    @Override
    public void invokeBusinessLogic(MessageContext inMC, MessageContext outMC) throws AxisFault {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        ClassLoader mycl = getClass().getClassLoader();
        try {
            if (tccl != mycl) {
                Thread.currentThread().setContextClassLoader(mycl);
            }
            try {

                invokeEntryPoint(inMC, outMC);

            } catch (Exception e) {
                throw new InvocationRuntimeException(e);
            }
        } finally {
            if (tccl != mycl) {
                Thread.currentThread().setContextClassLoader(tccl);
            }
        }
    }

    protected void invokeEntryPoint(MessageContext inMC, MessageContext outMC) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, SOAPProcessingException, AxisFault {

        OMElement requestOM = inMC.getEnvelope().getBody().getFirstElement();
        Object[] request = dataBinding.processRequest(requestOM);
        Object response = operationMethod.invoke(entryPointProxy, request);
        OMElement responseOM = dataBinding.processResponse(response);

        SOAPEnvelope soapEnvelope = getSOAPFactory(inMC).getDefaultEnvelope();
        soapEnvelope.getBody().addChild(responseOM);
        outMC.setEnvelope(soapEnvelope);
        outMC.getOperationContext().setProperty(Constants.RESPONSE_WRITTEN, Constants.VALUE_TRUE);
    }

}
