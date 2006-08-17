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


import java.lang.reflect.Method;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.receivers.AbstractInOutSyncMessageReceiver;
import org.apache.tuscany.binding.axis2.util.SDODataBinding;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;

public class Axis2ServiceInOutSyncMessageReceiver extends AbstractInOutSyncMessageReceiver {

    protected Method operationMethod;
    protected SDODataBinding dataBinding;
    // protected ClassLoader classLoader;
    private Object entryPointProxy;

    public Axis2ServiceInOutSyncMessageReceiver(Object entryPointProxy,
                                                        Method operationMethod,
                                                        SDODataBinding dataBinding) {
        this.entryPointProxy = entryPointProxy;
        this.operationMethod = operationMethod;
        this.dataBinding = dataBinding;
        // this.classLoader = classLoader;
    }

    @Override
    public void invokeBusinessLogic(MessageContext inMC, MessageContext outMC) throws AxisFault {
        try {

            OMElement requestOM = inMC.getEnvelope().getBody().getFirstElement();
            OMElement responseOM = null;
            Class<?>[] parameterTypes = operationMethod.getParameterTypes();
            // Try to guess if it's passing OMElements
            if(parameterTypes.length>0 && OMElement.class.isAssignableFrom(parameterTypes[0])) {    
                responseOM = (OMElement) operationMethod.invoke(entryPointProxy, requestOM);
            } else {
                // Assumming it's SDO then
                Object[] args = dataBinding.fromOMElement(requestOM);
                Object result = operationMethod.invoke(entryPointProxy,args);
                responseOM = dataBinding.toOMElement(new Object[] {result} );
            }

            SOAPEnvelope soapEnvelope = getSOAPFactory(inMC).getDefaultEnvelope();
            soapEnvelope.getBody().addChild(responseOM);
            outMC.setEnvelope(soapEnvelope);
            outMC.getOperationContext().setProperty(Constants.RESPONSE_WRITTEN, Constants.VALUE_TRUE);

        } catch (InvocationRuntimeException e) {
            Throwable t = e.getCause();
            if (t instanceof Exception) {
                throw AxisFault.makeFault((Exception) t);
            }
            throw e;
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }

    }
}
