/**
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
package org.apache.tuscany.binding.axis2.externalservice;

import java.util.Map;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.ServiceClient;
import org.apache.tuscany.core.extension.ExternalServiceInvoker;
import org.osoa.sca.ServiceRuntimeException;

/**
 * Axis2ServiceInvoker uses Axis2 to invoke a remote web service
 */
public class Axis2ServiceInvoker implements ExternalServiceInvoker {

    private ServiceClient serviceClient;

    private Map<String, Axis2OperationInvoker> operationInvokers;

    public Axis2ServiceInvoker(ServiceClient serviceClient, Map<String, Axis2OperationInvoker> operationInvokers) {
        this.serviceClient = serviceClient;
        this.operationInvokers = operationInvokers;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.binding.axis2.handler.ExternalServiceInvoker#invoke(java.lang.String, java.lang.Object[])
     */
    public Object invoke(String methodName, Object[] args) {
        try {

            Axis2OperationInvoker invoker = operationInvokers.get(methodName);

            // Axis2 operationClients can not be shared so create a new one for each request
            OperationClient operationClient = serviceClient.createClient(invoker.getWSDLOperationName());

            return invoker.invokeOperation(operationClient, args);

        } catch (AxisFault e) {
            throw new ServiceRuntimeException(e);
        }
    }

}
