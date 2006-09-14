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
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
//import org.apache.axis2.receivers.AbstractInOutAsyncMessageReceiver;
import org.apache.axis2.receivers.AbstractMessageReceiver;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.apache.tuscany.binding.axis2.Axis2Service.InvocationContext;
import org.apache.tuscany.binding.axis2.util.SDODataBinding;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.MessageId;

public class Axis2ServiceInOutAsyncMessageReceiver extends AbstractMessageReceiver {
    // private static final Log log = LogFactory.getLog(AbstractInOutAsyncMessageReceiver.class);

    protected Method operationMethod;
    protected SDODataBinding dataBinding;
    private Object entryPointProxy;
    private WorkContext workContext;
    private Axis2Service service;

    public Axis2ServiceInOutAsyncMessageReceiver(Object entryPointProxy,
                                                        Method operationMethod,
                                                        SDODataBinding dataBinding,
                                                        Axis2Service service,
                                                        WorkContext workContext) {
        this.entryPointProxy = entryPointProxy;
        this.operationMethod = operationMethod;
        this.dataBinding = dataBinding;
        this.workContext = workContext;
        this.service = service;
    }

    public final void receive(final MessageContext messageCtx) {
        
        Runnable theadedTask = new Runnable() {
            public void run() {
                try {
                    // Create a new message id and hand it to JDKInboundInvocationHandler
                    // via work context
                    MessageId messageId = new MessageId();
                    workContext.setCurrentMessageId(messageId);
                    // Now use message id as index to context to be used by callback target invoker
                    InvocationContext invCtx =
                        service.new InvocationContext(messageCtx, operationMethod, dataBinding, getSOAPFactory(messageCtx));
                    service.addMapping(messageId, invCtx);
                    
                    invokeBusinessLogic(messageCtx);
                } catch (AxisFault e) {
                    // log.error(e);
                }
            }
        };
        messageCtx.getConfigurationContext().getThreadPool().execute(theadedTask);
    }

    public void invokeBusinessLogic(MessageContext inMC) throws AxisFault {
        
        try {

            OMElement requestOM = inMC.getEnvelope().getBody().getFirstElement();
            Class<?>[] parameterTypes = operationMethod.getParameterTypes();
            // Try to guess if it's passing OMElements
            if(parameterTypes.length>0 && OMElement.class.isAssignableFrom(parameterTypes[0])) {    
                operationMethod.invoke(entryPointProxy, requestOM);
            } else {
                // Assumming it's SDO then
                Object[] args = dataBinding.fromOMElement(requestOM);
                operationMethod.invoke(entryPointProxy, args);
            }
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
