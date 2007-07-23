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
package org.apache.tuscany.sca.binding.axis2;

import java.lang.reflect.InvocationTargetException;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.receivers.AbstractInMessageReceiver;
import org.apache.tuscany.sca.interfacedef.Operation;

public class Axis2ServiceInMessageReceiver extends AbstractInMessageReceiver {

    protected Operation operation;

    private Axis2ServiceProvider provider;

    public Axis2ServiceInMessageReceiver(Axis2ServiceProvider provider, Operation operation) {
        this.provider = provider;
        this.operation = operation;
    }

    public Axis2ServiceInMessageReceiver() {
    }

    @Override
    public void invokeBusinessLogic(MessageContext inMC) throws AxisFault {
        try {
            OMElement requestOM = inMC.getEnvelope().getBody().getFirstElement();
            Object[] args = new Object[] {requestOM};

            String conversationID = provider.getConversationID(inMC);
            String callbackAddress = provider.getFromEPR(inMC);
            provider.invokeTarget(operation, args, null, conversationID, callbackAddress);

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
