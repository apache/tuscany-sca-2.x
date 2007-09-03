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
package org.apache.tuscany.sca.binding.ws.axis2;

import java.lang.reflect.InvocationTargetException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.receivers.AbstractInOutSyncMessageReceiver;
import org.apache.tuscany.sca.interfacedef.Operation;

public class Axis2ServiceInOutSyncMessageReceiver extends AbstractInOutSyncMessageReceiver {

    protected Operation operation;

    private Axis2ServiceProvider provider;

    public Axis2ServiceInOutSyncMessageReceiver(Axis2ServiceProvider provider, Operation operation) {
        this.provider = provider;
        this.operation = operation;
    }

    public Axis2ServiceInOutSyncMessageReceiver() {
    }

    @Override
    public void invokeBusinessLogic(MessageContext inMC, MessageContext outMC) throws AxisFault {
        try {
            OMElement requestOM = inMC.getEnvelope().getBody().getFirstElement();
            Object[] args = new Object[] {requestOM};
            OMElement responseOM = (OMElement)provider.invokeTarget(operation, args, inMC);

            SOAPEnvelope soapEnvelope = getSOAPFactory(inMC).getDefaultEnvelope();
            if (null != responseOM ) {
                soapEnvelope.getBody().addChild(responseOM);
            }
            outMC.setEnvelope(soapEnvelope);
            outMC.getOperationContext().setProperty(Constants.RESPONSE_WRITTEN, Constants.VALUE_TRUE);

        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof Exception) {
                throw AxisFault.makeFault((Exception)t);
            }
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }
}
