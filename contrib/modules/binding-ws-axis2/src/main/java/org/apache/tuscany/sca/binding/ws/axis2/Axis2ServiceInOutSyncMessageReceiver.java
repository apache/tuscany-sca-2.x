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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.receivers.AbstractInOutSyncMessageReceiver;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.apache.tuscany.sca.policy.util.PolicyHandler;
import org.osoa.sca.ServiceRuntimeException;

public class Axis2ServiceInOutSyncMessageReceiver extends AbstractInOutSyncMessageReceiver {
    private static final Logger logger = Logger.getLogger(Axis2ServiceInOutSyncMessageReceiver.class.getName());
	
    protected Operation operation;
    private List<PolicyHandler> policyHandlerList = null;

    private Axis2ServiceProvider provider;

    public Axis2ServiceInOutSyncMessageReceiver(Axis2ServiceProvider provider, Operation operation, List<PolicyHandler> policyHandlerList) {
        this.provider = provider;
        this.operation = operation;
        this.policyHandlerList = policyHandlerList;
    }

    public Axis2ServiceInOutSyncMessageReceiver() {
    }

    @Override
    public void invokeBusinessLogic(MessageContext inMC, MessageContext outMC) throws AxisFault {
        try {
            OMElement requestOM = inMC.getEnvelope().getBody().getFirstElement();
            Object[] args = null;
            	
            if (requestOM != null) {
            	args = new Object[] {requestOM};
            }
            
            /*
            for ( PolicyHandler policyHandler : policyHandlerList ) {
                policyHandler.beforeInvoke(operation, args, inMC);
            }
            */
            
            OMElement responseOM = (OMElement)provider.invokeTarget(operation, args, inMC);
            
            /*
            for ( PolicyHandler policyHandler : policyHandlerList ) {
                policyHandler.afterInvoke(operation, args, inMC, responseOM);
            }
            */

            SOAPEnvelope soapEnvelope = getSOAPFactory(inMC).getDefaultEnvelope();
            if (null != responseOM ) {
                soapEnvelope.getBody().addChild(responseOM);
            }
            outMC.setEnvelope(soapEnvelope);
            outMC.getOperationContext().setProperty(Constants.RESPONSE_WRITTEN, Constants.VALUE_TRUE);

        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof FaultException && ((FaultException)t).getFaultInfo() instanceof OMElement) {
                OMElement faultDetail = (OMElement)((FaultException)t).getFaultInfo();
                inMC.setProperty(Constants.FAULT_NAME, faultDetail.getQName().getLocalPart());
                AxisFault f = new AxisFault(null, e.getMessage(), "faultNode", "faultRole", faultDetail);
                throw f;
            }
            if (t instanceof Exception) {
                throw AxisFault.makeFault((Exception)t);
            }
            logger.log(Level.SEVERE, e.getMessage(), t);
            throw new ServiceRuntimeException(e);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw AxisFault.makeFault(e);
        }
    }
}
